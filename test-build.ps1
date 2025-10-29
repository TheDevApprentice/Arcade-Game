# Test script before building executable
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Testing Retro Arcade Before Build" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# Test 1: Check Java
Write-Host "`n1. Testing Java installation..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1 | Select-String "version"
    Write-Host "✅ Java found: $javaVersion" -ForegroundColor Green
    
    # Check if Java 17+
    if ($javaVersion -match '"(1[7-9]|[2-9][0-9])\.') {
        Write-Host "✅ Java 17+ detected" -ForegroundColor Green
    } else {
        Write-Host "⚠️  Warning: Java 17+ recommended for jpackage" -ForegroundColor Yellow
    }
} catch {
    Write-Host "❌ Java not found!" -ForegroundColor Red
    exit 1
}

# Test 2: Check Maven
Write-Host "`n2. Testing Maven installation..." -ForegroundColor Yellow
try {
    $mavenVersion = mvn -version | Select-String "Apache Maven"
    Write-Host "✅ Maven found: $mavenVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ Maven not found!" -ForegroundColor Red
    exit 1
}

# Test 3: Check jpackage
Write-Host "`n3. Testing jpackage availability..." -ForegroundColor Yellow
try {
    $jpackageVersion = jpackage --version 2>&1
    Write-Host "✅ jpackage found: $jpackageVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ jpackage not found! Please install Java 17+" -ForegroundColor Red
    exit 1
}

# Test 4: Check source files
Write-Host "`n4. Checking source files..." -ForegroundColor Yellow
$requiredFiles = @(
    "src/main/java/org/example/snakegame/GameApplication.java",
    "src/main/java/org/example/snakegame/snake/SnakeGame.java",
    "src/main/java/org/example/snakegame/pong/PongGame.java",
    "pom.xml"
)

foreach ($file in $requiredFiles) {
    if (Test-Path $file) {
        Write-Host "✅ Found: $file" -ForegroundColor Green
    } else {
        Write-Host "❌ Missing: $file" -ForegroundColor Red
        exit 1
    }
}

# Test 5: Try Maven compile
Write-Host "`n5. Testing Maven compilation..." -ForegroundColor Yellow
mvn clean compile -q
if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Maven compilation successful" -ForegroundColor Green
} else {
    Write-Host "❌ Maven compilation failed!" -ForegroundColor Red
    exit 1
}

# Test 6: Check JAR creation
Write-Host "`n6. Testing JAR creation..." -ForegroundColor Yellow
mvn package -DskipTests -q
if ($LASTEXITCODE -eq 0 -and (Test-Path "target/arcade-game-1.0-SNAPSHOT.jar")) {
    Write-Host "✅ JAR created successfully" -ForegroundColor Green
    
    # Test JAR execution
    Write-Host "`n7. Testing JAR execution..." -ForegroundColor Yellow
    try {
        $process = Start-Process -FilePath "java" -ArgumentList "-jar", "target/arcade-game-1.0-SNAPSHOT.jar" -PassThru -WindowStyle Hidden
        Start-Sleep -Seconds 2
        if (!$process.HasExited) {
            $process.Kill()
            Write-Host "✅ JAR starts successfully" -ForegroundColor Green
        } else {
            Write-Host "❌ JAR failed to start" -ForegroundColor Red
            exit 1
        }
    } catch {
        Write-Host "❌ Error testing JAR execution" -ForegroundColor Red
        exit 1
    }
} else {
    Write-Host "❌ JAR creation failed!" -ForegroundColor Red
    exit 1
}

Write-Host "`n========================================" -ForegroundColor Green
Write-Host "✅ ALL TESTS PASSED!" -ForegroundColor Green
Write-Host "Ready to build executable!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host "Run: .\build-executable.ps1" -ForegroundColor Cyan
