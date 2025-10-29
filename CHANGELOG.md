# Changelog

Toutes les modifications notables de ce projet seront documentées dans ce fichier.

## [1.1.0] - 2025-01-29

### ✨ Ajouté
- **Build automatique** via GitHub Actions sur push `main`
- **Exécutable Windows natif** avec jpackage
- **Scripts de build** PowerShell et Batch pour build local
- **Script de test** pré-build (`test-build.ps1`)
- **Documentation complète** du packaging dans `BUILD.md`
- **Hauteur adaptative** - Fenêtre s'ajuste automatiquement à l'écran (100%)
- **Template Pull Request** pour contributions
- **Workflow CI/CD** avec artefacts automatiques

### 🔧 Modifié
- README simplifié avec focus sur le packaging
- Workflow GitHub Actions mis à jour vers actions v4
- Branche de déploiement : `features` → `main`
- Suppression de l'affichage du score dans `SnakeRenderer` (redondant avec FXML)

### 📦 Distribution
- **Installateur Windows** avec JRE inclus
- **Package ZIP** complet pour distribution
- **Raccourcis automatiques** (Bureau + Menu Démarrer)
- **Désinstallation propre** via Panneau de configuration

### 🐛 Corrigé
- Erreur "Cannot set style once stage has been set visible"
- Erreur "Controller value already specified" dans FXML
- Problèmes de build avec jpackage et chemins

## [1.0.0] - 2025-01-28

### ✨ Version initiale
- Jeu Snake avec effets néon
- Jeu Pong avec IA ajustable
- Système de sauvegarde des scores
- Système audio complet
- Interface rétro néon
- Splash screen animé
- Barre de titre personnalisée

---

Format basé sur [Keep a Changelog](https://keepachangelog.com/fr/1.0.0/)
