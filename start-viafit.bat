@echo off
setlocal
powershell -NoLogo -NoProfile -ExecutionPolicy Bypass -File "%~dp0start-viafit.ps1"
exit /b %errorlevel%
