# REST API Specification Reference
## Craft & GI Product Verification System

- **API Base URL (Local)**: `http://localhost:8000`
- **Interactive Swagger UI**: `http://localhost:8000/docs`
- **ReDoc Interactive Documentation**: `http://localhost:8000/redoc`
- **OpenAPI Schema (JSON)**: `docs/openapi.json`

---

## Endpoints Summary

| Method | Route | Description | Auth |
|---|---|---|---|
| `GET` | `/` | Service root and health confirmation | None |
| `GET` | `/health` | Kubernetes / container liveness probe | None |
| `GET` | `/api/products` | Retrieve all registered products with pagination | None |
| `GET` | `/api/products/{barcode}` | Retrieve verified product details by scanned barcode | None |
| `POST` | `/api/products` | Register a new product into the database | None |

---

## Endpoint Details

### 1. Get Product by Barcode
Fetches complete product and GI authenticity details matching the scanned barcode.

- **URL**: `/api/products/{barcode}`
- **Method**: `GET`
- **Path Parameters**:
  - `barcode` *(string, required)*: The exact barcode read from the physical label (e.g. `8901234567890`).

#### Success Response: `200 OK`
```json
{
  "id": 1,
  "barcode": "8901234567890",
  "product_name": "Authentic Kashmiri Pashmina Stole",
  "category": "GI Registered Handicraft",
  "fabric_composition": "100% Pure Changthangi Pashmina Wool",
  "yarn_spinning": "Hand Spun (Traditional Charkha / Yinder)",
  "fabric_weave": "Handloom Diamond Weave (Chashm-e-Bulbul)",
  "embroidery_yarn": "Pure Mulberry Silk Thread",
  "design": "Traditional Paisley (Kalka / Badam motif) with Hashiya border",
  "size": "200 cm x 70 cm (Stole)",
  "weight": "145 grams",
  "embroidery": "Sozni Needlework (Finely handcrafted)",
  "authorized_gi_user": "Craft Development Institute (CDI) Srinagar / GI Reg. No: GI-KASH-PASH-042",
  "address_authorized_gi_user": "Noorbagh Industrial Complex, Srinagar, Jammu & Kashmir - 190002",
  "artisan_or_weaver": "Master Ghulam Mohammad Mir",
  "address_artisan_or_weaver": "Mir Mohalla, Zadibal, Srinagar, Jammu & Kashmir - 190011",
  "created_at": "2026-09-26T18:10:32",
  "updated_at": "2026-09-26T18:10:32"
}
```

#### Error Response: `404 Not Found`
Returned when the barcode is not recognized in the GI registry.
```json
{
  "detail": "Product with barcode '9999999999999' not found in the registry."
}
```

---

### 2. List Registered Products
Lists all registered products in the registry with pagination.

- **URL**: `/api/products?skip=0&limit=100`
- **Method**: `GET`
- **Query Parameters**:
  - `skip` *(integer, optional, default: 0)*: Number of records to skip.
  - `limit` *(integer, optional, default: 100)*: Maximum records to return.

#### Success Response: `200 OK`
Returns an array of product objects matching the schema above.

---

### 3. Register New Product
Adds a new handcrafted product to the verification registry.

- **URL**: `/api/products`
- **Method**: `POST`
- **Headers**: `Content-Type: application/json`
- **Request Body**:
```json
{
  "barcode": "8905555111222",
  "product_name": "Mysore Traditional Silk Saree",
  "category": "GI Pure Silk",
  "fabric_composition": "100% Pure Natural Mulberry Silk",
  "yarn_spinning": "Reeled & Twisted Silk Thread",
  "fabric_weave": "Traditional Handloom Jacquard",
  "embroidery_yarn": "Gold Zari Thread",
  "design": "Kasuti embroidery border motifs",
  "size": "6.25 meters",
  "weight": "650 grams",
  "embroidery": "Kasuti Needlepoint",
  "authorized_gi_user": "Karnataka Silk Industries Corporation (KSIC)",
  "address_authorized_gi_user": "Manandavadi Road, Mysore, Karnataka - 570008",
  "artisan_or_weaver": "Weaver K. Ramachandra",
  "address_artisan_or_weaver": "Weavers Colony, Mysore, Karnataka - 570008"
}
```

#### Success Response: `201 Created`
Returns the created product object including its database `id` and timestamps.

#### Error Response: `400 Bad Request`
Returned if a product with the same barcode already exists.
```json
{
  "detail": "Product with barcode '8905555111222' already exists."
}
```
