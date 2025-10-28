package org.example.snakegame.common;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Classe représentant le résultat d'une partie
 * Contient toutes les informations pertinentes sur une partie terminée
 */
public class GameResult {

    private final String gameName;
    private final int finalScore;
    private final boolean isVictory;
    private final LocalDateTime timestamp;
    private final GameStatistics statistics;

    /**
     * Constructeur complet
     */
    public GameResult(String gameName, int finalScore, boolean isVictory, GameStatistics statistics) {
        this.gameName = Objects.requireNonNull(gameName, "Game name cannot be null");
        this.finalScore = finalScore;
        this.isVictory = isVictory;
        this.timestamp = LocalDateTime.now();
        this.statistics = statistics;
    }

    /**
     * Constructeur simplifié
     */
    public GameResult(String gameName, int finalScore, boolean isVictory) {
        this(gameName, finalScore, isVictory, null);
    }

    // Getters
    public String getGameName() { return gameName; }
    public int getFinalScore() { return finalScore; }
    public boolean isVictory() { return isVictory; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public GameStatistics getStatistics() { return statistics; }

    /**
     * Vérifier si le résultat contient des statistiques
     */
    public boolean hasStatistics() {
        return statistics != null;
    }

    @Override
    public String toString() {
        return String.format("GameResult[game=%s, score=%d, victory=%s, time=%s]",
                gameName, finalScore, isVictory, timestamp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameResult that = (GameResult) o;
        return finalScore == that.finalScore &&
                isVictory == that.isVictory &&
                Objects.equals(gameName, that.gameName) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameName, finalScore, isVictory, timestamp);
    }

    /**
     * Classe interne pour les statistiques de jeu
     */
    public static class GameStatistics {
        private final int duration; // en secondes
        private final int maxCombo;
        private final String additionalInfo;

        public GameStatistics(int duration, int maxCombo, String additionalInfo) {
            this.duration = duration;
            this.maxCombo = maxCombo;
            this.additionalInfo = additionalInfo;
        }

        public int getDuration() { return duration; }
        public int getMaxCombo() { return maxCombo; }
        public String getAdditionalInfo() { return additionalInfo; }

        @Override
        public String toString() {
            return String.format("Stats[duration=%ds, maxCombo=%d, info=%s]",
                    duration, maxCombo, additionalInfo);
        }
    }
}
