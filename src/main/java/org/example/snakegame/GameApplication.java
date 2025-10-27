package org.example.snakegame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * Application principale - Menu de sélection des jeux
 * Version avec Splash Screen et sauvegarde automatique des scores
 */
public class GameApplication extends Application {

    private static final int CANVAS_WIDTH = 800;
    private static final int CANVAS_HEIGHT = 780;

    private static Stage primaryStage;
    private ScoreManager scoreManager;
    private MusicController musicController;
    private SplashScreen splashScreen;

    // Flag pour savoir si c'est le premier démarrage
    private static boolean isFirstLaunch = true;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        // Initialiser les gestionnaires
        scoreManager = ScoreManager.getInstance();
        musicController = MusicController.getInstance();

        System.out.println("🎮 Retro Arcade - Démarrage...");

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
                    System.err.println("❌ Erreur lors du chargement du menu principal: " + e.getMessage());
                    e.printStackTrace();
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
        System.out.println("🎮 Retro Arcade démarré !");
        System.out.println("📁 Fichier de scores: " + scoreManager.getSaveFilePath().toAbsolutePath());

        // Charger l'interface FXML du menu
        FXMLLoader fxmlLoader = new FXMLLoader(
                GameApplication.class.getResource("/org/example/snakegame/views/game-view-custom-titlebar.fxml")
        );

        // Créer la scène (800x600 pour un menu confortable)
        Scene menuScene = new Scene(fxmlLoader.load(), CANVAS_WIDTH, CANVAS_HEIGHT);

        // Charger tous les styles CSS rétro
        menuScene.getStylesheets().addAll(
                getClass().getResource("/org/example/snakegame/styles/styles.css").toExternalForm(),
                getClass().getResource("/org/example/snakegame/styles/menu-styles.css").toExternalForm()
        );

        // Configuration de la fenêtre
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("🕹️ RETRO ARCADE - Snake & Pong");
        stage.setScene(menuScene);
        stage.setResizable(false); // Taille fixe pour un aspect rétro
        stage.centerOnScreen();

        // Gestionnaire de fermeture pour sauvegarder les scores
        stage.setOnCloseRequest(event -> {
            System.out.println("🔄 Fermeture de l'application...");

            // Nettoyer l'audio
            musicController.cleanup();

            // Forcer la sauvegarde des scores
            scoreManager.forceSave();

            // Afficher un résumé final
            System.out.println(scoreManager.getScoreSummary());
            System.out.println("👋 À bientôt dans Retro Arcade !");
        });

        // Afficher la fenêtre principale
        stage.show();

        // Afficher les scores actuels au démarrage
        System.out.println("📊 Scores chargés:");
        System.out.println(scoreManager.getScoreSummary());
        System.out.println(musicController.getAudioStatus());
    }

    /**
     * NOUVELLE MÉTHODE: Retourner au menu principal sans splash screen
     */
    public static void returnToMainMenu() {
        try {
            // Charger directement le menu principal sans splash
            FXMLLoader fxmlLoader = new FXMLLoader(
                    GameApplication.class.getResource("/org/example/snakegame/views/game-view-custom-titlebar.fxml")
            );

            // Créer la nouvelle scène du menu
            Scene menuScene = new Scene(fxmlLoader.load(), CANVAS_WIDTH, CANVAS_HEIGHT);

            // Charger les styles CSS
            menuScene.getStylesheets().addAll(
                    GameApplication.class.getResource("/org/example/snakegame/styles/styles.css").toExternalForm(),
                    GameApplication.class.getResource("/org/example/snakegame/styles/menu-styles.css").toExternalForm()
            );

            // Changer la scène du stage principal
            if (primaryStage != null) {
                primaryStage.setTitle("🕹️ RETRO ARCADE - Snake & Pong");
                primaryStage.setScene(menuScene);
                primaryStage.setResizable(false); // Taille fixe pour un aspect rétro
                primaryStage.centerOnScreen();

                System.out.println("🔙 Retour au menu principal (sans splash)");
            }

        } catch (IOException e) {
            System.err.println("❌ Erreur lors du retour au menu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Méthode pour accéder au stage principal depuis d'autres classes
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
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
        ScoreManager.getInstance().forceSave();
    }

    /**
     * Méthode pour exporter les scores
     */
    public static void exportScores() {
        ScoreManager.getInstance().exportScores();
    }

    /**
     * Point d'entrée du programme
     */
    public static void main(String[] args) {
        // Hook pour sauvegarder à l'arrêt brutal du programme
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("🛑 Arrêt d'urgence détecté, sauvegarde des scores...");
            ScoreManager.getInstance().forceSave();

            // Nettoyer l'audio si possible
            try {
                MusicController.getInstance().cleanup();
            } catch (Exception e) {
                // Ignorer les erreurs de nettoyage au shutdown
            }
        }));

        launch();
    }
}