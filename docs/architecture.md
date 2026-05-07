# 🏗️ Architecture — Franchise API

## Arquitectura Hexagonal (Ports & Adapters)

El proyecto sigue la **arquitectura hexagonal**, separando el dominio de negocio de los detalles de infraestructura.

---

## Capas del Sistema

### 1. Domain Layer (Núcleo)

> No depende de ningún framework. Contiene modelos, excepciones y ports.

```
domain/
├── model/         → Franchise, Branch, Product
├── port/in/       → FranchiseUseCase (driving port)
├── port/out/      → FranchiseRepositoryPort (driven port)
└── exception/     → ResourceNotFoundException, DuplicateResourceException
```

### 2. Application Layer

> Orquesta los casos de uso implementando los driving ports.

```
application/
├── dto/           → Request/Response DTOs (6 archivos)
└── service/       → FranchiseServiceImpl (implementa FranchiseUseCase)
```

### 3. Infrastructure Layer

> Adaptadores que conectan el mundo exterior con el dominio.

```
infrastructure/adapter/
├── in/rest/            → FranchiseController, GlobalExceptionHandler, ErrorResponse
└── out/persistence/    → MongoFranchiseRepository, FranchiseRepositoryAdapter
```

---

## Flujo de una Request

```
HTTP Request
  → FranchiseController (Inbound Adapter)
    → FranchiseUseCase (Driving Port - Interface)
      → FranchiseServiceImpl (Application Layer)
        → FranchiseRepositoryPort (Driven Port - Interface)
          → FranchiseRepositoryAdapter (Outbound Adapter)
            → MongoFranchiseRepository → MongoDB
```

---

## Principios Aplicados

| Principio | Implementación |
|-----------|----------------|
| **Dependency Inversion** | Dominio define interfaces; infraestructura las implementa |
| **Single Responsibility** | Cada clase tiene una sola razón de cambio |
| **Open/Closed** | Nuevos adaptadores sin modificar el dominio |
| **Reactive Streams** | Mono/Flux en toda la cadena, sin bloqueos |

---

## Infraestructura Docker

| Contenedor | Imagen | Puerto | Descripción |
|------------|--------|--------|-------------|
| `franchise-app` | Build local (multi-stage) | 8080 | API Spring Boot WebFlux |
| `franchise-mongo` | `mongo:7` | 27017 | Base de datos MongoDB |

### Dockerfile (Multi-Stage)

- **Stage 1**: `eclipse-temurin:17-jdk-alpine` → Maven build
- **Stage 2**: `eclipse-temurin:17-jre-alpine` → Runtime mínimo (~200MB)

---

## Gestión de Configuración

```
.env → docker-compose.yml (env_file) → Contenedores
.env → application.yml (${VARIABLE:default}) → Spring Boot
```

- `.env` en `.gitignore` — nunca se sube al repositorio
- `.env.example` como template sin valores sensibles
