# 🔄 GitHub Actions Workflows

Ce dossier contient les workflows d'automatisation pour le projet Retro Arcade.

## 📁 Fichiers disponibles

### `build-windows.yml`
- **Déclencheur**: Push sur branche `main`, Pull Request, manuel
- **OS**: Windows Latest
- **Actions**: 
  - Build Maven avec cache
  - Création exécutable Windows avec jpackage
  - Package ZIP complet
  - Upload des artefacts
  - **Création automatique de Release** avec tag généré (branche main uniquement)

## 🚀 Utilisation

### Build automatique avec Release
```bash
# Push sur la branche main
git checkout main
git add .
git commit -m "feat: nouvelle fonctionnalité"
git push origin main

# → Déclenche automatiquement :
# 1. Build de l'exécutable
# 2. Upload des artefacts
# 3. Création d'une Release GitHub avec les fichiers
```

### Build manuel
1. Allez dans l'onglet **Actions** sur GitHub
2. Sélectionnez "Build Windows Executable"
3. Cliquez sur **"Run workflow"**

## 📦 Artefacts et Release

### Artefacts (disponibles dans Actions)
- **RetroArcade-Windows-Executable**: Fichier `.exe` prêt à installer
- **RetroArcade-Windows-Package**: Archive `.zip` avec tous les fichiers

### Release automatique (sur main)
- **Tag généré**: `build-YYYYMMDD-HHMMSS`
- **Fichiers publiés**: 
  - `RetroArcade-1.0.exe` - Installateur Windows
  - `RetroArcade-Windows.zip` - Package complet
- **Visible dans**: Onglet "Releases" du repository

## 🔧 Configuration du workflow

### Variables modifiables
- `SCREEN_HEIGHT_RATIO`: Ratio de hauteur de la fenêtre (1.0 = 100%)
- `app-version`: Version de l'application
- `vendor`: Nom du développeur

### Secrets requis
- `GITHUB_TOKEN`: Automatiquement fourni par GitHub

## 📋 Débogage

### Logs détaillés
Le workflow utilise `--verbose` pour jpackage et affiche toutes les étapes.

### Erreurs courantes
1. **Maven build failed**: Vérifier les dépendances dans `pom.xml`
2. **jpackage failed**: Vérifier Java 17+ et structure des fichiers
3. **Upload failed**: Vérifier la taille des artefacts (<2GB)

### Tests locaux
Avant de push, testez avec :
```powershell
.\test-build.ps1
.\build-executable.ps1
```

## 🎯 Bonnes pratiques

1. **Tester localement** avant chaque push
2. **Utiliser des messages de commit** clairs
3. **Vérifier les artefacts** après chaque build
4. **Maintenir la documentation** à jour

## 🔄 Évolutions futures

- [ ] Support macOS (pkg/dmg)
- [ ] Support Linux (deb/rpm)
- [ ] Tests automatisés
- [ ] Déploiement automatique sur releases
- [ ] Notifications de build
