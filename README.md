# Franchise API 🚀

API REST reactiva para la gestión de franquicias, sucursales y productos, construida con **Spring Boot WebFlux**, **MongoDB** y **Arquitectura Hexagonal**.

## 🏗️ Arquitectura
Este proyecto implementa una **Arquitectura Hexagonal** (Ports & Adapters) para asegurar el desacoplamiento de la lógica de negocio de los detalles de infraestructura.

- **Domain**: Modelos, excepciones y puertos (interfaces).
- **Application**: Casos de uso e implementación de servicios.
- **Infrastructure**: Adaptadores de entrada (REST Controllers) y salida (MongoDB Adapter), configuración y Docker.

## 🛠️ Tecnologías
- **Java 17** (Eclipse Temurin)
- **Spring Boot 3.5.14**
- **Spring WebFlux** (Programación Reactiva)
- **Spring Data MongoDB Reactive**
- **MongoDB 7**
- **Docker & Docker Compose**
- **Lombok**
- **JUnit 5 & Mockito** (27 tests implementados ✅)

## 🚀 Cómo empezar

### Requisitos previos
- Docker y Docker Compose instalados.
- Java 17 (si deseas ejecutarlo localmente sin Docker).

### Ejecución con Docker (Recomendado)
1. Clona el repositorio.
2. Asegúrate de tener el archivo `.env` configurado (puedes usar `.env.example` como base).
3. Ejecuta el comando:
   ```bash
   docker compose up --build
   ```
La aplicación estará disponible en `http://localhost:8080`.

### Ejecución Local
1. Configura una instancia de MongoDB (o usa Docker solo para la DB).
2. Ejecuta:
   ```bash
   ./mvnw spring-boot:run
   ```

## 🔌 API Endpoints
Todos los endpoints están versionados bajo `/api/v1/franchises`.

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `POST` | `/` | Crear una nueva franquicia |
| `POST` | `/{fId}/branches` | Agregar sucursal a una franquicia |
| `POST` | `/{fId}/branches/{bId}/products` | Agregar producto a una sucursal |
| `DELETE` | `/{fId}/branches/{bId}/products/{pId}` | Eliminar un producto |
| `PATCH` | `/{fId}/branches/{bId}/products/{pId}/stock` | Modificar stock de un producto |
| `GET` | `/{fId}/top-stock-products` | Producto con más stock por sucursal |
| `PATCH` | `/{fId}/name` | Actualizar nombre de franquicia |
| `PATCH` | `/{fId}/branches/{bId}/name` | Actualizar nombre de sucursal |
| `PATCH` | `/{fId}/branches/{bId}/products/{pId}/name` | Actualizar nombre de producto |

## 📊 Conexión a Base de Datos (MongoDB Compass)
Para visualizar los datos en MongoDB Compass, utiliza la siguiente URI de conexión:
```
mongodb://admin:secret123@localhost:27017/?authSource=admin
```

## 🧪 Testing
El proyecto cuenta con 27 tests unitarios y de integración que cubren la lógica de negocio y los controladores.
```bash
./mvnw test
```

## 📁 Documentación Detallada
Para más detalles, consulta la carpeta `docs/`:
- [Plan de Implementación](docs/implementation-plan.md)
- [Arquitectura Detallada](docs/architecture.md)
- [Flujos de Trabajo (CI/CD)](docs/workflows/agent-flow.md)
