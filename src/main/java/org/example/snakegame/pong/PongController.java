package org.example.snakegame.pong;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.example.snakegame.MusicController;
import org.example.snakegame.common.GameState;
import org.example.snakegame.ScoreManager;

import java.util.HashSet;
import java.util.Set;

/**
 * Contrôleur du jeu Pong utilisant les objets Ball et Paddle
 * Version corrigée avec ScoreManager et contrôles flèches
 */
public class PongController {

    private MusicController musicController;
    // Constantes du jeu
    private static final int CANVAS_WIDTH = 800;
    private static final int CANVAS_HEIGHT = 600;
    private static final int PADDLE_WIDTH = 15;
    private static final int PADDLE_HEIGHT = 80;
    private static final int BALL_SIZE = 15;
    private static final int WINNING_SCORE = 5;

    // État du jeu
    private GameState gameState;
    private Timeline gameLoop;
    private GraphicsContext gc;

    // Objets du jeu
    private Ball ball;
    private Paddle leftPaddle;
    private Paddle rightPaddle;

    // Scores de la partie en cours
    private int player1Score;
    private int player2Score;

    // Contrôles
    private Set<KeyCode> pressedKeys;

    // Statistiques locales
    private int maxBounceCount;
    private int totalBounces;

    // Référence au gestionnaire de scores global
    private ScoreManager scoreManager;

    // Callbacks pour l'interface
    private Runnable scoreUpdateCallback;
    private Runnable gameOverCallback;

    /**
     * Constructeur du contrôleur Pong
     */
    public PongController(GraphicsContext gc) {
        this.gc = gc;
        this.pressedKeys = new HashSet<>();
        this.scoreManager = ScoreManager.getInstance();
        this.gameState = GameState.STARTING;
        this.musicController = MusicController.getInstance();

        // Initialiser le jeu
        initializeGame();
        setupGameLoop();
    }

    /**
     * Initialiser une nouvelle partie
     */
    private void initializeGame() {
        // Créer la balle au centre
        ball = new Ball(CANVAS_WIDTH, CANVAS_HEIGHT, BALL_SIZE, 3.0);

        // Créer les raquettes
        double leftPaddleX = 30;
        double rightPaddleX = CANVAS_WIDTH - 30 - PADDLE_WIDTH;

        leftPaddle = new Paddle(
                leftPaddleX, 0, PADDLE_WIDTH, PADDLE_HEIGHT, 5,
                CANVAS_HEIGHT, Paddle.PaddleType.PLAYER_LEFT
        );

        rightPaddle = new Paddle(
                rightPaddleX, 0, PADDLE_WIDTH, PADDLE_HEIGHT, 5,
                CANVAS_HEIGHT, Paddle.PaddleType.AI_RIGHT
        );

        // Configurer l'IA par défaut (difficulté moyenne)
        rightPaddle.setAIDifficulty(0.7);

        // Scores de la partie
        player1Score = 0;
        player2Score = 0;

        // Statistiques
        maxBounceCount = 0;
        totalBounces = 0;

        // État initial
        gameState = GameState.WAITING_RESTART;

        // Dessiner l'état initial
        render();
    }

    /**
     * Configurer la boucle de jeu (60 FPS)
     */
    private void setupGameLoop() {
        gameLoop = new Timeline(new KeyFrame(
                Duration.millis(16.67), // ~60 FPS
                e -> updateGame()
        ));
        gameLoop.setCycleCount(Timeline.INDEFINITE);
    }

    /**
     * Mise à jour principale du jeu (appelée à chaque frame)
     */
    private void updateGame() {
        if (gameState != GameState.PLAYING) {
            return;
        }

        // Mettre à jour les raquettes
        updatePaddles();

        // Mettre à jour la balle
        ball.move();

        // Vérifier les collisions avec les raquettes
        checkPaddleCollisions();

        // Vérifier les goals
        checkGoals();

        // Vérifier les conditions de victoire
        checkWinCondition();

        // Redessiner
        render();
    }

