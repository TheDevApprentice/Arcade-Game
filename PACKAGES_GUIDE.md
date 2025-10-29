# 📦 Guide GitHub Packages

## 🎯 Qu'est-ce qu'un Package GitHub ?

Un **GitHub Package** est un artefact Maven publié dans le registre GitHub Packages, permettant à d'autres projets de l'utiliser comme dépendance.

---

## 🚀 Publication automatique

Le workflow publie automatiquement le package sur chaque push `main` :

```yaml
- Publish Package to GitHub Packages
  └─ mvn deploy → GitHub Packages Registry
```

### 📍 Où trouver le package ?

1. **Onglet "Packages"** du repository GitHub
2. URL : `https://github.com/TheDevApprentice/Arcade-Game/packages`
3. Package Maven : `org.example:arcade-game:1.0-SNAPSHOT`

---

## 📥 Utiliser le package dans un autre projet

### 1. Configurer Maven

Ajoutez dans votre `pom.xml` :

```xml
<repositories>
  <repository>
    <id>github</id>
    <url>https://maven.pkg.github.com/TheDevApprentice/Arcade-Game</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>org.example</groupId>
    <artifactId>arcade-game</artifactId>
    <version>1.0-SNAPSHOT</version>
  </dependency>
</dependencies>
```

### 2. Authentification

Créez `~/.m2/settings.xml` :

```xml
<settings>
  <servers>
    <server>
      <id>github</id>
      <username>VOTRE_USERNAME</username>
      <password>VOTRE_GITHUB_TOKEN</password>
    </server>
  </servers>
</settings>
```

**Note :** Créez un Personal Access Token avec scope `read:packages`

---

## 🔄 Différence Release vs Package

| Feature | Release | Package |
|---------|---------|---------|
| **Type** | Fichiers binaires (.exe, .zip) | Artefact Maven (JAR) |
| **Usage** | Utilisateurs finaux | Développeurs (dépendance) |
| **Emplacement** | Onglet "Releases" | Onglet "Packages" |
| **Installation** | Double-clic .exe | Maven dependency |
| **Contenu** | Exécutable + JRE | JAR avec classes |

---

## 📊 Workflow complet

```
Push sur main
    ↓
1. Build Maven
    ↓
2. Création .exe (jpackage)
    ↓
3. Upload Artefacts (Actions)
    ↓
4. Création Release (avec .exe et .zip)
    ↓
5. Publication Package (Maven JAR)
```

---

## 🎯 Cas d'usage

### Release (Utilisateurs finaux)
- ✅ Télécharger et installer le jeu
- ✅ Pas besoin de Java
- ✅ Installation en un clic

### Package (Développeurs)
- ✅ Réutiliser le code dans un autre projet
- ✅ Importer comme dépendance Maven
- ✅ Accéder aux classes Java

---

## 🔧 Configuration actuelle

**Repository :** `TheDevApprentice/Arcade-Game`  
**Package Maven :** `org.example:arcade-game:1.0-SNAPSHOT`  
**URL :** `https://maven.pkg.github.com/TheDevApprentice/Arcade-Game`

---

## 📝 Notes importantes

- Le package est publié **automatiquement** sur push `main`
- Nécessite un **GitHub Token** avec permissions `write:packages`
- Le token est fourni automatiquement par GitHub Actions
- Les packages publics sont **visibles par tous**
- Les packages privés nécessitent authentification

---

**Le package Maven est maintenant publié automatiquement à chaque build !** 🎉
