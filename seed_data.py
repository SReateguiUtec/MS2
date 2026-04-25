"""
Seed script MS2: descarga precios históricos REALES desde Yahoo Finance
y los inserta en PostgreSQL.

Uso:
    pip install psycopg2-binary python-dotenv yfinance
    python seed_data.py

Datos:
    - Fuente: Yahoo Finance via yfinance (sin API key)
    - Período: máximo disponible con granularidad diaria
    - AAPL: ~12,000 registros  | NVDA: ~11,000 | MSFT: ~12,000
    - GOOGL: ~5,000 registros  | TSLA: ~3,500
    - Total esperado: ~44,000 registros reales de mercado

Variables de entorno (.env o shell):
    DB_HOST, DB_PORT, DB_USER, DB_PASSWORD, DB_NAME
"""

import os
from datetime import datetime
from dotenv import load_dotenv
import psycopg2
from psycopg2.extras import execute_batch
import yfinance as yf

load_dotenv()

# ---------------------------------------------------------------------------
# Config BD
# ---------------------------------------------------------------------------

DB_CONFIG = {
    "host":     os.getenv("DB_HOST", "localhost"),
    "port":     int(os.getenv("DB_PORT", 5432)),
    "user":     os.getenv("DB_USER", "postgres"),
    "password": os.getenv("DB_PASSWORD", ""),
    "dbname":   os.getenv("DB_NAME", "precios_db"),
}

# ---------------------------------------------------------------------------
# Catálogo de símbolos (metadatos que Yahoo Finance no provee directamente)
# ---------------------------------------------------------------------------

SIMBOLOS_META = {
    # ── Tecnología ────────────────────────────────────────────
    "AAPL":  {"nombre": "Apple Inc.",               "sector": "Technology",             "industria": "Consumer Electronics",          "bolsa": "NASDAQ", "pais": "USA"},
    "NVDA":  {"nombre": "NVIDIA Corporation",        "sector": "Technology",             "industria": "Semiconductors",                "bolsa": "NASDAQ", "pais": "USA"},
    "MSFT":  {"nombre": "Microsoft Corporation",     "sector": "Technology",             "industria": "Software",                      "bolsa": "NASDAQ", "pais": "USA"},
    "GOOGL": {"nombre": "Alphabet Inc.",             "sector": "Communication Services", "industria": "Internet Content & Information","bolsa": "NASDAQ", "pais": "USA"},
    "META":  {"nombre": "Meta Platforms Inc.",       "sector": "Communication Services", "industria": "Internet Content & Information","bolsa": "NASDAQ", "pais": "USA"},
    "AMZN":  {"nombre": "Amazon.com Inc.",           "sector": "Consumer Cyclical",      "industria": "Internet Retail",               "bolsa": "NASDAQ", "pais": "USA"},
    "AMD":   {"nombre": "Advanced Micro Devices",    "sector": "Technology",             "industria": "Semiconductors",                "bolsa": "NASDAQ", "pais": "USA"},
    "INTC":  {"nombre": "Intel Corporation",         "sector": "Technology",             "industria": "Semiconductors",                "bolsa": "NASDAQ", "pais": "USA"},
    "CRM":   {"nombre": "Salesforce Inc.",           "sector": "Technology",             "industria": "Software",                      "bolsa": "NYSE",   "pais": "USA"},
    "ADBE":  {"nombre": "Adobe Inc.",                "sector": "Technology",             "industria": "Software",                      "bolsa": "NASDAQ", "pais": "USA"},
    "NFLX":  {"nombre": "Netflix Inc.",              "sector": "Communication Services", "industria": "Entertainment",                 "bolsa": "NASDAQ", "pais": "USA"},
    "TSLA":  {"nombre": "Tesla Inc.",                "sector": "Consumer Cyclical",      "industria": "Auto Manufacturers",            "bolsa": "NASDAQ", "pais": "USA"},

    # ── Finanzas ──────────────────────────────────────────────
    "JPM":   {"nombre": "JPMorgan Chase & Co.",      "sector": "Financial Services",     "industria": "Banks - Diversified",           "bolsa": "NYSE",   "pais": "USA"},
    "BAC":   {"nombre": "Bank of America Corp.",     "sector": "Financial Services",     "industria": "Banks - Diversified",           "bolsa": "NYSE",   "pais": "USA"},
    "GS":    {"nombre": "Goldman Sachs Group Inc.",  "sector": "Financial Services",     "industria": "Capital Markets",               "bolsa": "NYSE",   "pais": "USA"},
    "V":     {"nombre": "Visa Inc.",                 "sector": "Financial Services",     "industria": "Credit Services",               "bolsa": "NYSE",   "pais": "USA"},
    "MA":    {"nombre": "Mastercard Inc.",           "sector": "Financial Services",     "industria": "Credit Services",               "bolsa": "NYSE",   "pais": "USA"},

    # ── Salud ─────────────────────────────────────────────────
    "JNJ":   {"nombre": "Johnson & Johnson",         "sector": "Healthcare",             "industria": "Drug Manufacturers",            "bolsa": "NYSE",   "pais": "USA"},
    "PFE":   {"nombre": "Pfizer Inc.",               "sector": "Healthcare",             "industria": "Drug Manufacturers",            "bolsa": "NYSE",   "pais": "USA"},
    "UNH":   {"nombre": "UnitedHealth Group Inc.",   "sector": "Healthcare",             "industria": "Healthcare Plans",              "bolsa": "NYSE",   "pais": "USA"},

    # ── Energía ───────────────────────────────────────────────
    "XOM":   {"nombre": "Exxon Mobil Corporation",  "sector": "Energy",                 "industria": "Oil & Gas Integrated",          "bolsa": "NYSE",   "pais": "USA"},
    "CVX":   {"nombre": "Chevron Corporation",       "sector": "Energy",                 "industria": "Oil & Gas Integrated",          "bolsa": "NYSE",   "pais": "USA"},

    # ── Consumo ───────────────────────────────────────────────
    "WMT":   {"nombre": "Walmart Inc.",              "sector": "Consumer Defensive",     "industria": "Discount Stores",               "bolsa": "NYSE",   "pais": "USA"},
    "KO":    {"nombre": "The Coca-Cola Company",     "sector": "Consumer Defensive",     "industria": "Beverages - Non-Alcoholic",      "bolsa": "NYSE",   "pais": "USA"},
    "NKE":   {"nombre": "Nike Inc.",                 "sector": "Consumer Cyclical",      "industria": "Footwear & Accessories",        "bolsa": "NYSE",   "pais": "USA"},

    # ── ETFs de referencia ────────────────────────────────────
    "SPY":   {"nombre": "SPDR S&P 500 ETF Trust",   "sector": "ETF",                    "industria": "Large Blend",                   "bolsa": "NYSE",   "pais": "USA"},
    "QQQ":   {"nombre": "Invesco QQQ Trust",         "sector": "ETF",                    "industria": "Large Growth",                  "bolsa": "NASDAQ", "pais": "USA"},
}


