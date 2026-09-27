from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)

def test_root():
    response = client.get("/")
    assert response.status_code == 200
    assert response.json()["health"] == "ok"

def test_health():
    response = client.get("/health")
    assert response.status_code == 200
    assert response.json() == {"status": "healthy"}

def test_get_product_by_valid_barcode():
    response = client.get("/api/products/8901234567890")
    assert response.status_code == 200
    data = response.json()
    assert data["barcode"] == "8901234567890"
    assert "Pashmina" in data["product_name"]
    # Check Section 1 fields
    assert "fabric_composition" in data
    assert "yarn_spinning" in data
    assert "fabric_weave" in data
    assert "embroidery_yarn" in data
    # Check Section 2 fields
    assert "design" in data
    assert "size" in data
    assert "weight" in data
    assert "embroidery" in data
    # Check Section 3 fields
    assert "authorized_gi_user" in data
    assert "address_authorized_gi_user" in data
    assert "artisan_or_weaver" in data
    assert "address_artisan_or_weaver" in data

def test_get_product_by_invalid_barcode():
    response = client.get("/api/products/NON_EXISTENT_BARCODE_99999")
    assert response.status_code == 404
    assert "not found" in response.json()["detail"].lower()
