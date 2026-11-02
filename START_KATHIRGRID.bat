@echo off
cd /d "%~dp0"
echo ======================================
echo KathirGrid - Renewable Energy System
echo ======================================
echo.
node -v
if errorlevel 1 (
  echo Node.js is not installed or is not on PATH.
  echo Install Node.js 24 LTS, reopen this window, and try again.
  pause
  exit /b 1
)
echo.
if not exist node_modules (
  echo Installing project dependencies for the first run...
  call npm install
  if errorlevel 1 (
    echo Dependency installation failed. Check your internet connection and Node.js version.
    pause
    exit /b 1
  )
)
echo.
echo Ensure MongoDB Community Server is running.
echo Website: http://127.0.0.1:5501
echo API:     http://127.0.0.1:8081/api/health
echo.
call npm run dev
pause
