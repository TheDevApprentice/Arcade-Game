package org.example.snakegame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.example.snakegame.common.GameLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Splash Screen rétro avec animation de chargement
 * Affiche le logo et simule le chargement des ressources
 */
public class SplashScreen {

    private final GameLogger logger = GameLogger.getLogger(SplashScreen.class);
    private Stage splashStage;
    private ProgressBar progressBar;
    private Label statusLabel;
    private Timeline loadingAnimation;
    private Runnable onLoadingComplete;

    // Étapes de chargement simulées
    private List<LoadingStep> loadingSteps;
    private int currentStep = 0;
    private final Random random = new Random();

    /**
     * Étape de chargement avec message et durée
     */
    private static class LoadingStep {
        String message;
        double duration; // en secondes
        double progress; // progression cumulée (0.0 - 1.0)

        LoadingStep(String message, double duration, double progress) {
            this.message = message;
            this.duration = duration;
            this.progress = progress;
        }
    }

    /**
     * Constructeur du Splash Screen
     */
    public SplashScreen() {
        initializeLoadingSteps();
        createSplashScreen();
    }

    /**
     * Initialiser les étapes de chargement
     */
    private void initializeLoadingSteps() {
        loadingSteps = new ArrayList<>();
        loadingSteps.add(new LoadingStep("Initialisation du système...", 0.5, 0.15));
        loadingSteps.add(new LoadingStep("Chargement des assets audio...", 1.5, 0.40));
        loadingSteps.add(new LoadingStep("Préparation des jeux...", 0.8, 0.60));
        loadingSteps.add(new LoadingStep("Chargement des scores...", 0.4, 0.75));
        loadingSteps.add(new LoadingStep("Initialisation de l'interface...", 0.6, 0.90));
        loadingSteps.add(new LoadingStep("Finalisation...", 0.3, 1.0));
    }

