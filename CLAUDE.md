# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun

# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.garage.flightscanner.SomeClassTest"

# Run tests matching a pattern
./gradlew test --tests "*ServiceTest"

# Clean build
./gradlew clean build

# Build native image (requires GraalVM 25+)
./gradlew nativeCompile

# Build Docker image
./gradlew bootBuildImage
```

## Project Overview

FlightScanner is a Spring Boot 4.0.2 microservice using Java 21. Key dependencies:
- Spring Data JPA with Hibernate 7.2
- Spring WebFlux and WebMVC
- WebClient for external HTTP calls
- Lombok for boilerplate reduction
- GraalVM native image support

## Architecture

Standard layered microservice architecture under `com.garage.flightscanner`:

```
├── controller/      # REST endpoints (@RestController)
├── service/         # Business logic (@Service)
├── repository/      # Data access (@Repository, extends JpaRepository)
├── entity/          # JPA entities (@Entity)
├── dto/             # Request/Response DTOs
├── mapper/          # Entity <-> DTO conversions
├── config/          # Spring configuration (@Configuration)
├── exception/       # Custom exceptions and @ControllerAdvice handlers
└── client/          # External service clients (WebClient wrappers)
```

## Conventions

- **Controllers**: Thin layer, delegate to services, return DTOs
- **Services**: Interface + Impl pattern, contain business logic, transaction boundaries
- **Repositories**: Extend `JpaRepository<Entity, ID>`, custom queries via `@Query`
- **DTOs**: Separate request/response objects, use records where appropriate
- **Entities**: JPA annotations, Lombok `@Data`/`@Builder`, avoid exposing directly in APIs
- **Exception handling**: Global `@ControllerAdvice` with `@ExceptionHandler` methods
- **Testing**: Unit tests for services (mock repositories), integration tests for controllers (`@WebMvcTest`), repository tests (`@DataJpaTest`)