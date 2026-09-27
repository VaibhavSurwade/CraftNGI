#!/usr/bin/env python3
"""
Generate high-resolution sample barcode and QR code test assets along with
interactive HTML test cards for all registered GI products.
"""
import os
import sys
import barcode
from barcode.writer import ImageWriter
import qrcode
from PIL import Image, ImageDraw, ImageFont

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
BACKEND_DIR = os.path.join(BASE_DIR, "backend")
if BACKEND_DIR not in sys.path:
    sys.path.insert(0, BACKEND_DIR)

from app.database import SessionLocal
from app import crud

OUTPUT_DIR = os.path.join(BASE_DIR, "sample_barcodes")
DOCS_DIR = os.path.join(BASE_DIR, "docs")
os.makedirs(OUTPUT_DIR, exist_ok=True)
os.makedirs(DOCS_DIR, exist_ok=True)

def generate_assets():
    db = SessionLocal()
    products = crud.get_products(db, limit=100)
    db.close()

    if not products:
        print("[!] No products found in database. Please run 'make db-seed' first.")
        return

    print(f"[*] Generating scannable barcodes and detail cards for {len(products)} products...")

    products_data = []

    for idx, p in enumerate(products, start=1):
        clean_code = p.barcode.strip()
        slug = f"{idx}_{clean_code}"

        # 1. Generate Code-128 Barcode Image
        barcode_class = barcode.get_barcode_class('code128')
        writer = ImageWriter()
        writer.format = 'PNG'
        bc = barcode_class(clean_code, writer=writer)
        barcode_filename = os.path.join(OUTPUT_DIR, f"{slug}_barcode")
        bc.save(barcode_filename, options={
            'module_width': 0.4,
            'module_height': 15.0,
            'font_size': 14,
            'text_distance': 5.0,
            'quiet_zone': 4.0
        })

        # 2. Generate QR Code Image
        qr = qrcode.QRCode(
            version=1,
            error_correction=qrcode.constants.ERROR_CORRECT_M,
            box_size=10,
            border=2
        )
        qr.add_data(clean_code)
        qr.make(fit=True)
        qr_img = qr.make_image(fill_color="#1A1A1A", back_color="white")
        qr_filename = os.path.join(OUTPUT_DIR, f"{slug}_qr.png")
        qr_img.save(qr_filename)

        products_data.append({
            "barcode": clean_code,
            "product_name": p.product_name,
            "category": p.category or "GI Registered Craft",
            "fabric_composition": p.fabric_composition,
            "yarn_spinning": p.yarn_spinning,
            "fabric_weave": p.fabric_weave,
            "embroidery_yarn": p.embroidery_yarn,
            "design": p.design,
            "size": p.size,
            "weight": p.weight,
            "embroidery": p.embroidery,
            "authorized_gi_user": p.authorized_gi_user,
            "address_authorized_gi_user": p.address_authorized_gi_user,
            "artisan_or_weaver": p.artisan_or_weaver,
            "address_artisan_or_weaver": p.address_artisan_or_weaver,
            "barcode_png": f"{slug}_barcode.png",
            "qr_png": f"{slug}_qr.png"
        })

        print(f"  [+] Generated Barcode & QR for [{clean_code}] - {p.product_name}")

    # 3. Generate HTML Test Showcase
    html_content = create_html_showcase(products_data)
    html_file = os.path.join(OUTPUT_DIR, "index.html")
    with open(html_file, "w", encoding="utf-8") as f:
        f.write(html_content)

    # Also write a standalone copy to docs/sample_barcodes.html
    docs_html_file = os.path.join(DOCS_DIR, "sample_barcodes.html")
    with open(docs_html_file, "w", encoding="utf-8") as f:
        f.write(html_content)

    print(f"\n[+] Interactive Barcode Showcase generated successfully:")
    print(f"    - HTML Showcase: {html_file}")
    print(f"    - Docs Copy    : {docs_html_file}")
    print(f"    - Barcode PNGs : {OUTPUT_DIR}/\n")