    /**
     * Mettre à jour les positions des raquettes - CORRIGÉ: Contrôles flèches
     */
    private void updatePaddles() {
        // Raquette gauche (Joueur 1) - NOUVELLES TOUCHES: Flèches UP/DOWN
        if (pressedKeys.contains(KeyCode.UP) && leftPaddle.canMoveUp()) {
            boolean moved = leftPaddle.moveUp();
            if (moved) System.out.println("Paddle UP - Y: " + leftPaddle.getY());
        }
        if (pressedKeys.contains(KeyCode.DOWN) && leftPaddle.canMoveDown()) {
            boolean moved = leftPaddle.moveDown();
            if (moved) System.out.println("Paddle DOWN - Y: " + leftPaddle.getY());
        }

        // Raquette droite (IA)
        rightPaddle.updateAI(ball);
    }

    /**
     * Vérifier et gérer les collisions avec les raquettes
     */
    private void checkPaddleCollisions() {
        // Collision avec la raquette gauche
        if (leftPaddle.collidesWith(ball)) {
            leftPaddle.handleBallCollision(ball);
            System.out.println("Collision avec raquette gauche ! Hits: " + leftPaddle.getHits());
            musicController.playPongBallHit(); // Son de collision
        }

        // Collision avec la raquette droite
        if (rightPaddle.collidesWith(ball)) {
            rightPaddle.handleBallCollision(ball);
            System.out.println("Collision avec raquette droite (IA) ! Hits: " + rightPaddle.getHits());
            musicController.playPongBallHit(); // Son de collision
        }
    }

    /**
     * Vérifier les goals
     */
    private void checkGoals() {
        Ball.GoalResult goalResult = ball.checkGoal();

        if (goalResult != Ball.GoalResult.NO_GOAL) {
            // Mettre à jour les statistiques
            maxBounceCount = Math.max(maxBounceCount, ball.getBounceCount());
            totalBounces += ball.getBounceCount();

            // Mettre à jour les scores
            if (goalResult == Ball.GoalResult.PLAYER_1_GOAL) {
                player1Score++;

                System.out.println("Goal Joueur 1 ! Score: " + player1Score + "-" + player2Score);
            } else if (goalResult == Ball.GoalResult.PLAYER_2_GOAL) {
                player2Score++;
                System.out.println("Goal IA ! Score: " + player1Score + "-" + player2Score);
            }
            musicController.playPongGoal(); // Son de but
            // Réinitialiser la balle
            ball.reset();

            // Notifier l'interface
            if (scoreUpdateCallback != null) {
                scoreUpdateCallback.run();
            }
        }
    }

    /**
     * Vérifier les conditions de victoire - CORRIGÉ: Enregistrement dans ScoreManager
     */
    private void checkWinCondition() {
        if (player1Score >= WINNING_SCORE || player2Score >= WINNING_SCORE) {
            gameState = GameState.VICTORY;
            gameLoop.stop();

            // IMPORTANT: Enregistrer le résultat dans le gestionnaire global
            if (player1Score >= WINNING_SCORE) {
                musicController.playPongVictory(); // Son de victoire
                scoreManager.recordPongPlayerWin();
                System.out.println("Victoire du JOUEUR 1 !");
            } else {
                musicController.playSnakeGameOver();
                scoreManager.recordPongAIWin();
                System.out.println("Victoire de l'IA !");
            }

            System.out.println("Score final: " + player1Score + "-" + player2Score);
            System.out.println("Score global Pong: " + scoreManager.getPongScore());
            System.out.println("Statistiques de performance:");
            System.out.println("- Joueur 1: " + leftPaddle.getPerformanceStats());
            System.out.println("- IA: " + rightPaddle.getPerformanceStats());

            if (gameOverCallback != null) {
                gameOverCallback.run();
            }
        }
    }

