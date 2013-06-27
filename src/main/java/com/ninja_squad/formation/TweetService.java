package com.ninja_squad.formation;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

/**
 * The tweet service of our fictive application. It uses the DAO.
 * @author JB
 */
public class TweetService {
    private TweetDAO dao;

    public TweetService(TweetDAO dao) {
        this.dao = dao;
    }

    /**
     * extrait les senders des tweets, dans le même ordre que les tweets
     */
    public List<String> extractSenders() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * extrait les senders des tweets, dans le même ordre que les tweets, mais sans duplicata
     */
    public Set<String> extractSendersWithoutDuplication() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * extrait les senders des tweets, sans duplicata, et triés par ordre alphabêtique
     */
    public SortedSet<String> extractSendersInAlphabeticalOrder() {
        throw new UnsupportedOperationException("not implemented yet");
    }


    /**
     * extrait les tweets contenant un hashtag donné, en conservant l'ordre
     */
    public List<Tweet> extractTweetsWithHashTag(String hashTag) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * extrait les tweets contenant un hashtag donné, triés par sender, puis par date
     */
    public List<Tweet> extractTweetsWithHashTagSortedBySenderAndDate(String hashTag) {
        throw new UnsupportedOperationException("not implemented yet");
    }


    /**
     * extrait l'ensemble des hashTags des tweets
     */
    public Set<String> extractHashTags() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * extrait les tweets et les range par sender
     */
    public Map<String, List<Tweet>> extractTweetsBySender() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * extrait les tweets et les range en deux catégories : ceux qui contiennent un hashtag donné, et ceux qui ne le
     * contiennent pas
     */
    public Map<Boolean, List<Tweet>> splitTweetsForHashTag(String hashTag) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * calcule le nombre total de caractères des tweets, ainsi que le nombre de caractères par tweet
     */
    public CharacterStatistics computeCharacterStatistics() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * calcule, pour chaque jour entre deux dates données, le nombre de tweets envoyé par chaque sender, et les
     * sauvegarde en base de données (en appelant la méthode saveDailyStats)
     */
    public void computeAndSaveDailyStats(Date begin, Date end) {
        throw new UnsupportedOperationException("not implemented yet");
    }

}
