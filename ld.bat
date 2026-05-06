@echo off
set "SCRIPT_DIR=%~dp0"
if not defined QD_NPM_PREFIX (
    set "QD_NPM_PREFIX=%SCRIPT_DIR%lib"
)
if not defined NODE_PATH (
    set "NODE_PATH=%QD_NPM_PREFIX%\node_modules"
)
"%SCRIPT_DIR%LayerDocs-Source\build\install\layerdocs\bin\layerdocs.bat" %*
