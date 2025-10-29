package org.example.snakegame;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.example.snakegame.common.GameLogger;

/**
 * Gestionnaire global des scores avec sauvegarde persistante locale
 * Enum Singleton (meilleure pratique Java - thread-safe, protection réflexion/sérialisation)
 */
public enum ScoreManager {
    INSTANCE;

    private final GameLogger logger = GameLogger.getLogger(ScoreManager.class);

    // Nom du fichier de sauvegarde
    private static final String SAVE_FILE_NAME = "retro_arcade_scores.dat";
    private static final String BACKUP_FILE_NAME = "retro_arcade_scores_backup.dat";

    // Scores Snake
    private int snakeHighScore = 0;
    private int snakeTotalScore = 0;
    private int snakeGamesPlayed = 0;
    private int snakeCurrentSessionScore = 0;
    private String snakeHighScoreDate = "";

    // Scores Pong (format wins-losses)
    private int pongPlayerWins = 0;
    private int pongAIWins = 0;
    private int pongGamesPlayed = 0;
    private int pongCurrentSessionWins = 0;
    private String pongLastWinDate = "";

    // Métadonnées
    private String lastPlayedGame = "";
    private String lastSessionDate = "";
    private int totalGamesPlayed = 0;

    // Constructeur de l'enum (appelé automatiquement une seule fois)
    ScoreManager() {
        loadScores(); // Charger les scores au démarrage
    }

    // === MÉTHODES SNAKE ===

    /**
     * Enregistrer un score Snake
     */
    public void recordSnakeScore(int score) {
        snakeTotalScore += score;
        snakeCurrentSessionScore += score;
        snakeGamesPlayed++;
        totalGamesPlayed++;
        lastPlayedGame = "Snake";

        if (score > snakeHighScore) {
            snakeHighScore = score;
            snakeHighScoreDate = getCurrentDateTime();
            logger.game("🏆", "NOUVEAU HIGH SCORE SNAKE : %d !", score);
        }

        // Sauvegarder immédiatement
        saveScores();

        logger.info("Score Snake enregistré: %d | Total: %d", score, snakeTotalScore);
    }

    public int getSnakeHighScore() { return snakeHighScore; }
    public int getSnakeTotalScore() { return snakeTotalScore; }
    public int getSnakeGamesPlayed() { return snakeGamesPlayed; }
    public int getSnakeCurrentSessionScore() { return snakeCurrentSessionScore; }
    public String getSnakeHighScoreDate() { return snakeHighScoreDate; }

    public int getSnakeAverageScore() {
        return snakeGamesPlayed > 0 ? (snakeTotalScore / snakeGamesPlayed) : 0;
    }

    // === MÉTHODES PONG ===

    /**
     * Enregistrer une victoire Pong
     */
    public void recordPongPlayerWin() {
        pongPlayerWins++;
        pongCurrentSessionWins++;
        pongGamesPlayed++;
        totalGamesPlayed++;
        lastPlayedGame = "Pong";
        pongLastWinDate = getCurrentDateTime();

        // Sauvegarder immédiatement
        saveScores();

        logger.game("🏆", "Victoire Pong enregistrée ! Total: %d-%d", pongPlayerWins, pongAIWins);
    }

    /**
     * Enregistrer une défaite Pong
     */
    public void recordPongAIWin() {
        pongAIWins++;
        pongGamesPlayed++;
        totalGamesPlayed++;
        lastPlayedGame = "Pong";

        // Sauvegarder immédiatement
        saveScores();

        logger.info("Défaite Pong enregistrée ! Total: %d-%d", pongPlayerWins, pongAIWins);
    }

    public String getPongScore() { return pongPlayerWins + "-" + pongAIWins; }
    public int getPongPlayerWins() { return pongPlayerWins; }
    public int getPongAIWins() { return pongAIWins; }
    public int getPongGamesPlayed() { return pongGamesPlayed; }
    public int getPongCurrentSessionWins() { return pongCurrentSessionWins; }
    public String getPongLastWinDate() { return pongLastWinDate; }

