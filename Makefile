# ==============================================================================
# Makefile - Craft & GI Product Verification System
# ==============================================================================
# Unified control panel for Database changes, APK compilation, Docs generation,
# and backend server management.
# ==============================================================================

SHELL := /bin/bash

# Toolchain Paths
JAVA_HOME     ?= /opt/homebrew/opt/openjdk@17
ANDROID_HOME  ?= /opt/homebrew/share/android-commandlinetools
PROJECT_ROOT  := $(shell pwd)
BACKEND_DIR   := $(PROJECT_ROOT)/backend
ANDROID_DIR   := $(PROJECT_ROOT)/android
SCRIPTS_DIR   := $(PROJECT_ROOT)/scripts
DOCS_DIR      := $(PROJECT_ROOT)/docs

# Virtual Environment & Python Binaries
VENV          := $(BACKEND_DIR)/venv
PYTHON        := $(VENV)/bin/python
UVICORN       := $(VENV)/bin/uvicorn
PYTEST        := $(VENV)/bin/pytest

# Export environment variables for sub-processes
export JAVA_HOME
export ANDROID_HOME

.PHONY: help all \
        db-init db-seed db-status db-reset \
        apk app-apk admin-apk apk-app apk-scanner \
        apk-debug apk-release apk-clean apk-install-debug apk-install-release \
        apk-admin-debug apk-admin-release apk-admin-install-debug apk-admin-install-release \
        apk-all apk-release-all \
        docs barcodes backend-run backend-test backend-install \
        compile-backend package-client-bundle \
        clean clean-all

# Default target: display help
help:
	@echo "=============================================================================="
	@echo "           CRAFT & GI VERIFICATION SYSTEM - AUTOMATION CLI"
	@echo "=============================================================================="
	@echo "Usage: make [target]"
	@echo ""
	@echo " DATABASE TARGETS:"
	@echo "   make db-init              - Initialize database schema (SQLite / MySQL)"
	@echo "   make db-seed              - Seed sample GI products (Pashmina, Kanchipuram, etc.)"
	@echo "   make db-status            - Inspect DB connection and list registered products"
	@echo "   make db-reset             - Drop all tables and re-seed from scratch"
	@echo ""
	@echo " CLEANUP TARGETS:"
	@echo "   make clean                - Clean temporary build caches (keeps final APKs & archives)"
	@echo "   make clean-all            - Deep purge everything (removes APKs, archives & all caches)"
	@echo ""
	@echo " CLIENT HANDOVER & COMPILED BINARY (NO SOURCE CODE EXPOSED):"
	@echo "   make compile-backend      - Compile Python backend to native C++ machine code"
	@echo "   make package-client-bundle - Create clean client tar.gz package (zero .py files)"
	@echo ""
	@echo " CONSUMER SCANNER APK TARGETS:"
	@echo "   make apk                  - Compile Consumer Scanner Debug APK (CraftScanner-debug.apk)"
	@echo "   make app-apk              - Compile Consumer Scanner Debug APK (alias for apk)"
	@echo "   make apk-debug            - Compile Scanner Debug APK (outputs CraftScanner-debug.apk)"
	@echo "   make apk-release          - Compile Scanner Release APK (outputs CraftScanner-release.apk)"
	@echo "   make apk-install-debug    - Install Scanner Debug APK via ADB"
	@echo "   make apk-install-release  - Install Scanner Release APK via ADB"
	@echo ""
	@echo " ADMIN PRODUCT REGISTRATION APK TARGETS:"
	@echo "   make admin-apk            - Compile Admin Registration Debug APK (CraftAdmin-debug.apk)"
	@echo "   make apk-admin-debug      - Compile Admin Debug APK (outputs CraftAdmin-debug.apk)"
	@echo "   make apk-admin-release    - Compile Admin Release APK (outputs CraftAdmin-release.apk)"
	@echo "   make apk-admin-install-debug - Install Admin Debug APK via ADB"
	@echo "   make apk-all              - Compile ALL Debug APKs (Scanner + Admin)"
	@echo "   make apk-release-all      - Compile ALL Release APKs (Scanner + Admin)"
	@echo "   make apk-clean            - Clean all Android build outputs and Gradle cache"
	@echo ""
	@echo " SAMPLE BARCODES & ASSETS:"
	@echo "   make barcodes             - Generate scannable Barcode/QR PNGs & HTML test card"
	@echo ""
	@echo " DOCUMENTATION TARGETS:"
	@echo "   make docs                 - Generate OpenAPI 3.1 JSON, docs & barcode sheet"
	@echo ""
	@echo " BACKEND TARGETS:"
	@echo "   make backend-run          - Start FastAPI server on http://localhost:8000"
	@echo "   make backend-test         - Run automated pytest test suite"
	@echo "   make backend-install      - Set up Python virtual environment & dependencies"
	@echo ""
	@echo " GENERAL TARGETS:"
	@echo "   make all                  - Run tests, export docs, and compile Debug APK"
	@echo "=============================================================================="

