@echo off
REM Saves the OpenAPI spec of each service into docs\api\ (Kubernetes / Minikube must be running).
REM Usage (from anywhere):  scripts\export-api-docs.cmd
setlocal
cd /d "%~dp0.."
if not exist docs\api mkdir docs\api

call :export catalog-service 8081
call :export order-service 8082
call :export rating-service 8083
call :export notification-service 8084

echo.
dir docs\api
goto :eof

:export
echo Exporting %1 ...
start "pf-%1" /min kubectl port-forward -n cake-delight svc/%1 %2:%2
timeout /t 6 /nobreak >nul
curl -s -f http://localhost:%2/v3/api-docs -o docs\api\%1.json
if errorlevel 1 (
    echo    FAILED - is the %1 pod 1/1 Running? Try again in a minute.
) else (
    echo    saved docs\api\%1.json
)
taskkill /fi "WINDOWTITLE eq pf-%1*" /f >nul 2>&1
goto :eof