    /**
     * Gestion des touches pressées - CORRIGÉ: Nouvelles touches
     */
    public void handleKeyPressed(KeyCode keyCode) {
        System.out.println("Pong - Touche pressée: " + keyCode); // Debug
        pressedKeys.add(keyCode);

        switch (keyCode) {
            case SPACE -> {
                togglePause();
                System.out.println("Pong - Pause toggled");
            }
            case R -> {
                if (gameState.canBeRestarted()) {
                    restartGame();
                    System.out.println("Pong - Restart");
                }
            }
            case ENTER -> {
                if (gameState == GameState.WAITING_RESTART) {
                    startGame();
                    System.out.println("Pong - Game started");
                }
            }
            case DIGIT1 -> {
                rightPaddle.setAIDifficulty(0.3); // Facile
                System.out.println("Pong - Difficulté IA: Facile (30%)");
            }
            case DIGIT2 -> {
                rightPaddle.setAIDifficulty(0.5); // Moyen
                System.out.println("Pong - Difficulté IA: Moyen (50%)");
            }
            case DIGIT3 -> {
                rightPaddle.setAIDifficulty(0.8); // Difficile
                System.out.println("Pong - Difficulté IA: Difficile (80%)");
            }
            // NOUVEAUX CONTRÔLES: Flèches au lieu de W/S
            case UP -> System.out.println("Pong - Flèche HAUT pressée (Joueur 1 UP)");
            case DOWN -> System.out.println("Pong - Flèche BAS pressée (Joueur 1 DOWN)");
        }
    }

    /**
     * Gestion des touches relâchées
     */
    public void handleKeyReleased(KeyCode keyCode) {
        pressedKeys.remove(keyCode);
    }

    /**
     * Démarrer le jeu - CORRIGÉ
     */
    public void startGame() {
        if (gameState == GameState.WAITING_RESTART) {
            gameState = GameState.PLAYING;
            gameLoop.play();
            System.out.println("Pong démarré !");

            // Notifier l'interface pour mettre à jour le bouton
            if (scoreUpdateCallback != null) {
                scoreUpdateCallback.run();
            }
        }
    }

    /**
     * Basculer pause/play
     */
    public void togglePause() {
        if (gameState == GameState.PLAYING) {
            gameState = GameState.PAUSED;
            gameLoop.pause();
        } else if (gameState == GameState.PAUSED) {
            gameState = GameState.PLAYING;
            gameLoop.play();
        }
        render();
    }

