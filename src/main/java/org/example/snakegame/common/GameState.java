package org.example.snakegame.common;

/**
 * Énumération des états possibles d'un jeu
 * Utilisée par Snake et Pong pour gérer le cycle de vie du jeu
 */
public enum GameState {

    /**
     * Jeu en cours de démarrage
     */
    STARTING("Démarrage..."),

    /**
     * Jeu en cours d'exécution
     */
    PLAYING("En cours"),

    /**
     * Jeu en pause
     */
    PAUSED("En pause"),

    /**
     * Jeu terminé (défaite)
     */
    GAME_OVER("Game Over"),

    /**
     * Jeu terminé (victoire) - pour Pong
     */
    VICTORY("Victoire !"),

    /**
     * Jeu en attente de redémarrage
     */
    WAITING_RESTART("Appuyez sur R pour rejouer"),

    /**
     * Transition vers le menu
     */
    RETURNING_MENU("Retour au menu...");

    private final String displayName;

    /**
     * Constructeur de GameState
     * @param displayName Nom affiché à l'utilisateur
     */
    GameState(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Obtenir le nom d'affichage de l'état
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Vérifier si le jeu est actif (en cours)
     */
    public boolean isActive() {
        return this == PLAYING;
    }

    /**
     * Vérifier si le jeu est en pause
     */
    public boolean isPaused() {
        return this == PAUSED;
    }

    /**
     * Vérifier si le jeu est terminé
     */
    public boolean isGameOver() {
        return this == GAME_OVER || this == VICTORY;
    }

    /**
     * Vérifier si le jeu peut être mis en pause
     */
    public boolean canBePaused() {
        return this == PLAYING;
    }

    /**
     * Vérifier si le jeu peut être repris
     */
    public boolean canBeResumed() {
        return this == PAUSED;
    }

    /**
     * Vérifier si le jeu peut être redémarré
     */
    public boolean canBeRestarted() {
        return this == GAME_OVER || this == VICTORY || this == WAITING_RESTART;
    }

    /**
     * Obtenir l'état suivant logique
     */
    public GameState getNextState() {
        return switch (this) {
            case STARTING -> PLAYING;
            case PLAYING -> PAUSED;
            case PAUSED -> PLAYING;
            case GAME_OVER -> WAITING_RESTART;
            case VICTORY -> WAITING_RESTART;
            case WAITING_RESTART -> STARTING;
            case RETURNING_MENU -> STARTING;
        };
    }

    /**
     * Basculer entre PLAYING et PAUSED
     */
    public GameState togglePlayPause() {
        if (this == PLAYING) {
            return PAUSED;
        } else if (this == PAUSED) {
            return PLAYING;
        }
        return this; // Pas de changement pour les autres états
    }
}