package org.example.snakegame.snake;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.snakegame.common.GameState;
import org.example.snakegame.common.Point;
import org.example.snakegame.common.ValidationUtils;

import java.util.List;

/**
 * Renderer dédié au jeu Snake
 * Respecte le principe SRP (Single Responsibility Principle)
 * 
 * Responsabilité unique: Affichage graphique du jeu Snake
 * - Rendu du serpent
 * - Rendu de la nourriture
 * - Rendu de la grille
 * - Rendu des messages d'état
 * - Rendu du score
 */
public class SnakeRenderer {
    
    private final GraphicsContext gc;
    private final int cellSize;
    private final int boardWidth;
    private final int boardHeight;
    
    /**
     * Constructeur
     * @param gc Contexte graphique pour le rendu
     * @param cellSize Taille d'une cellule en pixels
     * @param boardWidth Largeur du plateau en cellules
     * @param boardHeight Hauteur du plateau en cellules
     */
    public SnakeRenderer(GraphicsContext gc, int cellSize, int boardWidth, int boardHeight) {
        this.gc = ValidationUtils.requireNonNull(gc, "graphicsContext");
        this.cellSize = ValidationUtils.requirePositive(cellSize, "cellSize");
        this.boardWidth = ValidationUtils.requirePositive(boardWidth, "boardWidth");
        this.boardHeight = ValidationUtils.requirePositive(boardHeight, "boardHeight");
    }
    
    /**
     * Effacer le canvas
     */
    public void clear() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, boardWidth * cellSize, boardHeight * cellSize);
    }
    
    /**
     * Dessiner la grille
     */
    public void drawGrid() {
        gc.setStroke(Color.rgb(30, 30, 30));
        gc.setLineWidth(0.5);
        
        // Lignes verticales
        for (int x = 0; x <= boardWidth; x++) {
            gc.strokeLine(x * cellSize, 0, x * cellSize, boardHeight * cellSize);
        }
        
        // Lignes horizontales
        for (int y = 0; y <= boardHeight; y++) {
            gc.strokeLine(0, y * cellSize, boardWidth * cellSize, y * cellSize);
        }
    }
    
    /**
     * Dessiner le serpent
     * @param snakeBody Corps du serpent (liste de points)
     */
    public void drawSnake(List<Point> snakeBody) {
        ValidationUtils.requireNonNull(snakeBody, "snakeBody");
        
        for (int i = 0; i < snakeBody.size(); i++) {
            Point segment = snakeBody.get(i);
            
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
                    segment.x * cellSize,
                    segment.y * cellSize,
                    cellSize - 1,
                    cellSize - 1
            );
        }
    }
    
    /**
     * Dessiner la nourriture
     * @param food Objet Food à dessiner
     */
    public void drawFood(Food food) {
        ValidationUtils.requireNonNull(food, "food");
        
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
                    pos.x * cellSize + 1,
                    pos.y * cellSize + 1,
                    cellSize - 2,
                    cellSize - 2
            );
            
            // Effet de brillance pour nourriture spéciale
            gc.setFill(Color.WHITE);
            gc.fillOval(
                    pos.x * cellSize + 4,
                    pos.y * cellSize + 4,
                    cellSize - 8,
                    cellSize - 8
            );
        } else {
            // Nourriture normale = carré simple
            gc.fillRect(
                    pos.x * cellSize + 2,
                    pos.y * cellSize + 2,
                    cellSize - 4,
                    cellSize - 4
            );
        }
    }
    
    /**
     * Dessiner le score
     * @param score Score actuel
     * @param highScore Meilleur score
     * @param foodEaten Nourriture mangée
     */
    public void drawScore(int score, int highScore, int foodEaten) {
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Courier New", 18));
        
        String scoreText = String.format("Score: %04d | High: %04d | Food: %d", 
                score, highScore, foodEaten);
        gc.fillText(scoreText, 10, 20);
    }
    
    /**
     * Dessiner les messages d'état
     * @param gameState État actuel du jeu
     */
    public void drawStatusMessage(GameState gameState) {
        ValidationUtils.requireNonNull(gameState, "gameState");
        
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Courier New", 16));
        
        String message = switch (gameState) {
            case WAITING_RESTART -> "Appuyez sur ENTRÉE pour commencer !";
            case PAUSED -> "JEU EN PAUSE - Appuyez sur ESPACE pour reprendre";
            case GAME_OVER -> "GAME OVER - Appuyez sur R pour rejouer";
            default -> "";
        };
        
        if (!message.isEmpty()) {
            gc.fillText(message, 50, boardHeight * cellSize / 2);
        }
    }
    
    /**
     * Dessiner un message personnalisé au centre de l'écran
     * @param message Message à afficher
     * @param color Couleur du texte
     */
    public void drawCenteredMessage(String message, Color color) {
        ValidationUtils.requireNonEmpty(message, "message");
        ValidationUtils.requireNonNull(color, "color");
        
        gc.setFill(color);
        gc.setFont(javafx.scene.text.Font.font("Courier New", 24));
        
        // Calculer la position pour centrer le texte (approximatif)
        double x = (boardWidth * cellSize - message.length() * 12) / 2;
        double y = boardHeight * cellSize / 2;
        
        gc.fillText(message, x, y);
    }
    
    /**
     * Rendu complet du jeu
     * @param snake Serpent à dessiner
     * @param food Nourriture à dessiner
     * @param score Score actuel
     * @param highScore Meilleur score
     * @param foodEaten Nourriture mangée
     * @param gameState État du jeu
     */
    public void render(Snake snake, Food food, int score, int highScore, int foodEaten, GameState gameState) {
        // Effacer le canvas
        clear();
        
        // Dessiner la grille
        drawGrid();
        
        // Dessiner les éléments du jeu
        drawSnake(snake.getBody());
        drawFood(food);
        
        // Dessiner le score
        drawScore(score, highScore, foodEaten);
        
        // Dessiner les messages d'état
        drawStatusMessage(gameState);
    }
}
