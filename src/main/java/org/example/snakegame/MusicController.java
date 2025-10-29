package org.example.snakegame;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.example.snakegame.common.GameLogger;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire global de la musique et des effets sonores
 * Singleton qui gère tous les assets audio du jeu Retro Arcade
 * Version refactorisée avec logging structuré
 */
public class MusicController {

    private static MusicController instance;
    private final GameLogger logger;

    // MediaPlayers pour la musique d'ambiance (en boucle)
    private MediaPlayer backgroundMusicPlayer;
    private MediaPlayer currentMusicPlayer;

    // Map pour stocker tous les sons chargés
    private Map<SoundEffect, MediaPlayer> soundEffects;
    private Map<BackgroundMusic, MediaPlayer> backgroundMusics;

    // Configuration audio
    private double masterVolume = 0.7;
    private double musicVolume = 0.5;
    private double sfxVolume = 0.8;
    private boolean isMuted = false;
    private boolean isMusicEnabled = true;
    private boolean areSFXEnabled = true;

    // État actuel
    private BackgroundMusic currentMusic = null;
    private boolean isInitialized = false;

    /**
     * Énumération des musiques d'ambiance
     */
    public enum BackgroundMusic {
        MENU("mixkit-swing-is-the-answer-526.mp3", "Musique du menu principal"),
        GAME_LEVEL("mixkit-game-level-music-689.wav", "Musique de jeu"),
        DISCO_RETRO("mixkit-disco-aint-old-school-935.mp3", "Musique disco rétro");

        private final String filename;
        private final String description;

        BackgroundMusic(String filename, String description) {
            this.filename = filename;
            this.description = description;
        }

        public String getFilename() { return filename; }
        public String getDescription() { return description; }
    }

    /**
     * Énumération des effets sonores
     */
    public enum SoundEffect {
        // Sons généraux
        BONUS_EARNED("mixkit-bonus-earned-in-video-game-2058.wav", "Bonus gagné"),
        LEVEL_COMPLETED("mixkit-completion-of-a-level-2063.wav", "Niveau terminé"),
        EXPERIENCE_GAINED("mixkit-game-experience-level-increased-2062.wav", "Expérience gagnée"),
        TREASURE_FOUND("mixkit-video-game-treasure-2066.wav", "Trésor trouvé"),
        COIN_COLLECTED("mixkit-winning-a-coin-video-game-2069.wav", "Pièce collectée"),
        GAME_OVER("mixkit-player-losing-or-failing-2042.wav", "Game Over"),

        // Sons spécifiques Snake
        SNAKE_EAT("mixkit-game-ball-tap-2073.wav", "Serpent mange"),
        SNAKE_SPECIAL_FOOD("mixkit-bonus-earned-in-video-game-2058.wav", "Nourriture spéciale"),

        // Sons spécifiques Pong
        PONG_BALL_HIT("mixkit-game-ball-tap-2073.wav", "Balle frappe raquette"),
        PONG_WALL_BOUNCE("mixkit-game-ball-tap-2073.wav", "Balle rebondit sur mur"),
        PONG_GOAL("mixkit-winning-a-coin-video-game-2069.wav", "But marqué"),
        PONG_VICTORY("mixkit-completion-of-a-level-2063.wav", "Victoire Pong");

        private final String filename;
        private final String description;

        SoundEffect(String filename, String description) {
            this.filename = filename;
            this.description = description;
        }

        public String getFilename() { return filename; }
        public String getDescription() { return description; }
    }

    /**
     * Constructeur privé pour Singleton
     */
    private MusicController() {
        this.logger = GameLogger.getLogger(MusicController.class);
        soundEffects = new HashMap<>();
        backgroundMusics = new HashMap<>();
    }

    /**
     * Obtenir l'instance unique du MusicController (thread-safe avec double-checked locking)
     */
    public static MusicController getInstance() {
        if (instance == null) {
            synchronized (MusicController.class) {
                if (instance == null) {
                    instance = new MusicController();
                }
            }
        }
        return instance;
    }

