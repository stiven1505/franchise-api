# 🤖 Agent Flow — CI/CD & Automation Workflows

## Descripción

Este documento describe los flujos de automatización configurados para el proyecto, incluyendo **GitHub Actions** para CI/CD, control de calidad y despliegue con Docker.

---

## 1. CI Pipeline — Build & Test

### Workflow: `.github/workflows/ci.yml`

```yaml
name: CI Pipeline

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  build-and-test:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven

      - name: Run tests
        run: ./mvnw test -B

      - name: Build application
        run: ./mvnw package -DskipTests -B

      - name: Upload test results
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: test-results
          path: target/surefire-reports/
```

### Flujo del Agente CI

```
Push/PR → Checkout → Setup JDK 17 → Run Tests (27) → Build JAR → Upload Reports
```

---

## 2. Docker Build & Push

### Workflow: `.github/workflows/docker.yml`

```yaml
name: Docker Build & Push

on:
  push:
    tags: ['v*']

jobs:
  docker:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v3

      - name: Login to Docker Hub
        uses: docker/login-action@v3
        with:
          username: ${{ secrets.DOCKERHUB_USERNAME }}
          password: ${{ secrets.DOCKERHUB_TOKEN }}

      - name: Extract version from tag
        id: version
        run: echo "VERSION=${GITHUB_REF#refs/tags/v}" >> $GITHUB_OUTPUT

      - name: Build and push
        uses: docker/build-push-action@v6
        with:
          context: .
          push: true
          tags: |
            ${{ secrets.DOCKERHUB_USERNAME }}/franchise-api:${{ steps.version.outputs.VERSION }}
            ${{ secrets.DOCKERHUB_USERNAME }}/franchise-api:latest
          cache-from: type=gha
          cache-to: type=gha,mode=max
```

### Flujo del Agente Docker

```
Tag v*.*.* → Checkout → Docker Buildx → Login DockerHub → Build Multi-Stage → Push Image
```

---

## 3. Secretos de GitHub Requeridos

| Secret | Descripción | Usado en |
|--------|-------------|----------|
| `DOCKERHUB_USERNAME` | Usuario de Docker Hub | Docker workflow |
| `DOCKERHUB_TOKEN` | Access token de Docker Hub | Docker workflow |

---

## 4. Flujo Completo del Desarrollo

```
Developer
    │
    ├── feature branch
    │   └── Push → CI Pipeline (tests + build)
    │
    ├── Pull Request → main
    │   └── CI Pipeline (tests + build) → Code Review → Merge
    │
    └── Release Tag (v1.0.0)
        └── Docker workflow → Build image → Push to DockerHub
                                               │
                                               ▼
                                        docker pull → docker compose up
```

---

## 5. Ejecución Local con Docker

### Levantar el entorno completo

```bash
# Clonar repositorio
git clone <repo-url>
cd franchise-api

# Configurar variables de entorno
cp .env.example .env
# Editar .env con tus credenciales

# Levantar servicios
docker compose up --build -d

# Verificar que funciona
curl http://localhost:8080/api/v1/franchises
```

### Comandos útiles

```bash
# Ver logs
docker compose logs -f app

# Reiniciar solo la app
docker compose restart app

# Detener todo
docker compose down

# Detener y eliminar volúmenes (reset DB)
docker compose down -v
```

---

## 6. Convenciones de Branching

| Branch | Propósito |
|--------|-----------|
| `main` | Producción — protegida, solo merge via PR |
| `develop` | Integración — branch de desarrollo |
| `feature/*` | Nuevas funcionalidades |
| `fix/*` | Corrección de bugs |
| `release/*` | Preparación de release |

---

## 7. Convenciones de Commits

Seguir [Conventional Commits](https://www.conventionalcommits.org/):

```
feat: add product to branch endpoint
fix: handle null products list in top-stock query
docs: update architecture diagram
test: add controller integration tests
chore: update Spring Boot to 3.5.14
```

---

## 8. Monitoreo Recomendado

| Herramienta | Propósito | Integración |
|-------------|-----------|-------------|
| Spring Boot Actuator | Health checks, métricas | Agregar dependencia |
| Prometheus + Grafana | Dashboards de métricas | Docker compose service |
| MongoDB Atlas | Monitoreo de BD | Cloud provider |

### Health Check Endpoint (si se agrega Actuator)

```yaml
# Agregar al docker-compose.yml
healthcheck:
  test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
  interval: 30s
  timeout: 10s
  retries: 3
```
