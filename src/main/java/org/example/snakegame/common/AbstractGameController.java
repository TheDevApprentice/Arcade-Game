package org.example.snakegame.common;

import javafx.animation.Timeline;
import java.util.Objects;

/**
 * Classe de base pour les contrôleurs de jeu.
 * Centralise la gestion des callbacks, du logging et des transitions d'état.
 */
public abstract class AbstractGameController {

    protected final GameLogger logger;
    private final GameCallbacks callbacks = new GameCallbacks();
    protected GameState gameState = GameState.STARTING;
    protected Timeline gameLoop;

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

    /**
     * Démarrer le jeu
     * Méthode factorisée pour éviter la duplication dans les controllers
     */
    public void startGame() {
        if (gameState == GameState.WAITING_RESTART || gameState == GameState.PAUSED) {
            updateGameState(GameState.PLAYING);
            if (gameLoop != null) {
                gameLoop.play();
            }
            logger.game("▶️", "%s démarré", getGameName());
        }
    }

    /**
     * Basculer pause/play
     * Méthode factorisée pour éviter la duplication dans les controllers
     */
    public void togglePause() {
        if (gameState == GameState.PLAYING) {
            updateGameState(GameState.PAUSED);
            if (gameLoop != null) {
                gameLoop.pause();
            }
            logger.game("⏸️", "%s en pause", getGameName());
        } else if (gameState == GameState.PAUSED) {
            updateGameState(GameState.PLAYING);
            if (gameLoop != null) {
                gameLoop.play();
            }
            logger.game("▶️", "%s repris", getGameName());
        }
        onPauseToggled(); // Hook pour render() ou autres actions spécifiques
    }

    /**
     * Arrêter le jeu
     * Méthode factorisée pour éviter la duplication dans les controllers
     */
    public void stopGame() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        updateGameState(GameState.WAITING_RESTART);
        logger.debug("%s arrêté", getGameName());
    }

    /**
     * Redémarrer le jeu
     * Méthode factorisée pour éviter la duplication dans les controllers
     */
    public void restartGame() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        onRestart(); // Hook pour réinitialiser le jeu
        logger.game("🔄", "%s redémarré", getGameName());
    }

    /**
     * Obtenir le nom du jeu (pour les logs)
     * À implémenter par les sous-classes
     */
    protected abstract String getGameName();

    /**
     * Hook appelé lors du toggle pause (pour render() par exemple)
     * Implémentation par défaut vide
     */
    protected void onPauseToggled() {
        // Implémentation par défaut vide
    }

    /**
     * Hook appelé lors du restart (pour réinitialiser le jeu)
     * À implémenter par les sous-classes
     */
    protected abstract void onRestart();

    protected static <T> T requireNonNull(T value, String message) {
        return Objects.requireNonNull(value, message);
    }
}
