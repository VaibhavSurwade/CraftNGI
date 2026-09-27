#!/usr/bin/env python3
"""
Database Management CLI for Craft & GI Verification Backend.
Supports both SQLite (localhost) and MySQL (production).
"""
import sys
import os
import argparse

# Add backend directory to sys.path
BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
BACKEND_DIR = os.path.join(BASE_DIR, "backend")
if BACKEND_DIR not in sys.path:
    sys.path.insert(0, BACKEND_DIR)

from app.database import engine, SessionLocal, Base
from app import models, crud, schemas
from app.config import settings
from seed_data import seed

def init_db():
    """Create all database tables if they do not exist."""
    print(f"[*] Initializing database schema on: {settings.DATABASE_URL}")
    Base.metadata.create_all(bind=engine)
    print("[+] Database tables initialized successfully.")

def status_db():
    """Inspect current database contents and record count."""
    init_db()
    db = SessionLocal()
    products = crud.get_products(db, limit=100)
    print("\n" + "=" * 70)
    print(f" DATABASE STATUS: {settings.DATABASE_URL}")
    print("=" * 70)
    print(f"Total Registered Products: {len(products)}\n")
    if not products:
        print("  (Database is currently empty. Run 'make db-seed' to populate)")
    for p in products:
        print(f"  • [{p.barcode}] {p.product_name}")
        print(f"    - Fabric : {p.fabric_composition}")
        print(f"    - GI User: {p.authorized_gi_user}")
        print(f"    - Weaver : {p.artisan_or_weaver}\n")
    print("=" * 70 + "\n")
    db.close()

def reset_db():
    """Safely drop all tables and re-create them with seed data."""
    print(f"[!] Resetting database on: {settings.DATABASE_URL}")
    Base.metadata.drop_all(bind=engine)
    print("[+] All existing tables dropped.")
    init_db()
    print("[*] Re-seeding database with sample GI products...")
    seed()
    print("[+] Database reset and re-seeded successfully.")

def export_openapi(output_path: str):
    """Export the live OpenAPI 3.1 JSON specification from FastAPI."""
    import json
    from app.main import app
    spec = app.openapi()
    os.makedirs(os.path.dirname(output_path), exist_ok=True)
    with open(output_path, "w", encoding="utf-8") as f:
        json.dump(spec, f, indent=2)
    print(f"[+] Exported OpenAPI JSON specification to: {output_path}")

def main():
    parser = argparse.ArgumentParser(description="Database & Docs CLI Utility")
    parser.add_argument("action", choices=["init", "seed", "reset", "status", "export-openapi"],
                        help="Action to perform")
    parser.add_argument("--output", default="docs/openapi.json", help="Output file for export-openapi")
    args = parser.parse_args()

    if args.action == "init":
        init_db()
    elif args.action == "seed":
        init_db()
        seed()
    elif args.action == "reset":
        reset_db()
    elif args.action == "status":
        status_db()
    elif args.action == "export-openapi":
        export_openapi(args.output)

if __name__ == "__main__":
    main()
