package com.ninja_squad.formation;

import java.util.Date;

/**
 * Statistics (number of sent tweets) for a given day and a given sender
 * @author JB
 */
public final class DailyStats {
    private final Date day;
    private final String sender;
    private final int tweetsSent;

    public DailyStats(Date day, String sender, int tweetsSent) {
        this.day = day;
        this.sender = sender;
        this.tweetsSent = tweetsSent;
    }

    public Date getDay() {
        return day;
    }

    public String getSender() {
        return sender;
    }

    public int getTweetsSent() {
        return tweetsSent;
    }
}