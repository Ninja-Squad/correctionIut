package com.ninja_squad.formation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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
        List<String> result = new ArrayList<>();
        for (Tweet tweet : getTweetsFromLast30Days()) {
            result.add(tweet.getSender());
        }
        return result;
    }

    /**
     * extrait les senders des tweets, dans le même ordre que les tweets, mais sans duplicata
     */
    public Set<String> extractSendersWithoutDuplication() {
        Set<String> result = new LinkedHashSet<>();
        for (Tweet tweet : getTweetsFromLast30Days()) {
            result.add(tweet.getSender());
        }
        return result;
    }

    /**
     * extrait les senders des tweets, sans duplicata, et triés par ordre alphabétique
     */
    public SortedSet<String> extractSendersInAlphabeticalOrder() {
        SortedSet<String> result = new TreeSet<>();
        for (Tweet tweet : getTweetsFromLast30Days()) {
            result.add(tweet.getSender());
        }
        return result;
    }


    /**
     * extrait les tweets contenant un hashtag donné, en conservant l'ordre
     */
    public List<Tweet> extractTweetsWithHashTag(String hashTag) {
        List<Tweet> result = new ArrayList<>();
        for (Tweet tweet : getTweetsFromLast30Days()) {
            if (tweet.containsHashTag(hashTag)) {
                result.add(tweet);
            }
        }
        return result;
    }

    /**
     * extrait les tweets contenant un hashtag donné, triés par sender, puis par date
     */
    public List<Tweet> extractTweetsWithHashTagSortedBySenderAndDate(String hashTag) {
        List<Tweet> result = extractTweetsWithHashTag(hashTag);
        Collections.sort(result, new Comparator<Tweet>() {
            @Override
            public int compare(Tweet o1, Tweet o2) {
                int result = o1.getSender().compareTo(o2.getSender());
                if (result == 0) {
                    result = o1.getDate().compareTo(o2.getDate());
                }
                return result;
            }
        });
        return result;
    }


    /**
     * extrait l'ensemble des hashTags des tweets
     */
    public Set<String> extractHashTags() {
        Set<String> result = new HashSet<>();
        for (Tweet tweet : getTweetsFromLast30Days()) {
            result.addAll(tweet.getHashTags());
        }
        return result;
    }

    /**
     * extrait les tweets et les range par sender
     */
    public Map<String, List<Tweet>> extractTweetsBySender() {
        Map<String, List<Tweet>> result = new HashMap<>();
        for (Tweet tweet : getTweetsFromLast30Days()) {
            List<Tweet> tweetsForSender = result.get(tweet.getSender());
            if (tweetsForSender == null) {
                tweetsForSender = new ArrayList<>();
                result.put(tweet.getSender(), tweetsForSender);
            }
            tweetsForSender.add(tweet);
        }
        return result;
    }

    /**
     * extrait les tweets et les range en deux catégories : ceux qui contiennent un hashtag donné, et ceux qui ne le
     * contiennent pas
     */
    public Map<Boolean, List<Tweet>> splitTweetsForHashTag(String hashTag) {
        Map<Boolean, List<Tweet>> result = new HashMap<>();
        List<Tweet> with = new ArrayList<>();
        List<Tweet> without = new ArrayList<>();
        result.put(true, with);
        result.put(false, without);
        for (Tweet tweet : getTweetsFromLast30Days()) {
            if (tweet.containsHashTag(hashTag)) {
                with.add(tweet);
            }
            else {
                without.add(tweet);
            }
        }
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
    public void computeAndSaveDailyStats(Date begin, Date end) {
        List<Tweet> tweets = dao.findByDates(begin, end);
        Map<DayAndSender, Integer> tweetsSentByDayAndSender = new HashMap<>();
        for (Tweet tweet : tweets) {
            DayAndSender key = new DayAndSender(getDay(tweet.getDate()), tweet.getSender());
            Integer tweetsSent = tweetsSentByDayAndSender.get(key);
            if (tweetsSent == null) {
                tweetsSent = 1;
            }
            else {
                tweetsSent ++;
            }
            tweetsSentByDayAndSender.put(key, tweetsSent);
        }
        for (Map.Entry<DayAndSender, Integer> entry : tweetsSentByDayAndSender.entrySet()) {
            dao.saveDailyStats(new DailyStats(entry.getKey().getDay(), entry.getKey().getSender(), entry.getValue()));
        }
    }

    /**
     * Returns the given date at 00:00:00
     */
    private Date getDay(Date date) {
        Calendar day = Calendar.getInstance();
        day.setTime(date);
        day.set(Calendar.HOUR_OF_DAY, 0);
        day.set(Calendar.MINUTE, 0);
        day.set(Calendar.SECOND, 0);
        day.set(Calendar.MILLISECOND, 0);
        return day.getTime();
    }

    private List<Tweet> getTweetsFromLast30Days() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        Calendar todayMinus30Days = Calendar.getInstance();
        todayMinus30Days.setTimeInMillis(today.getTimeInMillis());
        todayMinus30Days.add(Calendar.DATE, -30);

        Calendar tomorrow = Calendar.getInstance();
        tomorrow.setTimeInMillis(today.getTimeInMillis());
        tomorrow.add(Calendar.DATE, 1);

        return dao.findByDates(todayMinus30Days.getTime(), tomorrow.getTime());
    }

    private static final class DayAndSender {
        private final Date day;
        private final String sender;

        public DayAndSender(Date day, String sender) {
            this.day = day;
            this.sender = sender;
        }

        public Date getDay() {
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
