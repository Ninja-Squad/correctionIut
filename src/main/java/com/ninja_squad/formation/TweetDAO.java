package com.ninja_squad.formation;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
        String sql =
            "select t.id, t.sender, t.body, t.send_date, t.retweet_count from tweet t"
            + " where t.send_date >= ? and t.send_date < ?";
        try (Connection c = openConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setTimestamp(1, new Timestamp(begin.getMillis()));
            stmt.setTimestamp(2, new Timestamp(end.getMillis()));
            ResultSet rs = stmt.executeQuery();
            List<Tweet> tweets = Lists.newArrayList();
            while (rs.next()) {
                tweets.add(new Tweet(rs.getLong(1),
                                     rs.getString(2),
                                     rs.getString(3),
                                     new DateTime(rs.getTimestamp(4).getTime()),
                                     rs.getInt(5)));
            }
            return tweets;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Saves in database the given daily stats
     */
    public void saveDailyStats(DailyStats stats) {
        String sql =
            "insert into dailystats (sender, day, tweets_sent) values (?, ?, ?)";
        try (Connection c = openConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setString(1, stats.getSender());
            stmt.setDate(2, new Date(stats.getDay().toDate().getTime()));
            stmt.setInt(3, stats.getTweetsSent());
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection openConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/", "SA", "");
    }
}
