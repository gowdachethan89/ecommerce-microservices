# E-commerce Microservices — Inventory & Order

This repository contains two Spring Boot microservices demonstrating a simple e-commerce flow:

- inventory-service (port 8081)
- order-service (port 8082)

This README documents the available REST APIs (URLs, request/response examples) and quick run / debug instructions.

---

## Quick Start

From the repository root you can run both services with Maven (two shells or single command):

PowerShell (single command):

```powershell
mvn -pl inventory-service,order-service -am spring-boot:run
```

Or run each module separately (two terminals):

```powershell
# Inventory service
mvn -pl inventory-service -am spring-boot:run

# Order service
mvn -pl order-service -am spring-boot:run
```

Both services use in-memory H2 databases and Liquibase changelogs to load schema + sample CSV data at startup.

---

## Inventory Service (port 8081)

Base URL: http://localhost:8081

H2 Console: http://localhost:8081/h2-console
- JDBC URL: `jdbc:h2:mem:inventorydb`
- Username: `sa` (password empty)

Liquibase changelogs: `inventory-service/src/main/resources/db/changelog/`
- `changelog-001-create-tables.xml` (creates table `product_batch`)
- `changelog-002-data-load.xml` (loads `inventory.csv`)
- CSV: `inventory-service/src/main/resources/db/changelog/data/inventory.csv` (sample batches)

Endpoints
---------

1) GET /inventory/{productId}
- Description: Returns product information and a list of batches sorted by expiry date (ascending).
- URL example: `GET http://localhost:8081/inventory/1001`
- Response (example):

```json
{
  "productId": 1001,
  "productName": "Laptop",
  "batches": [
    { "batchId": 1, "quantity": 68, "expiryDate": "2026-06-25" }
    // ...
  ]
}
```

PowerShell example:
```powershell
Invoke-RestMethod -Method Get -Uri http://localhost:8081/inventory/1001
```

curl example:
```bash
curl -X GET http://localhost:8081/inventory/1001
```

2) POST /inventory/update
- Description: Reserve / decrement inventory quantity across batches (FEFO / earliest expiry first). Returns reservation result and batch ids used.
- URL: `POST http://localhost:8081/inventory/update`
- Request body (JSON):

```json
{
  "productId": 1002,
  "quantity": 3
}
```

- Successful response (example):
```json
{
  "success": true,
  "message": "Reserved",
  "reservedFromBatchIds": [10]
}
```

PowerShell example:
```powershell
$body = @{ productId = 1002; quantity = 3 } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri http://localhost:8081/inventory/update -Body $body -ContentType 'application/json'
```

curl example:
```bash
curl -X POST -H "Content-Type: application/json" -d '{"productId":1002,"quantity":3}' http://localhost:8081/inventory/update
```

---

## Order Service (port 8082)

Base URL: http://localhost:8082

H2 Console: http://localhost:8082/h2-console
- JDBC URL: `jdbc:h2:mem:orderdb`
- Username: `sa` (password empty)

Liquibase changelogs: `order-service/src/main/resources/db/changelog/`
- `changelog-001-create-tables.xml` (creates `orders` table)
- `changelog-005-reload-orders.xml` (loads sample orders CSV in a dev-friendly way)
- CSV: `order-service/src/main/resources/db/changelog/data/orders.csv`

Configuration
-------------
- Order service reads the inventory base URL from `application.yml`:
```yaml
inventory:
  service:
    url: http://localhost:8081
```
Change this if your inventory service runs on a different host/port.

Endpoints
---------

1) POST /order
- Description: Places an order. The Order service first queries Inventory service for batches, then calls Inventory service to reserve inventory, and finally stores the order.
- URL: `POST http://localhost:8082/order`
- Request body (JSON):

```json
{
  "productId": 1002,
  "quantity": 3
}
```

- Successful response (example):
```json
{
  "orderId": 1000,
  "productId": 1002,
  "productName": "Smartphone",
  "quantity": 3,
  "status": "PLACED",
  "reservedFromBatchIds": [9],
  "message": "Order placed. Inventory reserved."
}
```

PowerShell example:
```powershell
$body = @{ productId = 1002; quantity = 3 } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri http://localhost:8082/order -Body $body -ContentType 'application/json'
```

curl example:
```bash
curl -X POST -H "Content-Type: application/json" -d '{"productId":1002,"quantity":3}' http://localhost:8082/order
```

Notes: the response includes `reservedFromBatchIds` (batches where inventory was deducted) and a human `message`.

---

## Development / Debugging Tips

- If Liquibase has already applied a changeSet in a persistent DB, changing the changeSet file will not re-apply it. For development with in-memory H2 the DB is recreated on each restart so changes to changelogs / CSV will take effect when you restart the app.

- H2 Console connections (for each service):
  - Inventory: JDBC URL `jdbc:h2:mem:inventorydb`
  - Orders: JDBC URL `jdbc:h2:mem:orderdb`
  - User: `sa`, password empty

- Default ports: Inventory 8081, Order 8082. If you change ports, update the `inventory.service.url` property in the Order service `application.yml`.

- Factory pattern: Inventory reservation logic is implemented via `InventoryHandler` interface and a `DefaultInventoryHandler` bean; the `InventoryHandlerFactory` picks handler beans by name.

---

## Where to look in the code

- Inventory service
  - Controllers: `inventory-service/src/main/java/com/ecommerce/inventory/controller/InventoryController.java`
  - Service: `inventory-service/src/main/java/com/ecommerce/inventory/service/InventoryServiceImpl.java`
  - Repository: `inventory-service/src/main/java/com/ecommerce/inventory/repository/ProductBatchRepository.java`
  - Factory/handlers: `inventory-service/src/main/java/com/ecommerce/inventory/factory/`
  - Liquibase & sample data: `inventory-service/src/main/resources/db/changelog/`

- Order service
  - Controller: `order-service/src/main/java/com/ecommerce/order/controller/OrderController.java`
  - Service: `order-service/src/main/java/com/ecommerce/order/service/OrderServiceImpl.java`
  - Repository / entity: `order-service/src/main/java/com/ecommerce/order/repository/` and `.../model/OrderEntity.java`
  - Liquibase & sample data: `order-service/src/main/resources/db/changelog/`

---

If you want, I can also:
- Add OpenAPI/Swagger UI for both services;
- Add unit and integration test scaffolding (recommended for the assignment requirements);
- Add a small README per-module (inventory-service/ORDER-service) with focused examples.

Which of those would you like next?
