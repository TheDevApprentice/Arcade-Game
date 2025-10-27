package org.example.snakegame.common;

/**
 * Énumération des directions de mouvement
 * Utilisée par Snake pour les déplacements et par Pong pour la balle
 */
public enum Direction {
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0);

    private final int deltaX;
    private final int deltaY;

    /**
     * Constructeur de Direction
     * @param deltaX Déplacement en X
     * @param deltaY Déplacement en Y
     */
    Direction(int deltaX, int deltaY) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }

    /**
     * Obtenir le déplacement en X
     */
    public int getDeltaX() {
        return deltaX;
    }

    /**
     * Obtenir le déplacement en Y
     */
    public int getDeltaY() {
        return deltaY;
    }

    /**
     * Obtenir la direction opposée
     */
    public Direction getOpposite() {
        return switch (this) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }

    /**
     * Vérifier si une direction est opposée à celle-ci
     */
    public boolean isOpposite(Direction other) {
        return this.getOpposite() == other;
    }

    /**
     * Vérifier si c'est une direction horizontale
     */
    public boolean isHorizontal() {
        return this == LEFT || this == RIGHT;
    }

    /**
     * Vérifier si c'est une direction verticale
     */
    public boolean isVertical() {
        return this == UP || this == DOWN;
    }
}