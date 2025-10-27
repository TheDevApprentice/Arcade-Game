package org.example.snakegame.pong;

import java.util.Random;

/**
 * Classe Ball - Représente la balle du jeu Pong avec toute sa logique
 */
public class Ball {

    private double x;
    private double y;
    private double velocityX;
    private double velocityY;
    private double speed;
    private double size;
    private double canvasWidth;
    private double canvasHeight;
    private Random random;

    // Statistiques de la balle
    private int bounceCount;
    private int wallBounces;
    private int paddleBounces;
    private double maxSpeed;

    // Configuration
    private static final double SPEED_INCREASE_FACTOR = 1.05;
    private static final double MAX_SPEED_MULTIPLIER = 2.0;
    private static final double MIN_VELOCITY_Y = 0.5;

    /**
     * Constructeur de la balle
     */
    public Ball(double canvasWidth, double canvasHeight, double size, double initialSpeed) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        this.size = size;
        this.speed = initialSpeed;
        this.maxSpeed = initialSpeed * MAX_SPEED_MULTIPLIER;
        this.random = new Random();

        // Statistiques
        this.bounceCount = 0;
        this.wallBounces = 0;
        this.paddleBounces = 0;

        // Position initiale au centre
        reset();
    }

    /**
     * Déplacer la balle
     */
    public void move() {
        x += velocityX;
        y += velocityY;

        // Vérifier les collisions avec les murs haut/bas
        checkWallCollisions();
    }

    /**
     * Vérifier et gérer les collisions avec les murs
     */
    private void checkWallCollisions() {
        // Collision avec le mur du haut
        if (y <= 0) {
            y = 0;
            velocityY = Math.abs(velocityY); // Force vers le bas
            wallBounces++;
            bounceCount++;
        }

        // Collision avec le mur du bas
        if (y >= canvasHeight - size) {
            y = canvasHeight - size;
            velocityY = -Math.abs(velocityY); // Force vers le haut
            wallBounces++;
            bounceCount++;
        }
    }

    /**
     * Collision avec une raquette
     */
    public boolean checkPaddleCollision(double paddleX, double paddleY, double paddleWidth, double paddleHeight) {
        return x + size >= paddleX &&
                x <= paddleX + paddleWidth &&
                y + size >= paddleY &&
                y <= paddleY + paddleHeight;
    }

    /**
     * Gérer la collision avec une raquette
     */
    public void handlePaddleCollision(double paddleX, double paddleY, double paddleWidth, double paddleHeight, boolean isLeftPaddle) {
        // Calculer le point d'impact sur la raquette (0.0 = haut, 1.0 = bas)
        double ballCenterY = y + size / 2;
        double paddleCenterY = paddleY + paddleHeight / 2;
        double relativeIntersectY = (ballCenterY - paddleCenterY) / (paddleHeight / 2);

        // Limiter l'effet entre -1 et 1
        relativeIntersectY = Math.max(-1, Math.min(1, relativeIntersectY));

        // Calculer l'angle de rebond
        double bounceAngle = relativeIntersectY * Math.PI / 4; // Max 45 degrés

        // Ajuster la position pour éviter que la balle traverse la raquette
        if (isLeftPaddle) {
            x = paddleX + paddleWidth;
            velocityX = Math.abs(speed * Math.cos(bounceAngle));
        } else {
            x = paddleX - size;
            velocityX = -Math.abs(speed * Math.cos(bounceAngle));
        }

        // Calculer la nouvelle vitesse Y basée sur l'angle
        velocityY = speed * Math.sin(bounceAngle);

        // S'assurer qu'il y a toujours un minimum de vitesse verticale
        if (Math.abs(velocityY) < MIN_VELOCITY_Y) {
            velocityY = velocityY >= 0 ? MIN_VELOCITY_Y : -MIN_VELOCITY_Y;
        }

        // Augmenter légèrement la vitesse
        increaseSpeed();

        // Statistiques
        paddleBounces++;
        bounceCount++;
    }

    /**
     * Augmenter la vitesse de la balle
     */
    private void increaseSpeed() {
        if (speed < maxSpeed) {
            speed = Math.min(speed * SPEED_INCREASE_FACTOR, maxSpeed);

            // Normaliser les vitesses pour maintenir la nouvelle vitesse
            double currentMagnitude = Math.sqrt(velocityX * velocityX + velocityY * velocityY);
            if (currentMagnitude > 0) {
                velocityX = (velocityX / currentMagnitude) * speed;
                velocityY = (velocityY / currentMagnitude) * speed;
            }
        }
    }

    /**
     * Vérifier si la balle est sortie du terrain (goal)
     */
    public GoalResult checkGoal() {
        if (x < 0) {
            return GoalResult.PLAYER_2_GOAL; // Balle sortie à gauche
        } else if (x > canvasWidth) {
            return GoalResult.PLAYER_1_GOAL; // Balle sortie à droite
        }
        return GoalResult.NO_GOAL;
    }

    /**
     * Réinitialiser la balle au centre avec une direction aléatoire
     */
    public void reset() {
        // Position centrale
        x = canvasWidth / 2 - size / 2;
        y = canvasHeight / 2 - size / 2;

        // Direction aléatoire mais équitable
        double angle = (random.nextDouble() - 0.5) * Math.PI / 3; // ±60 degrés max
        boolean goLeft = random.nextBoolean();

        velocityX = goLeft ? -speed * Math.cos(angle) : speed * Math.cos(angle);
        velocityY = speed * Math.sin(angle);

        // Réinitialiser les stats pour ce point
        bounceCount = 0;
    }

    /**
     * Réinitialiser complètement la balle (nouveau match)
     */
    public void fullReset() {
        reset();
        bounceCount = 0;
        wallBounces = 0;
        paddleBounces = 0;
        speed = speed / Math.pow(SPEED_INCREASE_FACTOR, paddleBounces); // Vitesse initiale
    }

    /**
     * Obtenir la vitesse pour les effets visuels de traînée
     */
    public double getTotalVelocity() {
        return Math.sqrt(velocityX * velocityX + velocityY * velocityY);
    }

    /**
     * Prédire où la balle va toucher le mur (pour l'IA)
     */
    public double predictYAtX(double targetX) {
        if (Math.abs(velocityX) < 0.001) {
            return y; // La balle ne bouge pas horizontalement
        }

        double timeToReachX = (targetX - x) / velocityX;
        if (timeToReachX < 0) {
            return y; // La balle va dans la direction opposée
        }

        double predictedY = y + velocityY * timeToReachX;

        // Gérer les rebonds sur les murs
        while (predictedY < 0 || predictedY > canvasHeight - size) {
            if (predictedY < 0) {
                predictedY = -predictedY;
            } else {
                predictedY = 2 * (canvasHeight - size) - predictedY;
            }
        }

        return predictedY;
    }

    // Getters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getVelocityX() { return velocityX; }
    public double getVelocityY() { return velocityY; }
    public double getSpeed() { return speed; }
    public double getSize() { return size; }
    public int getBounceCount() { return bounceCount; }
    public int getWallBounces() { return wallBounces; }
    public int getPaddleBounces() { return paddleBounces; }

    // Setters pour ajustements fins
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setVelocityX(double velocityX) { this.velocityX = velocityX; }
    public void setVelocityY(double velocityY) { this.velocityY = velocityY; }

    /**
     * Enum pour les résultats de goal
     */
    public enum GoalResult {
        NO_GOAL,
        PLAYER_1_GOAL,
        PLAYER_2_GOAL
    }

    @Override
    public String toString() {
        return String.format("Ball[x=%.1f, y=%.1f, speed=%.1f, bounces=%d]",
                x, y, speed, bounceCount);
    }
}