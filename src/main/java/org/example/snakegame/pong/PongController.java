package org.example.snakegame.pong;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;
import org.example.snakegame.MusicController;
import org.example.snakegame.ScoreManager;
import org.example.snakegame.common.AbstractGameController;
import org.example.snakegame.common.GameResult;
import org.example.snakegame.common.GameState;
import org.example.snakegame.common.ValidationUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Contrôleur du jeu Pong
 * Version refactorisée avec logging structuré, validation et SRP
 * Le rendu est délégué à PongRenderer (SRP)
 */
public class PongController extends AbstractGameController {

    private final MusicController musicController;
    // Constantes du jeu
    private static final int CANVAS_WIDTH = 800;
    private static final int CANVAS_HEIGHT = 600;
    private static final int PADDLE_WIDTH = 15;
    private static final int PADDLE_HEIGHT = 80;
    private static final int BALL_SIZE = 15;
    private static final int WINNING_SCORE = 5;

    // État du jeu (gameState et gameLoop sont dans AbstractGameController)
    // GraphicsContext passé uniquement au renderer (SRP)
    
    // Renderer dédié (SRP)
    private final PongRenderer renderer;

    // Objets du jeu
    private Ball ball;
    private Paddle leftPaddle;
    private Paddle rightPaddle;

    // Scores de la partie en cours
    private int player1Score;
    private int player2Score;
    private int previousPlayer1Score;
    private int previousPlayer2Score;

    // Contrôles
    private final Set<KeyCode> pressedKeys;

    // Statistiques locales
    private int maxBounceCount;
    private int totalBounces;

    // Référence au gestionnaire de scores global
    private final ScoreManager scoreManager;

    /**
     * Constructeur du contrôleur Pong
     */
    public PongController(GraphicsContext gc) {
        super(PongController.class);
        ValidationUtils.requireNonNull(gc, "graphicsContext");
        this.renderer = new PongRenderer(gc, CANVAS_WIDTH, CANVAS_HEIGHT, WINNING_SCORE);
        this.scoreManager = ScoreManager.getInstance();
        this.musicController = MusicController.getInstance();
        this.pressedKeys = new HashSet<>();
        this.previousPlayer1Score = 0;
        this.previousPlayer2Score = 0;

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
        updateGameState(GameState.WAITING_RESTART);

        // Dessiner l'état initial
        render();
    }

    /**
     * Configurer la boucle de jeu (60 FPS)
     */
    private void setupGameLoop() {
        // Arrêter l'ancienne Timeline si elle existe pour éviter les fuites mémoire
        if (gameLoop != null) {
            gameLoop.stop();
        }
        
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
            if (moved) logger.debug("Paddle UP - Y: %d", (int)leftPaddle.getY());
        }
        if (pressedKeys.contains(KeyCode.DOWN) && leftPaddle.canMoveDown()) {
            boolean moved = leftPaddle.moveDown();
            if (moved) logger.debug("Paddle DOWN - Y: %d", (int)leftPaddle.getY());
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
            logger.debug("Collision avec raquette gauche ! Hits: %d", leftPaddle.getHits());
            musicController.playPongBallHit();
        }

        // Collision avec la raquette droite
        if (rightPaddle.collidesWith(ball)) {
            rightPaddle.handleBallCollision(ball);
            logger.debug("Collision avec raquette droite (IA) ! Hits: %d", rightPaddle.getHits());
            musicController.playPongBallHit();
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
                logger.info("Goal Joueur 1 ! Score: %d-%d", player1Score, player2Score);
            } else if (goalResult == Ball.GoalResult.PLAYER_2_GOAL) {
                player2Score++;
                logger.info("Goal IA ! Score: %d-%d", player1Score, player2Score);
            }
            musicController.playPongGoal();
            ball.reset();

            // Notifier l'interface
            int delta1 = player1Score - previousPlayer1Score;
            int delta2 = player2Score - previousPlayer2Score;
            notifyScoreUpdate(player1Score, delta1);
            previousPlayer1Score = player1Score;
            previousPlayer2Score = player2Score;
        }
    }

    /**
     * Vérifier les conditions de victoire
     */
    private void checkWinCondition() {
        if (player1Score >= WINNING_SCORE || player2Score >= WINNING_SCORE) {
            updateGameState(GameState.VICTORY);
            gameLoop.stop();

            boolean playerWon = player1Score >= WINNING_SCORE;
            
            if (playerWon) {
                musicController.playPongVictory();
                scoreManager.recordPongPlayerWin();
                logger.game("🏆", "Victoire du JOUEUR 1 !");
            } else {
                musicController.playSnakeGameOver();
                scoreManager.recordPongAIWin();
                logger.info("Victoire de l'IA !");
            }

            logger.info("Score final: %d-%d", player1Score, player2Score);
            logger.info("Score global Pong: %s", scoreManager.getPongScore());

            GameResult.GameStatistics statistics = new GameResult.GameStatistics(
                    maxBounceCount,
                    totalBounces,
                    String.format("Joueur: %d | IA: %d", player1Score, player2Score)
            );
            notifyGameOver(new GameResult("Pong", player1Score, playerWon, statistics));
        }
    }

    /**
     * Gestion des touches pressées
     */
    public void handleKeyPressed(KeyCode keyCode) {
        logger.debug("Pong - Touche pressée: %s", keyCode);
        pressedKeys.add(keyCode);

        switch (keyCode) {
            case SPACE -> {
                togglePause();
                logger.debug("Pong - Pause toggled");
            }
            case R -> {
                if (gameState.canBeRestarted()) {
                    restartGame();
                    logger.info("Pong - Restart");
                }
            }
            case ENTER -> {
                if (gameState == GameState.WAITING_RESTART) {
                    startGame();
                    logger.info("Pong - Game started");
                }
            }
            case DIGIT1 -> {
                rightPaddle.setAIDifficulty(0.3);
                logger.info("Pong - Difficulté IA: Facile (30%%)");
            }
            case DIGIT2 -> {
                rightPaddle.setAIDifficulty(0.5);
                logger.info("Pong - Difficulté IA: Moyen (50%%)");
            }
            case DIGIT3 -> {
                rightPaddle.setAIDifficulty(0.8);
                logger.info("Pong - Difficulté IA: Difficile (80%%)");
            }
            case UP -> logger.debug("Pong - Flèche HAUT pressée");
            case DOWN -> logger.debug("Pong - Flèche BAS pressée");
        }
    }

    public void handleKeyReleased(KeyCode keyCode) {
        pressedKeys.remove(keyCode);
    }

    @Override
    protected String getGameName() {
        return "Pong";
    }

    @Override
    protected void onRestart() {
        initializeGame();
    }

    @Override
    protected void onPauseToggled() {
        render(); // Rafraîchir l'affichage lors de la pause
    }

    /**
     * Rendu graphique principal - Délégation au renderer (SRP)
     */
    public void render() {
        renderer.render(leftPaddle, rightPaddle, ball, 
                       player1Score, player2Score, gameState);
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
}