# 🕹️ Retro Arcade - Snake & Pong

<div align="center">

[![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html)
[![JavaFX](https://img.shields.io/badge/JavaFX-17.0.6-blue?style=for-the-badge&logo=java)](https://openjfx.io/)
[![Maven](https://img.shields.io/badge/Maven-3.x-red?style=for-the-badge&logo=apache-maven)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)](LICENSE)
[![Build Status](https://img.shields.io/badge/Build-Automatic-brightgreen?style=for-the-badge&logo=github-actions)](https://github.com/TheDevApprentice/arcade-game/actions)
[![Build Windows Executable](https://github.com/TheDevApprentice/Arcade-Game/actions/workflows/build-windows.yml/badge.svg?branch=main)](https://github.com/TheDevApprentice/Arcade-Game/actions/workflows/build-windows.yml)
Une collection de jeux d'arcade rétro développée en JavaFX avec une interface néon stylisée, un système de scores persistant et une ambiance musicale immersive.

[Fonctionnalités](#-fonctionnalités) • [Installation](#-installation) • [Utilisation](#-utilisation) • [Architecture](#-architecture) • [Développement](#-développement) • [📦 Build & Distribution](#-build-et-distribution)

</div>

---

## 📋 Table des matières

- [🎮 Vue d'ensemble](#-vue-densemble)
- [✨ Fonctionnalités](#-fonctionnalités)
- [📦 Installation](#-installation)
- [🎯 Utilisation](#-utilisation)
- [🐍 Jeux disponibles](#-jeux-disponibles)
- [🚀 Build & Distribution](#-build--distribution)
- [👨‍💻 Développement](#-développement)
- [🤝 Contribution](#-contribution)

---

## 🎮 Vue d'ensemble

**Retro Arcade** est une collection de jeux d'arcade classiques (Snake & Pong) avec une esthétique rétro néon inspirée des années 80.

### ✨ Points forts

- 🎨 **Interface rétro-futuriste** avec effets néon et animations fluides
- 💾 **Sauvegarde automatique** des scores et statistiques
- 🎵 **Système audio immersif** avec musiques et effets sonores
- 📦 **Installation simple** - Exécutable Windows natif (pas besoin de Java)
- 🖥️ **Fenêtre adaptative** - S'ajuste automatiquement à votre écran
- 🎮 **Contrôles intuitifs** au clavier

---

## ✨ Fonctionnalités

- 🎮 **2 jeux classiques** : Snake et Pong
- 💾 **Sauvegarde automatique** des scores et statistiques
- 🎵 **Musiques rétro** et effets sonores
- ⏸️ **Pause, restart, retour menu** à tout moment
- 📊 **Statistiques détaillées** : high scores, moyennes, historique
- 🎨 **Interface néon** avec animations fluides
- 🖱️ **Barre de titre custom** draggable

---

## 📦 Installation

### 🎯 Pour les utilisateurs (Recommandé)

**Téléchargez simplement l'exécutable Windows :**

1. Allez dans [Releases](https://github.com/TheDevApprentice/Arcade-Game/releases)
2. Téléchargez `RetroArcade-1.0.exe`
3. Lancez l'installateur
4. Jouez ! (Pas besoin d'installer Java)

**Avantages :**
- ✅ Installation en un clic
- ✅ JRE inclus (aucun prérequis)
- ✅ Raccourcis automatiques
- ✅ Désinstallation propre

### 👨‍💻 Pour les développeurs

```bash
# Cloner le repository
git clone https://github.com/TheDevApprentice/Arcade-Game.git
cd Arcade-Game

# Compiler et lancer
mvn clean package
mvn javafx:run
```

**Prérequis développement :**
- Java 17+
- Maven 3.x

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

## 🚀 Build & Distribution

### 📦 Génération de l'exécutable Windows

#### Build automatique (CI/CD)

Le projet utilise **GitHub Actions** pour générer automatiquement l'exécutable :

```bash
# Push sur main déclenche le build
git push origin main

# Téléchargez les artefacts dans l'onglet Actions
```

**Artefacts générés :**
- `RetroArcade-1.0.exe` - Installateur Windows
- `RetroArcade-Windows.zip` - Package complet

#### Build local

```powershell
# Test des prérequis
.\test-build.ps1

# Build complet
.\build-executable.ps1
```

**Résultat :**
- Exécutable dans `target/dist/`
- JRE inclus (pas besoin de Java pour l'utilisateur final)
- Installation propre avec raccourcis

### 🔧 Technologies

- **Java 17** + **JavaFX 17.0.6**
- **Maven** pour le build
- **jpackage** pour l'exécutable natif
- **GitHub Actions** pour le CI/CD

> 📖 **Documentation complète** : Voir [BUILD.md](BUILD.md)

---

## 👨‍💻 Développement

### 🛠️ Setup

```bash
# Cloner et installer
git clone https://github.com/TheDevApprentice/Arcade-Game.git
cd Arcade-Game
mvn clean install

# Lancer en dev
mvn javafx:run
```

### 📁 Structure

```
src/main/java/org/example/snakegame/
├── GameApplication.java      # Point d'entrée
├── GameController.java       # Menu principal
├── ScoreManager.java         # Gestion scores
├── MusicController.java      # Gestion audio
├── snake/                    # Jeu Snake
└── pong/                     # Jeu Pong
```

### 🧪 Tests

```bash
mvn test                      # Lancer les tests
mvn clean package -DskipTests # Build sans tests
```

---

## 🤝 Contribution

Les contributions sont les bienvenues ! Voici comment participer:

1. **Fork** le projet
2. **Créer une branche** (`git checkout -b feature/AmazingFeature`)
3. **Commit** et **Push** vos changements
4. **Ouvrir une Pull Request**

### 💡 Idées de contributions

- 🎮 Nouveaux jeux (Tetris, Breakout, Space Invaders)
- 🎨 Nouveaux thèmes visuels
- 🏆 Système d'achievements
- 👥 Mode multijoueur local
- ⚙️ Menu de paramètres

---

## 📄 Licence

Projet sous licence **MIT** - Voir [LICENSE](LICENSE)

**Copyright © 2025 Hugo Abric**

---

## 🔮 Roadmap

- [ ] Menu de paramètres (volume, difficulté)
- [ ] Système d'achievements
- [ ] Nouveaux jeux (Tetris, Breakout)
- [ ] Mode multijoueur local

---

<div align="center">

**Fait avec ❤️ et ☕ par Hugo Abric**

⭐ N'oubliez pas de mettre une étoile si vous aimez ce projet !

</div> 
