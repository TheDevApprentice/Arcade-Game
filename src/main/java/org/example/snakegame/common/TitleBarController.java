package org.example.snakegame.common;

import javafx.application.Platform;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * Contrôleur dédié à la gestion de la barre de titre personnalisée
 * Respecte le principe SRP (Single Responsibility Principle)
 * 
 * Responsabilités:
 * - Gestion du drag & drop de la fenêtre
 * - Actions minimiser/fermer
 * - Effets visuels de la barre de titre
 */
public class TitleBarController {
    
    private final GameLogger logger = GameLogger.getLogger(TitleBarController.class);
    private final Stage stage;
    private final HBox titleBar;
    
    // Variables pour le drag & drop
    private double xOffset = 0;
    private double yOffset = 0;
    
    // Callback pour la fermeture (permet au parent de faire du cleanup)
    private Runnable onCloseCallback;
    
    /**
     * Constructeur
     * @param stage Le stage principal de l'application
     * @param titleBar L'élément HBox de la barre de titre
     */
    public TitleBarController(Stage stage, HBox titleBar) {
        this.stage = ValidationUtils.requireNonNull(stage, "stage");
        this.titleBar = ValidationUtils.requireNonNull(titleBar, "titleBar");
        
        setupDragAndDrop();
        setupCursorEffects();
        
        logger.debug("✅ TitleBarController initialisé");
    }
    
    /**
     * Définir le callback appelé lors de la fermeture
     * @param callback Action à exécuter avant de fermer l'application
     */
    public void setOnCloseCallback(Runnable callback) {
        this.onCloseCallback = callback;
    }
    
    /**
     * Configurer le drag & drop de la fenêtre
     */
    private void setupDragAndDrop() {
        // Gérer le début du drag
        titleBar.setOnMousePressed((MouseEvent event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        
        // Gérer le déplacement de la fenêtre
        titleBar.setOnMouseDragged((MouseEvent event) -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }
    
    /**
     * Configurer les effets de curseur
     */
    private void setupCursorEffects() {
        titleBar.setOnMouseEntered(e -> 
            titleBar.setStyle(titleBar.getStyle() + "-fx-cursor: move;")
        );
        
        titleBar.setOnMouseExited(e -> 
            titleBar.setStyle(titleBar.getStyle().replace("-fx-cursor: move;", ""))
        );
    }
    
    /**
     * Minimiser la fenêtre
     */
    public void minimize() {
        stage.setIconified(true);
        logger.debug("📦 Fenêtre minimisée");
    }
    
    /**
     * Fermer l'application proprement
     */
    public void close() {
        logger.info("❌ Fermeture de l'application via barre de titre...");
        
        // Exécuter le callback de fermeture si défini
        if (onCloseCallback != null) {
            try {
                onCloseCallback.run();
            } catch (Exception e) {
                logger.error("Erreur lors du callback de fermeture: %s", e.getMessage());
            }
        }
        
        // Fermer l'application proprement
        Platform.exit();
        System.exit(0);
    }
    
    /**
     * Obtenir le stage géré
     * @return Le stage principal
     */
    public Stage getStage() {
        return stage;
    }
    
    /**
     * Obtenir la barre de titre
     * @return L'élément HBox de la barre de titre
     */
    public HBox getTitleBar() {
        return titleBar;
    }
}
