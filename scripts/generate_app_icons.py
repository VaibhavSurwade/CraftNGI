#!/usr/bin/env python3
"""
Generate Android launcher mipmap icons (standard & round) for both apps:
1. Consumer Scanner App (:app) using craft_scanner_icon.jpg
2. Admin Console App (:admin) using craft_admin_icon.jpg
"""
import os
from PIL import Image, ImageDraw

PROJECT_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
ASSETS_DIR = os.path.join(PROJECT_ROOT, "assets", "icons")

SCANNER_SRC = os.path.join(ASSETS_DIR, "craft_scanner_icon.jpg")
ADMIN_SRC = os.path.join(ASSETS_DIR, "craft_admin_icon.jpg")

APP_RES_DIR = os.path.join(PROJECT_ROOT, "android", "app", "src", "main", "res")
ADMIN_RES_DIR = os.path.join(PROJECT_ROOT, "android", "admin", "src", "main", "res")

DENSITIES = {
    "mipmap-mdpi": 48,
    "mipmap-hdpi": 72,
    "mipmap-xhdpi": 96,
    "mipmap-xxhdpi": 144,
    "mipmap-xxxhdpi": 192,
}

def create_round_icon(image: Image.Image, size: int) -> Image.Image:
    """Create a circular version of the icon with transparent corners."""
    img = image.resize((size, size), Image.Resampling.LANCZOS).convert("RGBA")
    
    # Create circular mask
    mask = Image.new("L", (size * 4, size * 4), 0)
    draw = ImageDraw.Draw(mask)
    draw.ellipse((0, 0, size * 4, size * 4), fill=255)
    mask = mask.resize((size, size), Image.Resampling.LANCZOS)
    
    output = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    output.paste(img, (0, 0), mask=mask)
    return output

def process_app_icons(src_path: str, target_res_dir: str, app_name: str):
    print(f"[*] Processing icons for {app_name} from {os.path.basename(src_path)}...")
    src_img = Image.open(src_path).convert("RGBA")

    for folder, size in DENSITIES.items():
        out_folder = os.path.join(target_res_dir, folder)
        os.makedirs(out_folder, exist_ok=True)

        # Standard square/rounded icon
        square_icon = src_img.resize((size, size), Image.Resampling.LANCZOS)
        square_path = os.path.join(out_folder, "ic_launcher.png")
        square_icon.save(square_path, "PNG")

        # Round icon
        round_icon = create_round_icon(src_img, size)
        round_path = os.path.join(out_folder, "ic_launcher_round.png")
        round_icon.save(round_path, "PNG")

        print(f"  [+] Generated {folder} ({size}x{size})")

def main():
    if not os.path.exists(SCANNER_SRC) or not os.path.exists(ADMIN_SRC):
        print(f"[!] Error: Source icons not found in {ASSETS_DIR}")
        return

    process_app_icons(SCANNER_SRC, APP_RES_DIR, "Consumer Scanner App (:app)")
    process_app_icons(ADMIN_SRC, ADMIN_RES_DIR, "Admin Console App (:admin)")
    print("\n[+] All Android mipmap icons generated successfully!")

if __name__ == "__main__":
    main()
