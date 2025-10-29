package org.example.snakegame.pong;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.snakegame.common.GameState;
import org.example.snakegame.common.ValidationUtils;

/**
 * Renderer dédié au jeu Pong
 * Respecte le principe SRP (Single Responsibility Principle)
 * 
 * Responsabilité unique: Affichage graphique du jeu Pong
 * - Rendu du terrain
 * - Rendu des raquettes
 * - Rendu de la balle
 * - Rendu du score
 * - Rendu des messages d'état
 */
public class PongRenderer {
    
    private final GraphicsContext gc;
    private final double canvasWidth;
    private final double canvasHeight;
    private final int winningScore;
    
    /**
     * Constructeur
     * @param gc Contexte graphique pour le rendu
     * @param canvasWidth Largeur du canvas
     * @param canvasHeight Hauteur du canvas
     * @param winningScore Score pour gagner
     */
    public PongRenderer(GraphicsContext gc, double canvasWidth, double canvasHeight, int winningScore) {
        this.gc = ValidationUtils.requireNonNull(gc, "graphicsContext");
        if (canvasWidth <= 0) throw new IllegalArgumentException("canvasWidth must be positive");
        if (canvasHeight <= 0) throw new IllegalArgumentException("canvasHeight must be positive");
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        this.winningScore = ValidationUtils.requirePositive(winningScore, "winningScore");
    }
    
    /**
     * Effacer le canvas
     */
    public void clear() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvasWidth, canvasHeight);
    }
    
    /**
     * Dessiner le terrain de jeu (ligne centrale)
     */
    public void drawField() {
        // Ligne centrale en pointillés
        gc.setStroke(Color.CYAN);
        gc.setLineWidth(3);
        
        double dashLength = 15;
        double gapLength = 10;
        for (double y = dashLength; y < canvasHeight; y += dashLength + gapLength) {
            gc.strokeLine(canvasWidth/2, y, canvasWidth/2,
                    Math.min(y + dashLength, canvasHeight));
        }
    }
    
    /**
     * Dessiner les raquettes
     * @param leftPaddle Raquette gauche
     * @param rightPaddle Raquette droite
     */
    public void drawPaddles(Paddle leftPaddle, Paddle rightPaddle) {
        ValidationUtils.requireNonNull(leftPaddle, "leftPaddle");
        ValidationUtils.requireNonNull(rightPaddle, "rightPaddle");
        
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
     * @param ball Balle à dessiner
     */
    public void drawBall(Ball ball) {
        ValidationUtils.requireNonNull(ball, "ball");
        
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
     * Dessiner le score
     * @param player1Score Score du joueur 1
     * @param player2Score Score du joueur 2 (IA)
     */
    public void drawScore(int player1Score, int player2Score) {
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Courier New", 48));
        
        // Score joueur 1 (gauche)
        String score1 = String.valueOf(player1Score);
        gc.fillText(score1, canvasWidth/4, 60);
        
        // Score IA (droite)
        String score2 = String.valueOf(player2Score);
        gc.fillText(score2, 3*canvasWidth/4, 60);
    }
    
    /**
     * Dessiner les messages d'état
     * @param gameState État actuel du jeu
     * @param player1Score Score du joueur 1
     * @param player2Score Score du joueur 2
     */
    public void drawStatusMessage(GameState gameState, int player1Score, int player2Score) {
        ValidationUtils.requireNonNull(gameState, "gameState");
        
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Courier New", 16));
        
        String message = switch (gameState) {
            case WAITING_RESTART -> "Appuyez sur ENTRÉE pour commencer !";
            case PAUSED -> "JEU EN PAUSE - Appuyez sur ESPACE";
            case VICTORY -> {
                String winner = player1Score >= winningScore ? "JOUEUR 1" : "IA";
                yield winner + " GAGNE ! - Appuyez sur R pour rejouer";
            }
            default -> "";
        };
        
        if (!message.isEmpty()) {
            gc.fillText(message, 50, canvasHeight / 2);
        }
    }
    
    /**
     * Dessiner les instructions de contrôle
     */
    public void drawControls() {
        gc.setFill(Color.rgb(255, 255, 255, 0.5));
        gc.setFont(javafx.scene.text.Font.font("Courier New", 12));
        
        gc.fillText("Contrôles: ↑↓ ou Z/S", 10, canvasHeight - 10);
        gc.fillText("Difficulté IA: 1(Facile) 2(Moyen) 3(Difficile)", canvasWidth - 300, canvasHeight - 10);
    }
    
    /**
     * Rendu complet du jeu
     * @param leftPaddle Raquette gauche
     * @param rightPaddle Raquette droite
     * @param ball Balle
     * @param player1Score Score joueur 1
     * @param player2Score Score joueur 2
     * @param gameState État du jeu
     */
    public void render(Paddle leftPaddle, Paddle rightPaddle, Ball ball, 
                      int player1Score, int player2Score, GameState gameState) {
        // Effacer le canvas
        clear();
        
        // Dessiner le terrain
        drawField();
        
        // Dessiner les éléments du jeu
        drawPaddles(leftPaddle, rightPaddle);
        drawBall(ball);
        
        // Dessiner le score
        drawScore(player1Score, player2Score);
        
        // Dessiner les messages d'état
        drawStatusMessage(gameState, player1Score, player2Score);
        
        // Dessiner les contrôles
        drawControls();
    }
}