    /**
     * Initialiser le contrôleur musical (à appeler au démarrage)
     */
    public void initialize() {
        if (isInitialized) {
            logger.info("🎵 MusicController déjà initialisé");
            return;
        }

        logger.info("🎵 Initialisation du MusicController...");

        try {
            // Charger toutes les musiques d'ambiance
            loadBackgroundMusics();

            // Charger tous les effets sonores
            loadSoundEffects();

            isInitialized = true;
            logger.info("✅ MusicController initialisé avec succès !");
            logger.info("   - %d musiques d'ambiance chargées", backgroundMusics.size());
            logger.info("   - %d effets sonores chargés", soundEffects.size());

        } catch (Exception e) {
            logger.error("❌ Erreur lors de l'initialisation du MusicController: %s", e.getMessage());
        }
    }

    /**
     * Charger les musiques d'ambiance
     */
    private void loadBackgroundMusics() {
        for (BackgroundMusic music : BackgroundMusic.values()) {
            try {
                URL resourcePath = getClass().getResource("/org/example/snakegame/songs/" + music.getFilename());
                if (resourcePath != null) {
                    Media media = new Media(resourcePath.toString());
                    MediaPlayer player = new MediaPlayer(media);

                    // Configuration pour musique d'ambiance
                    player.setCycleCount(MediaPlayer.INDEFINITE);
                    player.setVolume(musicVolume * masterVolume);

                    // Gérer les erreurs de chargement média
                    player.setOnError(() -> {
                        logger.error("❌ Erreur de lecture pour %s: %s", music.getFilename(), player.getError().getMessage());
                    });

                    backgroundMusics.put(music, player);
                    logger.debug("🎼 Musique chargée: %s", music.getDescription());
                } else {
                    logger.warn("⚠️ Fichier musical introuvable: %s", music.getFilename());
                }
            } catch (Exception e) {
                logger.error("❌ Erreur chargement musique %s: %s", music.getFilename(), e.getMessage());
                // Continue avec les autres fichiers même si un échoue
            }
        }
    }

    /**
     * Charger les effets sonores
     */
    private void loadSoundEffects() {
        for (SoundEffect sfx : SoundEffect.values()) {
            try {
                URL resourcePath = getClass().getResource("/org/example/snakegame/songs/" + sfx.getFilename());
                if (resourcePath != null) {
                    Media media = new Media(resourcePath.toString());
                    MediaPlayer player = new MediaPlayer(media);

                    // Configuration pour effets sonores
                    player.setCycleCount(1);
                    player.setVolume(sfxVolume * masterVolume);

                    soundEffects.put(sfx, player);
                    logger.debug("🔊 SFX chargé: %s", sfx.getDescription());
                } else {
                    logger.warn("⚠️ Fichier SFX introuvable: %s", sfx.getFilename());
                }
            } catch (Exception e) {
                logger.error("❌ Erreur chargement SFX %s: %s", sfx.getFilename(), e.getMessage());
            }
        }
    }

    /**
     * Jouer une musique d'ambiance
     */
    public void playBackgroundMusic(BackgroundMusic music) {
        if (!isInitialized || !isMusicEnabled || isMuted) {
            return;
        }

        try {
            // Arrêter la musique actuelle si elle existe
            stopBackgroundMusic();

            // Démarrer la nouvelle musique
            MediaPlayer player = backgroundMusics.get(music);
            if (player != null) {
                // Vérifier que le player est en bon état
                if (player.getError() == null) {
                    player.setVolume(musicVolume * masterVolume);
                    player.play();
                    currentMusicPlayer = player;
                    currentMusic = music;
                    logger.info("🎵 Musique démarrée: %s", music.getDescription());
                } else {
                    logger.error("❌ Impossible de jouer %s - Fichier défectueux", music.getDescription());
                    // Essayer une musique de secours
                    tryFallbackMusic();
                }
            } else {
                logger.error("❌ Musique non trouvée: %s", music);
                // Essayer une musique de secours
                tryFallbackMusic();
            }
        } catch (Exception e) {
            logger.error("❌ Erreur lecture musique: %s", e.getMessage());
            tryFallbackMusic();
        }
    }

