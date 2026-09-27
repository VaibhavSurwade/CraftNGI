# Craft & GI Barcode Scanner - Android App

Native Android application built with **Kotlin** and **Jetpack Compose** that scans barcodes using **CameraX & Google ML Kit**, fetches product specifications from the FastAPI backend, and presents the authenticated details across the 3 designated sections.

---

## Key Features

1. **CameraX + Google ML Kit Barcode Scanning**: Real-time barcode detection with animated laser viewfinder reticle and flashlight toggle.
2. **Comprehensive 3-Section Display**:
   - **Section 1**: Fabric Composition, Yarn Spinning, Fabric Weave, Embroidery Yarn.
   - **Section 2**: Design, Size, Weight, Embroidery.
   - **Section 3**: Authorized GI User, Address of Authorized GI User, Artisan/Weaver, Address of Artisan/Weaver.
3. **Manual Code Entry & Quick Test Samples**: Easily test with pre-seeded sample barcodes without needing a printed physical barcode.
4. **Dynamic Server URL Configuration**: Switch between Android Emulator (`10.0.2.2:8000`), local Wi-Fi IP (`192.168.x.x:8000`), or production directly from the app.
5. **Modern Material 3 Theme**: Elegant craft / heritage styling with terracotta, gold, and forest green GI authenticity badges.

---

## Getting Started

### 1. Open Project in Android Studio
1. Launch **Android Studio** (Hedgehog, Iguana, Jellyfish, or newer).
2. Choose **Open** and select the `/android` folder within this repository.
3. Allow Gradle to sync dependencies.

### 2. Backend Connection Settings
By default, the app is configured to talk to `http://10.0.2.2:8000/` (the special loopback address used by Android Studio Emulator to access your computer's `localhost`).

- **If using the Android Emulator**:
  Ensure the backend is running (`uvicorn app.main:app --reload --host 0.0.0.0 --port 8000`). The default `http://10.0.2.2:8000/` will connect automatically.
- **If using a Physical Android Phone**:
  1. Make sure your phone and computer are on the same Wi-Fi network.
  2. Find your computer's local IP address (e.g. `ifconfig | grep "inet "` on Mac, e.g. `192.168.1.15`).
  3. In the app, tap the **Manual Entry / Edit** icon on the top right, tap **Configure Server URL**, and enter `http://192.168.1.15:8000/` (or update default in `NetworkClient.kt`).

### 3. Running & Testing
1. Select your target device or emulator in Android Studio and click **Run (Shift + F10)**.
2. Grant camera permissions when prompted.
3. Point the camera at a barcode, or tap **Manual Entry** to test with the pre-seeded samples:
   - `8901234567890` (Kashmiri Pashmina Stole)
   - `8909876543210` (Kanchipuram Silk Bridal Saree)
   - `8904567812345` (Banarasi Katan Silk Brocade)
   - `123456789012` (Chanderi Handloom Tissue Saree)
