package org.example.snakegame.common;

import java.util.Objects;

/**
 * Classe de base pour les contrôleurs de jeu.
 * Centralise la gestion des callbacks, du logging et des transitions d'état.
 */
public abstract class AbstractGameController {

    protected final GameLogger logger;
    private final GameCallbacks callbacks = new GameCallbacks();
    protected GameState gameState = GameState.STARTING;

    protected AbstractGameController(Class<?> contextClass) {
        this.logger = GameLogger.getLogger(contextClass);
    }

    public final void setScoreUpdateListener(ScoreUpdateListener listener) {
        callbacks.setScoreUpdateListener(requireNonNull(listener, "scoreUpdateListener cannot be null"));
    }

    public final void setGameEventListener(GameEventListener listener) {
        callbacks.setGameEventListener(requireNonNull(listener, "gameEventListener cannot be null"));
    }

    protected final void notifyScoreUpdate(int newScore, int delta) {
        callbacks.notifyScoreUpdate(newScore, delta);
    }

    protected final void notifyGameStateChange(GameState oldState, GameState newState) {
        callbacks.notifyGameStateChange(oldState, newState);
    }

    protected final void notifyGameOver(GameResult result) {
        callbacks.notifyGameOver(result);
    }

    protected final void notifySpecialEvent(String eventType, Object data) {
        callbacks.notifySpecialEvent(eventType, data);
    }

    protected final void updateGameState(GameState newState) {
        GameState validatedState = requireNonNull(newState, "newState cannot be null");
        if (this.gameState != validatedState) {
            GameState previousState = this.gameState;
            this.gameState = validatedState;
            notifyGameStateChange(previousState, validatedState);
        }
    }

    public GameState getGameState() {
        return gameState;
    }

    protected static <T> T requireNonNull(T value, String message) {
        return Objects.requireNonNull(value, message);
    }
}
