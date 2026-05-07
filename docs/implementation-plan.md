# 📋 Implementation Plan — Franchise API

## Descripción General

API REST reactiva para la gestión de franquicias, sucursales y productos construida con **Spring Boot WebFlux**, **MongoDB Reactive** y empaquetada con **Docker**.

---

## Objetivos del Proyecto

| Objetivo | Prioridad | Estado |
|----------|-----------|--------|
| API funcional con CRUD completo | Alta | ✅ Completado |
| Arquitectura hexagonal (Ports & Adapters) | Alta | ✅ Completado |
| Programación reactiva (WebFlux + Reactor) | Alta | ✅ Completado |
| Persistencia con MongoDB | Alta | ✅ Completado |
| Empaquetado con Docker | Media | ✅ Completado |
| Tests unitarios (Service + Controller) | Alta | ✅ Completado |
| Versionamiento de API (`/api/v1/`) | Media | ✅ Completado |
| Variables de entorno con `.env` | Media | ✅ Completado |
| Endpoints extra (update names) | Baja | ✅ Completado |

---

## Criterios de Aceptación

### Requeridos

| # | Criterio | Endpoint | Estado |
|---|----------|----------|--------|
| 1 | Proyecto en Spring Boot | — | ✅ |
| 2 | Agregar nueva franquicia | `POST /api/v1/franchises` | ✅ |
| 3 | Agregar sucursal a franquicia | `POST /api/v1/franchises/{id}/branches` | ✅ |
| 4 | Agregar producto a sucursal | `POST .../branches/{id}/products` | ✅ |
| 5 | Eliminar producto de sucursal | `DELETE .../products/{id}` | ✅ |
| 6 | Modificar stock de producto | `PATCH .../products/{id}/stock` | ✅ |
| 7 | Producto con más stock por sucursal | `GET .../top-stock-products` | ✅ |
| 8 | Sistema de persistencia (MongoDB) | — | ✅ |

### Puntos Extra

| # | Plus | Estado |
|---|------|--------|
| 1 | Empaquetado con Docker | ✅ |
| 2 | Programación funcional/reactiva (WebFlux) | ✅ |
| 3 | Actualizar nombre de franquicia | ✅ |
| 4 | Actualizar nombre de sucursal | ✅ |
| 5 | Actualizar nombre de producto | ✅ |

---

## Stack Tecnológico

| Componente | Tecnología | Versión |
|------------|-----------|---------|
| Framework | Spring Boot | 3.5.14 |
| Web | Spring WebFlux | 3.5.x |
| Persistencia | Spring Data MongoDB Reactive | 3.5.x |
| Validación | Spring Boot Starter Validation | 3.5.x |
| Base de Datos | MongoDB | 7.x |
| Build Tool | Maven | Wrapper incluido |
| Java | Eclipse Temurin | 17+ |
| Contenedores | Docker + Docker Compose | Latest |
| Testing | JUnit 5 + Mockito + Reactor Test | Latest |
| Utilidades | Lombok | Latest |

---

## Modelo de Datos

### Esquema MongoDB (Documento Embebido)

```json
{
  "_id": "ObjectId()",
  "name": "Franchise Name",
  "branches": [
    {
      "id": "uuid-string",
      "name": "Branch Name",
      "products": [
        {
          "id": "uuid-string",
          "name": "Product Name",
          "stock": 100
        }
      ]
    }
  ]
}
```

### Justificación del Modelo Embebido

- **Lecturas optimizadas**: Una sola consulta trae toda la franquicia con sus sucursales y productos
- **Consistencia atómica**: Actualizaciones de subdocumentos son atómicas dentro del documento
- **Tamaño controlado**: Las franquicias tienen un número finito de sucursales/productos, lejos del límite de 16MB de MongoDB

---

## Endpoints API

### Base URL: `/api/v1/franchises`

