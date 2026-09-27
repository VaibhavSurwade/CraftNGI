# Craft & GI Product Verification Backend API

FastAPI backend that manages handcrafted and Geographical Indication (GI) textile & product details, accessible via barcode lookup.

## Features
- **Database Flexibility**: Default zero-configuration **SQLite** for localhost testing, and one-variable switch to **MySQL** for staging/production via SQLAlchemy.
- **Auto API Documentation**: Swagger UI at `/docs` and ReDoc at `/redoc`.
- **Pre-seeded GI Products**: Includes realistic Indian GI crafts (Kashmiri Pashmina, Kanchipuram Silk, Banarasi Brocade, Chanderi Tissue).

---

## Quickstart (Localhost with SQLite)

### 1. Set up Virtual Environment & Install Dependencies
```bash
cd backend
python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt
```

### 2. Seed Sample Barcodes & Products
```bash
python seed_data.py
```
This creates `craft_products.db` (SQLite) and inserts sample GI records.

### 3. Start the API Server
```bash
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```
- API Base: `http://localhost:8000`
- Interactive Swagger UI: `http://localhost:8000/docs`

---

## Switching to MySQL (Production)

To connect to MySQL, configure `DATABASE_URL` in `.env` or as an environment variable:
```bash
DATABASE_URL=mysql+pymysql://<username>:<password>@<host>:3306/<database_name>
```
Example:
```bash
DATABASE_URL=mysql+pymysql://root:mypassword@localhost:3306/craft_gi_db
```
When running against MySQL, tables will automatically be created on launch, and you can seed the database with:
```bash
python seed_data.py
```

---

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/` | Welcome status |
| `GET` | `/health` | Healthcheck |
| `GET` | `/api/products` | List all registered products |
| `GET` | `/api/products/{barcode}` | Fetch complete product details by barcode |
| `POST` | `/api/products` | Register a new product |

---

## Pre-seeded Sample Barcodes for Testing

| Barcode | Product Name | GI Region |
|---|---|---|
| `8901234567890` | Authentic Kashmiri Pashmina Stole | Jammu & Kashmir |
| `8909876543210` | Kanchipuram Silk Bridal Saree | Tamil Nadu |
| `8904567812345` | Banarasi Katan Silk Brocade | Uttar Pradesh |
| `123456789012` | Chanderi Handloom Tissue Saree | Madhya Pradesh |

---

## Run Automated Tests
```bash
pytest test_api.py
```
