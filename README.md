# AppBase

Proyecto full-stack con autenticación JWT, gestión de usuarios y control de roles.

## Estructura del Proyecto

```
appbase/
├── appbase-rest/       # Backend (Spring Boot 3.5 + Java 17)
├── appbase-web/        # Frontend (React 19 + Vite + TypeScript)
├── logs/               # Logs de la aplicación
├── pom.xml             # Maven parent POM
└── src/                # Recursos compartidos
```

## Tecnologías

### Backend (`appbase-rest`)
- **Framework:** Spring Boot 3.5.13
- **Lenguaje:** Java 17
- **Base de datos:** PostgreSQL
- **ORM:** Spring Data JPA
- **Seguridad:** Spring Security + JWT
- **Rate Limiting:** Bucket4j
- **Documentación:** SpringDoc OpenAPI

### Frontend (`appbase-web`)
- **Framework:** React 19 + TypeScript
- **Build Tool:** Vite 7
- **Styling:** Tailwind CSS 4
- **State:** Jotai (atoms)
- **Data Fetching:** TanStack Query
- **Forms:** React Hook Form + Zod
- **i18n:** i18next
- **Routing:** React Router DOM 7
- **HTTP:** Axios

## Requisitos Previos

- Java 17+
- Maven 3.8+
- Node.js 18+
- PostgreSQL 12+

## Instalación

### 1. Clonar el repositorio

### 2. Backend - Configurar PostgreSQL

```sql
CREATE DATABASE appbase_db;
CREATE USER appbase_user WITH PASSWORD '123qweasd';
GRANT ALL PRIVILEGES ON DATABASE appbase_db TO appbase_user;
```

### 3. Configurar variables de entorno

```bash
# Backend
export JWT_SECRET=<tu-secret-base64>

# Frontend - crear .env en appbase-web/
echo "VITE_API_BASE_URL=http://localhost:8080/api" > appbase-web/.env
```

### 4. Instalar dependencias e iniciar

```bash
# Backend
cd appbase-rest
./mvnw clean install
./mvnw spring-boot:run -P dev

# Frontend (nueva terminal)
cd appbase-web
npm install
npm run dev
```

## Configuración por Defecto

| Servicio | URL |
|----------|-----|
| Backend API | http://localhost:8080 |
| Frontend | http://localhost:5173 |
| Swagger UI | http://localhost:8080/swagger-ui |

## Funcionalidades

- **Autenticación:** Registro, login, logout con JWT
- **Roles:** ROLE_ADMIN, ROLE_USER
- **Gestión de Usuarios:** CRUD completo (admin)
- **Exportación:** PDF y CSV de usuarios
- **Internacionalización:** Español e Inglés
- **Temas:** Claro, Oscuro y Sistema
- **Seguridad:** Rate limiting, CSRF, cookies HTTP-only
- **Sesión:** Timeout por inactividad

## Scripts

### Backend
```bash
./mvnw spring-boot:run -P dev    # Desarrollo
./mvnw spring-boot:run -P prod    # Producción
./mvnw clean package              # Compilar
./mvnw test                       # Tests
```

### Frontend
```bash
npm run dev      # Desarrollo
npm run build    # Producción
npm run lint     # ESLint
npm run preview  # Previsualizar build
```

## Licencia

MIT