# Client Handover & IP Protection Guide
## Native Compiled Backend (Nuitka C++ Compilation)

This guide explains how to compile, protect, and deliver the backend to your client without exposing any Python source code, with dedicated instructions for both **Windows** and **macOS / Linux**.

---

## 1. Why Nuitka Native Compilation?

| Traditional Delivery (.py) | Nuitka Compiled Delivery |
|---|---|
| Plaintext source code exposed to client | **Native machine code (PE `.exe` on Windows / Mach-O on macOS / ELF on Linux)** |
| Easily readable, editable, and copyable | **Python source code does not exist** |
| Client must install Python, pip, virtualenv, and C headers | **Standalone binary — no Python installation required** |
| Fragile dependency version conflicts | **All dependencies bundled statically inside the package** |

Nuitka translates the Python Abstract Syntax Tree (AST) into **C++ source code** and invokes the native C++ compiler (Apple Clang on macOS, MSVC or MinGW64 on Windows, GCC on Linux) to compile it directly into CPU machine instructions. Reverse engineering requires low-level binary disassembly tools (such as Ghidra or IDA Pro) and cannot be decompiled back into Python.

---

## 2. Multi-Platform Binary Matrix

Native compiled binaries are platform and architecture specific:

| Target Client OS | Binary Format | Compilation Machine | Output File / Bundle |
|---|---|---|---|
| **Windows 10 / 11 / Server (x86_64)** | PE Executable (`.exe`) | Compile on **Windows** | `craft-backend-windows-bundle.zip` (`craft-backend.exe`) |
| **macOS (Apple Silicon M1/M2/M3/M4)** | Mach-O 64-bit arm64 | Compile on **macOS** | `craft-backend-client-bundle.tar.gz` (`craft-backend`) |
| **Linux (Ubuntu / Debian / RHEL)** | ELF 64-bit | Compile on **Linux** | `craft-backend-linux-bundle.tar.gz` (`craft-backend`) |

---

## 3. Compiling on Windows (`.exe` Binary)

