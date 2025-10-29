# Build Script for Retro Arcade Windows Executable
# Requires Java 17+ and Maven

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Building Retro Arcade Windows Executable" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# Check Java version
Write-Host "`nChecking Java version..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1 | Select-String "version"
    Write-Host "Found: $javaVersion" -ForegroundColor Green
} catch {
    Write-Host "ERROR: Java not found! Please install Java 17+" -ForegroundColor Red
    exit 1
}

# Check Maven
Write-Host "`nChecking Maven..." -ForegroundColor Yellow
try {
    $mavenVersion = mvn -version | Select-String "Apache Maven"
    Write-Host "Found: $mavenVersion" -ForegroundColor Green
} catch {
    Write-Host "ERROR: Maven not found! Please install Maven" -ForegroundColor Red
    exit 1
}

# Clean and build
Write-Host "`n1. Cleaning and building with Maven..." -ForegroundColor Yellow
mvn clean package -DskipTests
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Maven build failed!" -ForegroundColor Red
    exit 1
}

# Create executable
Write-Host "`n2. Creating executable with jpackage..." -ForegroundColor Yellow

$jpackageArgs = @(
    "--name", "RetroArcade",
    "--input", "target/classes",
    "--main-jar", "arcade-game-1.0-SNAPSHOT.jar",
    "--main-class", "org.example.snakegame.GameApplication",
    "--type", "exe",
    "--win-dir-chooser",
    "--win-shortcut",
    "--win-menu",
    "--win-menu-group", "Retro Games",
    "--app-version", "1.0",
    "--vendor", "Retro Arcade Team",
    "--copyright", "Copyright 2024 Retro Arcade Team",
    "--description", "Classic Snake and Pong games with retro style",
    "--dest", "target/dist",
    "--verbose"
)

# Try with icon if exists
if (Test-Path "src/main/resources/icon.ico") {
    $jpackageArgs += @("--icon", "src/main/resources/icon.ico")
}

jpackage @jpackageArgs

if ($LASTEXITCODE -ne 0) {
    Write-Host "WARNING: jpackage failed - trying without icon..." -ForegroundColor Yellow
    
    # Fallback without icon
    $fallbackArgs = $jpackageArgs | Where-Object { $_ -ne "--icon" -and $_ -ne "src/main/resources/icon.ico" }
    jpackage @fallbackArgs
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: jpackage failed!" -ForegroundColor Red
        exit 1
    }
}

# Create ZIP package
Write-Host "`n3. Creating ZIP package..." -ForegroundColor Yellow
Set-Location "target/dist"
Compress-Archive -Path "*" -DestinationPath "../RetroArcade-Windows.zip" -Force
Set-Location "../.."

# Success
Write-Host "`n========================================" -ForegroundColor Green
Write-Host "BUILD SUCCESSFUL!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host "Executable: target/dist/RetroArcade-1.0.exe" -ForegroundColor Cyan
Write-Host "Package: target/RetroArcade-Windows.zip" -ForegroundColor Cyan
Write-Host "`nYou can now distribute these files!" -ForegroundColor Green

# Open output folder
Write-Host "`nOpening output folder..." -ForegroundColor Yellow
Start-Process "target/dist"