# ==============================================================================
# DATABASE TARGETS
# ==============================================================================

db-init:
	@echo "--> Initializing Database schema..."
	@$(PYTHON) $(SCRIPTS_DIR)/db_ops.py init

db-seed:
	@echo "--> Seeding sample GI products..."
	@$(PYTHON) $(SCRIPTS_DIR)/db_ops.py seed

db-status:
	@$(PYTHON) $(SCRIPTS_DIR)/db_ops.py status

db-reset:
	@echo "--> Resetting and re-seeding database..."
	@$(PYTHON) $(SCRIPTS_DIR)/db_ops.py reset

# ==============================================================================
# ANDROID APK COMPILATION TARGETS
# ==============================================================================

# Quick Convenience Aliases
apk: apk-debug
app-apk: apk-debug
apk-app: apk-debug
apk-scanner: apk-debug
admin-apk: apk-admin-debug

# Consumer Scanner App Targets (:app)
apk-debug:
	@echo "--> Compiling Consumer Scanner Android Debug APK (:app)..."
	@cd $(ANDROID_DIR) && ./gradlew :app:assembleDebug --console=plain
	@cp $(ANDROID_DIR)/app/build/outputs/apk/debug/app-debug.apk $(PROJECT_ROOT)/CraftScanner-debug.apk
	@echo ""
	@echo "[+] Consumer Scanner Debug APK successfully compiled!"
	@echo "    Location: $(PROJECT_ROOT)/CraftScanner-debug.apk"
	@ls -lh $(PROJECT_ROOT)/CraftScanner-debug.apk

apk-release:
	@echo "--> Compiling Consumer Scanner Android Release APK (:app)..."
	@cd $(ANDROID_DIR) && ./gradlew :app:assembleRelease --console=plain
	@cp $(ANDROID_DIR)/app/build/outputs/apk/release/app-release.apk $(PROJECT_ROOT)/CraftScanner-release.apk
	@echo ""
	@echo "[+] Consumer Scanner Release APK successfully compiled!"
	@echo "    Location: $(PROJECT_ROOT)/CraftScanner-release.apk"
	@ls -lh $(PROJECT_ROOT)/CraftScanner-release.apk

apk-clean:
	@echo "--> Cleaning Android build artifacts..."
	@cd $(ANDROID_DIR) && ./gradlew clean --console=plain
	@rm -f $(PROJECT_ROOT)/CraftScanner-debug.apk $(PROJECT_ROOT)/CraftScanner-release.apk
	@rm -f $(PROJECT_ROOT)/CraftAdmin-debug.apk $(PROJECT_ROOT)/CraftAdmin-release.apk
	@echo "[+] Android clean complete."

apk-install-debug:
	@echo "--> Installing Debug APK via ADB..."
	@$(ANDROID_HOME)/platform-tools/adb install -r $(PROJECT_ROOT)/CraftScanner-debug.apk

apk-install-release:
	@echo "--> Installing Release APK via ADB..."
	@$(ANDROID_HOME)/platform-tools/adb install -r $(PROJECT_ROOT)/CraftScanner-release.apk

