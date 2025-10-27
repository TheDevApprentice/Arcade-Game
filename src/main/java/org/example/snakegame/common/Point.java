package org.example.snakegame.common;

import java.util.Objects;

/**
 * Classe représentant une position (x, y) sur la grille de jeu
 * Utilisée pour les segments du serpent, la nourriture, et les éléments de Pong
 */
public class Point {
    public final int x;
    public final int y;

    /**
     * Constructeur de Point
     * @param x Coordonnée X
     * @param y Coordonnée Y
     */
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Créer un nouveau point en ajoutant une direction
     * @param direction Direction à ajouter
     * @return Nouveau point déplacé
     */
    public Point move(Direction direction) {
        return new Point(
                this.x + direction.getDeltaX(),
                this.y + direction.getDeltaY()
        );
    }

    /**
     * Créer un nouveau point en ajoutant des coordonnées
     * @param deltaX Déplacement en X
     * @param deltaY Déplacement en Y
     * @return Nouveau point déplacé
     */
    public Point move(int deltaX, int deltaY) {
        return new Point(this.x + deltaX, this.y + deltaY);
    }

    /**
     * Calculer la distance Manhattan entre deux points
     * @param other Autre point
     * @return Distance Manhattan
     */
    public int manhattanDistance(Point other) {
        return Math.abs(this.x - other.x) + Math.abs(this.y - other.y);
    }

    /**
     * Calculer la distance euclidienne entre deux points
     * @param other Autre point
     * @return Distance euclidienne
     */
    public double euclideanDistance(Point other) {
        int dx = this.x - other.x;
        int dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Vérifier si le point est dans les limites d'une zone
     * @param width Largeur de la zone
     * @param height Hauteur de la zone
     * @return true si dans les limites
     */
    public boolean isInBounds(int width, int height) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    /**
     * Créer une copie du point
     * @return Nouvelle instance avec les mêmes coordonnées
     */
    public Point copy() {
        return new Point(this.x, this.y);
    }

    /**
     * Comparer deux points
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Point point = (Point) obj;
        return x == point.x && y == point.y;
    }

    /**
     * Hash code pour utilisation dans les collections
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    /**
     * Représentation textuelle du point
     */
    @Override
    public String toString() {
        return String.format("Point(%d, %d)", x, y);
    }

    /**
     * Créer un point aléatoire dans une zone donnée
     * @param width Largeur maximale
     * @param height Hauteur maximale
     * @return Point aléatoire
     */
    public static Point random(int width, int height) {
        int randomX = (int) (Math.random() * width);
        int randomY = (int) (Math.random() * height);
        return new Point(randomX, randomY);
    }

    /**
     * Point origine (0, 0)
     */
    public static final Point ORIGIN = new Point(0, 0);
}