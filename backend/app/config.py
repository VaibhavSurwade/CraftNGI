import os
from pydantic_settings import BaseSettings, SettingsConfigDict

BACKEND_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DEFAULT_DB_URL = f"sqlite:///{os.path.join(BACKEND_DIR, 'craft_products.db')}"

class Settings(BaseSettings):
    APP_NAME: str = "Craft & GI Product Verification API"
    DEBUG: bool = True
    # By default, use SQLite file in the backend root directory for local dev.
    # For MySQL in production, set DATABASE_URL=mysql+pymysql://username:password@hostname:3306/dbname
    DATABASE_URL: str = os.getenv("DATABASE_URL", DEFAULT_DB_URL)

    model_config = SettingsConfigDict(env_file=".env", extra="ignore")

settings = Settings()
