package com.ninja_squad.formation;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ListMultimap;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TweetServiceImpl
 * @author JB
 */
public class TweetServiceImplTest {
    private TweetDAO mockTweetDAO;
    private TweetServiceImpl service;

    @Before
    public void prepare() {
        mockTweetDAO = mock(TweetDAO.class);
        service = new TweetServiceImpl(mockTweetDAO);

        when(mockTweetDAO.findByDates(any(DateTime.class), any(DateTime.class))).thenReturn(Tweet.TWEETS);
    }

    @Test
    public void testExtractSenders() throws Exception {
        assertThat(service.extractSenders()).containsExactly("@clacote", "@cedric_exbrayat", "@jbnizet", "@agnes_crepet", "@brian_goetz", "@jbnizet");
    }

    @Test
    public void testExtractSendersWithoutDuplication() throws Exception {
        assertThat(new ArrayList<>(service.extractSendersWithoutDuplication())).containsExactly("@clacote",
                                                                                                "@cedric_exbrayat",
                                                                                                "@jbnizet",
                                                                                                "@agnes_crepet",
                                                                                                "@brian_goetz");
    }

    @Test
    public void testExtractSendersInAlphabeticalOrder() throws Exception {
        assertThat(new ArrayList<>(service.extractSendersInAlphabeticalOrder())).containsExactly("@agnes_crepet", "@brian_goetz", "@cedric_exbrayat", "@clacote", "@jbnizet");
    }

    @Test
    public void testExtractTweetsWithHashTag() throws Exception {
        List<Tweet> result = service.extractTweetsWithHashTag("#lambda");
        assertThat(extractProperty("id").from(result)).containsExactly(1L, 2L, 3L, 6L);
    }

    @Test
    public void testExtractTweetsWithHashTagSortedBySenderAndDate() throws Exception {
        List<Tweet> result = service.extractTweetsWithHashTagSortedBySenderAndDate("#lambda");
        assertThat(extractProperty("id").from(result)).containsExactly(2L, 1L, 3L, 6L);
    }

    @Test
    public void testExtractHashTags() throws Exception {
        assertThat(service.extractHashTags()).containsOnly("#lambda", "#JDK8", "#suicide", "#baby");
    }

    @Test
    public void testExtractTweetsBySender() throws Exception {
        ListMultimap<String, Tweet> result = service.extractTweetsBySender();
        assertThat(result.asMap()).hasSize(5);
        assertThat(result.keySet()).containsOnly("@agnes_crepet", "@brian_goetz", "@cedric_exbrayat", "@clacote", "@jbnizet");
        assertThat(extractProperty("id").from(result.get("@jbnizet"))).containsExactly(3L, 6L);
    }

    @Test
    public void testSplitTweetsForHashTag() throws Exception {
        Map<Boolean, List<Tweet>> result = service.splitTweetsForHashTag("#lambda");
        assertThat(extractProperty("id").from(result.get(true))).containsExactly(1L, 2L, 3L, 6L);
        assertThat(extractProperty("id").from(result.get(false))).containsExactly(4L, 5L);
    }

    @Test
    public void testComputeCharacterStatistics() throws Exception {
        CharacterStatistics result = service.computeCharacterStatistics();
        assertThat(result.getTotalNumberOfCharacters()).isEqualTo(188);
        assertThat(result.getAverageNumberOfCharacters()).isEqualTo(31);
    }

    @Test
    public void testComputeAndSaveDailyStats() throws Exception {
        service.computeAndSaveDailyStats(new LocalDate("2012-01-12"), new LocalDate("2012-02-16"));

        ArgumentCaptor<DailyStats> captor = ArgumentCaptor.forClass(DailyStats.class);
        verify(mockTweetDAO, times(6)).saveDailyStats(captor.capture());
        List<DailyStats> savedDailyStats = captor.getAllValues();
        // sort the daily stats by day and sender to
        Collections.sort(savedDailyStats, new Comparator<DailyStats>() {
            @Override
            public int compare(DailyStats o1, DailyStats o2) {
                return ComparisonChain.start()
                                      .compare(o1.getDay(), o2.getDay())
                                      .compare(o1.getSender(), o2.getSender())
                                      .result();
            }
        });

        assertThat(extractProperty("day").from(savedDailyStats)).containsExactly(new LocalDate("2012-01-12"),
                                                                                 new LocalDate("2012-01-12"),
                                                                                 new LocalDate("2012-01-12"),
                                                                                 new LocalDate("2012-01-13"),
                                                                                 new LocalDate("2012-01-14"),
                                                                                 new LocalDate("2012-01-15"));
        assertThat(extractProperty("sender").from(savedDailyStats)).containsExactly("@cedric_exbrayat",
                                                                                    "@clacote",
                                                                                    "@jbnizet",
                                                                                    "@agnes_crepet",
                                                                                    "@brian_goetz",
                                                                                    "@jbnizet");
        assertThat(extractProperty("tweetsSent").from(savedDailyStats)).containsExactly(1, 1, 1, 1, 1, 1);
    }
}
