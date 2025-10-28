package org.example.snakegame.common;

import java.util.Objects;

/**
 * Utilitaires de validation des paramètres
 * Centralise la validation pour éviter la duplication
 */
public final class ValidationUtils {

    private ValidationUtils() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    /**
     * Valider qu'un objet n'est pas null
     */
    public static <T> T requireNonNull(T obj, String paramName) {
        return Objects.requireNonNull(obj, paramName + " cannot be null");
    }

    /**
     * Valider qu'un nombre est positif
     */
    public static int requirePositive(int value, String paramName) {
        if (value <= 0) {
            throw new IllegalArgumentException(paramName + " must be positive, got: " + value);
        }
        return value;
    }

    /**
     * Valider qu'un nombre est non-négatif
     */
    public static int requireNonNegative(int value, String paramName) {
        if (value < 0) {
            throw new IllegalArgumentException(paramName + " must be non-negative, got: " + value);
        }
        return value;
    }

    /**
     * Valider qu'un nombre est dans une plage
     */
    public static int requireInRange(int value, int min, int max, String paramName) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(
                    String.format("%s must be between %d and %d, got: %d", paramName, min, max, value)
            );
        }
        return value;
    }

    /**
     * Valider qu'un double est dans une plage
     */
    public static double requireInRange(double value, double min, double max, String paramName) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(
                    String.format("%s must be between %.2f and %.2f, got: %.2f", paramName, min, max, value)
            );
        }
        return value;
    }

    /**
     * Valider qu'une chaîne n'est pas vide
     */
    public static String requireNonEmpty(String str, String paramName) {
        requireNonNull(str, paramName);
        if (str.trim().isEmpty()) {
            throw new IllegalArgumentException(paramName + " cannot be empty");
        }
        return str;
    }

    /**
     * Valider qu'un tableau n'est pas vide
     */
    public static <T> T[] requireNonEmpty(T[] array, String paramName) {
        requireNonNull(array, paramName);
        if (array.length == 0) {
            throw new IllegalArgumentException(paramName + " cannot be empty");
        }
        return array;
    }
}
