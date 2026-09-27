@echo off
setlocal enabledelayedexpansion

echo ==============================================================================
echo    CRAFT & GI BACKEND - WINDOWS NATIVE BINARY COMPILATION (NUITKA)
echo ==============================================================================
echo.

:: 1. Check Python
where python >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERROR] Python 3 is not installed or not in PATH!
    echo Please install Python 3.10, 3.11, or 3.12 from python.org and check "Add to PATH".
    exit /b 1
)

echo [*] Python detected:
python --version

:: 2. Set up / Activate Virtual Environment
if not exist "backend\venv" (
    echo [*] Creating virtual environment at backend\venv...
    python -m venv backend\venv
)

echo [*] Activating virtual environment...
call backend\venv\Scripts\activate.bat

:: 3. Install Dependencies & Nuitka
echo [*] Installing dependencies and Nuitka...
python -m pip install --upgrade pip
pip install -r backend\requirements.txt nuitka ordered-set zstandard

:: 4. Clean previous dist
if exist "dist_backend" (
    echo [*] Cleaning previous build output in dist_backend...
    rmdir /s /q dist_backend
)
mkdir dist_backend

:: 5. Compile with Nuitka
echo [*] Compiling Python backend to native Windows C++ machine code...
echo     (Nuitka will auto-download MinGW64 compiler if Visual Studio C++ is not found)
echo.

python -m nuitka ^
    --standalone ^
    --assume-yes-for-downloads ^
    --include-package=app ^
    --include-package=uvicorn ^
    --include-package=sqlalchemy ^
    --include-package=pydantic ^
    --include-package=pymysql ^
    --include-package=cryptography ^
    --follow-imports ^
    --output-dir=dist_backend ^
    -o craft-backend.exe ^
    backend\run_server.py

if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Compilation failed! Review the compiler output above.
    exit /b %errorlevel%
)

:: 6. Create Client Delivery Package (No Python source files)
echo.
echo [*] Creating Client Delivery Package (Zero .py source files)...

if exist "craft-backend-windows-bundle" rmdir /s /q "craft-backend-windows-bundle"
mkdir "craft-backend-windows-bundle"

xcopy /E /I /Q "dist_backend\run_server.dist\*" "craft-backend-windows-bundle\"
copy /Y "backend\.env.example" "craft-backend-windows-bundle\.env.example"

:: Write client instructions
(
echo # Craft & GI Backend Server ^(Windows^)
echo.
echo Standalone native executable. No Python installation required.
echo.
echo ## Quick Start
echo 1. Copy environment template:
echo    copy .env.example .env
echo.
echo 2. ^(Optional^) Seed default products:
echo    craft-backend.exe --seed
echo.
echo 3. Start the server:
echo    craft-backend.exe --host 0.0.0.0 --port 8000
echo.
echo 4. Access API docs:
echo    http://localhost:8000/docs
) > "craft-backend-windows-bundle\README.txt"

:: 7. Create Zip archive
echo [*] Compressing into craft-backend-windows-bundle.zip...
powershell -Command "Compress-Archive -Path 'craft-backend-windows-bundle\*' -DestinationPath 'craft-backend-windows-bundle.zip' -Force"

echo.
echo ==============================================================================
echo [SUCCESS] Windows Native Binary Compiled Successfully!
echo ==============================================================================
echo  - Output Directory : craft-backend-windows-bundle\
echo  - Executable       : craft-backend-windows-bundle\craft-backend.exe
echo  - Client ZIP File  : craft-backend-windows-bundle.zip
echo  - Source Code      : 100%% C++ machine code (Zero .py files)
echo ==============================================================================
echo.
