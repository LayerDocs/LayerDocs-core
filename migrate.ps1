# LayerDocs Modular Migration Script (Push Only)
# Author: SatyamPote

$ORG = "LayerDocs"
$BASE_DIR = Get-Location

$MODULES = @{
    "LayerDocs-Source" = "LayerDocs-core"
    "LayerDocs-Source/layerdocs-cli" = "LayerDocs-cli"
    "LayerDocs-web" = "LayerDocs-web"
    "LayerDocs-Source/docs" = "LayerDocs-docs"
    "LayerDocs-Source/mock" = "LayerDocs-playground"
}

Write-Host "Starting modular push to GitHub Organization: $ORG..."

foreach ($folder in $MODULES.Keys) {
    $repo = $MODULES[$folder]
    Write-Host "Processing $repo..."
    
    $tempDir = Join-Path $env:TEMP "split_$repo"
    if (Test-Path $tempDir) { Remove-Item $tempDir -Recurse -Force -ErrorAction SilentlyContinue }
    New-Item -ItemType Directory -Path $tempDir
    
    Copy-Item -Path "$BASE_DIR/$folder/*" -Destination $tempDir -Recurse -Force -Exclude ".git"
    
    Push-Location $tempDir
    git init -b main
    git add .
    git commit -m "feat: modular initialization of $repo"
    
    $remoteUrl = "https://github.com/$ORG/$repo.git"
    git remote add origin $remoteUrl
    
    Write-Host "Syncing to $remoteUrl ..."
    git push -u origin main --force
    Pop-Location
    
    Remove-Item $tempDir -Recurse -Force
}

Write-Host "Migration Complete!"
