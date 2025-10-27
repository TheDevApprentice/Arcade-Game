package org.example.snakegame;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.example.snakegame.snake.SnakeGame;
import org.example.snakegame.pong.PongGame;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Contrôleur du menu principal avec barre de titre custom
 * Gère les interactions utilisateur, barre de titre et affiche les scores globaux
 */
public class GameController implements Initializable {

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

    // Variables pour le drag & drop de la fenêtre
    private double xOffset = 0;
    private double yOffset = 0;

    // Référence au gestionnaire de scores global
    private ScoreManager scoreManager;

    /**
     * Initialisation du contrôleur - AVEC BARRE DE TITRE CUSTOM
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialiser le gestionnaire de scores
        scoreManager = ScoreManager.getInstance();

        // Charger les high scores sauvegardés
        loadHighScores();

        // Ajouter des effets sonores aux boutons (optionnel)
        setupButtonEffects();

        // NOUVEAU: Configurer la barre de titre draggable
        setupCustomTitleBar();

        // Afficher des informations de debug
        System.out.println("🎮 Menu principal initialisé avec ScoreManager et barre de titre custom");
        System.out.println("📁 " + getSaveFileInfo());
        System.out.println("📊 " + getSessionStats());

        // Afficher le dernier jeu joué si disponible
        if (!scoreManager.getLastPlayedGame().isEmpty()) {
            System.out.println("🎯 Dernier jeu joué: " + scoreManager.getLastPlayedGame());
        }
    }

    /**
     * NOUVEAU: Configurer la barre de titre pour le drag & drop
     */
    private void setupCustomTitleBar() {
        if (titleBar != null) {
            // Gérer le début du drag
            titleBar.setOnMousePressed((MouseEvent event) -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });

            // Gérer le déplacement de la fenêtre
            titleBar.setOnMouseDragged((MouseEvent event) -> {
                Stage stage = GameApplication.getPrimaryStage();
                if (stage != null) {
                    stage.setX(event.getScreenX() - xOffset);
                    stage.setY(event.getScreenY() - yOffset);
                }
            });

            // Changer le curseur au survol
            titleBar.setOnMouseEntered(e -> titleBar.setStyle(titleBar.getStyle() + "-fx-cursor: move;"));
            titleBar.setOnMouseExited(e -> titleBar.setStyle(titleBar.getStyle().replace("-fx-cursor: move;", "")));

            System.out.println("✅ Barre de titre draggable configurée");
        }
    }

    /**
     * NOUVEAU: Action du bouton minimiser
     */
    @FXML
    protected void onMinimizeButtonClick() {
        Stage stage = GameApplication.getPrimaryStage();
        if (stage != null) {
            stage.setIconified(true);
            System.out.println("📦 Fenêtre minimisée");
        }
    }

    /**
     * NOUVEAU: Action du bouton fermer
     */
    @FXML
    protected void onCloseButtonClick() {
        System.out.println("❌ Fermeture de l'application via barre de titre...");

        // Afficher un résumé final des scores
        System.out.println("=== SCORES FINAUX ===");
        System.out.println(scoreManager.getScoreSummary());

        // Nettoyer l'audio
        MusicController.getInstance().cleanup();

        // Forcer la sauvegarde des scores
        scoreManager.forceSave();

        // Fermer l'application proprement
        Platform.exit();
        System.exit(0);
    }

    /**
     * Action du bouton Snake
     */
    @FXML
    protected void onSnakeButtonClick() {
        System.out.println("Lancement de Snake Game...");

        try {
            // Créer une nouvelle instance du jeu Snake
            SnakeGame snakeGame = new SnakeGame();

            // Obtenir le stage principal
            Stage primaryStage = GameApplication.getPrimaryStage();

            // Lancer le jeu Snake
            snakeGame.start(primaryStage);

        } catch (Exception e) {
            System.err.println("Erreur lors du lancement de Snake: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Action du bouton Pong
     */
    @FXML
    protected void onPongButtonClick() {
        System.out.println("Lancement de Pong Game...");

        try {
            // Créer une nouvelle instance du jeu Pong
            PongGame pongGame = new PongGame();

            // Obtenir le stage principal
            Stage primaryStage = GameApplication.getPrimaryStage();

            // Lancer le jeu Pong
            pongGame.start(primaryStage);

        } catch (Exception e) {
            System.err.println("Erreur lors du lancement de Pong: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Action du bouton Quitter
     */
    @FXML
    protected void onQuitButtonClick() {
        System.out.println("Fermeture de l'application...");

        // Afficher un résumé final des scores
        System.out.println("=== SCORES FINAUX ===");
        System.out.println(scoreManager.getScoreSummary());

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

        System.out.println("Scores chargés - Snake: " + snakeHigh + ", Pong: " + pongHigh);
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
        System.out.println("Scores rafraîchis dans le menu principal");
        System.out.println(scoreManager.getScoreSummary());
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
        System.out.println("🔙 Retour au menu principal...");

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
        System.out.println("Tous les scores ont été réinitialisés depuis le menu !");
    }

    /**
     * Méthode pour exporter les scores
     */
    public void exportScores() {
        scoreManager.exportScores();
        System.out.println("Scores exportés depuis le menu !");
    }

    /**
     * Obtenir des informations sur le fichier de sauvegarde
     */
    public String getSaveFileInfo() {
        return "Fichier de scores: " + scoreManager.getSaveFilePath().toAbsolutePath();
    }
}