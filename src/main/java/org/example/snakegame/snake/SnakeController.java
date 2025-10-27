package org.example.snakegame.snake;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.example.snakegame.MusicController;
import org.example.snakegame.common.Direction;
import org.example.snakegame.common.GameState;
import org.example.snakegame.common.Point;
import org.example.snakegame.ScoreManager;

/**
 * Contrôleur du jeu Snake utilisant les objets Snake et Food
 * Version corrigée avec système de scores global et performance améliorée
 */
public class SnakeController {

    private MusicController musicController;
    // Constantes du jeu
    private static final int CELL_SIZE = 20;
    private static final int BOARD_WIDTH = 40;  // 800px / 20px
    private static final int BOARD_HEIGHT = 30; // 600px / 20px
    private static final int INITIAL_GAME_SPEED = 120; // Réduit pour plus de fluidité

    // État du jeu
    private GameState gameState;
    private Timeline gameLoop;
    private GraphicsContext gc;

    // Objets du jeu
    private Snake snake;
    private Food food;

    // Statistiques locales (pour la partie en cours)
    private int currentScore;
    private int gameSpeed;
    private int foodEaten;

    // Référence au gestionnaire de scores global
    private ScoreManager scoreManager;

    // Callbacks pour l'interface
    private Runnable scoreUpdateCallback;
    private Runnable gameOverCallback;

    /**
     * Constructeur du contrôleur Snake
     */
    public SnakeController(GraphicsContext gc) {
        this.gc = gc;
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
        // Créer le serpent au centre
        Point startPosition = new Point(BOARD_WIDTH / 2, BOARD_HEIGHT / 2);
        snake = new Snake(startPosition, Direction.RIGHT);

        // Créer la nourriture
        food = new Food();
        food.generateNewPosition(BOARD_WIDTH, BOARD_HEIGHT, snake.getBody());

        // Statistiques de partie
        currentScore = 0;
        gameSpeed = INITIAL_GAME_SPEED;
        foodEaten = 0;

        // État initial
        gameState = GameState.WAITING_RESTART;

        // Dessiner l'état initial
        render();
    }

