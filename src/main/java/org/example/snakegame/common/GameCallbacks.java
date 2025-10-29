package org.example.snakegame.common;

import java.util.Objects;

/**
 * Gestion centralisée des callbacks de jeu pour éviter la duplication de code
 */
public class GameCallbacks {

    private ScoreUpdateListener scoreUpdateListener = (score, delta) -> { };
    private GameEventListener gameEventListener = new GameEventListener() {
        @Override
        public void onScoreUpdate(int newScore) {
            // no-op
        }

        @Override
        public void onGameOver(GameResult result) {
            // no-op
        }
    };

    /**
     * Enregistrer un listener de score
     */
    public void setScoreUpdateListener(ScoreUpdateListener listener) {
        this.scoreUpdateListener = Objects.requireNonNull(listener, "scoreUpdateListener cannot be null");
    }

    /**
     * Enregistrer un listener d'événements
     */
    public void setGameEventListener(GameEventListener listener) {
        this.gameEventListener = Objects.requireNonNull(listener, "gameEventListener cannot be null");
    }

    /**
     * Notifier une mise à jour de score
     */
    public void notifyScoreUpdate(int newScore, int delta) {
        scoreUpdateListener.onScoreChanged(newScore, delta);
        gameEventListener.onScoreUpdate(newScore);
    }

    /**
     * Notifier un changement d'état
     */
    public void notifyGameStateChange(GameState oldState, GameState newState) {
        gameEventListener.onGameStateChange(oldState, newState);
    }

    /**
     * Notifier un événement spécial
     */
    public void notifySpecialEvent(String eventType, Object data) {
        gameEventListener.onSpecialEvent(eventType, data);
    }

    /**
     * Notifier un game over
     */
    public void notifyGameOver(GameResult result) {
        gameEventListener.onGameOver(result);
    }
}
