from app.database import SessionLocal, engine
from app import models, schemas, crud

def seed():
    models.Base.metadata.create_all(bind=engine)
    db = SessionLocal()

    sample_products = [
        schemas.ProductCreate(
            barcode="8901234567890",
            product_name="Authentic Kashmiri Pashmina Stole",
            category="GI Registered Handicraft",
            # Section 1
            fabric_composition="100% Pure Changthangi Pashmina Wool",
            yarn_spinning="Hand Spun (Traditional Charkha / Yinder)",
            fabric_weave="Handloom Diamond Weave (Chashm-e-Bulbul)",
            embroidery_yarn="Pure Mulberry Silk Thread",
            # Section 2
            design="Traditional Paisley (Kalka / Badam motif) with Hashiya border",
            size="200 cm x 70 cm (Stole)",
            weight="145 grams",
            embroidery="Sozni Needlework (Finely handcrafted)",
            # Section 3
            authorized_gi_user="Craft Development Institute (CDI) Srinagar / GI Reg. No: GI-KASH-PASH-042",
            address_authorized_gi_user="Noorbagh Industrial Complex, Srinagar, Jammu & Kashmir - 190002",
            artisan_or_weaver="Master Ghulam Mohammad Mir",
            address_artisan_or_weaver="Mir Mohalla, Zadibal, Srinagar, Jammu & Kashmir - 190011"
        ),
        schemas.ProductCreate(
            barcode="8909876543210",
            product_name="Kanchipuram Silk Bridal Saree",
            category="GI Handloom Silk",
            # Section 1
            fabric_composition="100% Pure Mulberry Silk & Pure Silver Zari",
            yarn_spinning="Twisted 3-ply Mulberry Silk Yarn",
            fabric_weave="Korvai Handloom Technique with Petni interlocking",
            embroidery_yarn="Gold & Silver dipped Zari metallic thread",
            # Section 2
            design="Mayil (Peacock) & Rudraksha motifs with Temple Border",
            size="6.2 meters (includes 80 cm unstitched blouse piece)",
            weight="820 grams",
            embroidery="Woven Zari Brocade",
            # Section 3
            authorized_gi_user="Tamil Nadu Zari & Silk Weavers Cooperative Society / GI Reg. No: GI-TN-KAN-019",
            address_authorized_gi_user="12 Gandhi Road, Kanchipuram, Tamil Nadu - 631501",
            artisan_or_weaver="R. Shanmugam & Sons Weaving Guild",
            address_artisan_or_weaver="Pillayar Palayam, Kanchipuram, Tamil Nadu - 631502"
        ),
        schemas.ProductCreate(
            barcode="8904567812345",
            product_name="Banarasi Katan Silk Brocade",
            category="GI Registered Handloom",
            # Section 1
            fabric_composition="100% Pure Katan Natural Silk with Metallic Warp",
            yarn_spinning="Double-twisted Filature Silk Yarn",
            fabric_weave="Handloom Jangla Brocade Weave",
            embroidery_yarn="Gold Tested Zari Thread",
            # Section 2
            design="Floral Shikargah hunting scene with gold foliage border",
            size="5.5 meters saree length, 45 inches width",
            weight="680 grams",
            embroidery="Hand-woven Meenakari Resham Inlay",
            # Section 3
            authorized_gi_user="Banaras Bunkar Samiti / GI Reg. No: GI-UP-BAN-088",
            address_authorized_gi_user="Chowk Bazar, Kotwali, Varanasi, Uttar Pradesh - 221001",
            artisan_or_weaver="Ustad Irfan Ansari",
            address_artisan_or_weaver="Madanpura, Varanasi, Uttar Pradesh - 221001"
        ),
        schemas.ProductCreate(
            barcode="123456789012",
            product_name="Chanderi Handloom Tissue Saree",
            category="GI Handwoven Textile",
            # Section 1
            fabric_composition="70% Degummed Silk Warp, 30% Fine Mercerized Cotton Weft",
            yarn_spinning="Extra-fine 300s Count Hand-drafted Cotton & Organza Silk",
            fabric_weave="Traditional Chanderi Plain Weave with Eknalis",
            embroidery_yarn="Untwisted Silk Floss & Gold Zari",
            # Section 2
            design="Ashrafi (Coin) Buti with Nakshi Gold Border",
            size="6.3 meters length x 1.15 meters width",
            weight="390 grams",
            embroidery="Hand-manipulated Buti extra weft inlay",
            # Section 3
            authorized_gi_user="Chanderi Weavers Intellectual Property Guild / GI Reg. No: GI-MP-CHA-007",
            address_authorized_gi_user="Fort View Road, Chanderi, Ashoknagar, Madhya Pradesh - 473446",
            artisan_or_weaver="Smt. Rukmani Bai & Family",
            address_artisan_or_weaver="Bunkar Colony, Chanderi, Madhya Pradesh - 473446"
        )
    ]

    count = 0
    for product in sample_products:
        existing = crud.get_product_by_barcode(db, product.barcode)
        if not existing:
            crud.create_product(db, product)
            print(f"Added sample product: {product.product_name} (Barcode: {product.barcode})")
            count += 1
        else:
            print(f"Product already exists: {product.barcode}")

    db.close()
    print(f"Database seeded successfully! {count} products added.")

if __name__ == "__main__":
    seed()
