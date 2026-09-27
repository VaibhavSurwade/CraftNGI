from typing import List
from fastapi import FastAPI, Depends, HTTPException, status
from fastapi.middleware.cors import CORSMiddleware
from sqlalchemy.orm import Session

from app import models, schemas, crud
from app.database import engine, get_db
from app.config import settings

# Create database tables if they do not exist
models.Base.metadata.create_all(bind=engine)

app = FastAPI(
    title=settings.APP_NAME,
    version="1.0.0",
    description="Backend API for scanning barcodes and retrieving GI craft product details."
)

# CORS configuration
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/", tags=["Root"])
def root():
    return {
        "message": f"Welcome to {settings.APP_NAME}",
        "docs_url": "/docs",
        "health": "ok"
    }

@app.get("/health", tags=["Health"])
def health_check():
    return {"status": "healthy"}

@app.get("/api/products", response_model=List[schemas.ProductResponse], tags=["Products"])
def list_products(skip: int = 0, limit: int = 100, db: Session = Depends(get_db)):
    """Retrieve all products in the database."""
    return crud.get_products(db, skip=skip, limit=limit)

@app.get("/api/products/{barcode}", response_model=schemas.ProductResponse, tags=["Products"])
def get_product_by_barcode(barcode: str, db: Session = Depends(get_db)):
    """Fetch product specifications and GI authenticity details by scanned barcode."""
    product = crud.get_product_by_barcode(db, barcode=barcode)
    if not product:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Product with barcode '{barcode}' not found in the registry."
        )
    return product

@app.post("/api/products", response_model=schemas.ProductResponse, status_code=status.HTTP_201_CREATED, tags=["Products"])
def create_product(product_in: schemas.ProductCreate, db: Session = Depends(get_db)):
    """Register a new product with barcode and GI specifications."""
    existing = crud.get_product_by_barcode(db, barcode=product_in.barcode)
    if existing:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Product with barcode '{product_in.barcode}' already exists."
        )
    return crud.create_product(db, product_in=product_in)
