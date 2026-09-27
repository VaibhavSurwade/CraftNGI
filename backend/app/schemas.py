from datetime import datetime
from typing import Optional
from pydantic import BaseModel, ConfigDict

class Section1FabricDetails(BaseModel):
    fabric_composition: str
    yarn_spinning: str
    fabric_weave: str
    embroidery_yarn: str

class Section2ProductSpecs(BaseModel):
    design: str
    size: str
    weight: str
    embroidery: str

class Section3GIOriginDetails(BaseModel):
    authorized_gi_user: str
    address_authorized_gi_user: str
    artisan_or_weaver: str
    address_artisan_or_weaver: str

class ProductBase(BaseModel):
    barcode: str
    product_name: str
    category: Optional[str] = "Handloom / GI Craft"
    fabric_composition: str
    yarn_spinning: str
    fabric_weave: str
    embroidery_yarn: str
    design: str
    size: str
    weight: str
    embroidery: str
    authorized_gi_user: str
    address_authorized_gi_user: str
    artisan_or_weaver: str
    address_artisan_or_weaver: str

class ProductCreate(ProductBase):
    pass

class ProductUpdate(BaseModel):
    product_name: Optional[str] = None
    category: Optional[str] = None
    fabric_composition: Optional[str] = None
    yarn_spinning: Optional[str] = None
    fabric_weave: Optional[str] = None
    embroidery_yarn: Optional[str] = None
    design: Optional[str] = None
    size: Optional[str] = None
    weight: Optional[str] = None
    embroidery: Optional[str] = None
    authorized_gi_user: Optional[str] = None
    address_authorized_gi_user: Optional[str] = None
    artisan_or_weaver: Optional[str] = None
    address_artisan_or_weaver: Optional[str] = None

class ProductResponse(ProductBase):
    model_config = ConfigDict(from_attributes=True)

    id: int
    created_at: Optional[datetime] = None
    updated_at: Optional[datetime] = None

    # Nested section representations for easy UI rendering
    @property
    def section1(self) -> Section1FabricDetails:
        return Section1FabricDetails(
            fabric_composition=self.fabric_composition,
            yarn_spinning=self.yarn_spinning,
            fabric_weave=self.fabric_weave,
            embroidery_yarn=self.embroidery_yarn
        )

    @property
    def section2(self) -> Section2ProductSpecs:
        return Section2ProductSpecs(
            design=self.design,
            size=self.size,
            weight=self.weight,
            embroidery=self.embroidery
        )

    @property
    def section3(self) -> Section3GIOriginDetails:
        return Section3GIOriginDetails(
            authorized_gi_user=self.authorized_gi_user,
            address_authorized_gi_user=self.address_authorized_gi_user,
            artisan_or_weaver=self.artisan_or_weaver,
            address_artisan_or_weaver=self.address_artisan_or_weaver
        )
