# Craft & GI Barcode Scanner System

An end-to-end mobile barcode scanning solution for verifying and displaying handcrafted textile and Geographical Indication (GI) product details.

The repository contains:
1. **`backend/`**: A high-performance **FastAPI** REST API with **SQLAlchemy ORM**, supporting zero-config **SQLite** for localhost development and a one-variable switch to **MySQL** for production.
2. **`android/`**: A native **Kotlin + Jetpack Compose** Android app utilizing **CameraX** and **Google ML Kit** for real-time barcode scanning, structured around the 3 designated product specification sections.

---

## Data Schema & Sections

Every product in the database contains the scanned barcode identifier along with the following required specifications:

| Section | Field Name | Description |
|---|---|---|
| **Section 1: Fabric & Yarn** | `fabric_composition` | Raw fiber / composition (e.g. 100% Changthangi Pashmina Wool, Mulberry Silk) |
| | `yarn_spinning` | Spinning process (e.g. Hand Spun Charkha, 3-ply twisted silk) |
| | `fabric_weave` | Weaving technique (e.g. Diamond weave Chashm-e-Bulbul, Korvai interlocking) |
| | `embroidery_yarn` | Yarn used in ornamentation (e.g. Pure Mulberry Silk floss, tested Zari) |
| **Section 2: Specifications** | `design` | Traditional motifs and layout (e.g. Paisley Kalka, Mayil peacock border) |
| | `size` | Dimensions (e.g. 200 cm x 70 cm stole, 6.2m saree) |
| | `weight` | Physical weight (e.g. 145g, 820g) |
| | `embroidery` | Needlework/brocade technique (e.g. Sozni needlework, Meenakari Resham) |
| **Section 3: GI & Artisan Origin**| `authorized_gi_user` | Registered GI Society or Institute with Registry Number |
| | `address_authorized_gi_user` | Official registered address of the GI user organization |
| | `artisan_or_weaver` | Master artisan, weaver name or guild |
| | `address_artisan_or_weaver` | Workshop or artisan residential address |

---

## Directory Overview

```
.
├── backend/
│   ├── app/
│   │   ├── config.py         # App configuration & DB URL resolver
│   │   ├── database.py       # Engine & Session factory (SQLite / MySQL)
│   │   ├── models.py         # SQLAlchemy Product model
│   │   ├── schemas.py        # Pydantic validation & response models
│   │   ├── crud.py           # Database query functions
│   │   └── main.py           # FastAPI application & REST endpoints
│   ├── seed_data.py          # Pre-populates sample GI products
│   ├── test_api.py           # Automated test suite (pytest)
│   ├── requirements.txt      # Python dependencies
│   ├── .env.example          # Environment variable template
│   └── README.md
│
├── android/
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── AndroidManifest.xml
│   │   │   ├── java/com/example/craftscanner/
│   │   │   │   ├── MainActivity.kt               # Entry Activity & Camera permissions
│   │   │   │   ├── data/
│   │   │   │   │   ├── model/ProductDetail.kt    # Moshi data classes
│   │   │   │   │   └── remote/ProductApiService.kt # Retrofit API Client
│   │   │   │   ├── ui/
│   │   │   │   │   ├── CraftScannerApp.kt        # App navigation & state handling
│   │   │   │   │   ├── components/BarcodeScannerView.kt # CameraX + ML Kit scanning
│   │   │   │   │   ├── components/DetailSectionCard.kt  # Styled 3-section card
│   │   │   │   │   ├── screens/ScannerScreen.kt
│   │   │   │   │   ├── screens/ProductDetailScreen.kt
│   │   │   │   │   └── theme/                    # Material 3 styling & palette
│   │   │   │   └── viewmodel/ScannerViewModel.kt
│   │   │   └── res/
│   │   └── build.gradle.kts
│   ├── gradle/libs.versions.toml
│   ├── build.gradle.kts
│   ├── settings.gradle.kts
│   └── README.md
└── README.md
```

---

## Running the Backend

```bash
cd backend
source venv/bin/activate
# Seed database with sample products (creates craft_products.db)
python seed_data.py
# Start FastAPI server on port 8000
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

- API Docs (Swagger): [http://localhost:8000/docs](http://localhost:8000/docs)
- Test suite: `pytest test_api.py`

### To Switch to MySQL (Production)
Set `DATABASE_URL` in `backend/.env`:
```env
DATABASE_URL=mysql+pymysql://username:password@hostname:3306/database_name
```

---

## Running the Android App

1. Open `/android` in **Android Studio**.
2. Run on an Android Emulator or physical device connected over Wi-Fi.
3. If using emulator, `http://10.0.2.2:8000/` is connected automatically.
4. Point the camera at any barcode, or tap **Manual Entry** to test with the pre-seeded samples (e.g. `8901234567890`, `8909876543210`).

---

## Documentation Index

Comprehensive guides for development, architecture, testing, and deployment:

| Guide | Description |
|---|---|
| [**Development Environment Setup**](file:///Users/vaibhav/Projects/CraftNGI/docs/DEVELOPMENT_ENVIRONMENT_SETUP.md) | **Step-by-step developer onboarding (macOS, Windows, Linux), JDK 17, Android Studio & backend setup** |
| [**System Architecture**](file:///Users/vaibhav/Projects/CraftNGI/docs/ARCHITECTURE.md) | High-level architecture, module breakdown, and data flow diagrams |
| [**Database Schema & ERD**](file:///Users/vaibhav/Projects/CraftNGI/docs/DATABASE_SCHEMA.md) | Detailed relational schema, SQLite vs MySQL, and field dictionaries |
| [**API Specification**](file:///Users/vaibhav/Projects/CraftNGI/docs/API_SPECIFICATION.md) | REST API endpoints, request/response models, and status codes |
| [**Deployment & Operations Guide**](file:///Users/vaibhav/Projects/CraftNGI/docs/DEPLOYMENT_AND_OPERATIONS.md) | Gunicorn/Systemd/Nginx production setup, MySQL hosting, and release signing |
| [**Client Handover & IP Protection**](file:///Users/vaibhav/Projects/CraftNGI/docs/CLIENT_HANDOVER_GUIDE.md) | Standalone C++ binary compilation (Nuitka) for Windows (`.exe`) and macOS |
| [**Sample Barcodes Showcase**](file:///Users/vaibhav/Projects/CraftNGI/docs/sample_barcodes.html) | Interactive HTML test cards with scannable Code-128 & QR barcodes |
