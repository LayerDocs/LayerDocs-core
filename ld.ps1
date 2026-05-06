$ScriptDir = Split-Path $MyInvocation.MyCommand.Path
if (-not $env:QD_NPM_PREFIX) {
    $env:QD_NPM_PREFIX = "$ScriptDir\lib"
}
if (-not $env:NODE_PATH) {
    $env:NODE_PATH = "$env:QD_NPM_PREFIX\node_modules"
}
& "$ScriptDir\LayerDocs-Source\build\install\layerdocs\bin\layerdocs.bat" @args