```
POST   /                                                    → Crear franquicia
POST   /{franchiseId}/branches                              → Agregar sucursal
POST   /{franchiseId}/branches/{branchId}/products           → Agregar producto
DELETE /{franchiseId}/branches/{branchId}/products/{productId}         → Eliminar producto
PATCH  /{franchiseId}/branches/{branchId}/products/{productId}/stock   → Modificar stock
GET    /{franchiseId}/top-stock-products                     → Top stock por sucursal
PATCH  /{franchiseId}/name                                   → Renombrar franquicia
PATCH  /{franchiseId}/branches/{branchId}/name               → Renombrar sucursal
PATCH  /{franchiseId}/branches/{branchId}/products/{productId}/name   → Renombrar producto
```

---

## Plan de Tests

### Service Layer (15 tests)

| Test | Descripción | Validación |
|------|-------------|------------|
| `createFranchise_success` | Crea franquicia exitosamente | Mono con franchise, branches vacíos |
| `createFranchise_duplicateName` | Nombre duplicado | Error `DuplicateResourceException` |
| `addBranch_success` | Agrega sucursal | Branches size +1, products vacíos |
| `addBranch_franchiseNotFound` | ID inválido | Error `ResourceNotFoundException` |
| `addProduct_success` | Agrega producto | Products size +1 en branch correcta |
| `addProduct_branchNotFound` | Branch ID inválido | Error `ResourceNotFoundException` |
| `removeProduct_success` | Elimina producto | Products vacíos |
| `removeProduct_productNotFound` | Product ID inválido | Error `ResourceNotFoundException` |
| `updateProductStock_success` | Actualiza stock | Stock = nuevo valor |
| `updateProductStock_productNotFound` | Product ID inválido | Error `ResourceNotFoundException` |
| `getTopStockProducts_success` | Top stock por branch | Flux con top product por branch |
| `getTopStockProducts_emptyBranches` | Sin branches | Flux vacío |
| `updateFranchiseName_success` | Renombra franquicia | Nombre actualizado |
| `updateBranchName_success` | Renombra sucursal | Nombre actualizado |
| `updateProductName_success` | Renombra producto | Nombre actualizado |

### Controller Layer (11 tests)

| Test | Endpoint | Código HTTP Esperado |
|------|----------|---------------------|
| `createFranchise_returns201` | POST `/` | 201 Created |
| `createFranchise_invalidBody_returns400` | POST `/` (sin name) | 400 Bad Request |
| `addBranch_returns200` | POST `/{id}/branches` | 200 OK |
| `addProduct_returns200` | POST `.../products` | 200 OK |
| `removeProduct_returns200` | DELETE `.../products/{id}` | 200 OK |
| `updateProductStock_returns200` | PATCH `.../stock` | 200 OK |
| `getTopStockProducts_returns200` | GET `.../top-stock-products` | 200 OK |
| `updateFranchiseName_returns200` | PATCH `.../name` | 200 OK |
| `updateBranchName_returns200` | PATCH `.../branches/{id}/name` | 200 OK |
| `updateProductName_returns200` | PATCH `.../products/{id}/name` | 200 OK |
| `franchiseNotFound_returns404` | POST `/{invalid}/branches` | 404 Not Found |

---

## Configuración del Entorno

### Variables de Entorno (`.env`)

```env
MONGO_HOST=mongo           # Host de MongoDB (nombre del contenedor en Docker)
MONGO_PORT=27017           # Puerto de MongoDB
MONGO_DATABASE=franchisedb # Nombre de la base de datos
MONGO_USERNAME=admin       # Usuario de MongoDB
MONGO_PASSWORD=secret123   # Contraseña de MongoDB
MONGO_AUTH_DB=admin        # Base de datos de autenticación
SERVER_PORT=8080           # Puerto de la aplicación
```

### Ejecución

```bash
# Producción (Docker)
docker compose up --build

# Desarrollo local
./mvnw spring-boot:run

# Tests
./mvnw test
```
