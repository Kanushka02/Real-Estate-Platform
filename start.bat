@echo off
REM Real Estate Platform Startup Script for Windows

echo.
echo ============================================
echo   Real Estate Platform Startup
echo ============================================
echo.

REM Check if Java is installed
where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Java not found. Please install Java 17 or higher.
    pause
    exit /b 1
)

REM Check if Maven is installed
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Maven not found. Please install Maven.
    pause
    exit /b 1
)

REM Check if Node.js is installed
where node >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Node.js not found. Please install Node.js 16 or higher.
    pause
    exit /b 1
)

REM Check if npm is installed
where npm >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] npm not found. Please install npm.
    pause
    exit /b 1
)

echo [OK] All prerequisites met!
echo.

REM Start PostgreSQL with Docker (optional)
where docker >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    echo [INFO] Starting PostgreSQL with Docker...
    docker-compose up -d postgres
    echo [INFO] Waiting for PostgreSQL to be ready...
    timeout /t 10 /nobreak >nul
) else (
    echo [WARNING] Docker not found. Make sure PostgreSQL is running manually.
)

echo.
echo [INFO] Starting Backend...
start "Real Estate Backend" cmd /k "cd backend && mvn spring-boot:run"

echo [INFO] Waiting for backend to start...
timeout /t 20 /nobreak >nul

REM Install frontend dependencies if needed
if not exist "frontend\node_modules" (
    echo.
    echo [INFO] Installing frontend dependencies...
    cd frontend
    call npm install
    cd ..
)

echo.
echo [INFO] Starting Frontend...
start "Real Estate Frontend" cmd /k "cd frontend && npm start"

echo.
echo ============================================
echo   Application Started Successfully!
echo ============================================
echo.
echo   Frontend: http://localhost:3000
echo   Backend:  http://localhost:8080
echo.
echo   Close this window to keep services running
echo   Or press any key to stop all services
echo ============================================
echo.

pause

REM Stop services when user presses a key
echo.
echo [INFO] Stopping services...
taskkill /FI "WindowTitle eq Real Estate Backend*" /F >nul 2>nul
taskkill /FI "WindowTitle eq Real Estate Frontend*" /F >nul 2>nul

echo [INFO] Services stopped.
pause

