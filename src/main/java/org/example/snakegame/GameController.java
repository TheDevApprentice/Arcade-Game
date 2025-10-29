package org.example.snakegame;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.example.snakegame.snake.SnakeGame;
import org.example.snakegame.pong.PongGame;
import org.example.snakegame.common.GameLogger;
import org.example.snakegame.common.TitleBarController;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Contrôleur du menu principal
 * Gère les interactions utilisateur et affiche les scores globaux
 * La gestion de la barre de titre est déléguée à TitleBarController (SRP)
 */
public class GameController implements Initializable {

    private final GameLogger logger = GameLogger.getLogger(GameController.class);

    // Références aux éléments FXML du menu
    @FXML private Button snakeButton;
    @FXML private Button pongButton;
    @FXML private Button quitButton;
    @FXML private Label snakeHighScore;
    @FXML private Label pongHighScore;

    // Références aux éléments FXML de la barre de titre
    @FXML private HBox titleBar;
    @FXML private Button minimizeButton;
    @FXML private Button closeButton;

    // Contrôleur dédié à la barre de titre (SRP)
    private TitleBarController titleBarController;

    // Référence au gestionnaire de scores global
    private ScoreManager scoreManager;

    /**
     * Initialisation du contrôleur - AVEC BARRE DE TITRE CUSTOM
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialiser le gestionnaire de scores
        scoreManager = ScoreManager.INSTANCE;

        // Charger les high scores sauvegardés
        loadHighScores();

        // Ajouter des effets sonores aux boutons (optionnel)
        setupButtonEffects();

        // Initialiser le contrôleur de la barre de titre (SRP)
        initializeTitleBarController();

        // Afficher des informations de debug
        logger.info("🎮 Menu principal initialisé avec ScoreManager");
        logger.info("📁 %s", getSaveFileInfo());
        logger.info("📊 %s", getSessionStats());

        // Afficher le dernier jeu joué si disponible
        if (!scoreManager.getLastPlayedGame().isEmpty()) {
            logger.info("🎯 Dernier jeu joué: %s", scoreManager.getLastPlayedGame());
        }
    }

    /**
     * Initialiser le contrôleur de la barre de titre
     * Délégation de responsabilité (SRP)
     */
    private void initializeTitleBarController() {
        if (titleBar != null) {
            Stage stage = GameApplication.getPrimaryStage();
            if (stage != null) {
                titleBarController = new TitleBarController(stage, titleBar);
                
                // Définir le callback de fermeture
                titleBarController.setOnCloseCallback(() -> {
                    // Afficher un résumé final des scores
                    logger.info("=== SCORES FINAUX ===");
                    logger.info("%s", scoreManager.getScoreSummary());
                    
                    // Nettoyer l'audio
                    MusicController.INSTANCE.cleanup();
                    
                    // Forcer la sauvegarde des scores
                    scoreManager.forceSave();
                });
                
                logger.info("✅ TitleBarController initialisé");
            }
        }
    }

    /**
     * Action du bouton minimiser - Délégation au TitleBarController
     */
    @FXML
    protected void onMinimizeButtonClick() {
        if (titleBarController != null) {
            titleBarController.minimize();
        }
    }

    /**
     * Action du bouton fermer - Délégation au TitleBarController
     */
    @FXML
    protected void onCloseButtonClick() {
        if (titleBarController != null) {
            titleBarController.close();
        }
    }

    /**
     * Action du bouton Snake
     */
    @FXML
    protected void onSnakeButtonClick() {
        logger.info("Lancement de Snake Game...");

        try {
            // Créer une nouvelle instance du jeu Snake
            SnakeGame snakeGame = new SnakeGame();

            // Obtenir le stage principal
            Stage primaryStage = GameApplication.getPrimaryStage();

            // Lancer le jeu Snake
            snakeGame.start(primaryStage);

        } catch (Exception e) {
            logger.error("Erreur lors du lancement de Snake: %s", e.getMessage());
        }
    }

    /**
     * Action du bouton Pong
     */
    @FXML
    protected void onPongButtonClick() {
        logger.info("Lancement de Pong Game...");

        try {
            // Créer une nouvelle instance du jeu Pong
            PongGame pongGame = new PongGame();

            // Obtenir le stage principal
            Stage primaryStage = GameApplication.getPrimaryStage();

            // Lancer le jeu Pong
            pongGame.start(primaryStage);

        } catch (Exception e) {
            logger.error("Erreur lors du lancement de Pong: %s", e.getMessage());
        }
    }