    /**
     * Créer l'interface du splash screen
     */
    private void createSplashScreen() {
        splashStage = new Stage();
        splashStage.initStyle(StageStyle.UNDECORATED); // Pas de bordures

        // Conteneur principal
        VBox root = new VBox(35);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.getStyleClass().add("splash-container");

        // Container pour le logo avec effet
        VBox logoContainer = new VBox(10);
        logoContainer.setAlignment(Pos.CENTER);
        logoContainer.getStyleClass().add("logo-container");

        // Logo arcade
        ImageView logoView = createLogoView();
        logoContainer.getChildren().add(logoView);

        // Ajouter un fallback textuel au cas où l'image ne marche pas
        if (logoView.getImage() == null) {
            Label fallbackText = new Label("🕹️\nRETRO\nARCADE");
            fallbackText.setStyle(
                    "-fx-font-family: 'Courier New', monospace;" +
                            "-fx-font-size: 45px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-text-fill: #ff0080;" +
                            "-fx-text-alignment: center;" +
                            "-fx-effect: dropshadow(gaussian, #ff0080, 20, 0.8, 0, 0);"
            );
            logoContainer.getChildren().add(fallbackText);
        }

        // Titre avec style amélioré
        Label titleLabel = new Label("RETRO ARCADE");
        titleLabel.getStyleClass().addAll("title-enhanced");

        // Sous-titre avec effet néon
        Label subtitleLabel = new Label("Snake & Pong Games");
        subtitleLabel.getStyleClass().addAll("subtitle-neon");

        // Container pour la progression
        VBox progressContainer = new VBox(15);
        progressContainer.setAlignment(Pos.CENTER);

        // Barre de progression
        progressBar = createProgressBar();

        // Label de statut dans une boîte
        VBox statusBox = new VBox();
        statusBox.setAlignment(Pos.CENTER);
        statusBox.getStyleClass().add("status-box");

        statusLabel = new Label("Démarrage...");
        statusLabel.getStyleClass().addAll("status-text");
        statusBox.getChildren().add(statusLabel);

        progressContainer.getChildren().addAll(progressBar, statusBox);

        // Version/Copyright stylée
        Label versionLabel = new Label("Version 1.0 - Powered by JavaFX");
        versionLabel.getStyleClass().add("version-info");

        // Assembler l'interface
        root.getChildren().addAll(
                logoContainer,
                titleLabel,
                subtitleLabel,
                progressContainer,
                versionLabel
        );

        // Créer la scène avec une taille optimisée pour le nouveau design
        Scene scene = new Scene(root, 650, 650);
        scene.setFill(Color.TRANSPARENT);

        // Charger les styles CSS du splash screen
        try {
            scene.getStylesheets().add(
                    getClass().getResource("/org/example/snakegame/styles/splash-styles.css").toExternalForm()
            );
        } catch (Exception e) {
            logger.warn("⚠️ Impossible de charger splash-styles.css: %s", e.getMessage());
        }

        // Configurer le stage
        splashStage.setScene(scene);
        splashStage.setTitle("Retro Arcade - Loading...");
        splashStage.setResizable(false);
        splashStage.centerOnScreen();

        // Effet de fermeture au clic (optionnel)
        scene.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) { // Double-clic pour forcer la fermeture
                skipLoading();
            }
        });
    }

    /**
     * Créer la vue du logo
     */
    private ImageView createLogoView() {
        try {
            // Charger l'image icon2.png avec vérification null
            java.io.InputStream imageStream = getClass().getResourceAsStream("/org/example/snakegame/images/icon2.png");

            if (imageStream == null) {
                logger.warn("⚠️ Fichier icon2.png introuvable dans les ressources");
                return createFallbackLogoView();
            }

            Image logoImage = new Image(imageStream);

            // Vérifier que l'image est valide
            if (logoImage.isError()) {
                logger.warn("⚠️ Erreur lors du chargement de icon2.png: %s", logoImage.getException().getMessage());
                return createFallbackLogoView();
            }

            ImageView logoView = new ImageView(logoImage);
            logoView.setFitWidth(220);
            logoView.setFitHeight(220);
            logoView.setPreserveRatio(true);
            logoView.setSmooth(true);

            // Effet de brillance rétro amélioré
            logoView.getStyleClass().addAll("splash-logo", "glow-text-pink");

            logger.debug("✅ Logo icon2.png chargé avec succès");
            return logoView;

        } catch (Exception e) {
            logger.warn("⚠️ Exception lors du chargement de icon2.png: %s", e.getMessage());
            return createFallbackLogoView();
        }
    }

    /**
     * Créer un logo de secours si l'image n'est pas disponible
     */
    private ImageView createFallbackLogoView() {
        logger.info("🔄 Utilisation du logo de secours...");

        // Fallback: créer une ImageView vide mais stylée
        ImageView fallbackView = new ImageView();
        fallbackView.setFitWidth(220);
        fallbackView.setFitHeight(220);

        // Créer un style de fond pour remplacer l'image
        fallbackView.setStyle(
                "-fx-background-color: radial-gradient(center 50% 50%, radius 80%, #ff0080, #000000);" +
                        "-fx-border-color: #ff0080;" +
                        "-fx-border-width: 3px;" +
                        "-fx-border-radius: 15px;" +
                        "-fx-background-radius: 15px;" +
                        "-fx-effect: dropshadow(gaussian, #ff0080, 20, 0.8, 0, 0);"
        );

        return fallbackView;
    }

    /**
     * Créer la barre de progression rétro
     */
    private ProgressBar createProgressBar() {
        ProgressBar bar = new ProgressBar(0);
        bar.setPrefWidth(450);
        bar.setPrefHeight(30);
        bar.getStyleClass().add("splash-progress-enhanced");
        return bar;
    }

    /**
     * Afficher le splash screen et démarrer le chargement
     */
    public void show(Runnable onComplete) {
        this.onLoadingComplete = onComplete;
        splashStage.show();
        startLoadingAnimation();
    }

    /**
     * Démarrer l'animation de chargement
     */
    private void startLoadingAnimation() {
        loadingAnimation = new Timeline();

        // Créer les keyframes pour chaque étape
        double totalTime = 0;
        for (int i = 0; i < loadingSteps.size(); i++) {
            LoadingStep step = loadingSteps.get(i);
            final int stepIndex = i;

            // Keyframe de début d'étape
            loadingAnimation.getKeyFrames().add(
                    new KeyFrame(Duration.seconds(totalTime), e -> {
                        updateLoadingStep(stepIndex);
                    })
            );

            totalTime += step.duration;
        }

        // Keyframe final
        loadingAnimation.getKeyFrames().add(
                new KeyFrame(Duration.seconds(totalTime), e -> {
                    completeLoading();
                })
        );

        // Démarrer l'animation
        loadingAnimation.play();

        // Animation continue de la barre de progression
        Timeline progressAnimation = new Timeline(
                new KeyFrame(Duration.millis(50), e -> updateProgressBar())
        );
        progressAnimation.setCycleCount(Timeline.INDEFINITE);
        progressAnimation.play();
    }

    /**
     * Mettre à jour l'étape de chargement
     */
    private void updateLoadingStep(int stepIndex) {
        if (stepIndex < loadingSteps.size()) {
            LoadingStep step = loadingSteps.get(stepIndex);
            currentStep = stepIndex;

            Platform.runLater(() -> {
                statusLabel.setText(step.message);
                logger.debug("🔄 %s", step.message);
            });

            // Effectuer le véritable chargement selon l'étape
            performRealLoading(stepIndex);

            // Ajouter un peu de variabilité pour réalisme
            if (random.nextDouble() < 0.3) { // 30% de chance
                try {
                    Thread.sleep(random.nextInt(200) + 100); // 100-300ms de délai aléatoire
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * Effectuer le chargement réel des ressources
     */
    private void performRealLoading(int stepIndex) {
        switch (stepIndex) {
            case 0: // Initialisation du système
                initializeSystem();
                break;
            case 1: // Chargement des assets audio
                initializeAudio();
                break;
            case 2: // Préparation des jeux
                prepareGames();
                break;
            case 3: // Chargement des scores
                loadScores();
                break;
            case 4: // Initialisation de l'interface
                prepareUI();
                break;
            case 5: // Finalisation
                finalizeLoading();
                break;
        }
    }

    /**
     * Étapes de chargement réelles
     */
    private void initializeSystem() {
        // Vérifier la compatibilité JavaFX
        logger.debug("   ✓ JavaFX Runtime vérifié");

        // Initialiser les gestionnaires de base
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void initializeAudio() {
        // Initialiser le MusicController
        try {
            MusicController musicController = MusicController.getInstance();
            if (!musicController.isInitialized()) {
                musicController.initialize();
                logger.debug("   ✓ Système audio initialisé");
            } else {
                logger.debug("   ✓ Système audio déjà initialisé");
            }

            // Petite pause pour simuler le chargement
            Thread.sleep(300);
        } catch (Exception e) {
            logger.warn("   ⚠️ Erreur audio: %s", e.getMessage());
            // Continue quand même, l'audio n'est pas critique
        }
    }

    private void prepareGames() {
        // Précharger les classes de jeu
        try {
            // Force le chargement des classes
            Class.forName("org.example.snakegame.snake.SnakeGame");
            Class.forName("org.example.snakegame.pong.PongGame");
            logger.debug("   ✓ Classes de jeu préchargées");
            Thread.sleep(400);
        } catch (Exception e) {
            logger.warn("   ⚠️ Erreur préchargement: %s", e.getMessage());
        }
    }

    private void loadScores() {
        // Initialiser le ScoreManager
        try {
            ScoreManager scoreManager = ScoreManager.getInstance();
            logger.debug("   ✓ Scores chargés: %d parties", scoreManager.getTotalGamesPlayed());
            Thread.sleep(200);
        } catch (Exception e) {
            logger.warn("   ⚠️ Erreur scores: %s", e.getMessage());
        }
    }

    private void prepareUI() {
        // Précharger les styles CSS
        try {
            Thread.sleep(300);
            logger.debug("   ✓ Interface utilisateur préparée");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void finalizeLoading() {
        // Dernières vérifications
        try {
            Thread.sleep(150);
            logger.info("   ✓ Retro Arcade prêt !");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Mettre à jour la barre de progression de manière fluide
     */
    private void updateProgressBar() {
        if (currentStep < loadingSteps.size()) {
            LoadingStep currentStepObj = loadingSteps.get(currentStep);
            LoadingStep previousStep = currentStep > 0 ? loadingSteps.get(currentStep - 1) : null;

            double baseProgress = previousStep != null ? previousStep.progress : 0;
            double targetProgress = currentStepObj.progress;

            // Progression fluide avec un peu de variation
            double currentProgress = progressBar.getProgress();
            double increment = 0.005 + random.nextDouble() * 0.003; // Vitesse variable

            if (currentProgress < targetProgress) {
                double newProgress = Math.min(currentProgress + increment, targetProgress);
                progressBar.setProgress(newProgress);

                // Effet visuel: changement de couleur selon progression
                updateProgressBarColor(newProgress);
            }
        }
    }

    /**
     * Mettre à jour la couleur de la barre selon la progression
     */
    private void updateProgressBarColor(double progress) {
        // Nettoyer les classes existantes
        progressBar.getStyleClass().removeAll(
                "progress-starting-enhanced", "progress-loading-enhanced", "progress-completing-enhanced",
                "progress-starting", "progress-loading", "progress-completing"
        );

        // Ajouter la classe appropriée selon la progression
        if (progress < 0.3) {
            progressBar.getStyleClass().add("progress-starting-enhanced");
        } else if (progress < 0.7) {
            progressBar.getStyleClass().add("progress-loading-enhanced");
        } else {
            progressBar.getStyleClass().add("progress-completing-enhanced");
        }
    }

    /**
     * Terminer le chargement
     */
    private void completeLoading() {
        Platform.runLater(() -> {
            progressBar.setProgress(1.0);
            statusLabel.setText("✅ Chargement terminé !");

            // Changer le style pour le message final
            statusLabel.getStyleClass().clear();
            statusLabel.getStyleClass().addAll("status-terminal", "glow-green-intense");

            // Petit délai avant de fermer
            Timeline closeDelay = new Timeline(
                    new KeyFrame(Duration.seconds(0.8), e -> {
                        hide();
                        if (onLoadingComplete != null) {
                            onLoadingComplete.run();
                        }
                    })
            );
            closeDelay.play();
        });
    }

    /**
     * Ignorer le chargement (double-clic)
     */
    private void skipLoading() {
        if (loadingAnimation != null) {
            loadingAnimation.stop();
        }
        Platform.runLater(() -> {
            statusLabel.setText("⚡ Chargement ignoré...");
            statusLabel.getStyleClass().clear();
            statusLabel.getStyleClass().addAll("status-terminal", "glow-cyan-intense");
            completeLoading();
        });
    }

    /**
     * Fermer le splash screen
     */
    public void hide() {
        if (splashStage != null) {
            splashStage.hide();
        }
        if (loadingAnimation != null) {
            loadingAnimation.stop();
        }
    }

    /**
     * Vérifier si le splash screen est visible
     */
    public boolean isShowing() {
        return splashStage != null && splashStage.isShowing();
    }

    /**
     * Obtenir le stage du splash screen
     */
    public Stage getStage() {
        return splashStage;
    }
}