package org.example.snakegame.snake;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.snakegame.GameController;
import org.example.snakegame.ScoreManager;
import org.example.snakegame.common.Game;
import org.example.snakegame.common.GameEventListener;
import org.example.snakegame.common.GameResult;
import org.example.snakegame.common.GameLogger;

/**
 * Jeu Snake - Version corrigée avec synchronisation des boutons
 * Application JavaFX complète pour le jeu du serpent
 * Implémente l'interface Game pour respecter l'OCP
 */
public class SnakeGame extends Application implements Game {

    // Constantes du jeu
    private static final int CANVAS_WIDTH = 800;
    private static final int CANVAS_HEIGHT = 600;

    // Composants graphiques
    private Canvas gameCanvas;
    private SnakeController snakeController;
    private Label scoreLabel;
    private Label lengthLabel;
    private Label speedLabel;
    private Label statusLabel;
    private Label highScoreLabel;
    private Label totalStatsLabel;

    // CORRIGÉ: Référence au bouton pour synchronisation
    private Button startButton;

    // Référence au gestionnaire de scores
    private ScoreManager scoreManager;
    private final GameLogger logger = GameLogger.getLogger(SnakeGame.class);

    @Override
    public void start(Stage primaryStage) {
        // Initialiser le gestionnaire de scores
        scoreManager = ScoreManager.INSTANCE;

        // Configuration de la fenêtre
        primaryStage.setTitle("🐍 SNAKE GAME - Retro Arcade");

        // Créer l'interface
        VBox root = createGameInterface();

        // Créer la scène
        Scene scene = new Scene(root, CANVAS_WIDTH + 40, CANVAS_HEIGHT + 160);

        // Charger les styles CSS
        scene.getStylesheets().addAll(
                getClass().getResource("/org/example/snakegame/styles/styles.css").toExternalForm(),
                getClass().getResource("/org/example/snakegame/styles/snake-styles.css").toExternalForm(),
                getClass().getResource("/org/example/snakegame/styles/menu-styles.css").toExternalForm());

        // Appliquer les styles
        root.getStyleClass().add("snake-game-container");
        gameCanvas.getStyleClass().add("snake-canvas");

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
    }

    /**
     * Créer l'interface du jeu
     */
    private VBox createGameInterface() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-alignment: center;");

        // Panneau d'informations en haut
        HBox infoPanel = createInfoPanel();

