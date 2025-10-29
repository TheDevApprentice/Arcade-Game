package org.example.snakegame.pong;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.snakegame.GameApplication;
import org.example.snakegame.GameController;
import org.example.snakegame.ScoreManager;
import org.example.snakegame.common.Game;
import org.example.snakegame.common.GameEventListener;
import org.example.snakegame.common.GameResult;
import org.example.snakegame.common.GameLogger;
import org.example.snakegame.common.TitleBarController;

import java.io.IOException;

/**
 * Jeu Pong - Version corrigée avec synchronisation des boutons
 * Application JavaFX complète pour le jeu Pong
 * Implémente l'interface Game pour respecter l'OCP
 */
public class PongGame extends Application implements Game {

    // Constantes du jeu
    private static final int CANVAS_WIDTH = 800;

    // Composants graphiques FXML
    @FXML private Canvas gameCanvas;
    @FXML private Label scoreLabel;
    @FXML private Label bounceLabel;
    @FXML private Label speedLabel;
    @FXML private Button startButton;
    @FXML private Button restartButton;
    @FXML private Button menuButton;
    
    // Title bar FXML
    @FXML private HBox titleBar;
    @FXML private Button minimizeButton;
    @FXML private Button closeButton;
    
    // Contrôleur de la title bar
    private TitleBarController titleBarController;
    
    // Composants non-FXML
    private PongController pongController;
    private Label statusLabel;

    // Référence au gestionnaire de scores
    private ScoreManager scoreManager;
    private final GameLogger logger = GameLogger.getLogger(PongGame.class);

    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialiser le gestionnaire de scores
            scoreManager = ScoreManager.INSTANCE;

            // Configuration de la fenêtre (ne pas changer le style si déjà visible)
            primaryStage.setTitle("🏓 PONG GAME - Retro Arcade");

            // Charger l'interface FXML avec title bar
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/org/example/snakegame/views/pong-view-custom-titlebar.fxml"));
            fxmlLoader.setController(this);
            VBox root = fxmlLoader.load();

            // Créer la scène avec la hauteur de l'écran
            int windowHeight = GameApplication.getCanvasHeight();
            Scene scene = new Scene(root, CANVAS_WIDTH + 40, windowHeight);

            // Charger les styles CSS
            scene.getStylesheets().addAll(
                    getClass().getResource("/org/example/snakegame/styles/styles.css").toExternalForm(),
                    getClass().getResource("/org/example/snakegame/styles/pong-styles.css").toExternalForm(),
                    getClass().getResource("/org/example/snakegame/styles/menu-styles.css").toExternalForm()
            );
            
            // Initialiser la title bar
            initializeTitleBar(primaryStage);

        // Créer le contrôleur Pong
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        pongController = new PongController(gc);

        // Configurer les callbacks avec les nouvelles interfaces
        pongController.setScoreUpdateListener((newScore, delta) -> updateScoreDisplay());
        pongController.setGameEventListener(new GameEventListener() {
            @Override
            public void onScoreUpdate(int newScore) {
                updateScoreDisplay();
            }

            @Override
            public void onGameOver(GameResult result) {
                onGameOverEvent(result);
            }
        });

        // Gestion des touches - CORRIGÉ
        scene.setOnKeyPressed(event -> {
            pongController.handleKeyPressed(event.getCode());
            updateScoreDisplay();
            // IMPORTANT: Synchroniser le bouton après les touches
            synchronizeStartButton();
        });

        scene.setOnKeyReleased(event -> {
            if (event.getCode().toString().equals("ESCAPE")) {
                returnToMenu();
            } else {
                pongController.handleKeyReleased(event.getCode());
            }
        });

        // Mettre à jour l'affichage initial
        updateScoreDisplay();

        // Configurer la scène
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();

        // Focus pour les touches
        scene.getRoot().requestFocus();

            // Afficher
            primaryStage.show();

