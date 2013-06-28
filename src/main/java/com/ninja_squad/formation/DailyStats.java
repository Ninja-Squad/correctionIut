package com.ninja_squad.formation;

import org.joda.time.LocalDate;

/**
 * Statistics (number of sent tweets) for a given day and a given sender
 * @author JB
 */
public final class DailyStats {
    private final LocalDate day;
    private final String sender;
    private final int tweetsSent;

    public DailyStats(LocalDate day, String sender, int tweetsSent) {
        this.day = day;
        this.sender = sender;
        this.tweetsSent = tweetsSent;
    }

    public LocalDate getDay() {
        return day;
    }

    public String getSender() {
        return sender;
    }

    public int getTweetsSent() {
        return tweetsSent;
    }
}