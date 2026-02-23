# E-commerce Microservices (Inventory + Order)

This repository contains two Spring Boot microservices as modules under the same Maven parent POM:

- inventory-service (port 8081)
- order-service (port 8082)

What I created
- Module-level pom.xml files for both services
- Spring Boot application skeleton (Controller/Service/Repository)
- Liquibase changelogs and sample CSV data to auto-load into H2 at startup
- Factory Pattern skeleton in the Inventory Service (InventoryHandler + Factory)
- A README with quick run instructions and API examples

Project structure (top-level view)

- pom.xml (parent)
- inventory-service/
  - pom.xml
  - src/main/java/com/ecommerce/inventory/...
  - src/main/resources/db/changelog/... (Liquibase files + CSV)
- order-service/
  - pom.xml
  - src/main/java/com/ecommerce/order/...
  - src/main/resources/db/changelog/... (Liquibase files + CSV)

Quick start (Maven)

1. From the project root run:

   mvn -pl inventory-service,order-service -am spring-boot:run

   This will build modules and run (you can run them individually if you prefer):

   mvn -pl inventory-service -am spring-boot:run
   mvn -pl order-service -am spring-boot:run

2. Endpoints

Inventory Service (8081):
- GET /inventory/{productId} -> List batches sorted by expiry date
- POST /inventory/update -> Reserve quantity (body: {"productId":1002, "quantity":3})

Order Service (8082):
- POST /order -> Places an order and attempts to reserve inventory via Inventory Service
  Request body: {"productId":1002, "quantity":3}

Notes and next steps
- Add unit and integration tests (JUnit5 + Mockito + @SpringBootTest) — skeletons can be added easily
- Add more InventoryHandler implementations to demonstrate the Factory pattern
- Add OpenAPI/Swagger for API docs


