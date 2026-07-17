## About the assignment

You will find the tasks of this assignment on [CODE_ASSIGNMENT](assignment/CODE_ASSIGNMENT.md).

## Overview

This project is a small warehouse/store/product fulfilment sample built with Quarkus. It uses a layered, hexagonal (ports-and-adapters) style for features such as `products`, `stores`, `warehouses` and `fulfilment`.

## Prerequisites

- Java 17+ (set `JAVA_HOME` to a JDK 17 installation)
- Maven (the repository includes the Maven wrapper `mvnw`/`mvnw.cmd`)
- Docker (optional) — only required if you want Quarkus DevServices / Testcontainers to start a PostgreSQL container automatically

## Run locally (dev mode)

From a PowerShell terminal (recommended on Windows):

Start Database with Docker:

```powershell
docker run -it --rm --name quarkus_test -e POSTGRES_USER=quarkus_test -e POSTGRES_PASSWORD=quarkus_test -e POSTGRES_DB=quarkus_test -p 15432:5432 postgres:13.3
```
Run the application:

```powershell
cd java-assignment
.\mvnw.cmd quarkus:dev
```

This starts Quarkus in dev mode on `http://localhost:8080` and will pick up configuration from `src/main/resources/application.properties`.

If the Quarkus dev server fails to start because Testcontainers cannot find Docker, either start Docker or run Quarkus with DevServices disabled. To add the OpenAPI/Swagger UI extensions if missing:

```powershell
cd java-assignment
.\mvnw.cmd quarkus:add-extension -Dextensions="quarkus-smallrye-openapi,quarkus-swagger-ui"
.\mvnw.cmd quarkus:dev
```

## OpenAPI / Swagger UI

- Swagger UI (interactive): `http://localhost:8080/q/swagger-ui`
- OpenAPI JSON: `http://localhost:8080/q/openapi?format=json`
- OpenAPI YAML: `http://localhost:8080/q/openapi?format=yaml`
- Local copy of the YAML in the repo: [java-assignment/src/main/resources/openapi/warehouse-openapi.yaml](java-assignment/src/main/resources/openapi/warehouse-openapi.yaml)

If you want to quickly view the OpenAPI file without running the app, open the YAML file path above.

## Run tests

Run unit tests with Maven from the `java-assignment` folder:

```powershell
cd java-assignment
.\mvnw.cmd test
```

The test profile is configured to use an H2 in-memory database (see `src/test/resources/application.properties`) so tests run without Docker.

## Design pattern and architecture

This codebase follows a layered, hexagonal (ports-and-adapters) approach:

- Domain models: simple POJOs representing entities (`Product`, `Store`, `Warehouse`, `Fulfilment`, etc.).
- Ports: interfaces that express persistence or external system requirements (e.g. `ProductStore`, `StoreStore`, `FulfilmentStore`, `WarehouseStore`).
- Adapters: concrete implementations of ports (database adapters using Panache, external gateways, `DbStore`, `ProductRepository`, etc.).
- Services / Use-cases: application/business logic that orchestrates ports and domain operations (`ProductService`, `StoreService`, `FulfilmentService`, warehouse use-cases).
- REST resources: JAX-RS resources exposing endpoints and delegating to services (`ProductResource`, `StoreResource`, `FulfilmentResource`, `WarehouseResource`).

This separation makes business logic testable in isolation from the Quarkus runtime and allows swapping persistence or external systems without changing use-cases.

## Package / folder structure (important locations)

- `java-assignment/src/main/java/com/fulfilment/application/monolith/`
	- `products/` — domain, `ProductService`, `ProductRepository`, `ProductResource`
	- `stores/` — `StoreService`, `DbStore` (adapter), `StoreResource`, legacy gateway
	- `warehouses/` — layered warehouse domain, use-cases, adapters (db & rest)
	- `fulfilment/` — fulfilment entity, service, repository/port and REST resource (bonus feature)
	- `location/` — `LocationGateway` and related tests

- `src/main/resources/` — application config and OpenAPI YAML
- `src/test/java/` — unit tests (designed to run without a Quarkus runtime)
- `src/test/resources/application.properties` — H2 test database settings

## Assignment Status
### Tasks

#### 1. Location (Must have)  - Completed :heavy_check_mark:

#### 2. Store (Must have)  - Completed :heavy_check_mark:

#### 3. Warehouse (Must have) - Completed :heavy_check_mark:

#### BONUS task (nice to have) - Completed :heavy_check_mark:
