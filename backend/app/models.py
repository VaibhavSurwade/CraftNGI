from datetime import datetime
from sqlalchemy import Column, Integer, String, Text, DateTime
from app.database import Base

class Product(Base):
    __tablename__ = "products"

    id = Column(Integer, primary_key=True, index=True, autoincrement=True)
    barcode = Column(String(100), unique=True, index=True, nullable=False)
    product_name = Column(String(255), nullable=False)
    category = Column(String(100), nullable=True, default="Handloom / GI Craft")

    # Section 1: Fabric & Yarn Details
    fabric_composition = Column(String(255), nullable=False)
    yarn_spinning = Column(String(255), nullable=False)
    fabric_weave = Column(String(255), nullable=False)
    embroidery_yarn = Column(String(255), nullable=False)

    # Section 2: Product Specifications
    design = Column(String(255), nullable=False)
    size = Column(String(100), nullable=False)
    weight = Column(String(100), nullable=False)
    embroidery = Column(String(255), nullable=False)

    # Section 3: GI & Artisan Origin Details
    authorized_gi_user = Column(String(255), nullable=False)
    address_authorized_gi_user = Column(Text, nullable=False)
    artisan_or_weaver = Column(String(255), nullable=False)
    address_artisan_or_weaver = Column(Text, nullable=False)

    # Timestamps
    created_at = Column(DateTime, default=datetime.utcnow)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)