    public double getPongWinRate() {
        return pongGamesPlayed > 0 ? ((double) pongPlayerWins / pongGamesPlayed * 100) : 0;
    }

    // === MÉTHODES DE PERSISTANCE ===

    /**
     * Obtenir le répertoire de sauvegarde selon l'OS
     */
    private Path getSaveDirectory() {
        String userHome = System.getProperty("user.home");
        String os = System.getProperty("os.name").toLowerCase();

        Path saveDir;
        if (os.contains("win")) {
            // Windows: %APPDATA%/RetroArcade
            saveDir = Paths.get(System.getenv("APPDATA"), "RetroArcade");
        } else if (os.contains("mac")) {
            // macOS: ~/Library/Application Support/RetroArcade
            saveDir = Paths.get(userHome, "Library", "Application Support", "RetroArcade");
        } else {
            // Linux/Unix: ~/.retro-arcade
            saveDir = Paths.get(userHome, ".retro-arcade");
        }

        // Créer le répertoire s'il n'existe pas
        try {
            Files.createDirectories(saveDir);
        } catch (IOException e) {
            logger.error("Erreur création répertoire de sauvegarde: %s", e.getMessage());
            // Fallback: répertoire courant
            saveDir = Paths.get(".");
        }

        return saveDir;
    }

    /**
     * Sauvegarder les scores dans un fichier local
     */
    private void saveScores() {
        try {
            Path saveDir = getSaveDirectory();
            Path saveFile = saveDir.resolve(SAVE_FILE_NAME);
            Path backupFile = saveDir.resolve(BACKUP_FILE_NAME);

            // Créer une sauvegarde de l'ancien fichier
            if (Files.exists(saveFile)) {
                try {
                    Files.copy(saveFile, backupFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    logger.error("Erreur création backup: %s", e.getMessage());
                }
            }

            // Créer le contenu de sauvegarde (format simple)
            StringBuilder content = new StringBuilder();
            content.append("# Retro Arcade - Fichier de scores\n");
            content.append("# Généré automatiquement le ").append(getCurrentDateTime()).append("\n");
            content.append("version=1.0\n");
            content.append("lastSessionDate=").append(getCurrentDateTime()).append("\n");
            content.append("lastPlayedGame=").append(lastPlayedGame).append("\n");
            content.append("totalGamesPlayed=").append(totalGamesPlayed).append("\n");
            content.append("\n# Scores Snake\n");
            content.append("snake.highScore=").append(snakeHighScore).append("\n");
            content.append("snake.totalScore=").append(snakeTotalScore).append("\n");
            content.append("snake.gamesPlayed=").append(snakeGamesPlayed).append("\n");
            content.append("snake.highScoreDate=").append(snakeHighScoreDate).append("\n");
            content.append("\n# Scores Pong\n");
            content.append("pong.playerWins=").append(pongPlayerWins).append("\n");
            content.append("pong.aiWins=").append(pongAIWins).append("\n");
            content.append("pong.gamesPlayed=").append(pongGamesPlayed).append("\n");
            content.append("pong.lastWinDate=").append(pongLastWinDate).append("\n");

            // Écrire le fichier
            Files.write(saveFile, content.toString().getBytes());

            logger.debug("💾 Scores sauvegardés dans: %s", saveFile.toAbsolutePath());

        } catch (IOException e) {
            logger.error("❌ Erreur sauvegarde scores: %s", e.getMessage());
        }
    }

    /**
     * Charger les scores depuis le fichier local
     */
    private void loadScores() {
        try {
            Path saveDir = getSaveDirectory();
            Path saveFile = saveDir.resolve(SAVE_FILE_NAME);

            if (!Files.exists(saveFile)) {
                logger.info("📁 Aucun fichier de scores trouvé, démarrage avec scores par défaut");
                initializeDefaultScores();
                return;
            }

            logger.info("📖 Chargement des scores depuis: %s", saveFile.toAbsolutePath());

            // Lire le fichier ligne par ligne
            try (BufferedReader reader = Files.newBufferedReader(saveFile)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    parseLine(line.trim());
                }
            }

            logger.info("✅ Scores chargés avec succès !");
            logger.info("   Snake High Score: %d", snakeHighScore);
            logger.info("   Pong Score: %s", getPongScore());
            logger.info("   Total parties: %d", totalGamesPlayed);

        } catch (IOException e) {
            logger.error("❌ Erreur chargement scores: %s", e.getMessage());

            // Essayer de charger le backup
            tryLoadBackup();
        }
    }

