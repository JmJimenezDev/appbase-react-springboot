# appbase-rest
Backend REST API para autenticación y gestión de usuarios con Spring Boot 3.5.
## Tecnologías
- **Framework:** Spring Boot 3.5.13
- **Lenguaje:** Java 17
- **Base de datos:** PostgreSQL
- **ORM:** Spring Data JPA
- **Seguridad:** Spring Security + JWT
- **Rate Limiting:** Bucket4j 8.17.0
- **Documentación API:** SpringDoc OpenAPI 2.8.16
- **Password:** BCrypt via Spring Security
## Estructura del Proyecto
```
appbase-rest/
├── src/main/java/dev/jmjimenez/appbase_rest/
│   ├── controller/          # Controladores REST
│   ├── service/            # Lógica de negocio
│   ├── entity/             # Entidades JPA
│   ├── repository/         # Repositorios JPA
│   ├── security/           # Configuración de seguridad JWT
│   ├── dto/                # Data Transfer Objects
│   ├── mapper/             # MapStruct mappers
│   ├── exception/          # Manejo de excepciones
│   ├── config/             # Configuraciones adicionales
│   ├── specification/       # JPA Specifications
│   ├── util/               # Utilidades
│   └── jobs/               # Tareas programadas
├── src/main/resources/config/
│   ├── application.properties
│   ├── application-dev.properties
│   └── application-prod.properties
└── pom.xml
```
## Requisitos Previos
- Java 17+
- Maven 3.8+
- PostgreSQL 12+
## Instalación
1. Clonar el repositorio
2. Crear la base de datos PostgreSQL:
   ```sql
   CREATE DATABASE appbase_db;
   CREATE USER appbase_user WITH PASSWORD '123qweasd';
   GRANT ALL PRIVILEGES ON DATABASE appbase_db TO appbase_user;
   ```
3. Configurar variables de entorno:
   ```bash
   export JWT_SECRET=<tu-secret-base64>
   ```
4. Ejecutar:
   ```bash
   ./mvnw clean install
   ./mvnw spring-boot:run -P dev
   ```
## Configuración
### Variables de Entorno
| Variable | Descripción | Valor por defecto |
|----------|-------------|-------------------|
| `JWT_SECRET` | Clave secreta para JWT (Base64) | Requerido |
| `SPRING_DATASOURCE_URL` | URL de PostgreSQL | jdbc:postgresql://localhost:5432/appbase_db |
| `SPRING_DATASOURCE_USERNAME` | Usuario de BD | appbase_user |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de BD | 123qweasd |
### Rate Limiting
| Tipo | Límite | Duración |
|------|--------|----------|
| General | 120 requests | 1 minuto |
| Login | 5 requests | 5 minutos |
| Ban | - | 20 minutos (tras 3 violaciones) |
## Endpoints API
### Autenticación (`/api/auth`)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/register` | Registrar usuario |
| POST | `/login` | Iniciar sesión |
| POST | `/refresh` | Refrescar token |
| GET | `/profile` | Perfil del usuario actual |
| POST | `/logout` | Cerrar sesión |
### Usuarios (`/api/users`) - Requiere autenticación
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/` | Listar usuarios (paginado) |
| GET | `/{id}` | Obtener usuario por ID |
| POST | `/` | Crear usuario (Admin) |
| PUT | `/{id}` | Actualizar usuario |
| PUT | `/{id}/password` | Cambiar contraseña |
| PUT | `/{id}/email` | Cambiar email |
| DELETE | `/{id}` | Eliminar usuario (Admin) |
| GET | `/export/pdf` | Exportar a PDF |
| GET | `/export/csv` | Exportar a CSV |
### Admin (`/api/admin`) - Requiere ROLE_ADMIN
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/status` | Estado de la aplicación |
### Documentación
| Endpoint | Descripción |
|----------|-------------|
| `/swagger-ui` | Swagger UI |
| `/api-docs` | OpenAPI JSON |
## Autenticación
### Registro
```json
POST /api/auth/register
{
  "name": "Juan",
  "surnames": "Pérez",
  "email": "juan@example.com",
  "password": "securePassword123"
}
```
### Login
```json
POST /api/auth/login
{
  "email": "juan@example.com",
  "password": "securePassword123"
}
```
**Respuesta:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```
### Uso del Token
```
Authorization: Bearer <accessToken>
```
## Filtros de Búsqueda (Users)
| Parámetro | Descripción |
|-----------|-------------|
| `search` | Búsqueda global (nombre, apellidos, email) |
| `role` | Filtrar por rol |
| `emailVerified` | Filtrar por verificación de email |
| `enabled` | Filtrar por estado |
| `createdAfter` | Fecha de creación posterior |
| `createdBefore` | Fecha de creación anterior |
| `page` | Número de página |
| `size` | Tamaño de página |
| `sort` | Campo de ordenamiento |
## Seguridad
- Contraseñas codificadas con BCrypt
- Tokens JWT con expiración (access: 15min, refresh: 7 días)
- CSRF protection habilitado
- Rate limiting por IP
- Cookies HTTP-only y secure
- Roles: `ROLE_USER`, `ROLE_ADMIN`
## Comandos Maven
```bash
# Desarrollo
./mvnw spring-boot:run -P dev
# Producción
./mvnw spring-boot:run -P prod
# Solo compilar
./mvnw clean package
# Ejecutar tests
./mvnw test
# Tests con cobertura
./mvnw test jacoco:report
```
## Licencia
MIT