        // Canvas de jeu
        gameCanvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);

        // Panneau de statistiques totales
        totalStatsLabel = new Label("Parties: 0 | Total: 0 | Moyenne: 0");
        totalStatsLabel.getStyleClass().add("snake-length");

        // Panneau de contrôles en bas
        HBox controlPanel = createControlPanel();

        // Instructions
        Label instructions = new Label("Flèches: Déplacer | ENTRÉE: Start | ESPACE: Pause | R: Restart | ESC: Menu");
        instructions.getStyleClass().add("snake-controls");

        root.getChildren().addAll(infoPanel, gameCanvas, totalStatsLabel, controlPanel, instructions);

        return root;
    }

    /**
     * Créer le panneau d'informations
     */
    private HBox createInfoPanel() {
        HBox infoPanel = new HBox(20);
        infoPanel.setStyle("-fx-alignment: center;");
        infoPanel.getStyleClass().add("snake-info-panel");

        // Score actuel
        scoreLabel = new Label("SCORE: 0000");
        scoreLabel.getStyleClass().add("snake-score");

        // Longueur
        lengthLabel = new Label("LONGUEUR: 1");
        lengthLabel.getStyleClass().add("snake-length");

        // Vitesse
        speedLabel = new Label("VITESSE: 1");
        speedLabel.getStyleClass().add("snake-speed");

        // High Score
        highScoreLabel = new Label("HIGH SCORE: 0000");
        highScoreLabel.getStyleClass().add("snake-highscore");

        infoPanel.getChildren().addAll(scoreLabel, lengthLabel, speedLabel, highScoreLabel);

        return infoPanel;
    }

    /**
     * Créer le panneau de contrôles - CORRIGÉ
     */
    private HBox createControlPanel() {
        HBox controlPanel = new HBox(15);
        controlPanel.setStyle("-fx-alignment: center;");

        // Bouton Start/Pause - CORRIGÉ: Référence stockée
        startButton = new Button("START");
        startButton.getStyleClass().addAll("snake-control-button");
        startButton.setOnAction(e -> handleStartButtonClick());

        // Bouton Restart
        Button restartButton = new Button("RESTART");
        restartButton.getStyleClass().addAll("snake-control-button", "snake-restart-button");
        restartButton.setOnAction(e -> {
            snakeController.restartGame();
            synchronizeStartButton(); // Synchroniser après restart
            updateScoreDisplay();
        });

        // Bouton Menu
        Button menuButton = new Button("MENU");
        menuButton.getStyleClass().addAll("snake-control-button", "snake-menu-button");
        menuButton.setOnAction(e -> returnToMenu());

        // Status
        statusLabel = new Label("Appuyez sur START pour commencer !");
        statusLabel.getStyleClass().add("snake-pause");

        controlPanel.getChildren().addAll(startButton, restartButton, menuButton);

        return controlPanel;
    }

    /**
     * CORRIGÉ: Gestion du bouton Start avec logique claire
     */
    private void handleStartButtonClick() {
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
                // Ne rien faire, utiliser le bouton RESTART à la place
                logger.debug("Snake: Utiliser RESTART pour rejouer");
            }
        }
        updateScoreDisplay();
    }

    /**
     * NOUVEAU: Synchroniser le texte du bouton avec l'état du jeu
     */
    private void synchronizeStartButton() {
        if (startButton == null)
            return;

        switch (snakeController.getGameState()) {
            case WAITING_RESTART -> startButton.setText("START");
            case PLAYING -> startButton.setText("PAUSE");
            case PAUSED -> startButton.setText("RESUME");
            case GAME_OVER -> startButton.setText("START");
            default -> startButton.setText("START");
        }
    }

    /**
     * Mettre à jour l'affichage des scores et statistiques - CORRIGÉ
     */
    private void updateScoreDisplay() {
        if (snakeController != null) {
            scoreLabel.setText(String.format("SCORE: %04d", snakeController.getScore()));
            lengthLabel.setText("LONGUEUR: " + snakeController.getSnakeLength());
            speedLabel.setText("VITESSE: " + snakeController.getGameSpeed());
            highScoreLabel.setText(String.format("HIGH SCORE: %04d", snakeController.getHighScore()));

            // Statistiques totales - CORRIGÉ: Utilise le ScoreManager
            totalStatsLabel.setText(String.format("Parties: %d | Total: %d | Moyenne: %d",
                    scoreManager.getSnakeGamesPlayed(),
                    scoreManager.getSnakeTotalScore(),
                    scoreManager.getSnakeAverageScore()));

            // Mettre à jour le statut
            String status = switch (snakeController.getGameState()) {
                case WAITING_RESTART -> "Appuyez sur START pour commencer !";
                case PLAYING -> "Jeu en cours... Score: " + snakeController.getScore();
                case PAUSED -> "JEU EN PAUSE - Appuyez sur ESPACE pour reprendre";
                case GAME_OVER -> String.format("GAME OVER ! Score: %d | High Score: %d | Parties jouées: %d",
                        snakeController.getScore(),
                        scoreManager.getSnakeHighScore(),
                        scoreManager.getSnakeGamesPlayed());
                default -> "Prêt à jouer !";
            };

            if (statusLabel != null) {
                statusLabel.setText(status);
            }

            // IMPORTANT: Synchroniser le bouton à chaque mise à jour
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
     * Retourner au menu principal
     */
    private void returnToMenu() {
        logger.info("Retour au menu depuis Snake Game");

        // Arrêter le jeu proprement
        if (snakeController != null) {
            snakeController.stopGame();
        }

        // Retourner au menu
        GameController.returnToMenu();
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