# Admin App Targets (:admin)
apk-admin-debug:
	@echo "--> Compiling Admin Android Debug APK (:admin)..."
	@cd $(ANDROID_DIR) && ./gradlew :admin:assembleDebug --console=plain
	@cp $(ANDROID_DIR)/admin/build/outputs/apk/debug/admin-debug.apk $(PROJECT_ROOT)/CraftAdmin-debug.apk
	@echo ""
	@echo "[+] Admin Debug APK successfully compiled!"
	@echo "    Location: $(PROJECT_ROOT)/CraftAdmin-debug.apk"
	@ls -lh $(PROJECT_ROOT)/CraftAdmin-debug.apk

apk-admin-release:
	@echo "--> Compiling Admin Android Release APK (:admin)..."
	@cd $(ANDROID_DIR) && ./gradlew :admin:assembleRelease --console=plain
	@cp $(ANDROID_DIR)/admin/build/outputs/apk/release/admin-release.apk $(PROJECT_ROOT)/CraftAdmin-release.apk
	@echo ""
	@echo "[+] Admin Release APK successfully compiled!"
	@echo "    Location: $(PROJECT_ROOT)/CraftAdmin-release.apk"
	@ls -lh $(PROJECT_ROOT)/CraftAdmin-release.apk

apk-admin-install-debug:
	@echo "--> Installing Admin Debug APK via ADB..."
	@$(ANDROID_HOME)/platform-tools/adb install -r $(PROJECT_ROOT)/CraftAdmin-debug.apk

apk-all: apk-debug apk-admin-debug
	@echo ""
	@echo "=============================================================================="
	@echo "[+] All Android Debug APKs (Scanner & Admin) compiled successfully!"
	@echo "    • Scanner APK : $(PROJECT_ROOT)/CraftScanner-debug.apk"
	@echo "    • Admin APK   : $(PROJECT_ROOT)/CraftAdmin-debug.apk"
	@echo "=============================================================================="

apk-release-all: apk-release apk-admin-release
	@echo ""
	@echo "=============================================================================="
	@echo "[+] All Android Release APKs (Scanner & Admin) compiled successfully!"
	@echo "    • Scanner Release APK : $(PROJECT_ROOT)/CraftScanner-release.apk"
	@echo "    • Admin Release APK   : $(PROJECT_ROOT)/CraftAdmin-release.apk"
	@echo "=============================================================================="

# ==============================================================================
# DOCUMENTATION & SAMPLE BARCODE TARGETS
# ==============================================================================

barcodes:
	@echo "--> Generating sample barcode/QR assets and HTML showcase..."
	@$(PYTHON) $(SCRIPTS_DIR)/generate_barcodes.py

docs: barcodes
	@echo "--> Generating Software Documentation..."
	@mkdir -p $(DOCS_DIR)
	@$(PYTHON) $(SCRIPTS_DIR)/db_ops.py export-openapi --output $(DOCS_DIR)/openapi.json
	@echo ""
	@echo "[+] Software Documentation Suite Generated:"
	@echo "    - Dev Setup Guide       : $(DOCS_DIR)/DEVELOPMENT_ENVIRONMENT_SETUP.md"
	@echo "    - Architecture Document : $(DOCS_DIR)/ARCHITECTURE.md"
	@echo "    - Database Schema & ERD : $(DOCS_DIR)/DATABASE_SCHEMA.md"
	@echo "    - API Specification     : $(DOCS_DIR)/API_SPECIFICATION.md"
	@echo "    - Deployment & Ops Guide: $(DOCS_DIR)/DEPLOYMENT_AND_OPERATIONS.md"
	@echo "    - Client Handover Guide : $(DOCS_DIR)/CLIENT_HANDOVER_GUIDE.md"
	@echo "    - Barcode HTML Test Card: $(DOCS_DIR)/sample_barcodes.html"
	@echo "    - OpenAPI v3.1 Export   : $(DOCS_DIR)/openapi.json"
	@echo ""

# ==============================================================================
# BACKEND TARGETS
# ==============================================================================

backend-install:
	@echo "--> Setting up backend virtual environment..."
	@python3 -m venv $(VENV)
	@$(VENV)/bin/pip install --upgrade pip
	@$(VENV)/bin/pip install -r $(BACKEND_DIR)/requirements.txt httpx pytest
	@echo "[+] Backend dependencies installed."

