package org.example.snakegame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.snakegame.common.GameLogger;

import java.io.IOException;

/**
 * Application principale - Menu de sélection des jeux
 * Version avec Splash Screen et sauvegarde automatique des scores
 */
public class GameApplication extends Application {

    private static final int CANVAS_WIDTH = 800;
    // Calculer la hauteur en fonction de l'écran (80% de la hauteur disponible)
    private static final double SCREEN_HEIGHT_RATIO = 1;
    private static int CANVAS_HEIGHT;

    private static Stage primaryStage;
    private ScoreManager scoreManager;
    private MusicController musicController;
    private SplashScreen splashScreen;

    // Flag pour savoir si c'est le premier démarrage
    private static boolean isFirstLaunch = true;
    private final GameLogger logger = GameLogger.getLogger(GameApplication.class);

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        // Calculer la hauteur en fonction de l'écran
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        CANVAS_HEIGHT = (int) (screenBounds.getHeight() * SCREEN_HEIGHT_RATIO);
        
        // Initialiser les gestionnaires
        scoreManager = ScoreManager.INSTANCE;
        musicController = MusicController.INSTANCE;

        logger.info("🎮 Retro Arcade - Démarrage...");
        logger.info("📺 Résolution écran: %.0fx%.0f, Fenêtre: %dx%d", 
                screenBounds.getWidth(), screenBounds.getHeight(), CANVAS_WIDTH, CANVAS_HEIGHT);

        if (isFirstLaunch) {
            // Premier démarrage : afficher le splash screen
            isFirstLaunch = false;
            // Initialiser le contrôleur musical
            if (!musicController.isInitialized()) {
                musicController.initialize();
            }
            musicController.playMenuMusic();
            // Créer et afficher le splash screen
            splashScreen = new SplashScreen();
            splashScreen.show(() -> {
                // Cette fonction sera appelée quand le chargement est terminé
                try {
                    initializeMainApplication(stage);
                } catch (IOException e) {
                    logger.error("❌ Erreur lors du chargement du menu principal: %s", e.getMessage());
                }
            });
        } else {
            // Retour au menu : charger directement le menu principal
            initializeMainApplication(stage);
        }
    }

    /**
     * Initialiser l'application principale après le splash screen
     */
    private void initializeMainApplication(Stage stage) throws IOException {
        logger.info("🎮 Retro Arcade démarré !");
        logger.info("📁 Fichier de scores: %s", scoreManager.getSaveFilePath().toAbsolutePath());

        // Charger l'interface FXML du menu
        FXMLLoader fxmlLoader = new FXMLLoader(
                GameApplication.class.getResource("/org/example/snakegame/views/game-view-custom-titlebar.fxml"));

        // Créer la scène (800x600 pour un menu confortable)
        Scene menuScene = new Scene(fxmlLoader.load(), CANVAS_WIDTH, CANVAS_HEIGHT);

        // Charger tous les styles CSS rétro
        menuScene.getStylesheets().addAll(
                getClass().getResource("/org/example/snakegame/styles/styles.css").toExternalForm(),
                getClass().getResource("/org/example/snakegame/styles/menu-styles.css").toExternalForm());

        // Configuration de la fenêtre
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("🕹️ RETRO ARCADE - Snake & Pong");
        stage.setScene(menuScene);
        stage.setResizable(false); // Taille fixe pour un aspect rétro
        stage.centerOnScreen();

        // Gestionnaire de fermeture pour sauvegarder les scores
        stage.setOnCloseRequest(event -> {
            logger.info("🔄 Fermeture de l'application...");

            // Nettoyer l'audio
            musicController.cleanup();

            // Forcer la sauvegarde des scores
            scoreManager.forceSave();

            // Afficher un résumé final
            logger.info("%s", scoreManager.getScoreSummary());
            logger.info("👋 À bientôt dans Retro Arcade !");
        });

        // Afficher la fenêtre principale
        stage.show();

        // Afficher les scores actuels au démarrage
        logger.info("📊 Scores chargés:");
        logger.info("%s", scoreManager.getScoreSummary());
        logger.info("%s", musicController.getAudioStatus());
    }

    /**
     * NOUVELLE MÉTHODE: Retourner au menu principal sans splash screen
     */
    public static void returnToMainMenu() {
        try {
            // Charger directement le menu principal sans splash
            FXMLLoader fxmlLoader = new FXMLLoader(
                    GameApplication.class.getResource("/org/example/snakegame/views/game-view-custom-titlebar.fxml"));

            // Créer la nouvelle scène du menu
            Scene menuScene = new Scene(fxmlLoader.load(), CANVAS_WIDTH, CANVAS_HEIGHT);

            // Charger les styles CSS
            menuScene.getStylesheets().addAll(
                    GameApplication.class.getResource("/org/example/snakegame/styles/styles.css").toExternalForm(),
                    GameApplication.class.getResource("/org/example/snakegame/styles/menu-styles.css")
                            .toExternalForm());

            // Changer la scène du stage principal
            if (primaryStage != null) {
                primaryStage.setTitle("🕹️ RETRO ARCADE - Snake & Pong");
                primaryStage.setScene(menuScene);
                primaryStage.setResizable(false); // Taille fixe pour un aspect rétro
                primaryStage.centerOnScreen();

                GameLogger logger = GameLogger.getLogger(GameApplication.class);
                logger.info("\ud83d\udd19 Retour au menu principal (sans splash)");
            }

        } catch (IOException e) {
            GameLogger logger = GameLogger.getLogger(GameApplication.class);
            logger.error("\u274c Erreur lors du retour au menu: %s", e.getMessage());
        }
    }

    /**
     * Méthode pour accéder au stage principal depuis d'autres classes
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Méthode pour obtenir la hauteur calculée de la fenêtre
     */
    public static int getCanvasHeight() {
        return CANVAS_HEIGHT;
    }

    /**
     * Méthode pour obtenir la largeur de la fenêtre
     */
    public static int getCanvasWidth() {
        return CANVAS_WIDTH;
    }

    /**
     * Méthode pour changer de scène (menu -> jeu -> menu)
     */
    public static void setScene(Scene scene) {
        if (primaryStage != null) {
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
        }
    }

    /**
     * Méthode pour sauvegarder les scores depuis l'extérieur
     */
    public static void saveScores() {
        ScoreManager.INSTANCE.forceSave();
    }

    /**
     * Méthode pour exporter les scores
     */
    public static void exportScores() {
        ScoreManager.INSTANCE.exportScores();
    }

    /**
     * Point d'entrée du programme
     */
    public static void main(String[] args) {
        // Hook pour sauvegarder à l'arrêt brutal du programme
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            GameLogger logger = GameLogger.getLogger(GameApplication.class);
            logger.info("🛑 Arrêt d'urgence détecté, sauvegarde des scores...");
            ScoreManager.INSTANCE.forceSave();

            // Nettoyer l'audio si possible
            try {
                MusicController.INSTANCE.cleanup();
            } catch (Exception e) {
                // Ignorer les erreurs de nettoyage au shutdown
            }
        }));

        launch();
    }
}