    /**
     * Action du bouton Quitter
     */
    @FXML
    protected void onQuitButtonClick() {
        logger.info("Fermeture de l'application...");

        // Afficher un résumé final des scores
        logger.info("=== SCORES FINAUX ===");
        logger.info("%s", scoreManager.getScoreSummary());

        // Fermer l'application proprement
        Platform.exit();
        System.exit(0);
    }

    /**
     * Charger les high scores depuis le ScoreManager
     */
    private void loadHighScores() {
        // Obtenir les scores depuis le gestionnaire global
        int snakeHigh = scoreManager.getSnakeHighScore();
        String pongHigh = scoreManager.getPongScore();

        // Afficher les scores avec des informations détaillées
        updateSnakeScoreDisplay(snakeHigh);
        updatePongScoreDisplay(pongHigh);

        logger.debug("Scores chargés - Snake: %d, Pong: %s", snakeHigh, pongHigh);
    }

    /**
     * Mettre à jour l'affichage des scores Snake avec dates
     */
    private void updateSnakeScoreDisplay(int highScore) {
        if (snakeHighScore != null) {
            // Affichage détaillé avec statistiques et dates
            String snakeText;
            if (scoreManager.getSnakeGamesPlayed() > 0) {
                snakeText = String.format("%04d", highScore);
            } else {
                snakeText = String.format("%04d", highScore);
            }
            snakeHighScore.setText(snakeText);
        }
    }

    /**
     * Mettre à jour l'affichage des scores Pong avec statistiques
     */
    private void updatePongScoreDisplay(String score) {
        if (pongHighScore != null) {
            // Affichage détaillé avec statistiques et dates
            String pongText;
            if (scoreManager.getPongGamesPlayed() > 0) {
                pongText = score;
            } else {
                pongText = score;
            }
            pongHighScore.setText(pongText);
        }
    }

    /**
     * Méthode publique pour rafraîchir les scores (appelée au retour des jeux)
     */
    public void refreshScores() {
        loadHighScores();
        logger.debug("Scores rafraîchis dans le menu principal");
        logger.debug("%s", scoreManager.getScoreSummary());
    }

    /**
     * Configurer les effets des boutons
     */
    private void setupButtonEffects() {
        // Ajouter des effets de hover personnalisés si nécessaire
        // Les effets CSS devraient suffire pour l'instant

        // Exemple d'effet au survol avec mise à jour des scores
        snakeButton.setOnMouseEntered(e -> {
            // Rafraîchir les scores à chaque survol pour s'assurer qu'ils sont à jour
            refreshScores();
        });

        pongButton.setOnMouseEntered(e -> {
            // Rafraîchir les scores à chaque survol
            refreshScores();
        });

        quitButton.setOnMouseEntered(e -> {
            // Effet sonore ou animation personnalisée
        });
    }

    /**
     * Méthode pour retourner au menu depuis un jeu - SANS SPLASH SCREEN
     * Sera appelée par les jeux individuels
     */
    public static void returnToMenu() {
        GameLogger logger = GameLogger.getLogger(GameController.class);
        logger.info("🔙 Retour au menu principal...");

        // Utiliser la méthode qui ne relance pas le splash screen
        Platform.runLater(() -> {
            GameApplication.returnToMainMenu();
        });
    }

    /**
     * Méthode pour obtenir des statistiques de session
     */
    public String getSessionStats() {
        return String.format("Session actuelle - Snake: %d points | Pong: %d victoires",
                scoreManager.getSnakeCurrentSessionScore(),
                scoreManager.getPongCurrentSessionWins());
    }

    /**
     * Méthode pour réinitialiser tous les scores (debug)
     */
    public void resetAllScores() {
        scoreManager.resetAllScores();
        refreshScores();
        logger.info("Tous les scores ont été réinitialisés depuis le menu !");
    }

    /**
     * Méthode pour exporter les scores
     */
    public void exportScores() {
        scoreManager.exportScores();
        logger.info("Scores exportés depuis le menu !");
    }

    /**
     * Obtenir des informations sur le fichier de sauvegarde
     */
    public String getSaveFileInfo() {
        return "Fichier de scores: " + scoreManager.getSaveFilePath().toAbsolutePath();
    }
}