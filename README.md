# MS2 - Historial de Precios

Servicio de historial de precios y catalogo de simbolos para FinTrend.

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker&logoColor=white)

## Responsabilidad

- Consultar precios historicos por simbolo.
- Consultar ultimo precio disponible.
- Consultar precios por rango de fechas.
- Administrar catalogo de simbolos activos/inactivos.
- Validar conexion a PostgreSQL.

## Requisitos

- Java 17
- Maven o Maven Wrapper (`./mvnw`)
- PostgreSQL

## Instalacion

```bash
cp .env.example .env
```

Edita `.env` con las credenciales reales de PostgreSQL.

## Variables de entorno

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=precios_db
DB_USER=postgres
DB_PASSWORD=your_postgres_password
MS1_URL=http://localhost:5001
```

El servicio usa `src/main/resources/application.properties` para resolver estas variables.

## Ejecutar en desarrollo

```bash
./mvnw spring-boot:run
```

El servicio queda disponible en:

```text
http://localhost:5002
```

## Endpoints principales

| Metodo | Ruta | Descripcion |
| ------ | ---- | ----------- |
| GET | `/health` | Health check y conexion a DB |
| GET | `/api/precios/:simbolo` | Lista precios por simbolo |
| GET | `/api/precios/:simbolo/latest` | Ultimo precio de un simbolo |
| GET | `/api/precios/:simbolo/range?inicio=...&fin=...` | Precios por rango |
| POST | `/api/precios` | Crea un registro de precio |
| DELETE | `/api/precios/:simbolo` | Elimina precios por simbolo |
| GET | `/api/simbolos` | Lista simbolos activos |
| GET | `/api/simbolos/all` | Lista todos los simbolos |
| GET | `/api/simbolos/:simbolo` | Detalle de simbolo |
| GET | `/api/simbolos/sector/:sector` | Simbolos por sector |
| POST | `/api/simbolos` | Crea un simbolo |
| PUT | `/api/simbolos/:simbolo` | Actualiza un simbolo |
| DELETE | `/api/simbolos/:simbolo` | Desactiva un simbolo |

## Seed de datos

```bash
python seed_data.py
```

## Build

```bash
./mvnw clean package
```

## Docker

```bash
docker build -t fintrend-ms2-precios .
docker run --env-file .env -p 5002:5002 fintrend-ms2-precios
```

## Estructura

```text
.
├── src/
│   ├── main/
│   │   ├── java/com/example/historialprecios/
│   │   │   ├── controller/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   └── HistorialPreciosApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── seed_data.py
├── pom.xml
├── Dockerfile
└── .env.example
```
