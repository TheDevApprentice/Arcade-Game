package org.example.snakegame.pong;

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
import org.example.snakegame.common.GameEventListener;
import org.example.snakegame.common.GameResult;

/**
 * Jeu Pong - Version corrigée avec synchronisation des boutons
 * Application JavaFX complète pour le jeu de Pong
 */
public class PongGame extends Application {

    // Constantes du jeu
    private static final int CANVAS_WIDTH = 800;
    private static final int CANVAS_HEIGHT = 600;

    // Composants graphiques
    private Canvas gameCanvas;
    private PongController pongController;
    private Label scoreLabel;
    private Label bounceLabel;
    private Label speedLabel;
    private Label statusLabel;

    // CORRIGÉ: Référence au bouton pour synchronisation
    private Button startButton;

    // Référence au gestionnaire de scores
    private ScoreManager scoreManager;

    @Override
    public void start(Stage primaryStage) {
        // Initialiser le gestionnaire de scores
        scoreManager = ScoreManager.getInstance();

        // Configuration de la fenêtre
        primaryStage.setTitle(" PONG GAME - Retro Arcade");

        // Créer l'interface
        VBox root = createGameInterface();

        // Créer la scène
        Scene scene = new Scene(root, CANVAS_WIDTH + 40, CANVAS_HEIGHT + 160);

        // Charger les styles CSS
        scene.getStylesheets().addAll(
                getClass().getResource("/org/example/snakegame/styles/styles.css").toExternalForm(),
                getClass().getResource("/org/example/snakegame/styles/pong-styles.css").toExternalForm(),
                getClass().getResource("/org/example/snakegame/styles/menu-styles.css").toExternalForm()
        );

        // Appliquer les styles
        root.getStyleClass().add("pong-game-container");
        gameCanvas.getStyleClass().add("pong-canvas");

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

        System.out.println("Pong Game lancé avec contrôleur !");
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

        // Panneau de contrôles en bas
        HBox controlPanel = createControlPanel();

        // Instructions - CORRIGÉ: Contrôles mis à jour
        Label instructions = new Label("↑/↓: Joueur 1 | ENTRÉE: Start | ESPACE: Pause | R: Restart | 1/2/3: Difficulté IA | ESC: Menu");
        instructions.getStyleClass().add("pong-controls");

        root.getChildren().addAll(infoPanel, gameCanvas, controlPanel, instructions);

        return root;
    }

    /**
     * Créer le panneau d'informations
     */
    private HBox createInfoPanel() {
        HBox infoPanel = new HBox(30);
        infoPanel.setStyle("-fx-alignment: center;");
        infoPanel.getStyleClass().add("pong-info-panel");

        // Score
        scoreLabel = new Label("JOUEUR 1: 0  |  IA: 0");
        scoreLabel.getStyleClass().add("pong-player-score");

        // Rebonds
        bounceLabel = new Label("REBONDS: 0");
        bounceLabel.getStyleClass().add("pong-bounces");

        // Vitesse de la balle
        speedLabel = new Label("VITESSE: 3.0");
        speedLabel.getStyleClass().add("pong-ball-speed");

        infoPanel.getChildren().addAll(scoreLabel, bounceLabel, speedLabel);

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
        startButton.getStyleClass().addAll("pong-control-button");
        startButton.setOnAction(e -> handleStartButtonClick());

        // Bouton Restart
        Button restartButton = new Button("RESTART");
        restartButton.getStyleClass().addAll("pong-control-button", "pong-restart-button");
        restartButton.setOnAction(e -> {
            pongController.restartGame();
            synchronizeStartButton(); // Synchroniser après restart
            updateScoreDisplay();
        });

        // Bouton Menu
        Button menuButton = new Button("MENU");
        menuButton.getStyleClass().addAll("pong-control-button", "pong-menu-button");
        menuButton.setOnAction(e -> returnToMenu());

        // Status
        statusLabel = new Label("Appuyez sur START pour commencer !");
        statusLabel.getStyleClass().add("pong-pause");

        controlPanel.getChildren().addAll(startButton, restartButton, menuButton);

        return controlPanel;
    }

    /**
     * CORRIGÉ: Gestion du bouton Start avec logique claire
     */
    private void handleStartButtonClick() {
        switch (pongController.getGameState()) {
            case WAITING_RESTART -> {
                pongController.startGame();
                startButton.setText("PAUSE");
                System.out.println("Pong: Jeu démarré via bouton");
            }
            case PLAYING -> {
                pongController.togglePause();
                startButton.setText("RESUME");
                System.out.println("Pong: Jeu mis en pause via bouton");
            }
            case PAUSED -> {
                pongController.togglePause();
                startButton.setText("PAUSE");
                System.out.println("Pong: Jeu repris via bouton");
            }
            case VICTORY -> {
                // Ne rien faire, utiliser le bouton RESTART à la place
                System.out.println("Pong: Utiliser RESTART pour rejouer");
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
     * Mettre à jour l'affichage des scores et statistiques - CORRIGÉ
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

            // Mettre à jour le statut
            String status = switch (pongController.getGameState()) {
                case WAITING_RESTART -> "Appuyez sur START pour commencer ! (Premier à 5 points)";
                case PLAYING -> String.format("Match en cours... %d-%d | Score global: %s",
                        pongController.getPlayer1Score(),
                        pongController.getPlayer2Score(),
                        scoreManager.getPongScore());
                case PAUSED -> "JEU EN PAUSE";
                case VICTORY -> {
                    String winner = pongController.getPlayer1Score() >= 5 ? "JOUEUR 1" : "IA";
                    yield winner + " GAGNE ! Score final: " +
                            pongController.getPlayer1Score() + "-" + pongController.getPlayer2Score() +
                            " | Score global: " + scoreManager.getPongScore();
                }
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
     * Callback appelé lors de la fin de partie avec GameResult
     */
    private void onGameOverEvent(GameResult result) {
        updateScoreDisplay();
        System.out.println("Victoire ! Score final: " + result.getFinalScore());
        System.out.println("Score global Pong: " + scoreManager.getPongScore());
        if (result.hasStatistics()) {
            System.out.println("Statistiques: " + result.getStatistics());
        }
    }

    /**
     * Retourner au menu principal
     */
    private void returnToMenu() {
        System.out.println("Retour au menu depuis Pong Game");

        // Arrêter le jeu proprement
        if (pongController != null) {
            pongController.stopGame();
        }

        // Retourner au menu
        GameController.returnToMenu();
    }

    /**
     * Méthode main pour tests indépendants
     */
    public static void main(String[] args) {
        launch(args);
    }
}