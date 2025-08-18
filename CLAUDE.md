# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot multi-module microservices project called "2thecore_back" that handles GPS tracking and vehicle management. The system uses a microservices architecture with message queuing and consists of:

- **main-server** (port 8080): Primary REST API server handling admin, authentication, car management, collector services, and drive logs
- **hub-server** (port 8082): GPS data processing hub that consumes messages from RabbitMQ
- **common**: Shared module containing domain entities, DTOs, and infrastructure components

## Architecture

### Multi-Module Structure
- **common/**: Shared components (JWT, entities, DTOs, exceptions)
- **main-server/**: Primary API server with modules:
  - `admin/`: Admin user management
  - `auth/`: JWT authentication and authorization  
  - `car/`: Vehicle management and tracking
  - `collector/`: GPS data collection and RabbitMQ publishing
  - `drivelog/`: Drive history and analytics
- **hub-server/**: GPS data processing consumer service

### Key Technologies
- Spring Boot 3.5.3 with Java 17
- Spring Security with JWT authentication
- Spring Data JPA with MySQL
- MyBatis for dynamic queries
- RabbitMQ for message queuing
- Redis for caching
- Docker with docker-compose
- Prometheus monitoring
- Swagger/OpenAPI documentation

### Database & Infrastructure
- MySQL primary database
- Redis for session/token storage
- RabbitMQ for async GPS data processing
- Docker containerization for all services

## Development Commands

### Build & Test
```bash
# Navigate to back directory first
cd back

# Build all modules
./gradlew build

# Build specific module
./gradlew :main-server:build
./gradlew :hub-server:build
./gradlew :common:build

# Run tests
./gradlew test

# Run tests for specific module  
./gradlew :main-server:test
./gradlew :hub-server:test

# Clean build
./gradlew clean
```

### Running Applications
```bash
# Run main-server (port 8080)
./gradlew :main-server:bootRun

# Run hub-server (port 8082)  
./gradlew :hub-server:bootRun

# Run with Docker
cd docker
docker-compose up -d

# Stop Docker services
docker-compose down
```

### Development Setup
1. Ensure MySQL, Redis, and RabbitMQ are running (use docker-compose)
2. Configure environment variables in `prod.env` 
3. Main server uses MyBatis mappers in `src/main/resources/mapper/`
4. Application configs in `application.yml` support environment variable overrides

## Code Structure & Patterns

### Package Organization
- Domain-driven design with clear separation of concerns
- Each module follows layered architecture: `controller` → `application` → `domain` → `infrastructure`
- DTOs separate request/response objects from domain entities
- Exception handling with custom exceptions and global handlers

### Key Components
- **JWT Security**: Token-based authentication with refresh tokens
- **Message Queue**: RabbitMQ producer in main-server, consumer in hub-server
- **Database Access**: JPA repositories + MyBatis for complex queries
- **Monitoring**: Actuator endpoints with Prometheus metrics
- **API Documentation**: SpringDoc OpenAPI/Swagger UI

### Entity Relationships
- Cars have GPS logs and drive logs
- Admins manage the system with role-based access
- GPS data flows: collector → RabbitMQ → hub-server → database

### Testing
- Uses JUnit 5 with Spring Boot Test
- Separate test profiles with H2 in-memory database
- Test containers for integration tests
- Mockito for unit testing

## Environment Configuration

### Required Environment Variables
- `JWT_SECRET_KEY`: JWT signing key
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`: Database connection
- `RABBITMQ_HOST`, `RABBITMQ_USERNAME`, `RABBITMQ_PASSWORD`: Message queue
- `REDIS_HOST`, `REDIS_PORT`: Cache configuration

### Profiles
- Default: Development with local services
- `test`: Test profile with H2 database
- `prod`: Production profile using environment variables

## API Documentation
- Swagger UI available at `http://localhost:8080/swagger-ui.html` (main-server)
- OpenAPI spec includes authentication endpoints, car management, and GPS tracking APIs