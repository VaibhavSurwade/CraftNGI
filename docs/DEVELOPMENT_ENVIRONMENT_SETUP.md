# Development Environment Setup Guide
## Craft & GI Product Verification System

This document provides a comprehensive, step-by-step guide for developers setting up a local development environment for the **Craft & GI Product Verification System**, including the **FastAPI Backend**, **Consumer Scanner App (`:app`)**, and **Admin Product Registration App (`:admin`)**.

---

## 1. System Requirements

| Specification | Minimum | Recommended |
|---|---|---|
| **Operating System** | macOS 12+ (Apple Silicon or Intel), Windows 10/11 (64-bit), or Ubuntu 20.04+ | macOS (Apple Silicon M-Series) or Windows 11 |
| **Processor** | 4-Core CPU | 8-Core CPU (for fast Android Gradle builds) |
| **RAM** | 8 GB | 16 GB or higher |
| **Disk Space** | 10 GB free space | 25 GB free space (for Android SDKs, Emulators, Caches) |

---

## 2. Core Toolchain & Prerequisites

The project requires the following primary tools:
1. **Git** (Version control)
2. **Python 3.10, 3.11, or 3.12** (FastAPI backend & automation scripts)
3. **Java Development Kit (JDK) 17** (Required for Android Gradle Plugin 8.3+)
4. **Android Studio** (Hedgehog, Iguana, Jellyfish, or newer) OR **Android SDK Command-line Tools**
5. **GNU Make** (For unified CLI workflow)

---

## 3. OS-Specific Installation

### A. macOS (Using Homebrew)

1. **Install Homebrew** (if not already installed):
   ```bash
   /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
   ```

2. **Install Python, OpenJDK 17, and Tools**:
   ```bash
   brew install git python@3.11 openjdk@17 make
   brew install --cask android-studio
   ```

3. **Configure Environment Variables in `~/.zshrc`**:
   ```bash
   # OpenJDK 17
   export JAVA_HOME="/opt/homebrew/opt/openjdk@17"
   export PATH="$JAVA_HOME/bin:$PATH"

   # Android SDK
   export ANDROID_HOME="$HOME/Library/Android/sdk"
   export PATH="$ANDROID_HOME/platform-tools:$ANDROID_HOME/cmdline-tools/latest/bin:$PATH"
   ```
   Apply changes:
   ```bash
   source ~/.zshrc
   ```

---

### B. Windows (Using Windows Package Manager / Winget)

1. **Install Tools via Windows Terminal (PowerShell as Administrator)**:
   ```powershell
   # Install Git, Python 3.11, OpenJDK 17, and Android Studio
   winget install --id Git.Git -e --source winget
   winget install --id Python.Python.3.11 -e --source winget
   winget install --id Microsoft.OpenJDK.17 -e --source winget
   winget install --id Google.AndroidStudio -e --source winget
   ```

2. **Configure Environment Variables**:
   - Open **System Properties** (`sysdm.cpl`) -> **Advanced** -> **Environment Variables**.
   - Under **System Variables**, ensure:
     - `JAVA_HOME` points to `C:\Program Files\Microsoft\jdk-17.x.x` (or wherever JDK 17 installed).
     - `ANDROID_HOME` points to `C:\Users\<YourUsername>\AppData\Local\Android\Sdk`.
     - Add `%JAVA_HOME%\bin` and `%ANDROID_HOME%\platform-tools` to the `Path` variable.
   - Restart your terminal.

---

### C. Linux (Ubuntu / Debian)

1. **Install Packages**:
   ```bash
   sudo apt update
   sudo apt install -y git python3 python3-venv python3-pip openjdk-17-jdk make build-essential
   ```