    /**
     * Configurer la boucle de jeu avec meilleure performance
     */
    private void setupGameLoop() {
        gameLoop = new Timeline(new KeyFrame(
                Duration.millis(gameSpeed),
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

        // Déplacer le serpent
        snake.move();

        // Vérifier les collisions
        if (checkCollisions()) {
            gameOver();
            return;
        }

        // Vérifier si le serpent mange la nourriture
        if (snake.isEating(food.getPosition())) {
            eatFood();
        }

        // Vérifier expiration de la nourriture spéciale
        if (food.hasExpired()) {
            System.out.println("Nourriture expirée ! Nouvelle nourriture générée.");
            food.generateNewPosition(BOARD_WIDTH, BOARD_HEIGHT, snake.getBody());
        }

        // Redessiner le jeu
        render();
    }

    /**
     * Vérifier toutes les collisions
     */
    private boolean checkCollisions() {
        // Collision avec les murs
        if (snake.checkWallCollision(BOARD_WIDTH, BOARD_HEIGHT)) {
            return true;
        }

        // Collision avec soi-même
        if (snake.checkSelfCollision()) {
            return true;
        }

        return false;
    }

    /**
     * Le serpent mange la nourriture
     */
    private void eatFood() {
        // Obtenir les effets de la nourriture
        int points = food.getValue();
        int growthAmount = food.getGrowthAmount();
        String effect = food.getSpecialEffect();

        // Appliquer les effets
        currentScore += points;
        foodEaten++;

        // Faire grandir le serpent
        if (growthAmount == 1) {
            snake.grow();
        } else {
            snake.grow(growthAmount);
        }

        // Appliquer les effets spéciaux
        applySpecialEffect(food.getType());

        // Afficher l'effet
        if (!effect.isEmpty()) {
            System.out.println("Effet spécial: " + effect);
        }
        if (food.getType() == Food.FoodType.NORMAL) {
            musicController.playSnakeEat();
        } else {
            musicController.playSnakeSpecialFood();
        }
        // Augmenter la vitesse tous les 5 aliments normaux (plus progressif)
        if (food.getType() == Food.FoodType.NORMAL && foodEaten % 5 == 0 && gameSpeed > 60) {
            gameSpeed -= 8; // Réduction plus douce
            updateGameSpeed();
        }

        // Générer une nouvelle nourriture
        food.generateNewPosition(BOARD_WIDTH, BOARD_HEIGHT, snake.getBody());

        // Notifier l'interface du changement de score
        if (scoreUpdateCallback != null) {
            scoreUpdateCallback.run();
        }

        System.out.println("Score: " + currentScore + " | Longueur: " + snake.getLength() +
                " | Type: " + food.getType());
    }

    /**
     * Appliquer les effets spéciaux de la nourriture
     */
    private void applySpecialEffect(Food.FoodType type) {
        switch (type) {
            case SPEED_UP -> {
                if (gameSpeed > 40) {
                    gameSpeed -= 15;
                    updateGameSpeed();
                    System.out.println("Vitesse augmentée ! Nouveau délai: " + gameSpeed + "ms");
                }
            }
            case SLOW_DOWN -> {
                if (gameSpeed < 180) {
                    gameSpeed += 25;
                    updateGameSpeed();
                    System.out.println("Vitesse réduite ! Nouveau délai: " + gameSpeed + "ms");
                }
            }
            case MULTI_GROW -> {
                System.out.println("Le serpent grandit de " + food.getGrowthAmount() + " segments !");
            }
            case SUPER_BONUS -> {
                System.out.println("SUPER BONUS ! +" + food.getValue() + " points et croissance bonus !");
            }
        }
    }

    /**
     * Game Over - CORRIGÉ: Enregistrement dans le ScoreManager
     */
    private void gameOver() {
        musicController.playSnakeGameOver();
        gameState = GameState.GAME_OVER;
        gameLoop.stop();

        // IMPORTANT: Enregistrer le score dans le gestionnaire global
        scoreManager.recordSnakeScore(currentScore);

        // Afficher les statistiques finales
        System.out.println("=== GAME OVER ===");
        System.out.println("Score cette partie: " + currentScore);
        System.out.println("Longueur finale: " + snake.getLength());
        System.out.println("High Score global: " + scoreManager.getSnakeHighScore());
        System.out.println("Score total: " + scoreManager.getSnakeTotalScore());
        System.out.println("Parties jouées: " + scoreManager.getSnakeGamesPlayed());

        // Notifier l'interface
        if (gameOverCallback != null) {
            gameOverCallback.run();
        }

        render(); // Redessiner avec l'état de game over
    }

    /**
     * Mettre à jour la vitesse du jeu (performance améliorée)
     */
    private void updateGameSpeed() {
        if (gameLoop != null) {
            boolean wasPlaying = (gameState == GameState.PLAYING);
            gameLoop.stop();

            // Recréer la timeline avec la nouvelle vitesse
            setupGameLoop();

            if (wasPlaying) {
                gameLoop.play();
            }
        }
    }

    /**
     * Gestion des touches du clavier
     */
    public void handleKeyPress(KeyCode keyCode) {
        System.out.println("Touche pressée: " + keyCode); // Debug

        switch (keyCode) {
            case UP -> {
                boolean changed = snake.setDirection(Direction.UP);
                if (changed) System.out.println("Direction changée vers: UP");
            }
            case DOWN -> {
                boolean changed = snake.setDirection(Direction.DOWN);
                if (changed) System.out.println("Direction changée vers: DOWN");
            }
            case LEFT -> {
                boolean changed = snake.setDirection(Direction.LEFT);
                if (changed) System.out.println("Direction changée vers: LEFT");
            }
            case RIGHT -> {
                boolean changed = snake.setDirection(Direction.RIGHT);
                if (changed) System.out.println("Direction changée vers: RIGHT");
            }
            case SPACE -> {
                togglePause();
                System.out.println("Pause toggled - État: " + gameState);
            }
            case R -> {
                if (gameState.canBeRestarted()) {
                    restartGame();
                    System.out.println("Jeu redémarré");
                }
            }
            case ENTER -> {
                if (gameState == GameState.WAITING_RESTART) {
                    startGame();
                    System.out.println("Jeu démarré avec ENTRÉE");
                }
            }
            default -> {
                System.out.println("Touche ignorée: " + keyCode);
            }
        }
    }

    /**
     * Démarrer le jeu - CORRIGÉ
     */
    public void startGame() {
        if (gameState == GameState.WAITING_RESTART) {
            gameState = GameState.PLAYING;
            gameLoop.play();
            System.out.println("Snake: Jeu démarré !");

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
            if (gameLoop != null) {
                gameLoop.pause();
            }
            System.out.println("Jeu mis en pause");
        } else if (gameState == GameState.PAUSED) {
            gameState = GameState.PLAYING;
            if (gameLoop != null) {
                gameLoop.play();
            }
            System.out.println("Jeu repris");
        }
        render(); // Important: redessiner immédiatement pour montrer l'état
    }

    /**
     * Redémarrer le jeu
     */
    public void restartGame() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        initializeGame();
        System.out.println("Snake: Jeu redémarré !");
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
     * Rendu graphique principal (inchangé mais optimisé)
     */
    public void render() {
        // Effacer le canvas
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, BOARD_WIDTH * CELL_SIZE, BOARD_HEIGHT * CELL_SIZE);

        // Dessiner la grille (optionnel, effet rétro)
        drawGrid();

        // Dessiner la nourriture
        drawFood();

        // Dessiner le serpent
        drawSnake();

        // Dessiner les messages d'état
        drawStatusMessages();

        // Bordure du jeu
        drawBorder();
    }

    /**
     * Dessiner la grille de fond
     */
    private void drawGrid() {
        gc.setStroke(Color.rgb(0, 50, 0));
        gc.setLineWidth(0.5);

        // Lignes verticales
        for (int x = 0; x <= BOARD_WIDTH; x++) {
            gc.strokeLine(x * CELL_SIZE, 0, x * CELL_SIZE, BOARD_HEIGHT * CELL_SIZE);
        }

        // Lignes horizontales
        for (int y = 0; y <= BOARD_HEIGHT; y++) {
            gc.strokeLine(0, y * CELL_SIZE, BOARD_WIDTH * CELL_SIZE, y * CELL_SIZE);
        }
    }

    /**
     * Dessiner le serpent
     */
    private void drawSnake() {
        var body = snake.getBody();
        for (int i = 0; i < body.size(); i++) {
            Point segment = body.get(i);

            if (i == 0) {
                // Tête du serpent (plus brillante)
                gc.setFill(Color.LIME);
            } else {
                // Corps du serpent (dégradé)
                double alpha = 1.0 - (i * 0.1);
                alpha = Math.max(alpha, 0.3);
                gc.setFill(Color.rgb(0, (int)(255 * alpha), 0));
            }

            gc.fillRect(
                    segment.x * CELL_SIZE,
                    segment.y * CELL_SIZE,
                    CELL_SIZE - 1,
                    CELL_SIZE - 1
            );
        }
    }

    /**
     * Dessiner la nourriture avec couleur selon le type
     */
    private void drawFood() {
        Point pos = food.getPosition();

        // Couleur selon le type
        String colorStr = food.getColor();
        Color color = Color.web(colorStr);

        // Effet de clignotement si proche de l'expiration
        if (food.shouldBlink()) {
            long time = System.currentTimeMillis();
            if ((time / 200) % 2 == 0) { // Clignote toutes les 200ms
                color = Color.WHITE;
            }
        }

        gc.setFill(color);

        if (food.isSpecial()) {
            // Nourriture spéciale = forme différente + effet
            gc.fillOval(
                    pos.x * CELL_SIZE + 1,
                    pos.y * CELL_SIZE + 1,
                    CELL_SIZE - 2,
                    CELL_SIZE - 2
            );

            // Effet de brillance pour nourriture spéciale
            gc.setFill(Color.WHITE);
            gc.fillOval(
                    pos.x * CELL_SIZE + 4,
                    pos.y * CELL_SIZE + 4,
                    CELL_SIZE - 8,
                    CELL_SIZE - 8
            );
        } else {
            // Nourriture normale = carré simple
            gc.fillRect(
                    pos.x * CELL_SIZE + 2,
                    pos.y * CELL_SIZE + 2,
                    CELL_SIZE - 4,
                    CELL_SIZE - 4
            );
        }
    }

    /**
     * Dessiner les messages d'état
     */
    private void drawStatusMessages() {
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Courier New", 16));

        String message = switch (gameState) {
            case WAITING_RESTART -> "Appuyez sur ENTRÉE pour commencer !";
            case PAUSED -> "JEU EN PAUSE - Appuyez sur ESPACE pour reprendre";
            case GAME_OVER -> "GAME OVER - Appuyez sur R pour rejouer";
            default -> "";
        };

        if (!message.isEmpty()) {
            gc.fillText(message, 50, BOARD_HEIGHT * CELL_SIZE / 2);
        }

        // Afficher les statistiques globales en game over
        if (gameState == GameState.GAME_OVER) {
            gc.setFont(javafx.scene.text.Font.font("Courier New", 12));
            gc.setFill(Color.YELLOW);

            int baseY = BOARD_HEIGHT * CELL_SIZE / 2 + 40;
            gc.fillText("High Score: " + scoreManager.getSnakeHighScore(), 50, baseY);
            gc.fillText("Parties jouées: " + scoreManager.getSnakeGamesPlayed(), 50, baseY + 20);
            gc.fillText("Score total: " + scoreManager.getSnakeTotalScore(), 50, baseY + 40);
            gc.fillText("Moyenne: " + scoreManager.getSnakeAverageScore(), 50, baseY + 60);
        }

        // Afficher le type de nourriture actuelle
        if (food.isSpecial() && gameState == GameState.PLAYING) {
            String foodInfo = food.getType().name();
            if (food.getTimeToExpiration() < Long.MAX_VALUE) {
                foodInfo += " (" + food.getTimeToExpiration() + "s)";
            }
            gc.setFill(Color.YELLOW);
            gc.setFont(javafx.scene.text.Font.font("Courier New", 12));
            gc.fillText(foodInfo, 10, 20);
        }
    }

    /**
     * Dessiner la bordure
     */
    private void drawBorder() {
        gc.setStroke(Color.CYAN);
        gc.setLineWidth(2);
        gc.strokeRect(0, 0, BOARD_WIDTH * CELL_SIZE, BOARD_HEIGHT * CELL_SIZE);
    }

    // Getters pour l'interface (corrigés pour utiliser le ScoreManager)
    public int getScore() { return currentScore; }
    public int getHighScore() { return scoreManager.getSnakeHighScore(); }
    public int getSnakeLength() { return snake.getLength(); }
    public GameState getGameState() { return gameState; }
    public int getGameSpeed() { return INITIAL_GAME_SPEED - gameSpeed + 50; }
    public int getTotalScore() { return scoreManager.getSnakeTotalScore(); }
    public int getGamesPlayed() { return scoreManager.getSnakeGamesPlayed(); }
    public int getAverageScore() { return scoreManager.getSnakeAverageScore(); }

    // Setters pour les callbacks
    public void setScoreUpdateCallback(Runnable callback) {
        this.scoreUpdateCallback = callback;
    }

    public void setGameOverCallback(Runnable callback) {
        this.gameOverCallback = callback;
    }
}