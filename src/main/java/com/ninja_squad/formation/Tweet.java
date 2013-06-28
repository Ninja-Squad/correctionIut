package com.ninja_squad.formation;

import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A tweet
 * @author JB
 */
public final class Tweet {

    /**
     * The list of tweets to play with
     */
    public static final List<Tweet> TWEETS =
        Collections.unmodifiableList(Arrays.asList(new Tweet(1L,
                                                             "@clacote",
                                                             "#lambda are cool",
                                                             new DateTime("2012-01-12T10:00"),
                                                             2),
                                                   new Tweet(2L,
                                                             "@cedric_exbrayat",
                                                             "I love #lambda too",
                                                             new DateTime("2012-01-12T11:00"),
                                                             0),
                                                   new Tweet(3L,
                                                             "@jbnizet",
                                                             "Too bad we can't use #lambda in real projects yet",
                                                             new DateTime("2012-01-12T12:00"),
                                                             0),
                                                   new Tweet(4L,
                                                             "@agnes_crepet",
                                                             "I'm gonna have a #baby. Hormones rock!",
                                                             new DateTime("2012-01-13T14:00"),
                                                             1),
                                                   new Tweet(5L,
                                                             "@brian_goetz",
                                                             "#JDK8 is delayed to 2015 :-(",
                                                             new DateTime("2012-01-14T18:00"),
                                                             234),
                                                   new Tweet(6L,
                                                             "@jbnizet",
                                                             "Oh Nooooo! I want #lambda now! #suicide",
                                                             new DateTime("2012-01-15T22:00"),
                                                             0)));

    private final Long id;
    private final String sender;
    private final String text;
    private final DateTime date;
    private final int retweetCount;

    public Tweet(Long id, String sender, String text, DateTime date, int retweetCount) {
        this.id = id;
        this.sender = sender;
        this.text = text;
        this.date = date;
        this.retweetCount = retweetCount;
    }

    public Long getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public String getText() {
        return text;
    }

    public DateTime getDate() {
        return date;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    /**
     * Extracts the hash tags from the tweet
     * @return a set of hash tags, with the leading <code>#</code>
     */
    public Set<String> getHashTags() {
        String[] words = text.split("[\\s.,;:]");
        Set<String> result = new HashSet<>();
        for (String word : words) {
            if (word.startsWith("#")) {
                result.add(word);
            }
        }
        return result;
    }

    /**
     * Tests if the given hash tag is contained into the tweet
     * @param hashTag a hash tag, with a leading <code>#</code>
     */
    public boolean containsHashTag(String hashTag) {
        return text.contains(hashTag);
    }

    @Override
    public String toString() {
        return "Tweet{" +
               "id=" + id +
               ", sender='" + sender + '\'' +
               ", text='" + text + '\'' +
               ", date=" + date +
               ", retweetCount=" + retweetCount +
               '}';
    }
}
