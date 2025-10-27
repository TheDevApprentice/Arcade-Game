package org.example.snakegame.snake;

import org.example.snakegame.common.Direction;
import org.example.snakegame.common.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe Snake - Représente le serpent du jeu avec toute sa logique
 */
public class Snake {

    private List<Point> body;
    private Direction currentDirection;
    private Direction nextDirection;
    private boolean growing;
    private int growthPending; // Nombre de segments à ajouter

    /**
     * Constructeur du serpent
     */
    public Snake(Point startPosition) {
        body = new ArrayList<>();
        body.add(startPosition);
        currentDirection = Direction.RIGHT;
        nextDirection = Direction.RIGHT;
        growing = false;
        growthPending = 0;
    }

    /**
     * Constructeur avec direction initiale personnalisée
     */
    public Snake(Point startPosition, Direction initialDirection) {
        this(startPosition);
        this.currentDirection = initialDirection;
        this.nextDirection = initialDirection;
    }

    /**
     * Déplacer le serpent d'une case
     */
    public void move() {
        // Mettre à jour la direction
        currentDirection = nextDirection;

        // Calculer la nouvelle position de la tête
        Point head = getHead();
        Point newHead = head.move(currentDirection);

        // Ajouter la nouvelle tête
        body.add(0, newHead);

        // Gérer la croissance
        if (growing || growthPending > 0) {
            if (growing) {
                growing = false;
                growthPending++; // Ajouter à la queue de croissance
            }
            if (growthPending > 0) {
                growthPending--;
            }
        } else {
            // Retirer la queue si pas de croissance
            body.remove(body.size() - 1);
        }
    }

    /**
     * Changer la direction (avec validation anti-demi-tour)
     */
    public boolean setDirection(Direction newDirection) {
        // Empêcher le serpent de faire demi-tour
        if (currentDirection.isOpposite(newDirection)) {
            return false;
        }

        // Empêcher les changements de direction trop rapides
        if (nextDirection.isOpposite(newDirection)) {
            return false;
        }

        nextDirection = newDirection;
        return true;
    }

    /**
     * Faire grandir le serpent
     */
    public void grow() {
        growing = true;
    }

    /**
     * Faire grandir le serpent de plusieurs segments
     */
    public void grow(int segments) {
        growthPending += segments;
    }

    /**
     * Vérifier si le serpent se mange lui-même
     */
    public boolean checkSelfCollision() {
        Point head = getHead();
        // Commencer à partir du segment 1 (ignorer la tête)
        for (int i = 1; i < body.size(); i++) {
            if (head.equals(body.get(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Vérifier collision avec les murs
     */
    public boolean checkWallCollision(int boardWidth, int boardHeight) {
        Point head = getHead();
        return head.x < 0 || head.x >= boardWidth ||
                head.y < 0 || head.y >= boardHeight;
    }

    /**
     * Vérifier si le serpent mange la nourriture
     */
    public boolean isEating(Point foodPosition) {
        return getHead().equals(foodPosition);
    }

    /**
     * Obtenir la tête du serpent
     */
    public Point getHead() {
        return body.get(0);
    }



    /**
     * Obtenir le corps du serpent (copie défensive)
     */
    public List<Point> getBody() {
        return new ArrayList<>(body);
    }



    /**
     * Obtenir la longueur du serpent
     */
    public int getLength() {
        return body.size();
    }

    /**
     * Vérifier si le serpent contient un point
     */
    public boolean contains(Point point) {
        return body.contains(point);
    }

    @Override
    public String toString() {
        return String.format("Snake[length=%d, head=%s, direction=%s]",
                getLength(), getHead(), currentDirection);
    }
}