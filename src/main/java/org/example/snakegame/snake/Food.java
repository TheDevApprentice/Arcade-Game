package org.example.snakegame.snake;

import org.example.snakegame.common.Point;

import java.util.List;
import java.util.Random;

/**
 * Classe Food - Représente la nourriture du serpent avec toute sa logique
 */
public class Food {

    private Point position;
    private FoodType type;
    private int value;
    private Random random;
    private long creationTime;
    private boolean isSpecialFood;

    /**
     * Types de nourriture possibles
     */
    public enum FoodType {
        NORMAL(10, "#FF0000"),        // Rouge - Nourriture normale
        BONUS(25, "#FFD700"),         // Or - Nourriture bonus
        SUPER_BONUS(50, "#FF00FF"),   // Magenta - Super bonus
        SPEED_UP(15, "#00FFFF"),      // Cyan - Augmente la vitesse
        SLOW_DOWN(15, "#FFFF00"),     // Jaune - Ralentit le jeu
        MULTI_GROW(20, "#00FF00");    // Vert brillant - Fait grandir de 3 segments

        private final int points;
        private final String color;

        FoodType(int points, String color) {
            this.points = points;
            this.color = color;
        }

        public int getPoints() { return points; }
        public String getColor() { return color; }
    }

    /**
     * Constructeur de la nourriture
     */
    public Food() {
        this.random = new Random();
        this.type = FoodType.NORMAL;
        this.value = type.getPoints();
        this.creationTime = System.currentTimeMillis();
        this.isSpecialFood = false;
    }

    /**
     * Constructeur avec type spécifique
     */
    public Food(FoodType type) {
        this();
        this.type = type;
        this.value = type.getPoints();
        this.isSpecialFood = type != FoodType.NORMAL;
    }

    /**
     * Générer une nouvelle position pour la nourriture
     */
    public void generateNewPosition(int boardWidth, int boardHeight, List<Point> obstacles) {
        Point newPosition;
        int attempts = 0;
        final int maxAttempts = 100;

        do {
            int x = random.nextInt(boardWidth);
            int y = random.nextInt(boardHeight);
            newPosition = new Point(x, y);
            attempts++;

            // Éviter une boucle infinie si le plateau est presque plein
            if (attempts >= maxAttempts) {
                newPosition = findFirstFreePosition(boardWidth, boardHeight, obstacles);
                break;
            }

        } while (obstacles.contains(newPosition));

        this.position = newPosition;
        this.creationTime = System.currentTimeMillis();

        // Déterminer le type de nourriture
        determineType();
    }

    /**
     * Trouver la première position libre (méthode de secours)
     */
    private Point findFirstFreePosition(int boardWidth, int boardHeight, List<Point> obstacles) {
        for (int y = 0; y < boardHeight; y++) {
            for (int x = 0; x < boardWidth; x++) {
                Point candidate = new Point(x, y);
                if (!obstacles.contains(candidate)) {
                    return candidate;
                }
            }
        }
        // Si vraiment aucune position libre, retourner le centre
        return new Point(boardWidth / 2, boardHeight / 2);
    }

    /**
     * Déterminer le type de nourriture à générer
     */
    private void determineType() {
        double rand = random.nextDouble();

        if (rand < 0.7) {
            // 70% - Nourriture normale
            this.type = FoodType.NORMAL;
            this.isSpecialFood = false;
        } else if (rand < 0.85) {
            // 15% - Bonus simple
            this.type = FoodType.BONUS;
            this.isSpecialFood = true;
        } else if (rand < 0.93) {
            // 8% - Effets spéciaux
            FoodType[] specialTypes = {FoodType.SPEED_UP, FoodType.SLOW_DOWN, FoodType.MULTI_GROW};
            this.type = specialTypes[random.nextInt(specialTypes.length)];
            this.isSpecialFood = true;
        } else {
            // 7% - Super bonus
            this.type = FoodType.SUPER_BONUS;
            this.isSpecialFood = true;
        }

        this.value = type.getPoints();
    }



    /**
     * Vérifier si la nourriture a expiré (pour les nourritures spéciales)
     */
    public boolean hasExpired() {
        if (!isSpecialFood) {
            return false; // La nourriture normale n'expire jamais
        }

        long currentTime = System.currentTimeMillis();
        long lifetime = switch (type) {
            case BONUS -> 15000;      // 15 secondes
            case SUPER_BONUS -> 10000; // 10 secondes
            case SPEED_UP, SLOW_DOWN, MULTI_GROW -> 12000; // 12 secondes
            default -> Long.MAX_VALUE;
        };

        return (currentTime - creationTime) > lifetime;
    }

    /**
     * Obtenir le temps restant avant expiration (en secondes)
     */
    public long getTimeToExpiration() {
        if (!isSpecialFood) {
            return Long.MAX_VALUE;
        }

        long currentTime = System.currentTimeMillis();
        long lifetime = switch (type) {
            case BONUS -> 15000;
            case SUPER_BONUS -> 10000;
            case SPEED_UP, SLOW_DOWN, MULTI_GROW -> 12000;
            default -> Long.MAX_VALUE;
        };

        long elapsed = currentTime - creationTime;
        return Math.max(0, (lifetime - elapsed) / 1000);
    }

    /**
     * Obtenir la position de la nourriture
     */
    public Point getPosition() {
        return position;
    }

    /**
     * Obtenir la valeur en points
     */
    public int getValue() {
        return value;
    }

    /**
     * Obtenir le type de nourriture
     */
    public FoodType getType() {
        return type;
    }

    /**
     * Vérifier si c'est une nourriture spéciale
     */
    public boolean isSpecial() {
        return isSpecialFood;
    }

    /**
     * Obtenir la couleur de la nourriture
     */
    public String getColor() {
        return type.getColor();
    }

    /**
     * Vérifier si la nourriture clignote (proche de l'expiration)
     */
    public boolean shouldBlink() {
        if (!isSpecialFood) {
            return false;
        }

        long timeLeft = getTimeToExpiration();
        return timeLeft <= 3; // Clignote dans les 3 dernières secondes
    }

    /**
     * Obtenir l'effet spécial de la nourriture
     */
    public String getSpecialEffect() {
        return switch (type) {
            case SPEED_UP -> "Augmente la vitesse !";
            case SLOW_DOWN -> "Ralentit le jeu !";
            case MULTI_GROW -> "Grandit de 3 segments !";
            case BONUS -> "Bonus de points !";
            case SUPER_BONUS -> "SUPER BONUS !";
            default -> "";
        };
    }

    /**
     * Obtenir le nombre de segments de croissance
     */
    public int getGrowthAmount() {
        return switch (type) {
            case MULTI_GROW -> 3;
            case SUPER_BONUS -> 2;
            default -> 1;
        };
    }

    @Override
    public String toString() {
        return String.format("Food[type=%s, position=%s, value=%d, special=%s]",
                type, position, value, isSpecialFood);
    }
}