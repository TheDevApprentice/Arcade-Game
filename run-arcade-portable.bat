@echo off
echo ========================================
echo    🎮 RETRO ARCADE - Snake & Pong 🎮
echo ========================================
echo.
echo Lancement du jeu...
echo La console va se fermer automatiquement.
echo.

REM Vérifier que le dossier target existe
if not exist "target" (
    echo ERREUR: Dossier target non trouvé.
    echo Veuillez executer "mvn clean package" d'abord.
    pause
    exit /b 1
)

REM Vérifier que le JAR existe
if not exist "target\arcade-game-executable.jar" (
    echo ERREUR: JAR executable non trouvé.
    echo Veuillez executer "mvn clean package" d'abord.
    pause
    exit /b 1
)

REM Vérifier que les dépendances existent
if not exist "target\lib" (
    echo ERREUR: Dossier lib non trouvé.
    echo Veuillez executer "mvn dependency:copy-dependencies -DoutputDirectory=target/lib" d'abord.
    pause
    exit /b 1
)

cd target
javaw --module-path lib --add-modules javafx.controls,javafx.fxml,javafx.base,javafx.graphics,javafx.media -jar arcade-game-executable.jar

if errorlevel 1 (
    echo Une erreur est survenue lors du lancement.
    pause
)

echo Jeu fermé.