            logger.info("Pong Game lancé avec contrôleur !");
        } catch (IOException e) {
            logger.error("❌ Erreur lors du chargement du FXML Pong: %s", e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Initialiser la barre de titre custom
     */
    private void initializeTitleBar(Stage stage) {
        if (titleBar != null) {
            titleBarController = new TitleBarController(stage, titleBar);
            titleBarController.setOnCloseCallback(() -> {
                if (pongController != null) {
                    pongController.stopGame();
                }
            });
            logger.info("✅ Title bar initialisée pour Pong");
        }
    }
    
    /**
     * Méthode FXML pour le bouton minimiser
     */
    @FXML
    protected void onMinimizeButtonClick() {
        if (titleBarController != null) {
            titleBarController.minimize();
        }
    }
    
    /**
     * Méthode FXML pour le bouton fermer
     */
    @FXML
    protected void onCloseButtonClick() {
        if (titleBarController != null) {
            titleBarController.close();
        }
    }
    
    /**
     * Méthode FXML pour le bouton Start
     */
    @FXML
    protected void handleStartButtonClick() {
        switch (pongController.getGameState()) {
            case WAITING_RESTART -> {
                pongController.startGame();
                startButton.setText("PAUSE");
                logger.debug("Pong: Jeu démarré via bouton");
            }
            case PLAYING -> {
                pongController.togglePause();
                startButton.setText("RESUME");
                logger.debug("Pong: Jeu mis en pause via bouton");
            }
            case PAUSED -> {
                pongController.togglePause();
                startButton.setText("PAUSE");
                logger.debug("Pong: Jeu repris via bouton");
            }
            case VICTORY -> {
                logger.debug("Pong: Utiliser RESTART pour rejouer");
            }
        }
        updateScoreDisplay();
    }
    
    /**
     * NOUVEAU: Synchroniser le texte du bouton avec l'état du jeu
     */
    private void synchronizeStartButton() {
        if (startButton == null) return;

        switch (pongController.getGameState()) {
            case WAITING_RESTART -> startButton.setText("START");
            case PLAYING -> startButton.setText("PAUSE");
            case PAUSED -> startButton.setText("RESUME");
            case VICTORY -> startButton.setText("START");
            default -> startButton.setText("START");
        }
    }

    /**
     * Mettre à jour l'affichage des scores et statistiques
     */
    private void updateScoreDisplay() {
        if (pongController != null) {
            scoreLabel.setText(String.format("JOUEUR 1: %d  |  IA: %d",
                    pongController.getPlayer1Score(),
                    pongController.getPlayer2Score()));
            bounceLabel.setText("REBONDS: " + pongController.getBounceCount() +
                    " (Max: " + pongController.getMaxBounceCount() + ")");
            speedLabel.setText(String.format("VITESSE: %.1f | IA: %d%%",
                    pongController.getBallSpeed(),
                    (int)(pongController.getAIDifficulty() * 100)));

            // Synchroniser le bouton
            synchronizeStartButton();
        }
    }

    /**
     * Callback appelé lors de la fin de partie avec GameResult
     */
    private void onGameOverEvent(GameResult result) {
        updateScoreDisplay();
        logger.info("Victoire ! Score final: %d", result.getFinalScore());
        logger.info("Score global Pong: %s", scoreManager.getPongScore());
        if (result.hasStatistics()) {
            logger.info("Statistiques: %s", result.getStatistics());
        }
    }

    /**
     * Méthode FXML pour le bouton Restart
     */
    @FXML
    protected void handleRestartButtonClick() {
        pongController.restartGame();
        synchronizeStartButton();
        updateScoreDisplay();
    }
    
    /**
     * Méthode FXML pour le bouton Menu
     */
    @FXML
    protected void returnToMenu() {
        logger.info("Retour au menu depuis Pong Game");
        if (pongController != null) {
            pongController.stopGame();
        }
        GameController.returnToMenu();
    }

    /**
     * Implémentation de l'interface Game
     */
    @Override
    public String getName() {
        return "Pong";
    }
    
    /**
     * Méthode main pour tests indépendants
     */
    public static void main(String[] args) {
        launch(args);
    }
}