# Día de corte: velas hasta esta fecha son FREE, el resto son PREMIUM
PREMIUM_CUTOFF = datetime(2023, 1, 1)

# ---------------------------------------------------------------------------
# DDL
# ---------------------------------------------------------------------------

CREATE_SIMBOLOS = """
CREATE TABLE IF NOT EXISTS simbolos (
    id          BIGSERIAL PRIMARY KEY,
    simbolo     VARCHAR(10)   UNIQUE NOT NULL,
    nombre      VARCHAR(120)  NOT NULL,
    sector      VARCHAR(80),
    industria   VARCHAR(80),
    bolsa       VARCHAR(20),
    pais        VARCHAR(50),
    activo      BOOLEAN       DEFAULT TRUE,
    created_at  TIMESTAMP     DEFAULT NOW()
);
"""

CREATE_PRECIOS = """
CREATE TABLE IF NOT EXISTS precios_acciones (
    id          BIGSERIAL PRIMARY KEY,
    simbolo     VARCHAR(10)    NOT NULL,
    open        NUMERIC(10,4)  NOT NULL,
    close       NUMERIC(10,4)  NOT NULL,
    high        NUMERIC(10,4)  NOT NULL,
    low         NUMERIC(10,4)  NOT NULL,
    volumen     BIGINT         NOT NULL,
    fecha       TIMESTAMP      NOT NULL,
    es_premium  BOOLEAN        DEFAULT FALSE,
    simbolo_id  BIGINT         REFERENCES simbolos(id)
);
CREATE INDEX IF NOT EXISTS idx_simbolo_fecha ON precios_acciones (simbolo, fecha);
"""

