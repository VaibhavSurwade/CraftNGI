#!/usr/bin/env python3
"""
Production Entrypoint for Compiled Craft & GI Backend Binary.
Can be executed as a standalone native binary without needing Python installed.
"""
import sys
import os
import argparse
import uvicorn

# Ensure the backend directory is in path
CURRENT_DIR = os.path.dirname(os.path.abspath(__file__))
if CURRENT_DIR not in sys.path:
    sys.path.insert(0, CURRENT_DIR)

from app.main import app
from app.config import settings
from app.database import engine, Base
from seed_data import seed

def parse_args():
    parser = argparse.ArgumentParser(
        description="Craft & GI Product Verification Server (Compiled Native Binary)"
    )
    parser.add_argument(
        "--host",
        default=os.getenv("HOST", "0.0.0.0"),
        help="Host interface to bind (default: 0.0.0.0)"
    )
    parser.add_argument(
        "--port",
        type=int,
        default=int(os.getenv("PORT", "8000")),
        help="Port to listen on (default: 8000)"
    )
    parser.add_argument(
        "--seed",
        action="store_true",
        help="Seed the database with sample products and exit"
    )
    return parser.parse_args()

def main():
    args = parse_args()

    # Ensure tables exist
    print(f"[*] Initializing database on: {settings.DATABASE_URL}")
    Base.metadata.create_all(bind=engine)

    if args.seed:
        print("[*] Running database seed...")
        seed()
        sys.exit(0)

    print("\n" + "=" * 65)
    print(f"  {settings.APP_NAME}")
    print("=" * 65)
    print(f"  • Database URL : {settings.DATABASE_URL}")
    print(f"  • Listening on : http://{args.host}:{args.port}")
    print(f"  • API Swagger  : http://{args.host}:{args.port}/docs")
    print("=" * 65 + "\n")

    uvicorn.run(
        app,
        host=args.host,
        port=args.port,
        log_level="info",
        access_log=True
    )

if __name__ == "__main__":
    main()
