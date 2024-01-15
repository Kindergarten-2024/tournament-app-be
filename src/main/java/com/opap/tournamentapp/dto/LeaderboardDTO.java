package com.opap.tournamentapp.dto;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
@Component
public class LeaderboardDTO {
    private Map<String, Integer> topPlayers;
    /**
     * Constructs a new instance with an empty leaderboard.
     */


    public LeaderboardDTO() {
        this.topPlayers = new LinkedHashMap<>();
    } //Hashmap vs minHeap


    /**
     * Returns a new {@link LinkedHashMap} with the top players and their scores.
     * The map maintains the order of the players based on their scores, from highest to lowest.
     *
     * @return a map of top player IDs and their scores
     */
    public Map<String, Integer> getTopPlayers() {
        return new LinkedHashMap<>(topPlayers);
    }

    /**
     * Retrieves the ID of the top player in the leaderboard.
     *
     * @return the ID of the top player
     * @throws NoSuchElementException if the leaderboard is empty
     */
    public String getTopKey() {
        return this.topPlayers.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new NoSuchElementException("No top player found"));
    }
    public void setTopPlayers(Map<String, Integer> topPlayers) {
        this.topPlayers = new LinkedHashMap<>(topPlayers); // Use LinkedHashMap to maintain order
        limitToTopPlayers(10);
    }
    /**
     * Updates the score of a player identified by the given ID.
     * If the player is already on the leaderboard, their score is increased by the specified value.
     * After updating, the leaderboard is trimmed to the top specified number of players.
     * @see #limitToTopPlayers(int)
     * @param id the ID of the player whose score is to be updated
     * @param increment the value to add to the player's current score
     */
    public void updatePlayerScore(String id, int increment) {
        this.topPlayers.merge(id, increment, Integer::sum);
        limitToTopPlayers(10);
    }
    /**
     * Trims the leaderboard to keep only the top specified number of players.
     * The trimming is based on the scores, maintaining the highest scores in the leaderboard.
     *
     * @param limit the maximum number of top players to retain in the leaderboard
     */
    private void limitToTopPlayers(int limit) {
        this.topPlayers = this.topPlayers.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
    }
}