def create_html_showcase(products):
    cards_html = ""
    for p in products:
        cards_html += f"""
        <div class="card">
            <div class="card-header">
                <span class="badge">GI Authenticated</span>
                <h2>{p['product_name']}</h2>
                <div class="category">{p['category']}</div>
            </div>

            <div class="barcode-container">
                <div class="barcode-box">
                    <div class="code-title">Barcode (Code-128)</div>
                    <img src="{p['barcode_png']}" alt="Barcode {p['barcode']}" class="barcode-img">
                </div>
                <div class="qr-box">
                    <div class="code-title">QR Code</div>
                    <img src="{p['qr_png']}" alt="QR {p['barcode']}" class="qr-img">
                </div>
            </div>
            
            <div class="barcode-number">
                <span>Barcode: <strong>{p['barcode']}</strong></span>
                <button class="copy-btn" onclick="navigator.clipboard.writeText('{p['barcode']}'); alert('Copied {p['barcode']} to clipboard!');">Copy Code</button>
            </div>

            <!-- SECTION 1 -->
            <div class="section section-1">
                <div class="section-title">
                    <span class="sec-badge">Section 1</span> Fabric & Yarn Composition
                </div>
                <div class="detail-row">
                    <span class="label">Fabric Composition:</span>
                    <span class="val">{p['fabric_composition']}</span>
                </div>
                <div class="detail-row">
                    <span class="label">Yarn Spinning:</span>
                    <span class="val">{p['yarn_spinning']}</span>
                </div>
                <div class="detail-row">
                    <span class="label">Fabric Weave:</span>
                    <span class="val">{p['fabric_weave']}</span>
                </div>
                <div class="detail-row">
                    <span class="label">Embroidery Yarn:</span>
                    <span class="val">{p['embroidery_yarn']}</span>
                </div>
            </div>

            <!-- SECTION 2 -->
            <div class="section section-2">
                <div class="section-title">
                    <span class="sec-badge">Section 2</span> Product Specifications
                </div>
                <div class="detail-row">
                    <span class="label">Design:</span>
                    <span class="val">{p['design']}</span>
                </div>
                <div class="detail-row">
                    <span class="label">Size:</span>
                    <span class="val">{p['size']}</span>
                </div>
                <div class="detail-row">
                    <span class="label">Weight:</span>
                    <span class="val">{p['weight']}</span>
                </div>
                <div class="detail-row">
                    <span class="label">Embroidery:</span>
                    <span class="val">{p['embroidery']}</span>
                </div>
            </div>

            <!-- SECTION 3 -->
            <div class="section section-3">
                <div class="section-title">
                    <span class="sec-badge">Section 3</span> GI & Artisan Origin Details
                </div>
                <div class="detail-row">
                    <span class="label">Authorized GI User:</span>
                    <span class="val">{p['authorized_gi_user']}</span>
                </div>
                <div class="detail-row">
                    <span class="label">Address of GI User:</span>
                    <span class="val">{p['address_authorized_gi_user']}</span>
                </div>
                <div class="detail-row">
                    <span class="label">Artisan / Weaver:</span>
                    <span class="val">{p['artisan_or_weaver']}</span>
                </div>
                <div class="detail-row">
                    <span class="label">Address of Artisan:</span>
                    <span class="val">{p['address_artisan_or_weaver']}</span>
                </div>
            </div>
        </div>
        """

    return f"""<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sample Barcodes & GI Product Details</title>
    <style>
        :root {{
            --primary: #C04000;
            --gold: #D4AF37;
            --green: #1B5E20;
            --bg: #F8F5F0;
            --card-bg: #FFFFFF;
            --text-dark: #1E1E1E;
            --text-muted: #555555;
            --border: #E2D9D0;
        }}
        * {{ box-sizing: border-box; margin: 0; padding: 0; }}
        body {{
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            background-color: var(--bg);
            color: var(--text-dark);
            padding: 30px 20px;
        }}
        .header {{
            max-width: 1200px;
            margin: 0 auto 30px auto;
            text-align: center;
        }}
        .header h1 {{
            color: var(--primary);
            font-size: 32px;
            font-weight: 800;
            margin-bottom: 8px;
        }}
        .header p {{
            color: var(--text-muted);
            font-size: 16px;
            max-width: 700px;
            margin: 0 auto;
        }}
        .instructions {{
            max-width: 1200px;
            margin: 0 auto 25px auto;
            background: #FFF3E0;
            border-left: 5px solid var(--primary);
            padding: 14px 20px;
            border-radius: 8px;
            font-size: 14px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }}
        .print-btn {{
            background: var(--primary);
            color: white;
            border: none;
            padding: 8px 16px;
            border-radius: 6px;
            font-weight: bold;
            cursor: pointer;
        }}
        .print-btn:hover {{ opacity: 0.9; }}
        .grid {{
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(540px, 1fr));
            gap: 25px;
            max-width: 1200px;
            margin: 0 auto;
        }}
        .card {{
            background: var(--card-bg);
            border: 1px solid var(--border);
            border-radius: 16px;
            padding: 24px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.04);
            display: flex;
            flex-direction: column;
            gap: 16px;
            page-break-inside: avoid;
        }}
        .card-header {{
            border-bottom: 1px solid var(--border);
            padding-bottom: 12px;
        }}
        .badge {{
            display: inline-block;
            background: #E8F5E9;
            color: var(--green);
            font-size: 11px;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            padding: 4px 10px;
            border-radius: 12px;
            margin-bottom: 6px;
        }}
        .card-header h2 {{
            font-size: 20px;
            font-weight: 700;
            color: var(--text-dark);
            margin-bottom: 4px;
        }}
        .category {{
            font-size: 13px;
            color: var(--text-muted);
        }}
        .barcode-container {{
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 15px;
            background: #FAFAFA;
            padding: 16px;
            border-radius: 12px;
            border: 1px dashed var(--gold);
        }}
        .barcode-box {{
            flex: 1;
            text-align: center;
        }}
        .barcode-img {{
            max-width: 100%;
            height: 90px;
            object-fit: contain;
            background: white;
            padding: 6px;
            border-radius: 6px;
        }}
        .qr-box {{
            width: 100px;
            text-align: center;
        }}
        .qr-img {{
            width: 90px;
            height: 90px;
            background: white;
            padding: 4px;
            border-radius: 6px;
        }}
        .code-title {{
            font-size: 11px;
            color: var(--text-muted);
            text-transform: uppercase;
            font-weight: 600;
            margin-bottom: 6px;
        }}
        .barcode-number {{
            display: flex;
            justify-content: space-between;
            align-items: center;
            background: #F1EFEA;
            padding: 8px 14px;
            border-radius: 8px;
            font-size: 13px;
        }}
        .copy-btn {{
            background: white;
            border: 1px solid #CCC;
            padding: 4px 10px;
            border-radius: 6px;
            font-size: 12px;
            cursor: pointer;
        }}
        .copy-btn:hover {{ background: #EAEAEA; }}
        .section {{
            border-radius: 10px;
            padding: 12px 14px;
            border: 1px solid var(--border);
            font-size: 13px;
        }}
        .section-1 {{ background: #FFF9F5; border-color: #FFD8C7; }}
        .section-2 {{ background: #FFFCF2; border-color: #FFEBB3; }}
        .section-3 {{ background: #F6FAF6; border-color: #C8E6C9; }}
        .section-title {{
            font-weight: 700;
            font-size: 13px;
            margin-bottom: 8px;
            display: flex;
            align-items: center;
            gap: 8px;
        }}
        .sec-badge {{
            font-size: 10px;
            text-transform: uppercase;
            padding: 2px 6px;
            border-radius: 4px;
            font-weight: 800;
            color: white;
        }}
        .section-1 .sec-badge {{ background: var(--primary); }}
        .section-2 .sec-badge {{ background: #B38600; }}
        .section-3 .sec-badge {{ background: var(--green); }}
        .detail-row {{
            display: flex;
            margin-bottom: 4px;
            line-height: 1.4;
        }}
        .detail-row:last-child {{ margin-bottom: 0; }}
        .label {{
            width: 170px;
            font-weight: 600;
            color: var(--text-muted);
            font-size: 12px;
            flex-shrink: 0;
        }}
        .val {{
            font-weight: 500;
            color: var(--text-dark);
            font-size: 12px;
        }}
        @media print {{
            body {{ background: white; padding: 0; }}
            .instructions, .print-btn, .copy-btn {{ display: none; }}
            .card {{ box-shadow: none; border: 1px solid #999; margin-bottom: 20px; }}
        }}
    </style>
</head>
<body>
    <div class="header">
        <h1>Craft & GI Verification Test Barcodes</h1>
        <p>Scan these barcodes directly from your screen or print this sheet to test the Android Scanner app.</p>
    </div>

    <div class="instructions">
        <span>📸 <strong>Testing Instruction:</strong> Open the <strong>Craft & GI Scanner</strong> Android app, aim the camera at any of the barcodes or QR codes below, and watch the verified details appear instantly!</span>
        <button class="print-btn" onclick="window.print()">🖨️ Print Test Sheet</button>
    </div>

    <div class="grid">
        {cards_html}
    </div>
</body>
</html>
"""

if __name__ == "__main__":
    generate_assets()
