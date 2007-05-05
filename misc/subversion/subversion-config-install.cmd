@echo off
rem $Id$


copy "%~dp0subversion_config" "%APPDATA%\Subversion\config"
if errorlevel 1 (
    echo Error calling copy
    pause
    exit /b 1
)
