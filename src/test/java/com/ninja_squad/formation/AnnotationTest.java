package com.ninja_squad.formation;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DriverManagerDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import static com.ninja_squad.dbsetup.Operations.*;

/**
 * This class is not really a unit test, but it's used to populate the database, and the call the annotated service
 * and see if the logs are printed
 * @author JB
 */
public class AnnotationTest {
    private TweetService service;

    @Before
    public void prepare() {
        Operation op =
            sequenceOf(
                deleteAllFrom("tweet", "daily_stats"),
                insertInto("tweet")
                    .columns("id", "sender", "body", "send_date", "retweet_count")
                    .values(1L, "@clacote", "#lambda are cool", DateTime.now().minusDays(2).toDate(), 2)
                    .values(2L, "@agnes_crepet", "I'm gonna have a #baby. Hormones rock!", DateTime.now().minusDays(1).toDate(), 1)
                    .build());
        new DbSetup(new DriverManagerDestination("jdbc:hsqldb:hsql://localhost/", "SA", ""), op).launch();

        service = TweetServiceFactory.create();
    }

    @Test
    public void testAnnotations() {
        System.out.println("Should log the method call only:");
        service.extractSenders();

        System.out.println("Should log the method call and its return value:");
        service.extractSendersWithoutDuplication();

        System.out.println("Should log the method call, its return value and its arguments:");
        service.extractTweetsWithHashTag("#lambda");

        System.out.println("Should log the method call, its return value and its arguments, and the time it took:");
        service.extractTweetsWithHashTagSortedBySenderAndDate("#lambda");
    }
}
