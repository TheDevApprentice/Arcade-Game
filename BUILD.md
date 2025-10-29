# 📦 Build & Distribution Guide

## 🚀 Build Local Executable

### Prérequis
- **Java 17+** (jpackage est inclus)
- **Maven 3.6+**
- **Windows 10/11** (pour l'exécutable .exe)

### Methode 1: Script PowerShell (Recommandé)
```powershell
.\build-executable.ps1
```

### Methode 2: Script Batch
```cmd
build-executable.bat
```

### Methode 3: Manuel
```cmd
# 1. Build Maven
mvn clean package -DskipTests

# 2. Créer l'exécutable
jpackage --name "RetroArcade" ^
         --input "target/classes" ^
         --main-jar "arcade-game-1.0-SNAPSHOT.jar" ^
         --main-class "org.example.snakegame.GameApplication" ^
         --type "exe" ^
         --win-dir-chooser ^
         --win-shortcut ^
         --win-menu ^
         --win-menu-group "Retro Games" ^
         --app-version "1.0" ^
         --vendor "Retro Arcade Team" ^
         --description "Classic Snake and Pong games with retro style" ^
         --dest "target/dist"
```

## 🔄 Build Automatique (CI/CD)

### GitHub Actions
Le build automatique est déclenché lors d'un push sur la branche `features`:

1. **Push votre code**
```bash
git checkout features
git add .
git commit -m "feat: new feature"
git push origin features
```

2. **Télécharger les artefacts**
- Allez dans l'onglet **Actions** de votre repository
- Cliquez sur le workflow "Build Windows Executable"
- Téléchargez les artefacts en bas de la page

### Artefacts générés
- ✅ `RetroArcade-Windows-Executable`: L'exécutable .exe
- ✅ `RetroArcade-Windows-Package`: Package ZIP complet

## 📁 Fichiers générés

Après build, vous trouverez:
```
target/
├── dist/
│   └── RetroArcade-1.0.exe    # Exécutable Windows
└── RetroArcade-Windows.zip    # Package distribuable
```

## 🎮 Distribution

### Pour les utilisateurs finaux:
1. **Téléchargez** le fichier `.exe` ou le `.zip`
2. **Exécutez** `RetroArcade-1.0.exe`
3. **Installez** où vous voulez (choix du dossier)
4. **Lancez** depuis le menu Démarrer ou le raccourci

### Avantages du package:
- ✅ **Installation propre** avec désinstallation
- ✅ **Raccourcis** automatiques (Bureau + Menu Démarrer)
- ✅ **JRE inclus** (pas besoin d'installer Java)
- ✅ **Compatible** Windows 10/11

## 🔧 Configuration avancée

### Ajouter une icône personnalisée
1. Créez un fichier `icon.ico` (256x256px)
2. Placez-le dans `src/main/resources/`
3. Relancez le build

### Modifier les informations
Éditez les arguments jpackage dans les scripts:
- `--app-version`: Version de l'application
- `--vendor`: Nom du développeur
- `--description`: Description courte

## 🐛 Dépannage

### Problèmes courants:
1. **"jpackage not found"** → Installez Java 17+
2. **"Build failed"** → Vérifiez Maven et Java versions
3. **"Icon not found"** → Le build continue sans icône

### Logs détaillés:
Les scripts utilisent `--verbose` pour afficher les détails du build.

## 📋 Checklist avant distribution

- [ ] Test sur Windows 10
- [ ] Test sur Windows 11  
- [ ] Vérifier les raccourcis
- [ ] Tester la désinstallation
- [ ] Vérifier l'icône (si personnalisée)
- [ ] Tester l'installation sur un chemin avec espaces