backend-run:
	@echo "--> Starting FastAPI server on port 8000..."
	@cd $(BACKEND_DIR) && $(UVICORN) app.main:app --reload --host 0.0.0.0 --port 8000

backend-test:
	@echo "--> Running backend test suite..."
	@cd $(BACKEND_DIR) && $(PYTEST) test_api.py -v

# ==============================================================================
# CLIENT HANDOVER & COMPILED BINARY (NO SOURCE CODE EXPOSED)
# ==============================================================================

compile-backend:
	@echo "--> Compiling Backend into Native C++ Machine Code with Nuitka..."
	@mkdir -p $(PROJECT_ROOT)/dist_backend
	@$(PYTHON) -m nuitka \
		--standalone \
		--include-package=app \
		--include-package=uvicorn \
		--include-package=sqlalchemy \
		--include-package=pydantic \
		--include-package=pymysql \
		--include-package=cryptography \
		--follow-imports \
		--output-dir=$(PROJECT_ROOT)/dist_backend \
		-o craft-backend \
		$(BACKEND_DIR)/run_server.py
	@echo "[+] Compilation complete: $(PROJECT_ROOT)/dist_backend/run_server.dist"

package-client-bundle:
	@echo "--> Packaging Client Delivery Bundle (Zero Python source code)..."
	@mkdir -p $(PROJECT_ROOT)/craft-backend-client-bundle
	@cp -a $(PROJECT_ROOT)/dist_backend/run_server.dist/* $(PROJECT_ROOT)/craft-backend-client-bundle/
	@cp $(BACKEND_DIR)/.env.example $(PROJECT_ROOT)/craft-backend-client-bundle/.env.example
	@tar -czf $(PROJECT_ROOT)/craft-backend-client-bundle.tar.gz -C $(PROJECT_ROOT) craft-backend-client-bundle
	@echo ""
	@echo "[+] Client Delivery Bundle created successfully!"
	@echo "    Archive : $(PROJECT_ROOT)/craft-backend-client-bundle.tar.gz"
	@echo "    Directory: $(PROJECT_ROOT)/craft-backend-client-bundle"
	@ls -lh $(PROJECT_ROOT)/craft-backend-client-bundle.tar.gz

# ==============================================================================
# ALL
# ==============================================================================

all: backend-test docs apk-debug
	@echo ""
	@echo "=============================================================================="
	@echo "[+] All tasks (Tests, Documentation, Debug APK) completed successfully!"
	@echo "=============================================================================="

# ==============================================================================
# CLEANUP TARGETS
# ==============================================================================

clean:
	@echo "--> Cleaning temporary build caches and intermediate files..."
	@rm -rf $(PROJECT_ROOT)/dist_backend
	@rm -rf $(PROJECT_ROOT)/craft-backend-client-bundle
	@rm -rf $(PROJECT_ROOT)/craft-backend-windows-bundle
	@rm -rf $(ANDROID_DIR)/app/build $(ANDROID_DIR)/admin/build $(ANDROID_DIR)/.gradle $(ANDROID_DIR)/.kotlin
	@find $(PROJECT_ROOT) -type d -name "__pycache__" -exec rm -rf {} + 2>/dev/null || true
	@find $(PROJECT_ROOT) -type d -name ".pytest_cache" -exec rm -rf {} + 2>/dev/null || true
	@find $(PROJECT_ROOT) -name ".DS_Store" -delete 2>/dev/null || true
	@echo "[+] Build caches and temporary files cleaned (final APKs and release archives preserved)."

clean-all: clean
	@echo "--> Purging all built output artifacts (APKs and release archives)..."
	@rm -f $(PROJECT_ROOT)/CraftScanner-*.apk
	@rm -f $(PROJECT_ROOT)/CraftAdmin-*.apk
	@rm -f $(PROJECT_ROOT)/craft-backend-client-bundle.tar.gz
	@rm -f $(PROJECT_ROOT)/craft-backend-windows-bundle.zip
	@echo "[+] Complete deep clean finished! Repository reset to pristine source state."