2. **Install Android SDK / Studio**:
   - Download Android Studio from [developer.android.com/studio](https://developer.android.com/studio).
   - Set environment variables in `~/.bashrc`:
     ```bash
     export JAVA_HOME="/usr/lib/jvm/java-17-openjdk-amd64"
     export ANDROID_HOME="$HOME/Android/Sdk"
     export PATH="$JAVA_HOME/bin:$ANDROID_HOME/platform-tools:$PATH"
     ```
     Apply with `source ~/.bashrc`.

---

## 4. Setting Up the Backend

### Step 1: Clone the Repository
```bash
git clone https://github.com/your-org/CraftNGI.git
cd CraftNGI
```

### Step 2: Set Up Python Virtual Environment
- **macOS / Linux**:
  ```bash
  python3 -m venv backend/venv
  source backend/venv/bin/activate
  pip install --upgrade pip
  pip install -r backend/requirements.txt pytest httpx
  ```
- **Windows (Command Prompt)**:
  ```cmd
  python -m venv backend\venv
  call backend\venv\Scripts\activate.bat
  pip install --upgrade pip
  pip install -r backend\requirements.txt pytest httpx
  ```

*(Or simply run `make backend-install` on macOS/Linux).*

### Step 3: Environment Configuration (`.env`)
Copy the example environment file:
```bash
cp backend/.env.example backend/.env
```
Default contents:
```env
# SQLite for local development (zero configuration required)
DATABASE_URL=sqlite:///./craft_products.db
APP_NAME="Craft & GI Product Verification API"
DEBUG=True
```
> **Note on MySQL**: If you prefer testing with MySQL, ensure MySQL Server is running and change `DATABASE_URL` to:
> `DATABASE_URL=mysql+pymysql://<user>:<password>@localhost:3306/<database>`

### Step 4: Initialize & Seed the Database
Populate the database with authentic GI product samples (Kashmiri Pashmina, Kanchipuram Silk, Banarasi Brocade, Chanderi):
```bash
make db-seed
# Or: python scripts/db_ops.py seed
```

Verify the records:
```bash
make db-status
# Or: python scripts/db_ops.py status
```

### Step 5: Run the Backend Server
```bash
make backend-run
# Or: uvicorn app.main:app --reload --host 0.0.0.0 --port 8000 (inside backend/)
```
The server will start on `http://0.0.0.0:8000`.

### Step 6: Verify Backend Health & API Docs
Open your browser:
- **Interactive Swagger Documentation**: [http://localhost:8000/docs](http://localhost:8000/docs)
- **Health Check Endpoint**: [http://localhost:8000/health](http://localhost:8000/health)
- **Sample Product Query**: [http://localhost:8000/api/v1/products/8901234567890](http://localhost:8000/api/v1/products/8901234567890)

### Step 7: Run Automated Tests
In a separate terminal:
```bash
make backend-test
# Or: pytest backend/test_api.py -v
```
All tests should pass (4/4 passed).

---

## 5. Setting Up the Android Development Environment

The Android project is a multi-module Gradle project containing:
- `:app`: Consumer Barcode/QR Scanner app (CameraX + ML Kit + Jetpack Compose)
- `:admin`: Artisan & Admin Product Registration app (Forms across all 3 sections + Barcode Scanner)

### Step 1: SDK Components Installation
In Android Studio:
1. Open **SDK Manager** (Settings / Preferences -> Appearance & Behavior -> System Settings -> Android SDK).
2. Under **SDK Platforms**:
   - Select **Android 14.0 ("UpsideDownCake") - API Level 34**.
3. Under **SDK Tools**:
   - Check **Android SDK Build-Tools 34.0.0**.
   - Check **Android SDK Command-line Tools (latest)**.
   - Check **Android SDK Platform-Tools**.

### Step 2: Open Project in Android Studio
1. Launch **Android Studio**.
2. Select **Open** and select the `/android` directory inside the project:
   `CraftNGI/android`
3. Configure the **Gradle JDK**:
   - Navigate to: **Settings -> Build, Execution, Deployment -> Build Tools -> Gradle**.
   - Under **Gradle JDK**, ensure **JDK 17 (version 17.0.x)** is selected.

### Step 3: Configure `local.properties`
Android Studio will automatically generate `android/local.properties`. If building from the command line, create `android/local.properties` with:
- **macOS**: `sdk.dir=/Users/<username>/Library/Android/sdk`
- **Windows**: `sdk.dir=C\:\\Users\\<username>\\AppData\\Local\\Android\\Sdk`
- **Linux**: `sdk.dir=/home/<username>/Android/Sdk`

### Step 4: Compiling APKs via Command Line
You can compile APKs without opening the IDE:
```bash
# Compile Consumer Scanner Debug APK (outputs CraftScanner-debug.apk)
make apk-debug

# Compile Admin Product Registration Debug APK (outputs CraftAdmin-debug.apk)
make apk-admin-debug

# Compile both APKs simultaneously
make apk-all
```

---

## 6. Running & Debugging the Mobile Apps

### Network Configuration Matrix (Mobile -> Backend)

Because the mobile apps must connect to the backend server, the IP address depends on your test setup:

| Test Environment | Backend Host URL in App | Explanation |
|---|---|---|
| **Android Studio Emulator** | `http://10.0.2.2:8000/` | Android emulator loopback alias pointing to the host PC |
| **Physical Android Device (Wi-Fi)** | `http://<HOST_LAN_IP>:8000/` (e.g. `http://192.168.1.150:8000/`) | Phone and PC must be on the same Wi-Fi network |
| **Physical Device (USB Cable)** | `http://localhost:8000/` | Requires running `adb reverse tcp:8000 tcp:8000` |

### Using the In-App Settings Dialog (`⚙️`)
Both the **CraftScanner** app and **CraftAdmin** app have a **Settings gear icon (`⚙️`)** in the top-right header:
1. Tap the **`⚙️`** icon in either app.
2. Enter your backend host URL (e.g., `http://10.0.2.2:8000/` or your computer's local IP).
3. Tap **Save**. The app instantly switches the API endpoint without recompilation!

---

## 7. End-to-End Testing Workflow

### Step 1: Open Sample Barcodes Showcase
The project includes pre-rendered sample barcodes and an interactive web card for testing:
1. Open [**`docs/sample_barcodes.html`**](file:///Users/vaibhav/Projects/CraftNGI/docs/sample_barcodes.html) in your browser.
2. You will see scannable Code-128 and QR codes for:
   - Authentic Kashmiri Pashmina (`8901234567890` / `GI-IN-JK-001`)
   - Kanchipuram Silk Bridal Saree (`8909876543210` / `GI-IN-TN-002`)
   - Banarasi Katan Silk Brocade (`8904567812345` / `GI-IN-UP-003`)
   - Chanderi Handloom Tissue Saree (`123456789012` / `GI-IN-MP-004`)

### Step 2: Test Scanning in Consumer App
1. Launch **CraftScanner** on your device or emulator.
2. Point the camera at the barcode on your screen.
3. The app will detect the barcode via Google ML Kit, fetch product specifications from the backend, and present the 3 verification cards:
   - **Section 1: Fabric & Yarn**
   - **Section 2: Specifications & Dimensions**
   - **Section 3: GI Registry & Artisan Origin**

### Step 3: Test Product Registration in Admin App
1. Launch **CraftAdmin**.
2. Tap **"+ Register New Product"**.
3. Fill in product details across the 3 sections or tap the camera icon to scan a physical barcode.
4. Tap **Submit Product Registration**.
5. Return to terminal and run `make db-status` to see the new product added to the database.

---

## 8. Development Automation Commands (`Makefile`)

The root `Makefile` provides one-command execution for all common development tasks:

```bash
# Database
make db-init              # Initialize schema (SQLite/MySQL)
make db-seed              # Populate sample GI products
make db-status            # View registered products and DB health
make db-reset             # Wipe DB and re-seed from scratch

# Backend
make backend-install      # Setup venv and install dependencies
make backend-run          # Start live-reloading dev server
make backend-test         # Run pytest test suite

# Android Compilation
make apk-debug            # Build Consumer Scanner Debug APK
make apk-release          # Build Consumer Scanner Release APK
make apk-admin-debug      # Build Admin Registration Debug APK
make apk-admin-release    # Build Admin Registration Release APK
make apk-all              # Build both Debug APKs
make apk-clean            # Clean build folders and Gradle caches

# Documentation & Assets
make docs                 # Rebuild OpenAPI JSON and documentation suite
make barcodes             # Re-generate barcode PNGs and HTML test card

# Standalone Native Binaries (Client Handover - No Source Code)
make compile-backend      # Compile backend to native C++ machine code
make package-client-bundle # Create client delivery tarball
```

---

## 9. Common Troubleshooting & FAQs

### Q1: `Unsupported class file major version` during Gradle build
- **Cause**: Android Gradle Plugin 8.3 requires Java 17. You are running Java 21+ or Java 8/11.
- **Fix**: Check `java -version`. Ensure `JAVA_HOME` points to OpenJDK 17 (`/opt/homebrew/opt/openjdk@17` on macOS or `C:\Program Files\...\jdk-17` on Windows). In Android Studio, set **Gradle JDK** to JDK 17.

### Q2: Android app displays `Network Error` or fails to connect
- **Cause**: The phone cannot reach `http://localhost:8000`.
- **Fix**:
  - If running in Emulator: tap the `⚙️` Settings icon and enter `http://10.0.2.2:8000/`.
  - If running on a physical phone: connect your phone to the same Wi-Fi as your computer, find your computer's IP (`ipconfig` on Windows, `ifconfig` or `ip a` on Mac/Linux), and enter `http://<YOUR_IP>:8000/` in the in-app Settings.
  - Or use ADB reverse over USB: `adb reverse tcp:8000 tcp:8000`.

### Q3: Camera shows a black screen in the Emulator
- **Cause**: Android Studio Emulator virtual camera is disabled or unconfigured.
- **Fix**: Open Android Virtual Device (AVD) settings -> Click **Edit** (Pencil) -> **Show Advanced Settings** -> Set **Front Camera** and **Back Camera** to **Webcam0** (uses your laptop webcam) or **VirtualScene**. Alternatively, test with the **Manual Barcode Entry** button in the app.

### Q4: Database changes not showing or need a clean slate
- **Fix**: Run `make db-reset`. This safely recreates the schema and re-seeds all default GI craft products.
