package org.example.snakegame.common;

/**
 * Interface pour écouter les mises à jour de score
 * Plus spécifique et type-safe que Runnable
 */
@FunctionalInterface
public interface ScoreUpdateListener {

    /**
     * Appelé lorsque le score change
     * @param newScore Le nouveau score
     * @param delta La différence avec le score précédent
     */
    void onScoreChanged(int newScore, int delta);

    /**
     * Version simplifiée sans delta
     */
    default void onScoreChanged(int newScore) {
        onScoreChanged(newScore, 0);
    }
}
