from typing import List, Optional
from sqlalchemy.orm import Session
from app import models, schemas

def get_product_by_barcode(db: Session, barcode: str) -> Optional[models.Product]:
    return db.query(models.Product).filter(models.Product.barcode == barcode.strip()).first()

def get_product_by_id(db: Session, product_id: int) -> Optional[models.Product]:
    return db.query(models.Product).filter(models.Product.id == product_id).first()

def get_products(db: Session, skip: int = 0, limit: int = 100) -> List[models.Product]:
    return db.query(models.Product).offset(skip).limit(limit).all()

def create_product(db: Session, product_in: schemas.ProductCreate) -> models.Product:
    db_product = models.Product(**product_in.model_dump())
    db.add(db_product)
    db.commit()
    db.refresh(db_product)
    return db_product

def update_product(db: Session, db_product: models.Product, product_update: schemas.ProductUpdate) -> models.Product:
    update_data = product_update.model_dump(exclude_unset=True)
    for field, val in update_data.items():
        setattr(db_product, field, val)
    db.commit()
    db.refresh(db_product)
    return db_product

def delete_product(db: Session, db_product: models.Product) -> None:
    db.delete(db_product)
    db.commit()
