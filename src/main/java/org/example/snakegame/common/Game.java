package org.example.snakegame.common;

import javafx.stage.Stage;

/**
 * Interface commune pour tous les jeux
 * Respecte le principe OCP (Open/Closed Principle)
 * 
 * Permet d'ajouter de nouveaux jeux sans modifier GameController
 * 
 * Exemple d'utilisation:
 * <pre>
 * public class TetrisGame implements Game {
 *     @Override
 *     public void start(Stage primaryStage) throws Exception {
 *         // Lancer Tetris
 *     }
 *     
 *     @Override
 *     public String getName() {
 *         return "Tetris";
 *     }
 * }
 * </pre>
 */
public interface Game {
    
    /**
     * Démarrer le jeu
     * @param primaryStage Le stage principal de l'application
     * @throws Exception Si une erreur survient lors du démarrage
     */
    void start(Stage primaryStage) throws Exception;
    
    /**
     * Obtenir le nom du jeu
     * @return Le nom du jeu (ex: "Snake", "Pong", "Tetris")
     */
    String getName();
    
    /**
     * Obtenir une description courte du jeu (optionnel)
     * @return Description du jeu
     */
    default String getDescription() {
        return "Jeu " + getName();
    }
    
    /**
     * Vérifier si le jeu est disponible (optionnel)
     * Permet de désactiver temporairement un jeu
     * @return true si le jeu est disponible, false sinon
     */
    default boolean isAvailable() {
        return true;
    }
}
