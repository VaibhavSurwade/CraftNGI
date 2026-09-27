# ==============================================================================
# CRAFT & GI BACKEND - WINDOWS NATIVE COMPILATION SCRIPT (PowerShell)
# ==============================================================================

Write-Host "==============================================================================" -ForegroundColor Cyan
Write-Host "   CRAFT & GI BACKEND - WINDOWS NATIVE BINARY COMPILATION (NUITKA)             " -ForegroundColor Cyan
Write-Host "==============================================================================" -ForegroundColor Cyan

# 1. Verify Python
$pythonCmd = Get-Command python -ErrorAction SilentlyContinue
if (-not $pythonCmd) {
    Write-Host "[ERROR] Python 3 was not found in PATH." -ForegroundColor Red
    Write-Host "Please install Python 3.10, 3.11, or 3.12 from python.org and check 'Add to PATH'." -ForegroundColor Yellow
    exit 1
}

Write-Host "[*] Python version:" -ForegroundColor Green
python --version

# 2. Virtual Environment
if (-not (Test-Path "backend\venv")) {
    Write-Host "[*] Creating virtual environment at backend\venv..." -ForegroundColor Yellow
    python -m venv backend\venv
}

Write-Host "[*] Activating virtual environment..." -ForegroundColor Green
& ".\backend\venv\Scripts\Activate.ps1"

# 3. Install Requirements
Write-Host "[*] Installing dependencies and Nuitka compiler..." -ForegroundColor Yellow
python -m pip install --upgrade pip
pip install -r backend\requirements.txt nuitka ordered-set zstandard

# 4. Clean previous dist
if (Test-Path "dist_backend") {
    Remove-Item -Recurse -Force "dist_backend"
}
New-Item -ItemType Directory -Force -Path "dist_backend" | Out-Null

# 5. Compile with Nuitka
Write-Host "[*] Compiling to native Windows C++ machine code..." -ForegroundColor Cyan
Write-Host "    (Nuitka will auto-download MinGW64 if Visual Studio C++ is not found)" -ForegroundColor DarkGray

python -m nuitka `
    --standalone `
    --assume-yes-for-downloads `
    --include-package=app `
    --include-package=uvicorn `
    --include-package=sqlalchemy `
    --include-package=pydantic `
    --include-package=pymysql `
    --include-package=cryptography `
    --follow-imports `
    --output-dir=dist_backend `
    -o craft-backend.exe `
    backend\run_server.py

if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Compilation failed. See compiler messages above." -ForegroundColor Red
    exit $LASTEXITCODE
}

# 6. Assemble Client Bundle
Write-Host "[*] Assembling Client Delivery Package (Zero .py source files)..." -ForegroundColor Yellow
$bundleDir = "craft-backend-windows-bundle"
if (Test-Path $bundleDir) {
    Remove-Item -Recurse -Force $bundleDir
}
New-Item -ItemType Directory -Force -Path $bundleDir | Out-Null

Copy-Item -Recurse -Force "dist_backend\run_server.dist\*" $bundleDir
Copy-Item -Force "backend\.env.example" "$bundleDir\.env.example"

$readmeContent = @"
# Craft & GI Backend Server (Windows)

Standalone native executable. No Python installation required.

## Quick Start
1. Copy environment template:
   copy .env.example .env

2. (Optional) Seed default products:
   craft-backend.exe --seed

3. Start the server:
   craft-backend.exe --host 0.0.0.0 --port 8000

4. Access API docs:
   http://localhost:8000/docs
"@
Set-Content -Path "$bundleDir\README.txt" -Value $readmeContent

# 7. Compress into ZIP
$zipPath = "craft-backend-windows-bundle.zip"
if (Test-Path $zipPath) { Remove-Item -Force $zipPath }
Write-Host "[*] Compressing package into $zipPath..." -ForegroundColor Yellow
Compress-Archive -Path "$bundleDir\*" -DestinationPath $zipPath -Force

Write-Host ""
Write-Host "==============================================================================" -ForegroundColor Green
Write-Host "[SUCCESS] Windows Native Binary Compiled Successfully!" -ForegroundColor Green
Write-Host "==============================================================================" -ForegroundColor Green
Write-Host "  - Executable folder : $bundleDir\"
Write-Host "  - Native Binary     : $bundleDir\craft-backend.exe"
Write-Host "  - Client Zip        : $zipPath"
Write-Host "  - Source Code       : 100% C++ Machine Code (Zero .py source code exposed)"
Write-Host "==============================================================================" -ForegroundColor Green
