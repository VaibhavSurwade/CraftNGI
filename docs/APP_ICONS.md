# Android Application Icons
## Craft & GI Verification Suite

High-resolution app icons designed specifically for the Consumer Scanner and Admin Console apps.

Assets directory: [`assets/icons/`](file:///Users/vaibhav/Projects/CraftNGI/assets/icons/)

---

## 1. Consumer Scanner App Icons

### Option A: Heritage Loom & Barcode Fusion (Recommended)
- **Asset**: [`assets/icons/craft_scanner_icon.jpg`](file:///Users/vaibhav/Projects/CraftNGI/assets/icons/craft_scanner_icon.jpg)
- **Concept**: A radiant laser barcode seamlessly morphing into handcrafted Indian textile weave patterns and silk threads.
- **Color Theme**: Terracotta, Saffron, and Gold on a warm ivory base.

### Option B: Minimalist Paisley Scanner Glyph
- **Asset**: [`assets/icons/craft_minimal_scanner_icon.jpg`](file:///Users/vaibhav/Projects/CraftNGI/assets/icons/craft_minimal_scanner_icon.jpg)
- **Concept**: Clean vector aesthetic featuring a glowing reticle frame, traditional paisley motif, and high-contrast barcode bars.
- **Color Theme**: Terracotta red background with white and gold accents.

---

## 2. Admin Console App Icon

### Official GI Authority Seal
- **Asset**: [`assets/icons/craft_admin_icon.jpg`](file:///Users/vaibhav/Projects/CraftNGI/assets/icons/craft_admin_icon.jpg)
- **Concept**: Polished 3D gold certification emblem combining a weaver shuttle, digital QR verification grid, and official registry checkmark.
- **Color Theme**: Royal Navy Blue with rich metallic gold and emerald green accents.

---

## 3. How to Set App Icon in Android Studio

1. In Android Studio, right-click on the module (`app` or `admin`) -> `res` folder.
2. Select **New** -> **Image Asset**.
3. In the **Asset Studio** window:
   - Asset Type: **Image**
   - Path: Select the icon from `assets/icons/` (e.g., `craft_scanner_icon.jpg` or `craft_admin_icon.jpg`).
   - Adjust Scaling to fit inside the circle / squircle safe zone.
4. Click **Next** -> **Finish**. Android Studio will generate all density variants:
   - `mipmap-mdpi` (48x48)
   - `mipmap-hdpi` (72x72)
   - `mipmap-xhdpi` (96x96)
   - `mipmap-xxhdpi` (144x144)
   - `mipmap-xxxhdpi` (192x192)
