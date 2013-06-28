package com.ninja_squad.formation;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Multiset;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.Comparators;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * The tweet service of our fictive application. It uses the DAO.
 * @author JB
 */
public class TweetService {
    private TweetDAO dao;

    public TweetService(TweetDAO dao) {
        this.dao = dao;
    }

    /**
     * extrait les senders des tweets, dans le même ordre que les tweets
     */
    public List<String> extractSenders() {
        return getTweetsFromLast30Days().stream()
                                        .map(Tweet::getSender)
                                        .collect(Collectors.toList());
    }

    /**
     * extrait les senders des tweets, dans le même ordre que les tweets, mais sans duplicata
     */
    public Set<String> extractSendersWithoutDuplication() {
        return getTweetsFromLast30Days().stream()
                                        .map(Tweet::getSender)
                                        .collect(Collectors.toCollection(() -> new LinkedHashSet<>()));
    }

    /**
     * extrait les senders des tweets, sans duplicata, et triés par ordre alphabétique
     */
    public SortedSet<String> extractSendersInAlphabeticalOrder() {
        return getTweetsFromLast30Days().stream()
                                        .map(Tweet::getSender)
                                        .collect(Collectors.toCollection(() -> new TreeSet<>()));
    }


    /**
     * extrait les tweets contenant un hashtag donné, en conservant l'ordre
     */
    public List<Tweet> extractTweetsWithHashTag(String hashTag) {
        return getTweetsFromLast30Days().stream()
                                        .filter(t -> t.containsHashTag(hashTag))
                                        .collect(Collectors.toList());
    }

    /**
     * extrait les tweets contenant un hashtag donné, triés par sender, puis par date
     */
    public List<Tweet> extractTweetsWithHashTagSortedBySenderAndDate(String hashTag) {
        return getTweetsFromLast30Days().stream()
                                        .filter(t -> t.containsHashTag(hashTag))
                                        .sorted(Comparators.<Tweet, String>comparing(Tweet::getSender).<Tweet, DateTime>thenComparing(Tweet::getDate))
                                        .collect(Collectors.toList());
    }


    /**
     * extrait l'ensemble des hashTags des tweets
     */
    public Set<String> extractHashTags() {
        return getTweetsFromLast30Days().stream()
                                        .flatMap(t -> t.getHashTags().stream())
                                        .collect(Collectors.toSet());
    }

    /**
     * extrait les tweets et les range par sender
     */
    public ListMultimap<String, Tweet> extractTweetsBySender() {
        return Multimaps.index(getTweetsFromLast30Days(), Tweet::getSender);
    }

    /**
     * extrait les tweets et les range en deux catégories : ceux qui contiennent un hashtag donné, et ceux qui ne le
     * contiennent pas
     */
    public Map<Boolean, List<Tweet>> splitTweetsForHashTag(String hashTag) {
        return getTweetsFromLast30Days().stream()
                                        .collect(Collectors.partitioningBy(t -> t.containsHashTag(hashTag)));
    }

    /**
     * calcule le nombre total de caractères des tweets, ainsi que le nombre de caractères par tweet
     */
    public CharacterStatistics computeCharacterStatistics() {
        return getTweetsFromLast30Days().parallelStream()
                                        .map(t -> new CharacterStatistics(t.getText().length(), 1))
                                        .reduce(new CharacterStatistics(0, 0),
                                                (c1, c2) -> new CharacterStatistics(c1.getTotalNumberOfCharacters() + c2.getTotalNumberOfCharacters(),
                                                                                    c1.getNumberOfTweets() + c2.getNumberOfTweets()));
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
