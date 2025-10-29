package org.example.snakegame.snake;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
 * Jeu Snake - Version corrigée avec synchronisation des boutons
 * Application JavaFX complète pour le jeu du serpent
 * Implémente l'interface Game pour respecter l'OCP
 */
public class SnakeGame extends Application implements Game {

    // Constantes du jeu
    private static final int CANVAS_WIDTH = 800;


    // Composants graphiques FXML
    @FXML private Canvas gameCanvas;
    @FXML private Label scoreLabel;
    @FXML private Label lengthLabel;
    @FXML private Label speedLabel;
    @FXML private Label highScoreLabel;
    @FXML private Label totalStatsLabel;
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
    private SnakeController snakeController;
    private Label statusLabel;

    // Référence au gestionnaire de scores
    private ScoreManager scoreManager;
    private final GameLogger logger = GameLogger.getLogger(SnakeGame.class);

    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialiser le gestionnaire de scores
            scoreManager = ScoreManager.INSTANCE;

            // Configuration de la fenêtre (ne pas changer le style si déjà visible)
            primaryStage.setTitle("🐍 SNAKE GAME - Retro Arcade");

            // Charger l'interface FXML avec title bar
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/org/example/snakegame/views/snake-view-custom-titlebar.fxml"));
            fxmlLoader.setController(this);
            VBox root = fxmlLoader.load();

            // Créer la scène avec la hauteur de l'écran
            int windowHeight = GameApplication.getCanvasHeight();
            Scene scene = new Scene(root, CANVAS_WIDTH + 40, windowHeight);

            // Charger les styles CSS
            scene.getStylesheets().addAll(
                    getClass().getResource("/org/example/snakegame/styles/styles.css").toExternalForm(),
                    getClass().getResource("/org/example/snakegame/styles/snake-styles.css").toExternalForm(),
                    getClass().getResource("/org/example/snakegame/styles/menu-styles.css").toExternalForm());

            // Initialiser la title bar
            initializeTitleBar(primaryStage);

            // Créer le contrôleur Snake
            GraphicsContext gc = gameCanvas.getGraphicsContext2D();
            snakeController = new SnakeController(gc);

            // Configurer les callbacks avec les nouvelles interfaces
            snakeController.setScoreUpdateListener((newScore, delta) -> updateScoreDisplay());
            snakeController.setGameEventListener(new GameEventListener() {
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
                snakeController.handleKeyPress(event.getCode());
                updateScoreDisplay();
                // IMPORTANT: Synchroniser le bouton après les touches
                synchronizeStartButton();
            });

            scene.setOnKeyReleased(event -> {
                if (event.getCode().toString().equals("ESCAPE")) {
                    returnToMenu();
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

            logger.info("Snake Game lancé avec contrôleur !");
        } catch (IOException e) {
            logger.error("❌ Erreur lors du chargement du FXML Snake: %s", e.getMessage());
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
                if (snakeController != null) {
                    snakeController.stopGame();
                }
            });
            logger.info("✅ Title bar initialisée pour Snake");
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
        switch (snakeController.getGameState()) {
            case WAITING_RESTART -> {
                snakeController.startGame();
                startButton.setText("PAUSE");
                logger.debug("Snake: Jeu démarré via bouton");
            }
            case PLAYING -> {
                snakeController.togglePause();
                startButton.setText("RESUME");
                logger.debug("Snake: Jeu mis en pause via bouton");
            }
            case PAUSED -> {
                snakeController.togglePause();
                startButton.setText("PAUSE");
                logger.debug("Snake: Jeu repris via bouton");
            }
            case GAME_OVER -> {
                logger.debug("Snake: Utiliser RESTART pour rejouer");
            }
        }
        updateScoreDisplay();
    }
    
    /**
     * Méthode FXML pour le bouton Restart
     */
    @FXML
    protected void handleRestartButtonClick() {
        snakeController.restartGame();
        synchronizeStartButton();
        updateScoreDisplay();
    }
    
    /**
     * Méthode FXML pour le bouton Menu
     */
    @FXML
    protected void returnToMenu() {
        logger.info("Retour au menu depuis Snake Game");
        if (snakeController != null) {
            snakeController.stopGame();
        }
        GameController.returnToMenu();
    }
    
    /**
     * NOUVEAU: Synchroniser le texte du bouton avec l'état du jeu
     */
    private void synchronizeStartButton() {
        if (startButton == null) return;

        switch (snakeController.getGameState()) {
            case WAITING_RESTART -> startButton.setText("START");
            case PLAYING -> startButton.setText("PAUSE");
            case PAUSED -> startButton.setText("RESUME");
            case GAME_OVER -> startButton.setText("START");
            default -> startButton.setText("START");
        }
    }

    /**
     * Mettre à jour l'affichage des scores et statistiques
     */
    private void updateScoreDisplay() {
        if (snakeController != null) {
            scoreLabel.setText(String.format("SCORE: %04d", snakeController.getScore()));
            lengthLabel.setText("LONGUEUR: " + snakeController.getSnakeLength());
            speedLabel.setText("VITESSE: " + snakeController.getGameSpeed());
            highScoreLabel.setText(String.format("HIGH SCORE: %04d", snakeController.getHighScore()));

            // Statistiques totales
            totalStatsLabel.setText(String.format("Parties: %d | Total: %d | Moyenne: %d",
                    scoreManager.getSnakeGamesPlayed(),
                    scoreManager.getSnakeTotalScore(),
                    scoreManager.getSnakeAverageScore()));

            // Synchroniser le bouton
            synchronizeStartButton();
        }
    }

    /**
     * Callback appelé lors du game over avec GameResult
     */
    private void onGameOverEvent(GameResult result) {
        updateScoreDisplay();
        logger.info("Game Over ! Score final: %d", result.getFinalScore());
        logger.info("Score enregistré dans ScoreManager: %d", scoreManager.getSnakeTotalScore());
        if (result.hasStatistics()) {
            logger.info("Statistiques: %s", result.getStatistics());
        }
    }

    /**
     * Implémentation de l'interface Game
     */
    @Override
    public String getName() {
        return "Snake";
    }
    
    /**
     * Méthode main pour tests indépendants
     */
    public static void main(String[] args) {
        launch(args);
    }
}