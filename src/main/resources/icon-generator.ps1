# Script pour générer une icône simple si nécessaire
# Note: Ce script est optionnel - l'application fonctionnera sans icône

Write-Host "Icon Generator for Retro Arcade" -ForegroundColor Cyan
Write-Host "=================================" -ForegroundColor Cyan

# Créer une icône simple avec PowerShell (fallback)
# Pour une vraie icône, utilisez un outil comme GIMP + plugin ICO

$iconPath = "src/main/resources/icon.ico"

if (Test-Path $iconPath) {
    Write-Host "Icon already exists at $iconPath" -ForegroundColor Green
} else {
    Write-Host "No icon found at $iconPath" -ForegroundColor Yellow
    Write-Host "To create a custom icon:" -ForegroundColor Cyan
    Write-Host "1. Create a 256x256px PNG image" -ForegroundColor White
    Write-Host "2. Use an online converter or GIMP to convert to .ico" -ForegroundColor White
    Write-Host "3. Save as src/main/resources/icon.ico" -ForegroundColor White
    Write-Host "4. Re-run the build script" -ForegroundColor White
    Write-Host ""
    Write-Host "The build will work without icon - it will use the default Windows icon." -ForegroundColor Green
}
