package org.example.snakegame.pong;

/**
 * Classe Paddle - Représente une raquette du jeu Pong avec toute sa logique
 */
public class Paddle {

    private double x;
    private double y;
    private final double width;
    private final double height;
    private double speed;
    private final double canvasHeight;
    private final boolean isAI;
    private final PaddleType type;

    // IA Properties
    private double aiDifficulty;
    private double aiReactionDelay;
    private double aiTargetY;
    private long lastAIUpdate;

    // Statistiques
    private int hits;
    private int totalMovement;

    /**
     * Types de raquette
     */
    public enum PaddleType {
        PLAYER_LEFT,
        PLAYER_RIGHT,
        AI_LEFT,
        AI_RIGHT
    }

    /**
     * Constructeur de la raquette
     */
    public Paddle(double x, double y, double width, double height, double speed,
                  double canvasHeight, PaddleType type) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.canvasHeight = canvasHeight;
        this.type = type;
        this.isAI = (type == PaddleType.AI_LEFT || type == PaddleType.AI_RIGHT);

        // Configuration IA par défaut
        this.aiDifficulty = 0.7;
        this.aiReactionDelay = 0;
        this.aiTargetY = y;
        this.lastAIUpdate = System.currentTimeMillis();

        // Statistiques
        this.hits = 0;
        this.totalMovement = 0;

