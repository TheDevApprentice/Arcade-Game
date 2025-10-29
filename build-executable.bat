@echo off
echo ========================================
echo Building Retro Arcade Windows Executable
echo ========================================

echo.
echo 1. Cleaning and building with Maven...
call mvn clean package -DskipTests
if %ERRORLEVEL% neq 0 (
    echo ERROR: Maven build failed!
    pause
    exit /b 1
)

echo.
echo 2. Creating executable with jpackage...
jpackage ^
  --name "RetroArcade" ^
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
  --copyright "Copyright 2024 Retro Arcade Team" ^
  --description "Classic Snake and Pong games with retro style" ^
  --dest "target/dist" ^
  --verbose

if %ERRORLEVEL% neq 0 (
    echo WARNING: jpackage failed - trying without icon...
    jpackage ^
      --name "RetroArcade" ^
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
      --copyright "Copyright 2024 Retro Arcade Team" ^
      --description "Classic Snake and Pong games with retro style" ^
      --dest "target/dist" ^
      --verbose
)

if %ERRORLEVEL% neq 0 (
    echo ERROR: jpackage failed!
    pause
    exit /b 1
)

echo.
echo 3. Creating ZIP package...
cd target/dist
7z a -tzip ../RetroArcade-Windows.zip *
cd ../..

echo.
echo ========================================
echo BUILD SUCCESSFUL!
echo ========================================
echo Executable: target/dist/RetroArcade-1.0.exe
echo Package: target/RetroArcade-Windows.zip
echo.
echo You can now distribute these files!
echo.
pause
