package com.ninja_squad.formation;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Handles the persistence part of our fictive application
 * @author JB
 */
public class TweetDAO {
    /**
     * Returns the tweets sent between the given dates, from the database
     * @param begin the begin date, inclusive
     * @param end the end date, exclusive
     */
    public List<Tweet> findByDates(DateTime begin, DateTime end) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * Saves in database the given daily stats
     */
    public void saveDailyStats(DailyStats stats) {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