    /**
     * Essayer de jouer une musique de secours
     */
    private void tryFallbackMusic() {
        for (BackgroundMusic fallback : BackgroundMusic.values()) {
            MediaPlayer player = backgroundMusics.get(fallback);
            if (player != null && player.getError() == null) {
                try {
                    player.setVolume(musicVolume * masterVolume * 0.8); // Volume plus bas
                    player.play();
                    currentMusicPlayer = player;
                    currentMusic = fallback;
                    logger.info("🎵 Musique de secours: %s", fallback.getDescription());
                    return;
                } catch (Exception e) {
                    // Continue vers le prochain
                }
            }
        }
        logger.error("❌ Aucune musique fonctionnelle trouvée");
    }

    /**
     * Arrêter la musique d'ambiance
     */
    public void stopBackgroundMusic() {
        if (currentMusicPlayer != null) {
            currentMusicPlayer.stop();
            logger.debug("⏹️ Musique arrêtée: %s", (currentMusic != null ? currentMusic.getDescription() : "Inconnue"));
        }
        currentMusicPlayer = null;
        currentMusic = null;
    }

    /**
     * Mettre en pause/reprendre la musique d'ambiance
     */
    public void pauseBackgroundMusic() {
        if (currentMusicPlayer != null) {
            if (currentMusicPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                currentMusicPlayer.pause();
                logger.debug("⏸️ Musique mise en pause");
            } else if (currentMusicPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
                currentMusicPlayer.play();
                logger.debug("▶️ Musique reprise");
            }
        }
    }

    /**
     * Jouer un effet sonore
     */
    public void playSoundEffect(SoundEffect effect) {
        if (!isInitialized || !areSFXEnabled || isMuted) {
            return;
        }

        try {
            MediaPlayer player = soundEffects.get(effect);
            if (player != null) {
                // Arrêter et rembobiner si déjà en cours
                player.stop();
                player.seek(Duration.ZERO);

                // Régler le volume et jouer
                player.setVolume(sfxVolume * masterVolume);
                player.play();

                logger.debug("🔊 SFX joué: %s", effect.getDescription());
            } else {
                logger.error("❌ Effet sonore non trouvé: %s", effect);
            }
        } catch (Exception e) {
            logger.error("❌ Erreur lecture SFX: %s", e.getMessage());
        }
    }

    /**
     * Régler le volume principal (0.0 à 1.0)
     */
    public void setMasterVolume(double volume) {
        this.masterVolume = Math.max(0.0, Math.min(1.0, volume));
        updateAllVolumes();
        logger.info("🔊 Volume principal: %d%%", (int)(masterVolume * 100));
    }

    /**
     * Régler le volume de la musique (0.0 à 1.0)
     */
    public void setMusicVolume(double volume) {
        this.musicVolume = Math.max(0.0, Math.min(1.0, volume));
        updateMusicVolume();
        logger.info("🎵 Volume musique: %d%%", (int)(musicVolume * 100));
    }

    /**
     * Régler le volume des effets sonores (0.0 à 1.0)
     */
    public void setSFXVolume(double volume) {
        this.sfxVolume = Math.max(0.0, Math.min(1.0, volume));
        updateSFXVolume();
        logger.info("🔊 Volume SFX: %d%%", (int)(sfxVolume * 100));
    }

    /**
     * Activer/désactiver le son complètement
     */
    public void setMuted(boolean muted) {
        this.isMuted = muted;
        if (muted) {
            pauseBackgroundMusic();
            logger.info("🔇 Audio désactivé");
        } else {
            if (currentMusic != null && isMusicEnabled) {
                playBackgroundMusic(currentMusic);
            }
            logger.info("🔊 Audio activé");
        }
    }

    /**
     * Activer/désactiver la musique d'ambiance
     */
    public void setMusicEnabled(boolean enabled) {
        this.isMusicEnabled = enabled;
        if (!enabled) {
            stopBackgroundMusic();
            logger.info("🎵 Musique désactivée");
        } else if (!isMuted) {
            // Redémarrer la musique du menu par défaut
            playBackgroundMusic(BackgroundMusic.MENU);
            logger.info("🎵 Musique activée");
        }
    }

