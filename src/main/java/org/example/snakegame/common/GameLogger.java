package org.example.snakegame.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.*;

/**
 * Système de logging structuré pour Retro Arcade
 * Remplace les System.out.println par un vrai logger
 */
public class GameLogger {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private final Logger logger;
    private final String context;

    // Logger global pour l'application
    private static final Logger ROOT_LOGGER = Logger.getLogger("RetroArcade");

    static {
        // Configuration du logger root
        try {
            ROOT_LOGGER.setUseParentHandlers(false);
            
            // Handler console avec format personnalisé
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.ALL);
            consoleHandler.setFormatter(new GameLogFormatter());
            
            ROOT_LOGGER.addHandler(consoleHandler);
            ROOT_LOGGER.setLevel(Level.INFO);
            
        } catch (Exception e) {
            System.err.println("Erreur initialisation logger: " + e.getMessage());
        }
    }

    /**
     * Constructeur privé
     */
    private GameLogger(String context) {
        this.context = context;
        this.logger = Logger.getLogger("RetroArcade." + context);
    }

    /**
     * Obtenir un logger pour un contexte spécifique
     */
    public static GameLogger getLogger(Class<?> clazz) {
        return new GameLogger(clazz.getSimpleName());
    }

    /**
     * Obtenir un logger pour un contexte nommé
     */
    public static GameLogger getLogger(String context) {
        return new GameLogger(context);
    }

    /**
     * Log niveau DEBUG
     */
    public void debug(String message) {
        logger.fine(message);
    }

    public void debug(String message, Object... args) {
        logger.fine(String.format(message, args));
    }

    /**
     * Log niveau INFO
     */
    public void info(String message) {
        logger.info(message);
    }

    public void info(String message, Object... args) {
        logger.info(String.format(message, args));
    }

    /**
     * Log niveau WARNING
     */
    public void warn(String message) {
        logger.warning(message);
    }

    public void warn(String message, Object... args) {
        logger.warning(String.format(message, args));
    }

    public void warn(String message, Throwable throwable) {
        logger.log(Level.WARNING, message, throwable);
    }

    /**
     * Log niveau ERROR
     */
    public void error(String message) {
        logger.severe(message);
    }

    public void error(String message, Object... args) {
        logger.severe(String.format(message, args));
    }

    public void error(String message, Throwable throwable) {
        logger.log(Level.SEVERE, message, throwable);
    }

    /**
     * Log avec emoji pour les événements de jeu
     */
    public void game(String emoji, String message) {
        logger.info(emoji + " " + message);
    }

    public void game(String emoji, String message, Object... args) {
        logger.info(emoji + " " + String.format(message, args));
    }

    /**
     * Configurer le niveau de log global
     */
    public static void setLogLevel(Level level) {
        ROOT_LOGGER.setLevel(level);
        for (Handler handler : ROOT_LOGGER.getHandlers()) {
            handler.setLevel(level);
        }
    }

    /**
     * Formatter personnalisé pour les logs
     */
    private static class GameLogFormatter extends Formatter {

        @Override
        public String format(LogRecord record) {
            String time = LocalDateTime.now().format(TIME_FORMATTER);
            String level = formatLevel(record.getLevel());
            String loggerName = record.getLoggerName().replace("RetroArcade.", "");
            String message = formatMessage(record);

            // Format: [HH:mm:ss.SSS] [LEVEL] [Context] Message
            StringBuilder sb = new StringBuilder();
            sb.append("[").append(time).append("] ");
            sb.append("[").append(level).append("] ");
            
            if (!loggerName.equals("RetroArcade")) {
                sb.append("[").append(loggerName).append("] ");
            }
            
            sb.append(message);
            sb.append(System.lineSeparator());

            // Ajouter la stack trace si présente
            if (record.getThrown() != null) {
                sb.append(formatThrowable(record.getThrown()));
            }

            return sb.toString();
        }

        private String formatLevel(Level level) {
            if (level == Level.SEVERE) return "ERROR";
            if (level == Level.WARNING) return "WARN ";
            if (level == Level.INFO) return "INFO ";
            if (level == Level.FINE) return "DEBUG";
            return level.getName();
        }

        private String formatThrowable(Throwable throwable) {
            StringBuilder sb = new StringBuilder();
            sb.append("  ").append(throwable.getClass().getName());
            sb.append(": ").append(throwable.getMessage());
            sb.append(System.lineSeparator());
            
            for (StackTraceElement element : throwable.getStackTrace()) {
                sb.append("    at ").append(element.toString());
                sb.append(System.lineSeparator());
            }
            
            return sb.toString();
        }
    }
}
