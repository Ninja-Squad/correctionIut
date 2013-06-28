package com.ninja_squad.formation;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Ordering;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;

/**
 * The tweet service of our fictive application. It uses the DAO.
 * @author JB
 */
public class TweetService {
    private TweetDAO dao;

    public TweetService(TweetDAO dao) {
        this.dao = dao;
    }

    private static final Function<Tweet, String> TWEET_TO_SENDER = new Function<Tweet, String>() {
        @Override
        public String apply(Tweet input) {
            return input.getSender();
        }
    };

    /**
     * extrait les senders des tweets, dans le même ordre que les tweets
     */
    public List<String> extractSenders() {
        return FluentIterable.from(getTweetsFromLast30Days())
                             .transform(TWEET_TO_SENDER)
                             .toList();
    }

    /**
     * extrait les senders des tweets, dans le même ordre que les tweets, mais sans duplicata
     */
    public Set<String> extractSendersWithoutDuplication() {
        return FluentIterable.from(getTweetsFromLast30Days())
                             .transform(TWEET_TO_SENDER)
                             .toSet();
    }

    /**
     * extrait les senders des tweets, sans duplicata, et triés par ordre alphabétique
     */
    public SortedSet<String> extractSendersInAlphabeticalOrder() {
        return FluentIterable.from(getTweetsFromLast30Days())
                             .transform(TWEET_TO_SENDER)
                             .toSortedSet(Ordering.natural());
    }


    /**
     * extrait les tweets contenant un hashtag donné, en conservant l'ordre
     */
    public List<Tweet> extractTweetsWithHashTag(final String hashTag) {
        return FluentIterable.from(getTweetsFromLast30Days())
                             .filter(containsHashTagPredicate(hashTag))
                             .toList();
    }

    private Predicate<Tweet> containsHashTagPredicate(final String hashTag) {
        return new Predicate<Tweet>() {
            @Override
            public boolean apply(Tweet input) {
                return input.containsHashTag(hashTag);
            }
        };
    }

    /**
     * extrait les tweets contenant un hashtag donné, triés par sender, puis par date
     */
    public List<Tweet> extractTweetsWithHashTagSortedBySenderAndDate(String hashTag) {
        return FluentIterable.from(getTweetsFromLast30Days())
                             .filter(containsHashTagPredicate(hashTag))
                             .toSortedList(new Comparator<Tweet>() {
                                 @Override
                                 public int compare(Tweet o1, Tweet o2) {
                                     return ComparisonChain.start()
                                                           .compare(o1.getSender(), o2.getSender())
                                                           .compare(o1.getDate(), o2.getDate())
                                                           .result();
                                 }
                             });
    }


    /**
     * extrait l'ensemble des hashTags des tweets
     */
    public Set<String> extractHashTags() {
        return FluentIterable.from(getTweetsFromLast30Days())
                             .transformAndConcat(new Function<Tweet, Iterable<String>>() {
                                 @Override
                                 public Iterable<String> apply(Tweet input) {
                                     return input.getHashTags();
                                 }
                             })
                             .toSet();
    }

    /**
     * extrait les tweets et les range par sender
     */
    public ListMultimap<String, Tweet> extractTweetsBySender() {
        return Multimaps.index(getTweetsFromLast30Days(), new Function<Tweet, String>() {
            @Override
            public String apply(Tweet tweet) {
                return tweet.getSender();
            }
        });
        // ou:
        // ListMultimap<String, Tweet> result = ArrayListMultimap.create();
        // for (Tweet tweet : getTweetsFromLast30Days()) {
        //     result.put(tweet.getSender(), tweet);
        // }
        // return result;
    }

    /**
     * extrait les tweets et les range en deux catégories : ceux qui contiennent un hashtag donné, et ceux qui ne le
     * contiennent pas
     */
    public Map<Boolean, List<Tweet>> splitTweetsForHashTag(String hashTag) {
        Map<Boolean, List<Tweet>> result = Maps.newHashMap();
        List<Tweet> tweetsFromLast30Days = getTweetsFromLast30Days();
        result.put(true, FluentIterable.from(tweetsFromLast30Days).filter(containsHashTagPredicate(hashTag)).toList());
        result.put(false, FluentIterable.from(tweetsFromLast30Days).filter(Predicates.not(containsHashTagPredicate(hashTag))).toList());
        return result;
    }

    /**
     * calcule le nombre total de caractères des tweets, ainsi que le nombre de caractères par tweet
     */
    public CharacterStatistics computeCharacterStatistics() {
        int tweetCount = 0;
        int characterCount = 0;
        for (Tweet tweet : getTweetsFromLast30Days()) {
            tweetCount++;
            characterCount += tweet.getText().length();
        }
        return new CharacterStatistics(characterCount, tweetCount);
    }

    /**
     * calcule, pour chaque jour entre deux dates données, le nombre de tweets envoyé par chaque sender, et les
     * sauvegarde en base de données (en appelant la méthode saveDailyStats)
     * @param begin the begin date, inclusive
     * @param end the end date, exclusive
     */
    public void computeAndSaveDailyStats(LocalDate begin, LocalDate end) {
        List<Tweet> tweets = dao.findByDates(begin.toDateTime(LocalTime.MIDNIGHT), end.toDateTime(LocalTime.MIDNIGHT));
        Multiset<DayAndSender> tweetsSentByDayAndSender = HashMultiset.create();
        for (Tweet tweet : tweets) {
            DayAndSender key = new DayAndSender(tweet.getDate().toLocalDate(), tweet.getSender());
            tweetsSentByDayAndSender.add(key);
        }
        for (Multiset.Entry<DayAndSender> entry : tweetsSentByDayAndSender.entrySet()) {
            dao.saveDailyStats(new DailyStats(entry.getElement().getDay(), entry.getElement().getSender(), entry.getCount()));
        }
    }

    private List<Tweet> getTweetsFromLast30Days() {
        DateTime today = DateTime.now().withTimeAtStartOfDay();
        DateTime todayMinus30Days = today.minusDays(30);
        DateTime tomorrow = today.plusDays(1);

        return dao.findByDates(todayMinus30Days, tomorrow);
    }

    private static final class DayAndSender {
        private final LocalDate day;
        private final String sender;

        public DayAndSender(LocalDate day, String sender) {
            this.day = day;
            this.sender = sender;
        }

        public LocalDate getDay() {
            return day;
        }

        public String getSender() {
            return sender;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            DayAndSender other = (DayAndSender) o;
            return Objects.equals(sender, other.sender)
                   && Objects.equals(day, other.day);
        }

        @Override
        public int hashCode() {
            return Objects.hash(sender, day);
        }
    }
}