INSERT_SIMBOLO = """
INSERT INTO simbolos (simbolo, nombre, sector, industria, bolsa, pais)
VALUES (%s, %s, %s, %s, %s, %s)
ON CONFLICT (simbolo) DO NOTHING
RETURNING id;
"""

INSERT_PRECIO = """
INSERT INTO precios_acciones (simbolo, open, close, high, low, volumen, fecha, es_premium, simbolo_id)
VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
"""

# ---------------------------------------------------------------------------
# Descarga desde Yahoo Finance
# ---------------------------------------------------------------------------

def descargar_precios(ticker: str) -> list[tuple]:
    """
    Descarga el histórico completo diario desde Yahoo Finance.
    Retorna lista de (open, close, high, low, volumen, fecha, es_premium).
    """
    print(f"  Descargando datos de Yahoo Finance para {ticker}...", end=" ", flush=True)
    df = yf.download(
        ticker,
        period="max",        # Todo el histórico disponible
        interval="1d",       # Granularidad diaria
        auto_adjust=True,    # Ajusta splits y dividendos
        progress=False,
    )

    if df.empty:
        print("⚠ Sin datos")
        return []

    # yfinance devuelve MultiIndex cuando se descarga 1 ticker con auto_adjust
    # Aplanamos si es necesario
    if isinstance(df.columns, type(df.columns)) and hasattr(df.columns, 'levels'):
        df.columns = df.columns.droplevel(1)

    rows = []
    for fecha, row in df.iterrows():
        try:
            open_p  = round(float(row["Open"]),   4)
            close_p = round(float(row["Close"]),  4)
            high_p  = round(float(row["High"]),   4)
            low_p   = round(float(row["Low"]),    4)
            vol     = int(row["Volume"])
            fecha_dt = fecha.to_pydatetime().replace(tzinfo=None)
            es_premium = fecha_dt >= PREMIUM_CUTOFF
            rows.append((open_p, close_p, high_p, low_p, vol, fecha_dt, es_premium))
        except Exception:
            continue  # Ignora filas con NaN

    print(f"{len(rows):,} velas descargadas ✓")
    return rows


# ---------------------------------------------------------------------------
# Main seed
# ---------------------------------------------------------------------------

def seed():
    print("=" * 60)
    print("  MS2 Seed — Precios reales desde Yahoo Finance")
    print("=" * 60)

    print("\nConectando a PostgreSQL...")
    conn = psycopg2.connect(**DB_CONFIG)
    cur = conn.cursor()

    print("Reseteando tablas (DROP + CREATE)...")
    cur.execute("DROP TABLE IF EXISTS precios_acciones CASCADE")
    cur.execute("DROP TABLE IF EXISTS simbolos CASCADE")
    conn.commit()

    cur.execute(CREATE_SIMBOLOS)
    cur.execute(CREATE_PRECIOS)
    conn.commit()

    total = 0
    for symbol, meta in SIMBOLOS_META.items():
        print(f"\n[{symbol}]")

        # 1. Insertar símbolo en catálogo
        cur.execute(INSERT_SIMBOLO, (
            symbol, meta["nombre"], meta["sector"],
            meta["industria"], meta["bolsa"], meta["pais"],
        ))
        row = cur.fetchone()
        if row:
            simbolo_id = row[0]
        else:
            cur.execute("SELECT id FROM simbolos WHERE simbolo = %s", (symbol,))
            simbolo_id = cur.fetchone()[0]
        conn.commit()

        # 2. Descargar precios reales
        candles = descargar_precios(symbol)
        if not candles:
            continue

        # 3. Insertar en BD
        print(f"  Insertando en PostgreSQL...", end=" ", flush=True)
        rows_db = [
            (symbol, op, cl, hi, lo, vol, fecha, prem, simbolo_id)
            for (op, cl, hi, lo, vol, fecha, prem) in candles
        ]
        execute_batch(cur, INSERT_PRECIO, rows_db, page_size=500)
        conn.commit()
        total += len(rows_db)
        print(f"OK ({len(rows_db):,} registros)")

    cur.close()
    conn.close()

    print("\n" + "=" * 60)
    print(f"  Seed completado: {total:,} registros reales de mercado")
    print(f"  Símbolos: {len(SIMBOLOS_META)}")
    print("=" * 60)


if __name__ == "__main__":
    seed()
