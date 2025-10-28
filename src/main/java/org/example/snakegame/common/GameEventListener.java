package org.example.snakegame.common;

/**
 * Interface pour écouter les événements de jeu
 * Remplace l'utilisation de Runnable pour plus de clarté et de type-safety
 */
public interface GameEventListener {

    /**
     * Appelé lorsque le score est mis à jour
     * @param newScore Le nouveau score
     */
    void onScoreUpdate(int newScore);

    /**
     * Appelé lorsque le jeu se termine
     * @param result Le résultat de la partie
     */
    void onGameOver(GameResult result);

    /**
     * Appelé lorsque l'état du jeu change
     * @param oldState L'ancien état
     * @param newState Le nouvel état
     */
    default void onGameStateChange(GameState oldState, GameState newState) {
        // Implémentation par défaut vide
    }

    /**
     * Appelé lorsqu'un événement spécial se produit
     * @param eventType Type d'événement
     * @param data Données associées
     */
    default void onSpecialEvent(String eventType, Object data) {
        // Implémentation par défaut vide
    }
}
