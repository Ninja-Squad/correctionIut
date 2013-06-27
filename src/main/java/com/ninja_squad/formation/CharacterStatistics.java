package com.ninja_squad.formation;

/**
 * Statistics about the number of characters contained in a set of tweets
 * @author JB
 */
public class CharacterStatistics {
    private int totalNumberOfCharacters;
    private int numberOfTweets;

    public CharacterStatistics(int totalNumberOfCharacters, int numberOfTweets) {
        this.totalNumberOfCharacters = totalNumberOfCharacters;
        this.numberOfTweets = numberOfTweets;
    }

    public int getTotalNumberOfCharacters() {
        return totalNumberOfCharacters;
    }

    public int getAverageNumberOfCharacters() {
        if (totalNumberOfCharacters == 0) {
            return 0;
        }
        return totalNumberOfCharacters / numberOfTweets;
    }
}
