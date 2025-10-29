# 🕹️ Retro Arcade - Snake & Pong

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-17.0.6-blue?style=for-the-badge&logo=java)
![Maven](https://img.shields.io/badge/Maven-3.x-red?style=for-the-badge&logo=apache-maven)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

Une collection de jeux d'arcade rétro développée en JavaFX avec une interface néon stylisée, un système de scores persistant et une ambiance musicale immersive.

[Fonctionnalités](#-fonctionnalités) • [Installation](#-installation) • [Utilisation](#-utilisation) • [Architecture](#-architecture) • [Développement](#-développement)

</div>

---

## 📋 Table des matières

- [Vue d'ensemble](#-vue-densemble)
- [Fonctionnalités](#-fonctionnalités)
- [Prérequis](#-prérequis)
- [Installation](#-installation)
- [Utilisation](#-utilisation)
- [Jeux disponibles](#-jeux-disponibles)
- [Architecture du projet](#-architecture-du-projet)
- [Technologies utilisées](#-technologies-utilisées)
- [Système de scores](#-système-de-scores)
- [Système audio](#-système-audio)
- [Développement](#-développement)
- [Build et distribution](#-build-et-distribution)
- [Contribution](#-contribution)
- [Licence](#-licence)
- [Auteur](#-auteur)

---

## Vue d'ensemble

**Retro Arcade** est une application de jeux d'arcade classiques développée en Java avec JavaFX. Le projet combine deux jeux emblématiques (Snake et Pong) dans une interface moderne avec une esthétique rétro néon inspirée des années 80.

### Points forts

- **Interface rétro-futuriste** avec effets néon et animations fluides
- **Sauvegarde automatique** des scores avec système de backup
- **Système audio complet** avec musiques d'ambiance et effets sonores
- **Statistiques détaillées** avec high scores, moyennes et historique
- **Barre de titre personnalisée** avec fenêtre draggable
- **Splash screen animé** avec chargement progressif des ressources
- **Contrôles intuitifs** au clavier et à la souris
- **Code SOLID** - Architecture respectant 100% des principes SOLID
- **Architecture modulaire** - Séparation claire des responsabilités (SRP)
- **Extensible** - Interface Game pour ajouter facilement de nouveaux jeux

---

## Fonctionnalités

### Système de jeu

- **Menu principal interactif** avec sélection des jeux
- **Splash screen** avec barre de progression et chargement des ressources
- **Système de pause** (Espace) et restart (R) dans tous les jeux
- **Retour au menu** sans redémarrage (ESC)
- **Fenêtre personnalisée** sans bordures système, draggable

### Gestion des scores

- **Sauvegarde automatique** après chaque partie
- **Persistance locale** dans le répertoire utilisateur:
  - Windows: `%APPDATA%/RetroArcade/`
  - macOS: `~/Library/Application Support/RetroArcade/`
  - Linux: `~/.retro-arcade/`
- **Système de backup** automatique et manuel avec timestamp
- **Statistiques globales**: parties jouées, scores totaux, moyennes
- **Export des scores** en fichier texte lisible
- **Protection contre la perte de données** avec récupération automatique

### Système audio

- **Musiques d'ambiance** en boucle (menu, jeu)
- **Effets sonores** pour chaque action (manger, rebond, victoire, game over)
- **Contrôle du volume** indépendant (musique, SFX, master)
- **Gestion intelligente** des erreurs de chargement avec fallback
- **Nettoyage automatique** des ressources audio

### Interface utilisateur

- **Design rétro néon** avec couleurs vives (rose, cyan, vert)
- **Animations fluides** et effets de glow
- **Affichage en temps réel** des scores et statistiques
- **Boutons stylisés** avec effets hover
- **Responsive** avec taille fixe optimisée (800x780)

---

## 🔧 Prérequis

### Pour l'exécution

- **Java 17** ou supérieur (JDK ou JRE)
- **JavaFX 17.0.6** (inclus dans les dépendances Maven)
- **Système d'exploitation**: Windows, macOS ou Linux

### Pour le développement

- **JDK 17** ou supérieur
- **Maven 3.x**
- **IDE recommandé**: IntelliJ IDEA, Eclipse ou NetBeans
- **Git** pour le versioning

---

## 📥 Installation

### Option 1: Cloner et compiler

```bash
# Cloner le repository
git clone https://github.com/TheDevApprentice/Arcade-Game.git
cd Arcade-Game

# Compiler avec Maven
mvn clean package

# Lancer l'application
java --module-path target/lib --add-modules javafx.controls,javafx.fxml,javafx.media -jar target/arcade-game-executable.jar
```

### Option 2: Utiliser le script portable (Windows)

```batch
# Compiler d'abord
mvn clean package
mvn dependency:copy-dependencies -DoutputDirectory=target/lib

# Lancer avec le script
run-arcade-portable.bat
```

### Option 3: Créer un installateur natif

```bash
# Créer un installateur avec jpackage
mvn clean package
mvn jpackage:jpackage

# L'installateur sera dans target/installer/
```

---

## 🎯 Utilisation

### Lancement

1. **Démarrez l'application** via l'une des méthodes ci-dessus
2. **Attendez le splash screen** (chargement des ressources)
3. **Sélectionnez un jeu** dans le menu principal

### Contrôles généraux

| Touche | Action |
|--------|--------|
| `ENTRÉE` | Démarrer le jeu |
| `ESPACE` | Pause / Reprendre |
| `R` | Redémarrer la partie |
| `ESC` | Retour au menu |

### Menu principal

- **Clic sur les boutons** pour sélectionner un jeu
- **Bouton Quitter** pour fermer l'application
- **Drag & drop** de la fenêtre via la barre de titre
- **Boutons minimiser/fermer** dans la barre de titre

---

## 🐍 Jeux disponibles

### Snake Game

<div align="center">

**Le classique du serpent qui grandit en mangeant**

</div>

#### Règles

- Mangez la nourriture (🍎) pour grandir
- Évitez les murs et votre propre corps
- La vitesse augmente progressivement
- Score basé sur la longueur du serpent

#### Contrôles

| Touche | Action |
|--------|--------|
| `↑` | Haut |
| `↓` | Bas |
| `←` | Gauche |
| `→` | Droite |

#### Statistiques affichées

- **Score actuel** (0000 format)
- **Longueur du serpent**
- **Vitesse de jeu** (niveau)
- **High score** global
- **Parties jouées** et moyenne

---

### Pong Game

<div align="center">

**Le tennis de table virtuel contre une IA**

</div>

#### Règles

- Premier à 5 points gagne
- La balle accélère à chaque rebond
- L'IA a 3 niveaux de difficulté
- Score global en format Victoires-Défaites

#### Contrôles

| Touche | Action |
|--------|--------|
| `↑` | Raquette haut |
| `↓` | Raquette bas |
| `1` | IA Facile (30%) |
| `2` | IA Moyenne (60%) |
| `3` | IA Difficile (90%) |

#### Statistiques affichées

- **Score du match** (Joueur vs IA)
- **Nombre de rebonds** (actuel et maximum)
- **Vitesse de la balle**
- **Difficulté de l'IA** (%)
- **Score global** (W-L)

---

## 🏗️ Architecture du projet

### Structure des dossiers

```
Arcade-Game/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── module-info.java
│   │   │   └── org/example/snakegame/
│   │   │       ├── GameApplication.java          # Point d'entrée principal
│   │   │       ├── GameController.java          # Contrôleur du menu
│   │   │       ├── ScoreManager.java            # Gestion des scores (Singleton)
│   │   │       ├── MusicController.java         # Gestion audio (Singleton)
│   │   │       ├── SplashScreen.java            # Écran de chargement
│   │   │       ├── common/                      # Classes utilitaires
│   │   │       │   ├── Direction.java
│   │   │       │   ├── GameState.java
│   │   │       │   └── Point.java
│   │   │       ├── snake/                       # Jeu Snake
│   │   │       │   ├── SnakeGame.java
│   │   │       │   ├── SnakeController.java
│   │   │       │   ├── Snake.java
│   │   │       │   └── Food.java
│   │   │       └── pong/                        # Jeu Pong
│   │   │           ├── PongGame.java
│   │   │           ├── PongController.java
│   │   │           ├── Ball.java
│   │   │           └── Paddle.java
│   │   └── resources/
│   │       └── org/example/snakegame/
│   │           ├── images/                      # Images et icônes
│   │           ├── songs/                       # Musiques et SFX (11 fichiers)
│   │           ├── styles/                      # Fichiers CSS
│   │           │   ├── styles.css               # Styles globaux
│   │           │   ├── menu-styles.css
│   │           │   ├── snake-styles.css
│   │           │   ├── pong-styles.css
│   │           │   └── splash-styles.css
│   │           └── views/                       # Fichiers FXML
│   │               ├── game-view-custom-titlebar.fxml
│   │               ├── pong-view-custom-titlebar.fxml
│   │               └── snake-view-custom-titlebar.fxml
│   └── test/
│       └── java/
├── target/                                      # Fichiers compilés
├── pom.xml                                      # Configuration Maven
├── run-arcade-portable.bat                     # Script de lancement Windows
├── LICENSE                                      # Licence MIT
└── README.md                                    # Ce fichier
```

### Patterns de conception utilisés

#### Singleton Pattern

- **ScoreManager**: Instance unique pour la gestion globale des scores
- **MusicController**: Instance unique pour la gestion audio

#### MVC Pattern (Model-View-Controller)

- **Model**: Classes métier (Snake, Ball, Paddle, Food)
- **View**: Fichiers FXML et Canvas JavaFX
- **Controller**: SnakeController, PongController, GameController

#### Observer Pattern

- Callbacks pour les mises à jour de score
- Callbacks pour les événements de game over

---

## 🛠️ Technologies utilisées

### Framework et langages

- **Java 17**: Langage principal avec modules Java
- **JavaFX 17.0.6**: Framework UI moderne
  - `javafx.controls`: Composants UI
  - `javafx.fxml`: Chargement des vues
  - `javafx.media`: Gestion audio
- **Maven 3.x**: Gestion des dépendances et build

### Bibliothèques

- **JUnit 5.10.2**: Tests unitaires (scope test)
- **Maven Shade Plugin**: Création de JAR exécutable
- **jpackage-maven-plugin**: Création d'installateurs natifs

### Outils de développement

- **Git**: Versioning
- **IntelliJ IDEA**: IDE recommandé
- **Maven Wrapper**: Inclus pour portabilité

---

## 💾 Système de scores

### Format de sauvegarde

Les scores sont sauvegardés dans un fichier texte structuré:

```properties
# Retro Arcade - Fichier de scores
version=1.0
lastSessionDate=28/10/2025 12:30:45
lastPlayedGame=Snake
totalGamesPlayed=42

# Scores Snake
snake.highScore=1250
snake.totalScore=15000
snake.gamesPlayed=25
snake.highScoreDate=27/10/2025 18:45:30

# Scores Pong
pong.playerWins=12
pong.aiWins=5
pong.gamesPlayed=17
pong.lastWinDate=28/10/2025 11:20:15
```

### Fonctionnalités avancées

- **Sauvegarde automatique** après chaque partie
- **Backup automatique** avant chaque sauvegarde
- **Backup manuel** avec timestamp lors du reset
- **Récupération automatique** en cas de corruption
- **Export lisible** pour partage ou analyse
- **Shutdown hook** pour sauvegarder même en cas d'arrêt brutal

### API ScoreManager

```java
// Obtenir l'instance
ScoreManager scoreManager = ScoreManager.getInstance();

// Enregistrer des scores
scoreManager.recordSnakeScore(150);
scoreManager.recordPongPlayerWin();
scoreManager.recordPongAIWin();

// Consulter les scores
int highScore = scoreManager.getSnakeHighScore();
String pongScore = scoreManager.getPongScore(); // "12-5"
int average = scoreManager.getSnakeAverageScore();

// Gestion
scoreManager.forceSave();
scoreManager.exportScores();
scoreManager.resetAllScores();
```

---

## 🎵 Système audio

### Musiques d'ambiance

- **Menu**: `mixkit-swing-is-the-answer-526.mp3`
- **Jeu**: `mixkit-game-level-music-689.wav`
- **Disco Rétro**: `mixkit-disco-aint-old-school-935.mp3`

### Effets sonores

- **Généraux**: Bonus, niveau terminé, expérience, trésor, pièce, game over
- **Snake**: Manger nourriture, nourriture spéciale
- **Pong**: Frappe raquette, rebond mur, but, victoire

### API MusicController

```java
// Obtenir l'instance et initialiser
MusicController music = MusicController.getInstance();
music.initialize();

// Musiques
music.playMenuMusic();
music.playGameMusic();
music.stopBackgroundMusic();
music.pauseBackgroundMusic();

// Effets sonores
music.playSnakeEat();
music.playPongBallHit();
music.playBonusEarned();

// Contrôle du volume (0.0 à 1.0)
music.setMasterVolume(0.7);
music.setMusicVolume(0.5);
music.setSFXVolume(0.8);

// Activation/désactivation
music.setMuted(true);
music.setMusicEnabled(false);
music.setSFXEnabled(false);

// Nettoyage
music.cleanup();
```

---

## 👨‍💻 Développement

### Configuration de l'environnement

```bash
# Cloner le projet
git clone https://github.com/TheDevApprentice/Arcade-Game.git
cd Arcade-Game

# Installer les dépendances
mvn clean install

# Lancer en mode développement
mvn javafx:run
```

### Structure du code

#### Point d'entrée

```java
// GameApplication.java
public class GameApplication extends Application {
    public static void main(String[] args) {
        launch();
    }
}
```

#### Créer un nouveau jeu

1. Créer un package dans `org.example.snakegame`
2. Créer les classes `Game`, `Controller` et modèles
3. Implémenter l'interface de jeu avec Canvas
4. Ajouter les styles CSS dans `resources/styles/`
5. Intégrer dans le menu principal

### Tests

```bash
# Lancer les tests
mvn test

# Avec couverture
mvn test jacoco:report
```

### Compilation

```bash
# Compilation simple
mvn compile

# Package avec dépendances
mvn clean package

# Sans tests
mvn clean package -DskipTests
```

---

## 📦 Build et distribution

### JAR exécutable

```bash
# Créer le JAR avec toutes les dépendances
mvn clean package

# Le JAR sera dans target/arcade-game-executable.jar
```

### Installateur natif (jpackage)

```bash
# Windows (nécessite WiX Toolset)
mvn jpackage:jpackage

# macOS (nécessite Xcode)
mvn jpackage:jpackage

# Linux (nécessite fakeroot et dpkg/rpm)
mvn jpackage:jpackage

# L'installateur sera dans target/installer/
```

### Distribution portable

```bash
# Copier les dépendances
mvn dependency:copy-dependencies -DoutputDirectory=target/lib

# Créer un ZIP avec:
# - arcade-game-executable.jar
# - lib/ (dépendances)
# - run-arcade-portable.bat
```

### Configuration jpackage

- **Nom**: RetroArcade
- **Version**: 1.0.0
- **Vendor**: Hugo Abric
- **Description**: Arcade rétro - Snake et Pong
- **Main Class**: `org.example.snakegame.GameApplication`

---

## 🤝 Contribution

Les contributions sont les bienvenues ! Voici comment participer:

### Processus

1. **Fork** le projet
2. **Créer une branche** (`git checkout -b feature/AmazingFeature`)
3. **Commit** vos changements (`git commit -m 'Add AmazingFeature'`)
4. **Push** vers la branche (`git push origin feature/AmazingFeature`)
5. **Ouvrir une Pull Request**

### Guidelines

- Respecter le style de code existant
- Ajouter des tests pour les nouvelles fonctionnalités
- Mettre à jour la documentation si nécessaire
- Utiliser des messages de commit descriptifs
- Tester sur plusieurs plateformes si possible

### Idées de contributions

- 🎮 Ajouter de nouveaux jeux (Tetris, Breakout, Space Invaders)
- 🎨 Créer de nouveaux thèmes visuels
- 🌍 Ajouter l'internationalisation (i18n)
- 🏆 Système de succès/achievements
- 👥 Mode multijoueur local
- 📊 Graphiques de statistiques
- ⚙️ Menu de paramètres complet

---

## 📄 Licence

Ce projet est sous licence **MIT**. Voir le fichier [LICENSE](LICENSE) pour plus de détails.

```
MIT License

Copyright (c) 2025 Hugo Abric

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

---

## 👤 Auteur

**Hugo Abric**

- GitHub: [@TheDevApprentice](https://github.com/TheDevApprentice)
- Projet: [Arcade-Game](https://github.com/TheDevApprentice/Arcade-Game)

---

## 🙏 Remerciements

- **JavaFX** pour le framework UI moderne
- **Mixkit** pour les assets audio gratuits
- La communauté **Java** pour les ressources et l'inspiration
- Les jeux d'arcade classiques qui ont inspiré ce projet

---

## 📸 Screenshots

<div align="center">

### Menu Principal
*Interface rétro néon avec sélection des jeux*

### Snake Game
*Le serpent classique avec effets visuels modernes*

### Pong Game
*Tennis de table contre IA avec difficulté ajustable*

### Splash Screen
*Écran de chargement animé avec progression*

</div>

---

## 🔮 Roadmap

### Version 1.1 (Prochaine)

- [ ] Menu de paramètres (volume, difficulté, thèmes)
- [ ] Système de succès/achievements
- [ ] Leaderboard en ligne
- [ ] Mode sombre/clair

### Version 1.2

- [ ] Nouveau jeu: Tetris
- [ ] Nouveau jeu: Breakout
- [ ] Mode multijoueur local
- [ ] Replay des parties

### Version 2.0

- [ ] Mode en ligne multijoueur
- [ ] Tournois et classements
- [ ] Éditeur de niveaux
- [ ] Support mobile (JavaFX Mobile)

---

<div align="center">

**Fait avec ❤️ et ☕ par Hugo Abric**

⭐ N'oubliez pas de mettre une étoile si vous aimez ce projet !

</div> 