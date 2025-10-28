package org.example.snakegame;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire global de la musique et des effets sonores
 * Singleton qui gère tous les assets audio du jeu Retro Arcade
 */
public class MusicController {

    private static MusicController instance;

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
            System.out.println("🎵 MusicController déjà initialisé");
            return;
        }

        System.out.println("🎵 Initialisation du MusicController...");

        try {
            // Charger toutes les musiques d'ambiance
            loadBackgroundMusics();

            // Charger tous les effets sonores
            loadSoundEffects();

            isInitialized = true;
            System.out.println("✅ MusicController initialisé avec succès !");
            System.out.println("   - " + backgroundMusics.size() + " musiques d'ambiance chargées");
            System.out.println("   - " + soundEffects.size() + " effets sonores chargés");

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'initialisation du MusicController: " + e.getMessage());
            e.printStackTrace();
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
                        System.err.println("❌ Erreur de lecture pour " + music.getFilename() + ": " + player.getError().getMessage());
                    });

                    backgroundMusics.put(music, player);
                    System.out.println("🎼 Musique chargée: " + music.getDescription());
                } else {
                    System.err.println("⚠️ Fichier musical introuvable: " + music.getFilename());
                }
            } catch (Exception e) {
                System.err.println("❌ Erreur chargement musique " + music.getFilename() + ": " + e.getMessage());
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
                    System.out.println("🔊 SFX chargé: " + sfx.getDescription());
                } else {
                    System.err.println("⚠️ Fichier SFX introuvable: " + sfx.getFilename());
                }
            } catch (Exception e) {
                System.err.println("❌ Erreur chargement SFX " + sfx.getFilename() + ": " + e.getMessage());
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
                    System.out.println("🎵 Musique démarrée: " + music.getDescription());
                } else {
                    System.err.println("❌ Impossible de jouer " + music.getDescription() + " - Fichier défectueux");
                    // Essayer une musique de secours
                    tryFallbackMusic();
                }
            } else {
                System.err.println("❌ Musique non trouvée: " + music);
                // Essayer une musique de secours
                tryFallbackMusic();
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lecture musique: " + e.getMessage());
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
                    System.out.println("🎵 Musique de secours: " + fallback.getDescription());
                    return;
                } catch (Exception e) {
                    // Continue vers le prochain
                }
            }
        }
        System.err.println("❌ Aucune musique fonctionnelle trouvée");
    }

    /**
     * Arrêter la musique d'ambiance
     */
    public void stopBackgroundMusic() {
        if (currentMusicPlayer != null) {
            currentMusicPlayer.stop();
            System.out.println("⏹️ Musique arrêtée: " + (currentMusic != null ? currentMusic.getDescription() : "Inconnue"));
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
                System.out.println("⏸️ Musique mise en pause");
            } else if (currentMusicPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
                currentMusicPlayer.play();
                System.out.println("▶️ Musique reprise");
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

                System.out.println("🔊 SFX joué: " + effect.getDescription());
            } else {
                System.err.println("❌ Effet sonore non trouvé: " + effect);
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lecture SFX: " + e.getMessage());
        }
    }

    /**
     * Régler le volume principal (0.0 à 1.0)
     */
    public void setMasterVolume(double volume) {
        this.masterVolume = Math.max(0.0, Math.min(1.0, volume));
        updateAllVolumes();
        System.out.println("🔊 Volume principal: " + (int)(masterVolume * 100) + "%");
    }

    /**
     * Régler le volume de la musique (0.0 à 1.0)
     */
    public void setMusicVolume(double volume) {
        this.musicVolume = Math.max(0.0, Math.min(1.0, volume));
        updateMusicVolume();
        System.out.println("🎵 Volume musique: " + (int)(musicVolume * 100) + "%");
    }

    /**
     * Régler le volume des effets sonores (0.0 à 1.0)
     */
    public void setSFXVolume(double volume) {
        this.sfxVolume = Math.max(0.0, Math.min(1.0, volume));
        updateSFXVolume();
        System.out.println("🔊 Volume SFX: " + (int)(sfxVolume * 100) + "%");
    }

    /**
     * Activer/désactiver le son complètement
     */
    public void setMuted(boolean muted) {
        this.isMuted = muted;
        if (muted) {
            pauseBackgroundMusic();
            System.out.println("🔇 Audio désactivé");
        } else {
            if (currentMusic != null && isMusicEnabled) {
                playBackgroundMusic(currentMusic);
            }
            System.out.println("🔊 Audio activé");
        }
    }

    /**
     * Activer/désactiver la musique d'ambiance
     */
    public void setMusicEnabled(boolean enabled) {
        this.isMusicEnabled = enabled;
        if (!enabled) {
            stopBackgroundMusic();
            System.out.println("🎵 Musique désactivée");
        } else if (!isMuted) {
            // Redémarrer la musique du menu par défaut
            playBackgroundMusic(BackgroundMusic.MENU);
            System.out.println("🎵 Musique activée");
        }
    }

    /**
     * Activer/désactiver les effets sonores
     */
    public void setSFXEnabled(boolean enabled) {
        this.areSFXEnabled = enabled;
        System.out.println("🔊 Effets sonores " + (enabled ? "activés" : "désactivés"));
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
        System.out.println("🧹 Nettoyage du MusicController...");

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

        System.out.println("✅ MusicController nettoyé");
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