# Deployment & Operations Guide
## Craft & GI Product Verification System

---

## 1. Quickstart (Local Development)

```bash
# 1. Initialize and Seed SQLite database
make db-seed

# 2. Check Database Status
make db-status

# 3. Start Backend API Server
make backend-run

# 4. In a separate terminal, compile Debug or Release APK
make apk-debug
make apk-release
```

---

## 2. Production Backend Deployment

### A. Environment Configuration
Create a production `.env` file in `backend/`:
```env
APP_NAME="Craft & GI Product Verification API"
DEBUG=False
DATABASE_URL=mysql+pymysql://craft_admin:SecurePass123!@mysql-prod.internal:3306/craft_gi_registry
```

### B. Production ASGI Server (Gunicorn + Uvicorn Workers)
For production concurrency, run with multiple Uvicorn workers behind Gunicorn:
```bash
pip install gunicorn
gunicorn -w 4 -k uvicorn.workers.UvicornWorker app.main:app --bind 0.0.0.0:8000
```

### C. Systemd Service Unit (`/etc/systemd/system/craft-api.service`)
```ini
[Unit]
Description=Craft GI Verification FastAPI Service
After=network.target mysql.service

[Service]
User=www-data
Group=www-data
WorkingDirectory=/var/www/CraftNGI/backend
Environment="PATH=/var/www/CraftNGI/backend/venv/bin"
ExecStart=/var/www/CraftNGI/backend/venv/bin/uvicorn app.main:app --host 0.0.0.0 --port 8000 --workers 4
Restart=always

[Install]
WantedBy=multi-user.target
```

### D. Reverse Proxy (Nginx with HTTPS / TLS)
```nginx
server {
    listen 80;
    server_name api.craftgi.example.com;
    return 301 https://$host$request_uri;
}

server {
    listen 443 ssl http2;
    server_name api.craftgi.example.com;

    ssl_certificate /etc/letsencrypt/live/api.craftgi.example.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/api.craftgi.example.com/privkey.pem;

    location / {
        proxy_pass http://127.0.0.1:8000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

---

## 3. Production MySQL Database Setup

1. **Create Database & User**:
```sql
CREATE DATABASE craft_gi_registry CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'craft_admin'@'%' IDENTIFIED BY 'SecurePass123!';
GRANT ALL PRIVILEGES ON craft_gi_registry.* TO 'craft_admin'@'%';
FLUSH PRIVILEGES;
```

2. **Run Initialization via Make**:
```bash
DATABASE_URL="mysql+pymysql://craft_admin:SecurePass123!@127.0.0.1:3306/craft_gi_registry" make db-init
DATABASE_URL="mysql+pymysql://craft_admin:SecurePass123!@127.0.0.1:3306/craft_gi_registry" make db-seed
```

---

## 4. Android Production Release & Keystore Signing

For Google Play Store distribution:
1. Generate an official upload keystore:
   ```bash
   keytool -genkey -v -keystore craft-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias craft-alias
   ```
2. Store key credentials securely in `android/keystore.properties`:
   ```properties
   storeFile=../craft-release-key.jks
   storePassword=YourStorePassword
   keyAlias=craft-alias
   keyPassword=YourKeyPassword
   ```
3. Run `make apk-release` to generate the production release APK.

---

## 5. Standalone Native Binary Deployment (No Python Required)

For client deliveries where Python source code must remain protected:
- **macOS (ARM64)**: Run `make compile-backend` and `make package-client-bundle`. Produces `craft-backend-client-bundle.tar.gz`.
- **Windows (x86_64)**: On a Windows machine, run `scripts\compile_windows.bat`. Produces `craft-backend-windows-bundle.zip` with `craft-backend.exe`.
- See [**`docs/CLIENT_HANDOVER_GUIDE.md`**](file:///Users/vaibhav/Projects/CraftNGI/docs/CLIENT_HANDOVER_GUIDE.md) for full step-by-step instructions.