    /**
     * Redémarrer le jeu
     */
    public void restartGame() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        initializeGame();
        System.out.println("Pong redémarré !");
    }

    /**
     * Arrêter le jeu
     */
    public void stopGame() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        gameState = GameState.WAITING_RESTART;
    }

    /**
     * Rendu graphique principal
     */
    public void render() {
        // Effacer le canvas
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

        // Dessiner le terrain
        drawField();

        // Dessiner les raquettes
        drawPaddles();

        // Dessiner la balle
        drawBall();

        // Dessiner les messages d'état
        drawStatusMessages();

        // Bordure du jeu
        drawBorder();
    }

    /**
     * Dessiner le terrain de jeu
     */
    private void drawField() {
        // Ligne centrale en pointillés
        gc.setStroke(Color.CYAN);
        gc.setLineWidth(3);

        double dashLength = 15;
        double gapLength = 10;
        for (double y = dashLength; y < CANVAS_HEIGHT; y += dashLength + gapLength) {
            gc.strokeLine(CANVAS_WIDTH/2, y, CANVAS_WIDTH/2,
                    Math.min(y + dashLength, CANVAS_HEIGHT));
        }
    }

    /**
     * Dessiner les raquettes
     */
    private void drawPaddles() {
        gc.setFill(Color.CYAN);

        // Raquette gauche (Joueur 1)
        gc.fillRect(leftPaddle.getX(), leftPaddle.getY(),
                leftPaddle.getWidth(), leftPaddle.getHeight());

        // Raquette droite (IA)
        gc.fillRect(rightPaddle.getX(), rightPaddle.getY(),
                rightPaddle.getWidth(), rightPaddle.getHeight());

        // Effet de brillance sur les raquettes
        gc.setFill(Color.WHITE);
        gc.fillRect(leftPaddle.getX() + 2, leftPaddle.getY() + 5,
                3, leftPaddle.getHeight() - 10);
        gc.fillRect(rightPaddle.getX() + 2, rightPaddle.getY() + 5,
                3, rightPaddle.getHeight() - 10);
    }

    /**
     * Dessiner la balle avec effet de traînée
     */
    private void drawBall() {
        // Effet de traînée selon la vitesse
        double speed = ball.getTotalVelocity();
        int trailLength = (int)(speed * 2);

        for (int i = 1; i <= trailLength; i++) {
            double trailX = ball.getX() - (ball.getVelocityX() / speed) * i * 3;
            double trailY = ball.getY() - (ball.getVelocityY() / speed) * i * 3;
            double alpha = 1.0 - (double)i / trailLength;

            gc.setFill(Color.rgb(255, 255, 255, alpha * 0.5));
            gc.fillOval(trailX, trailY, ball.getSize() * alpha, ball.getSize() * alpha);
        }

        // Balle principale
        gc.setFill(Color.WHITE);
        gc.fillOval(ball.getX(), ball.getY(), ball.getSize(), ball.getSize());
    }

    /**
     * Dessiner les messages d'état - CORRIGÉ: Contrôles mis à jour
     */
    private void drawStatusMessages() {
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Courier New", 16));

        String message = switch (gameState) {
            case WAITING_RESTART -> "Appuyez sur ENTRÉE pour commencer !";
            case PAUSED -> "JEU EN PAUSE - Appuyez sur ESPACE";
            case VICTORY -> {
                String winner = player1Score >= WINNING_SCORE ? "JOUEUR 1" : "IA";
                yield winner + " GAGNE ! - Appuyez sur R pour rejouer";
            }
            default -> "";
        };

        if (!message.isEmpty()) {
            double textWidth = message.length() * 8; // Approximation
            gc.fillText(message, (CANVAS_WIDTH - textWidth) / 2, CANVAS_HEIGHT / 2 + 100);
        }

        // Instructions de difficulté - CORRIGÉ: Nouveaux contrôles
        if (gameState == GameState.WAITING_RESTART) {
            gc.setFont(javafx.scene.text.Font.font("Courier New", 12));
            gc.fillText("1: Facile | 2: Moyen | 3: Difficile", 20, CANVAS_HEIGHT - 40);
            gc.fillText("↑/↓: Contrôles Joueur 1", 20, CANVAS_HEIGHT - 20); // Mis à jour
        }

        // Afficher les statistiques en cours de jeu
        if (gameState == GameState.PLAYING) {
            gc.setFont(javafx.scene.text.Font.font("Courier New", 10));
            gc.fillText("Rebonds: " + ball.getBounceCount(), 10, 20);
            gc.fillText("Vitesse: " + String.format("%.1f", ball.getSpeed()), 10, 35);
            gc.fillText("IA: " + (int)(rightPaddle.getAIDifficulty() * 100) + "%", 10, 50);
        }

        // Afficher les scores globaux en fin de partie
        if (gameState == GameState.VICTORY) {
            gc.setFont(javafx.scene.text.Font.font("Courier New", 12));
            gc.setFill(Color.YELLOW);

            int baseY = CANVAS_HEIGHT / 2 + 140;
            gc.fillText("Score global: " + scoreManager.getPongScore(), 20, baseY);
            gc.fillText("Parties jouées: " + scoreManager.getPongGamesPlayed(), 20, baseY + 20);
            gc.fillText("Taux de victoire: " + String.format("%.1f%%", scoreManager.getPongWinRate()), 20, baseY + 40);
        }
    }

    /**
     * Dessiner la bordure
     */
    private void drawBorder() {
        gc.setStroke(Color.CYAN);
        gc.setLineWidth(2);
        gc.strokeRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
    }

    // Getters pour l'interface (utilisant maintenant les objets)
    public int getPlayer1Score() { return player1Score; }
    public int getPlayer2Score() { return player2Score; }
    public GameState getGameState() { return gameState; }
    public int getBounceCount() { return ball.getBounceCount(); }
    public int getMaxBounceCount() { return maxBounceCount; }
    public double getBallSpeed() { return ball.getSpeed(); }
    public double getAIDifficulty() { return rightPaddle.getAIDifficulty(); }

    // Getters pour statistiques avancées
    public int getTotalBounces() { return totalBounces; }
    public int getPlayerHits() { return leftPaddle.getHits(); }
    public int getAIHits() { return rightPaddle.getHits(); }

    // Setters pour les callbacks
    public void setScoreUpdateCallback(Runnable callback) {
        this.scoreUpdateCallback = callback;
    }

    public void setGameOverCallback(Runnable callback) {
        this.gameOverCallback = callback;
    }
}