    /**
     * Activer/désactiver les effets sonores
     */
    public void setSFXEnabled(boolean enabled) {
        this.areSFXEnabled = enabled;
        logger.info("🔊 Effets sonores %s", (enabled ? "activés" : "désactivés"));
    }

    /**
     * Mettre à jour tous les volumes
     */
    private void updateAllVolumes() {
        updateMusicVolume();
        updateSFXVolume();
    }

    /**
     * Mettre à jour le volume de la musique
     */
    private void updateMusicVolume() {
        if (currentMusicPlayer != null) {
            currentMusicPlayer.setVolume(musicVolume * masterVolume);
        }
    }

    /**
     * Mettre à jour le volume des SFX
     */
    private void updateSFXVolume() {
        for (MediaPlayer player : soundEffects.values()) {
            player.setVolume(sfxVolume * masterVolume);
        }
    }

    /**
     * Nettoyer les ressources (à appeler à la fermeture)
     */
    public void cleanup() {
        logger.info("🧹 Nettoyage du MusicController...");

        // Arrêter et libérer la musique d'ambiance
        stopBackgroundMusic();
        for (MediaPlayer player : backgroundMusics.values()) {
            player.dispose();
        }

        // Libérer les effets sonores
        for (MediaPlayer player : soundEffects.values()) {
            player.dispose();
        }

        backgroundMusics.clear();
        soundEffects.clear();
        isInitialized = false;

        logger.info("✅ MusicController nettoyé");
    }

    // === MÉTHODES DE CONVENANCE POUR LES JEUX ===

    /**
     * Sons pour Snake
     */
    public void playSnakeEat() { playSoundEffect(SoundEffect.SNAKE_EAT); }
    public void playSnakeSpecialFood() { playSoundEffect(SoundEffect.SNAKE_SPECIAL_FOOD); }
    public void playSnakeGameOver() { playSoundEffect(SoundEffect.GAME_OVER); }

    /**
     * Sons pour Pong
     */
    public void playPongBallHit() { playSoundEffect(SoundEffect.PONG_BALL_HIT); }
    public void playPongWallBounce() { playSoundEffect(SoundEffect.PONG_WALL_BOUNCE); }
    public void playPongGoal() { playSoundEffect(SoundEffect.PONG_GOAL); }
    public void playPongVictory() { playSoundEffect(SoundEffect.PONG_VICTORY); }

    /**
     * Sons généraux
     */
    public void playBonusEarned() { playSoundEffect(SoundEffect.BONUS_EARNED); }
    public void playLevelCompleted() { playSoundEffect(SoundEffect.LEVEL_COMPLETED); }
    public void playCoinCollected() { playSoundEffect(SoundEffect.COIN_COLLECTED); }

    /**
     * Musiques par contexte
     */
    public void playMenuMusic() { playBackgroundMusic(BackgroundMusic.MENU); }
    public void playGameMusic() { playBackgroundMusic(BackgroundMusic.GAME_LEVEL); }
    public void playRetroMusic() { playBackgroundMusic(BackgroundMusic.DISCO_RETRO); }

    // === GETTERS ===

    public double getMasterVolume() { return masterVolume; }
    public double getMusicVolume() { return musicVolume; }
    public double getSFXVolume() { return sfxVolume; }
    public boolean isMuted() { return isMuted; }
    public boolean isMusicEnabled() { return isMusicEnabled; }
    public boolean areSFXEnabled() { return areSFXEnabled; }
    public BackgroundMusic getCurrentMusic() { return currentMusic; }
    public boolean isInitialized() { return isInitialized; }

    /**
     * Obtenir un résumé de l'état audio
     */
    public String getAudioStatus() {
        return String.format(
                "🎵 Audio Status: Master=%d%%, Music=%d%%, SFX=%d%%, Muted=%s, Current=%s",
                (int)(masterVolume * 100),
                (int)(musicVolume * 100),
                (int)(sfxVolume * 100),
                isMuted ? "Oui" : "Non",
                currentMusic != null ? currentMusic.getDescription() : "Aucune"
        );
    }
}