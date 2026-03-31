# Quantity Measurement App

Spring Boot application for quantity conversion and arithmetic operations with JWT-based authentication and user management.

## Tech Stack

- Java 17+
- Spring Boot
- Spring Security (JWT)
- Spring Data JPA
- Maven

## Features

- Quantity conversions (for supported units)
- Quantity arithmetic operations
- User signup and login
- JWT token generation and request authorization
- Global exception handling with structured error responses

## Project Structure

- `src/main/java/com/example/quantity_measurement/controller` - REST controllers
- `src/main/java/com/example/quantity_measurement/service` - business logic
- `src/main/java/com/example/quantity_measurement/repository` - data access
- `src/main/java/com/example/quantity_measurement/security` - JWT and security filter/config
- `src/main/java/com/example/quantity_measurement/dto` - request/response DTOs
- `src/main/java/com/example/quantity_measurement/exception` - custom exceptions and handlers
- `src/test/java/com/example/quantity_measurement` - tests

## Getting Started

### Prerequisites

- JDK 17 or higher
- Maven 3.8+

### Run the application

```bash
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

By default, the app runs on:

- `http://localhost:8080`

## Build and Test

Run all tests:

```bash
./mvnw test
```

Create a build artifact:

```bash
./mvnw clean package
```

## Branches

- `main`: Production-ready baseline branch. Keep this branch stable and release-focused.
- `dev`: Shared integration branch where completed feature branches are merged and validated together.

Suggested flow: `feature/*` -> `dev` -> `main`

- `feature/UC1-FeetEquality`: Introduces the first equality rule using feet-to-feet comparisons as the initial domain behavior.
- `feature/UC2-InchEquality`: Extends equality coverage to inch quantities and aligns equality behavior across basic length units.
- `feature/UC3-GenericLength`: Refactors length handling to be more generic so multiple length units can share common logic.
- `feature/UC4-YardEquality`: Adds yard support and validates consistent equality checks between yard values and other length units.
- `feature/UC5-UnitConversion`: Implements unit conversion rules so compatible units can be converted before comparison or operations.
- `feature/UC6-UnitAddition`: Adds arithmetic addition for compatible quantities (for example, length + length).
- `feature/UC7-TargetUnitAddition`: Enhances addition by allowing the final result to be returned in a caller-requested target unit.
- `feature/UC8-StandaloneUnit`: Improves unit modeling so each unit can be handled cleanly and reused consistently across operations.
- `feature/UC9-WeightEquality`: Adds weight-domain equality support, expanding beyond only length-based scenarios.
- `feature/UC10-GenericQuantity`: Generalizes core quantity modeling to support multiple domains (length, weight, volume) through shared abstractions.
- `feature/UC11-VolumeEquality`: Adds equality support for volume measurements and validates cross-unit volume equivalence.
- `feature/UC12-QuantityOperations`: Expands supported operations beyond equality and addition to a broader quantity operation set.
- `feature/UC13-EnforceDRY`: Refactors duplicated logic into shared components to improve maintainability and reduce regression risk.
- `feature/UC14-SelectiveArithmeticSupport`: Enforces arithmetic compatibility checks so only valid unit/domain combinations are processed.
- `feature/UC15-N-TierArchitecture`: Restructures the codebase into clearer layers (controller, service, repository, model/entity) for better separation of concerns.
- `feature/UC16-DatabaseIntegration`: Introduces persistence with entities/repositories so quantity records can be stored and retrieved from a database.
- `feature/UC17-SpringFrameworkIntegration`: Integrates use cases into Spring Boot endpoints/services and wiring (DI, configuration, request handling).
- `feature/UC18-AuthenticationAndUserManagement`: Adds user signup/login, JWT issuance/validation, and secured endpoint access control.

## Notes

- Main app entry point: `QuantityMeasurementApplication`
- App configuration: `src/main/resources/application.properties`
- Security configuration: `src/main/java/com/example/quantity_measurement/config/SecurityConfig.java`