        // Position initiale centrée
        centerVertically();
    }

    /**
     * Déplacer la raquette vers le haut
     */
    public boolean moveUp() {
        double oldY = y;
        y = Math.max(0, y - speed);

        boolean moved = (y != oldY);
        if (moved) {
            totalMovement += (int)(oldY - y);
        }
        return moved;
    }

    /**
     * Déplacer la raquette vers le bas
     */
    public boolean moveDown() {
        double oldY = y;
        y = Math.min(canvasHeight - height, y + speed);

        boolean moved = (y != oldY);
        if (moved) {
            totalMovement += (int)(y - oldY);
        }
        return moved;
    }

    /**
     * Déplacer la raquette vers une position cible (pour l'IA)
     */
    public void moveTowards(double targetY) {
        double center = getCenterY();
        double difference = targetY - center;

        if (Math.abs(difference) > speed / 2) {
            if (difference > 0) {
                moveDown();
            } else {
                moveUp();
            }
        }
    }

    /**
     * Mise à jour de l'IA
     */
    public void updateAI(Ball ball) {
        if (!isAI) return;

        long currentTime = System.currentTimeMillis();

        // Délai de réaction de l'IA
        if (aiReactionDelay > 0) {
            aiReactionDelay--;
            return;
        }

        // L'IA réagit différemment selon la direction de la balle
        boolean ballComingTowards = (type == PaddleType.AI_LEFT && ball.getVelocityX() < 0) ||
                (type == PaddleType.AI_RIGHT && ball.getVelocityX() > 0);

        if (ballComingTowards) {
            // Prédire où la balle va arriver
            double predictedY = ball.predictYAtX(x + width/2);
            aiTargetY = predictedY + ball.getSize()/2 - height/2;

            // Ajouter de l'imprécision basée sur la difficulté
            double error = (1 - aiDifficulty) * height * 0.3;
            aiTargetY += (Math.random() - 0.5) * error;

        } else {
            // La balle s'éloigne, retourner au centre lentement
            aiTargetY = (canvasHeight - height) / 2;
        }

        // Limiter la vitesse de l'IA selon la difficulté
        double aiSpeed = speed * aiDifficulty;
        double currentCenter = getCenterY();
        double targetCenter = aiTargetY + height/2;

        if (Math.abs(targetCenter - currentCenter) > aiSpeed) {
            if (targetCenter > currentCenter) {
                y = Math.min(y + aiSpeed, canvasHeight - height);
            } else {
                y = Math.max(y - aiSpeed, 0);
            }
        }

        lastAIUpdate = currentTime;
    }

    /**
     * Vérifier la collision avec la balle
     */
    public boolean collidesWith(Ball ball) {
        double ballX = ball.getX();
        double ballY = ball.getY();
        double ballSize = ball.getSize();

        return ballX + ballSize >= x &&
                ballX <= x + width &&
                ballY + ballSize >= y &&
                ballY <= y + height;
    }

    /**
     * Gérer la collision avec la balle
     */
    public void handleBallCollision(Ball ball) {
        if (!collidesWith(ball)) return;

        // Déterminer si c'est une raquette gauche ou droite
        boolean isLeftPaddle = (type == PaddleType.PLAYER_LEFT || type == PaddleType.AI_LEFT);

        // Laisser la balle gérer la physique de collision
        ball.handlePaddleCollision(x, y, width, height, isLeftPaddle);

        // Mettre à jour les statistiques
        hits++;

        // Ajouter un délai de réaction pour l'IA après un hit
        if (isAI) {
            aiReactionDelay = (int)(20 * (1 - aiDifficulty)); // Plus l'IA est difficile, moins le délai
        }
    }

    /**
     * Calculer l'effet sur la balle selon le point de contact
     */
    public double getHitEffect(Ball ball) {
        double ballCenterY = ball.getY() + ball.getSize() / 2;
        double paddleCenterY = getCenterY();
        double relativePosition = (ballCenterY - paddleCenterY) / (height / 2);

        // Limiter l'effet entre -1 et 1
        return Math.max(-1, Math.min(1, relativePosition));
    }

    /**
     * Réinitialiser la position de la raquette
     */
    public void reset() {
        centerVertically();
        hits = 0;
        totalMovement = 0;
        aiReactionDelay = 0;
        aiTargetY = getCenterY();
    }

    /**
     * Centrer la raquette verticalement
     */
    public void centerVertically() {
        y = (canvasHeight - height) / 2;
    }

    /**
     * Configurer l'IA
     */
    public void setAIDifficulty(double difficulty) {
        this.aiDifficulty = Math.max(0.1, Math.min(1.0, difficulty));
    }

    /**
     * Vérifier si la raquette peut bouger vers le haut
     */
    public boolean canMoveUp() {
        return y > 0;
    }

    /**
     * Vérifier si la raquette peut bouger vers le bas
     */
    public boolean canMoveDown() {
        return y < canvasHeight - height;
    }

    /**
     * Obtenir la distance jusqu'à la balle (pour l'IA)
     */
    public double distanceToBall(Ball ball) {
        double ballCenterX = ball.getX() + ball.getSize() / 2;
        double ballCenterY = ball.getY() + ball.getSize() / 2;
        double paddleCenterX = x + width / 2;
        double paddleCenterY = getCenterY();

        double dx = ballCenterX - paddleCenterX;
        double dy = ballCenterY - paddleCenterY;

        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Obtenir le pourcentage de couverture de la balle (0.0 = manqué, 1.0 = centre parfait)
     */
    public double getBallCoverage(Ball ball) {
        double ballCenterY = ball.getY() + ball.getSize() / 2;

        if (ballCenterY < y || ballCenterY > y + height) {
            return 0.0; // La balle n'est pas couverte
        }

        double relativePosition = Math.abs((ballCenterY - getCenterY()) / (height / 2));
        return 1.0 - relativePosition; // Plus proche du centre = meilleure couverture
    }

    // Getters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public double getSpeed() { return speed; }
    public boolean isAI() { return isAI; }
    public PaddleType getType() { return type; }
    public double getAIDifficulty() { return aiDifficulty; }
    public int getHits() { return hits; }
    public int getTotalMovement() { return totalMovement; }

    // Setters
    public void setY(double y) {
        this.y = Math.max(0, Math.min(canvasHeight - height, y));
    }
    public void setSpeed(double speed) { this.speed = speed; }

    /**
     * Obtenir le centre vertical de la raquette
     */
    public double getCenterY() {
        return y + height / 2;
    }

    /**
     * Obtenir les statistiques de performance
     */
    public String getPerformanceStats() {
        return String.format("Hits: %d, Movement: %d pixels, Efficiency: %.1f%%",
                hits, totalMovement,
                totalMovement > 0 ? (hits * 100.0 / totalMovement) : 0);
    }

    @Override
    public String toString() {
        return String.format("Paddle[type=%s, y=%.1f, hits=%d, AI=%.1f]",
                type, y, hits, aiDifficulty);
    }
}