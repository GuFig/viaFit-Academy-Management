@echo off
setlocal
powershell -NoLogo -NoProfile -ExecutionPolicy Bypass -File "%~dp0stop-viafit.ps1"
exit /b %errorlevel%
