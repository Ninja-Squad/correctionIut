package com.ninja_squad.formation;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DriverManagerDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.ninja_squad.dbsetup.Operations.*;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Test for TweetDAO
 * @author JB
 */
public class TweetDAOTest {

    private TweetDAO dao;

    @Before
    public void prepare() {
        Operation op =
            sequenceOf(
                deleteAllFrom("tweet", "daily_stats"),
                insertInto("tweet")
                    .columns("id", "sender", "body", "send_date", "retweet_count")
                    .values(1L, "@clacote", "#lambda are cool", "2012-01-12 10:00:00", 2)
                    .values(2L, "@agnes_crepet", "I'm gonna have a #baby. Hormones rock!", "2012-01-13 10:00:00", 1)
                    .build());
        new DbSetup(new DriverManagerDestination("jdbc:hsqldb:hsql://localhost/", "SA", ""), op).launch();

        dao = new TweetDAO();
    }

    @Test
    public void testFindByDates() throws Exception {
        List<Tweet> result = dao.findByDates(new DateTime("2012-01-01"), new DateTime("2012-01-05"));
        assertThat(result).isEmpty();

        result = dao.findByDates(new DateTime("2012-01-01"), new DateTime("2012-01-13"));
        assertThat(result).hasSize(1);
        Tweet tweet = result.get(0);
        assertThat(tweet.getId()).isEqualTo(1L);
        assertThat(tweet.getSender()).isEqualTo("@clacote");
        assertThat(tweet.getText()).isEqualTo("#lambda are cool");
        assertThat(tweet.getDate()).isEqualTo(new DateTime("2012-01-12T10:00:00"));
        assertThat(tweet.getRetweetCount()).isEqualTo(2);

        result = dao.findByDates(new DateTime("2012-01-01"), new DateTime("2012-01-14"));
        assertThat(result).hasSize(2);

        result = dao.findByDates(new DateTime("2012-01-14"), new DateTime("2012-01-20"));
        assertThat(result).hasSize(0);
    }
}
