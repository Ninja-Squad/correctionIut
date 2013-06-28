package com.ninja_squad.formation;

import com.google.common.collect.ListMultimap;
import org.joda.time.LocalDate;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

/**
 * Interface of the tweet service
 * @author JB
 */
public interface TweetService {
    List<String> extractSenders();

    Set<String> extractSendersWithoutDuplication();

    SortedSet<String> extractSendersInAlphabeticalOrder();

    List<Tweet> extractTweetsWithHashTag(String hashTag);

    List<Tweet> extractTweetsWithHashTagSortedBySenderAndDate(String hashTag);

    Set<String> extractHashTags();

    ListMultimap<String, Tweet> extractTweetsBySender();

    Map<Boolean, List<Tweet>> splitTweetsForHashTag(String hashTag);

    CharacterStatistics computeCharacterStatistics();

    void computeAndSaveDailyStats(LocalDate begin, LocalDate end);
}