### A. Prerequisites on Windows
1. **Python 3.10, 3.11, or 3.12** installed from [python.org](https://www.python.org/downloads/)
   - *Important*: Check the box **"Add python.exe to PATH"** during installation.
2. **C++ Compiler**:
   - Either **Visual Studio C++ Build Tools** (recommended for production), OR
   - **MinGW64** (Nuitka will **automatically download and configure MinGW64** if Visual Studio is not installed).

---

### B. Option 1: One-Click Automated Script (Recommended)

Transfer or clone the project to your Windows machine, open **Command Prompt** as Administrator or standard user in the project folder, and run:

```cmd
scripts\compile_windows.bat
```

Or in **PowerShell**:
```powershell
.\scripts\compile_windows.ps1
```

**What the script does automatically**:
1. Detects Python 3 and activates/creates `backend\venv`.
2. Installs backend dependencies + `nuitka`, `ordered-set`, `zstandard`.
3. Invokes Nuitka with `--standalone`, `--assume-yes-for-downloads`, and all required packages (`app`, `uvicorn`, `sqlalchemy`, `pydantic`, `pymysql`, `cryptography`).
4. Generates `dist_backend\run_server.dist\craft-backend.exe`.
5. Assembles a clean `craft-backend-windows-bundle\` folder containing only the `.exe`, required runtime DLLs, `.env.example`, and `README.txt`.
6. Compresses the folder into **`craft-backend-windows-bundle.zip`** ready for immediate handover to the client.

---

### C. Option 2: Manual Step-by-Step Compilation in Windows CMD

If you prefer to run the commands manually:

```cmd
:: 1. Navigate to project root
cd /d C:\path\to\CraftNGI

:: 2. Set up virtual environment
python -m venv backend\venv
call backend\venv\Scripts\activate.bat

:: 3. Install dependencies and Nuitka
python -m pip install --upgrade pip
pip install -r backend\requirements.txt nuitka ordered-set zstandard

:: 4. Compile with Nuitka
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

:: 5. Create clean client bundle
mkdir craft-backend-windows-bundle
xcopy /E /I /Q dist_backend\run_server.dist\* craft-backend-windows-bundle\
copy /Y backend\.env.example craft-backend-windows-bundle\.env.example
```

---

### D. Option 3: GitHub Actions Automated Cloud Build (Zero Setup)

You can also generate the Windows `.exe` bundle without compiling locally, using the automated GitHub Actions workflow:

1. Push your repository to GitHub.
2. In your GitHub repository, navigate to the **Actions** tab.
3. Select **"Build Windows Native Backend Binary"** on the left.
4. Click **Run workflow** -> Select `main` or `master` branch.
5. GitHub will spin up a fresh Windows runner, run the pytest suite, compile with Nuitka, verify zero source files exist, and upload **`craft-backend-windows-bundle.zip`** as a downloadable artifact.
6. The workflow configuration is located at [`.github/workflows/build-windows.yml`](file:///Users/vaibhav/Projects/CraftNGI/.github/workflows/build-windows.yml).

---

## 4. Compiling on macOS (`Mach-O` Binary)

On macOS, compile and package using the Makefile:

```bash
# 1. Compile backend to native C++ machine code
make compile-backend

# 2. Package into delivery tarball (Zero .py files)
make package-client-bundle
```

Output:
- Archive: `craft-backend-client-bundle.tar.gz`
- Executable: `craft-backend`

---

## 5. Instructions for Your Windows Client

Deliver `craft-backend-windows-bundle.zip` to the client. Provide these instructions:

### A. Quick Start (Windows)
1. **Extract ZIP**: Extract `craft-backend-windows-bundle.zip` to any folder (e.g. `C:\CraftBackend`).
2. **Configure Database (Optional)**:
   - Copy `.env.example` to `.env`:
     ```cmd
     copy .env.example .env
     ```
   - By default, it uses SQLite (`craft_products.db`).
   - For MySQL, edit `.env`:
     ```env
     DATABASE_URL=mysql+pymysql://username:password@mysql-server:3306/craft_gi_db
     PORT=8000
     HOST=0.0.0.0
     ```
3. **Seed Initial Data (Optional)**:
   ```cmd
   craft-backend.exe --seed
   ```
4. **Start the Server**:
   ```cmd
   craft-backend.exe --host 0.0.0.0 --port 8000
   ```
5. **Verify in Browser**:
   - Swagger Interactive Documentation: [http://localhost:8000/docs](http://localhost:8000/docs)
   - Health Check: [http://localhost:8000/health](http://localhost:8000/health)

---

### B. Running as a Persistent Windows Service (Production)

To ensure the backend runs continuously in the background and restarts automatically upon server reboot:

#### Method 1: Using NSSM (Non-Sucking Service Manager — Recommended)
1. Download [NSSM](https://nssm.cc/download).
2. Open Administrator Command Prompt:
   ```cmd
   nssm install CraftBackend "C:\CraftBackend\craft-backend.exe" "--host 0.0.0.0 --port 8000"
   nssm set CraftBackend AppDirectory "C:\CraftBackend"
   nssm start CraftBackend
   ```
3. The service now runs continuously as a native Windows service.

#### Method 2: Windows Task Scheduler
1. Open **Task Scheduler** (`taskschd.msc`).
2. Create Basic Task -> Trigger: **"When the computer starts"**.
3. Action: **"Start a program"** -> Select `C:\CraftBackend\craft-backend.exe` and arguments `--host 0.0.0.0 --port 8000`.

---

## 6. Security & IP Protection Verification

Before handing the bundle to your client, you can verify that zero source code exists in the delivery package:

- **Windows PowerShell**:
  ```powershell
  Get-ChildItem -Recurse craft-backend-windows-bundle -Filter *.py
  # Output: empty (No .py files found)
  ```
- **macOS / Linux**:
  ```bash
  find craft-backend-client-bundle -name "*.py"
  # Output: empty (No .py files found)
  ```

All endpoint logic, database models, schemas, and business algorithms are baked as compiled C++ machine code inside the primary executable.