    /**
     * Essayer de charger le fichier de backup
     */
    private void tryLoadBackup() {
        try {
            Path saveDir = getSaveDirectory();
            Path backupFile = saveDir.resolve(BACKUP_FILE_NAME);

            if (Files.exists(backupFile)) {
                logger.info("🔄 Tentative de chargement du backup...");

                try (BufferedReader reader = Files.newBufferedReader(backupFile)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        parseLine(line.trim());
                    }
                }

                logger.info("✅ Backup chargé avec succès !");
                // Sauvegarder immédiatement pour restaurer le fichier principal
                saveScores();

            } else {
                logger.info("❌ Aucun backup trouvé, initialisation par défaut");
                initializeDefaultScores();
            }

        } catch (IOException e) {
            logger.error("❌ Erreur chargement backup: %s", e.getMessage());
            initializeDefaultScores();
        }
    }

    /**
     * Parser une ligne du fichier de sauvegarde
     */
    private void parseLine(String line) {
        if (line.isEmpty() || line.startsWith("#")) {
            return; // Ignorer les commentaires et lignes vides
        }

        String[] parts = line.split("=", 2);
        if (parts.length != 2) {
            return;
        }

        String key = parts[0].trim();
        String value = parts[1].trim();

        try {
            switch (key) {
                case "lastPlayedGame" -> lastPlayedGame = value;
                case "lastSessionDate" -> lastSessionDate = value;
                case "totalGamesPlayed" -> totalGamesPlayed = Integer.parseInt(value);

                // Snake
                case "snake.highScore" -> snakeHighScore = Integer.parseInt(value);
                case "snake.totalScore" -> snakeTotalScore = Integer.parseInt(value);
                case "snake.gamesPlayed" -> snakeGamesPlayed = Integer.parseInt(value);
                case "snake.highScoreDate" -> snakeHighScoreDate = value;

                // Pong
                case "pong.playerWins" -> pongPlayerWins = Integer.parseInt(value);
                case "pong.aiWins" -> pongAIWins = Integer.parseInt(value);
                case "pong.gamesPlayed" -> pongGamesPlayed = Integer.parseInt(value);
                case "pong.lastWinDate" -> pongLastWinDate = value;
            }
        } catch (NumberFormatException e) {
            logger.warn("⚠️ Erreur parsing ligne: %s", line);
        }
    }

    /**
     * Initialiser les scores par défaut
     */
    private void initializeDefaultScores() {
        snakeHighScore = 0;
        snakeTotalScore = 0;
        snakeGamesPlayed = 0;
        snakeCurrentSessionScore = 0;
        snakeHighScoreDate = "";

        pongPlayerWins = 0;
        pongAIWins = 0;
        pongGamesPlayed = 0;
        pongCurrentSessionWins = 0;
        pongLastWinDate = "";

        lastPlayedGame = "";
        lastSessionDate = getCurrentDateTime();
        totalGamesPlayed = 0;

        // Sauvegarder les valeurs par défaut
        saveScores();
    }

    /**
     * Obtenir la date/heure actuelle formatée
     */
    private String getCurrentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    // === MÉTHODES UTILITAIRES ===

    /**
     * Réinitialiser les scores de session (nouveau lancement du jeu)
     */
    public void resetSessionScores() {
        snakeCurrentSessionScore = 0;
        pongCurrentSessionWins = 0;
        lastSessionDate = getCurrentDateTime();

        logger.info("🔄 Scores de session réinitialisés");
    }

    /**
     * Réinitialiser tous les scores (debug/reset complet)
     */
    public void resetAllScores() {
        // Créer un backup avant de tout effacer
        createManualBackup();

        initializeDefaultScores();

        logger.info("🗑️ Tous les scores ont été réinitialisés !");
    }

    /**
     * Créer un backup manuel avec timestamp
     */
    private void createManualBackup() {
        try {
            Path saveDir = getSaveDirectory();
            Path saveFile = saveDir.resolve(SAVE_FILE_NAME);

            if (Files.exists(saveFile)) {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                Path manualBackup = saveDir.resolve("retro_arcade_scores_backup_" + timestamp + ".dat");
                Files.copy(saveFile, manualBackup);
                logger.info("💾 Backup manuel créé: %s", manualBackup.getFileName());
            }
        } catch (IOException e) {
            logger.error("❌ Erreur création backup manuel: %s", e.getMessage());
        }
    }

    /**
     * Exporter les scores vers un fichier lisible
     */
    public void exportScores() {
        try {
            Path saveDir = getSaveDirectory();
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            Path exportFile = saveDir.resolve("retro_arcade_export_" + timestamp + ".txt");

            StringBuilder export = new StringBuilder();
            export.append("=== RETRO ARCADE - EXPORT DES SCORES ===\n");
            export.append("Date d'export: ").append(getCurrentDateTime()).append("\n\n");

            export.append("🐍 SNAKE:\n");
            export.append("  High Score: ").append(snakeHighScore);
            if (!snakeHighScoreDate.isEmpty()) {
                export.append(" (").append(snakeHighScoreDate).append(")");
            }
            export.append("\n");
            export.append("  Score Total: ").append(snakeTotalScore).append("\n");
            export.append("  Parties jouées: ").append(snakeGamesPlayed).append("\n");
            export.append("  Moyenne: ").append(getSnakeAverageScore()).append("\n\n");

            export.append("🏓 PONG:\n");
            export.append("  Score: ").append(getPongScore()).append("\n");
            export.append("  Parties jouées: ").append(pongGamesPlayed).append("\n");
            export.append("  Taux de victoire: ").append(String.format("%.1f%%", getPongWinRate())).append("\n");
            if (!pongLastWinDate.isEmpty()) {
                export.append("  Dernière victoire: ").append(pongLastWinDate).append("\n");
            }
            export.append("\n");

            export.append("📊 GLOBAL:\n");
            export.append("  Total parties: ").append(totalGamesPlayed).append("\n");
            export.append("  Dernier jeu: ").append(lastPlayedGame).append("\n");
            export.append("  Session courante: Snake +").append(snakeCurrentSessionScore)
                    .append(", Pong +").append(pongCurrentSessionWins).append("\n");

            Files.write(exportFile, export.toString().getBytes());
            logger.info("📄 Scores exportés vers: %s", exportFile.toAbsolutePath());

        } catch (IOException e) {
            logger.error("❌ Erreur export scores: %s", e.getMessage());
        }
    }

    /**
     * Obtenir un résumé des scores pour le debug
     */
    public String getScoreSummary() {
        return String.format(
                "=== SCORES GLOBAUX ===\n" +
                        "Snake: High=%d, Total=%d, Parties=%d, Moyenne=%d, Session=%d\n" +
                        "Pong: %s, Parties=%d, Winrate=%.1f%%, Session=%d victoires\n" +
                        "Global: %d parties jouées, dernier jeu: %s",
                snakeHighScore, snakeTotalScore, snakeGamesPlayed, getSnakeAverageScore(), snakeCurrentSessionScore,
                getPongScore(), pongGamesPlayed, getPongWinRate(), pongCurrentSessionWins,
                totalGamesPlayed, lastPlayedGame
        );
    }

    /**
     * Forcer la sauvegarde (utile pour l'arrêt du programme)
     */
    public void forceSave() {
        saveScores();
        logger.info("💾 Sauvegarde forcée des scores");
    }

    // === GETTERS SUPPLÉMENTAIRES ===

    public String getLastPlayedGame() { return lastPlayedGame; }
    public String getLastSessionDate() { return lastSessionDate; }
    public int getTotalGamesPlayed() { return totalGamesPlayed; }
    public Path getSaveFilePath() { return getSaveDirectory().resolve(SAVE_FILE_NAME); }
}