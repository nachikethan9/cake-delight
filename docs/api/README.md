# API reference

All endpoints are reachable through the **API Gateway**: `http://localhost:8080`.
Machine-readable OpenAPI 3.1 specs are in this folder:

| File | Service |
|---|---|
| `catalog-service.json` | Catalog |
| `order-service.json` | Order (basket and orders) |
| `rating-service.json` | Rating |
| `notification-service.json` | Notification |

Errors are returned as **ProblemDetail** (`application/problem+json`) with `title`, `status`,
`detail`, `instance`, `timestamp` and, for validation errors, an `errors` map.

---

## Catalog – `/api/cakes`

| Method | Path | Description | Success | Errors |
|---|---|---|---|---|
| GET | `/api/cakes` | Search cakes. Query: `name`, `category`, `minPrice`, `maxPrice`, `available`, `page`, `size` (max 50), `sort` (e.g. `price,desc`) | 200 (paged) | 400 invalid range, sort or enum |
| GET | `/api/cakes/{id}` | Get one cake | 200 | 404 |
| POST | `/api/cakes` | Create a cake | 201 + `Location` | 400 validation |
| PUT | `/api/cakes/{id}` | Replace a cake | 200 | 400, 404 |
| DELETE | `/api/cakes/{id}` | Delete a cake | 204 | 404 |

Request body (POST/PUT):
```json
{ "name": "Chocolate Truffle", "description": "Rich dark chocolate layered cake",
  "category": "BIRTHDAY", "price": 899.00, "available": true,
  "imageUrl": "https://example.com/truffle.jpg" }
```

## Basket – `/api/baskets/{userId}` (Order service)

| Method | Path | Description | Success | Errors |
|---|---|---|---|---|
| GET | `/api/baskets/{userId}` | View basket with line totals and grand total | 200 | – |
| POST | `/api/baskets/{userId}/items` | Add a cake `{ "cakeId": 10, "quantity": 2 }` (the same cake again increases the quantity) | 200 | 400, 404 cake, 409 unavailable, 503 Catalog down |
| PUT | `/api/baskets/{userId}/items/{cakeId}` | Set the quantity `{ "quantity": 3 }` | 200 | 400, 404 |
| DELETE | `/api/baskets/{userId}/items/{cakeId}` | Remove an item | 200 | 404 |
| DELETE | `/api/baskets/{userId}` | Empty the basket | 204 | – |

## Orders – `/api/orders` (Order service)

| Method | Path | Description | Success | Errors |
|---|---|---|---|---|
| POST | `/api/orders/checkout` | `{ "userId": "alice", "customerEmail": "alice@example.com" }`. Creates the order, empties the basket, publishes `ORDER_COMPLETED` | 201 + `Location` | 400 empty basket or validation, 409 unavailable, 503 |
| GET | `/api/orders/{orderId}` | Order details with items | 200 | 404 |
| GET | `/api/orders?userId=alice` | Order history (summaries, newest first, paged) | 200 | 400 |
| PATCH | `/api/orders/{orderId}/status` | `{ "status": "CONFIRMED" }`. Only valid transitions are allowed | 200 | 400, 404, 409 invalid transition or concurrent update |

## Ratings – `/api/ratings`

| Method | Path | Description | Success | Errors |
|---|---|---|---|---|
| POST | `/api/ratings` | `{ "cakeId": 10, "userId": "alice", "score": 5, "comment": "Amazing!" }` | 201 + `Location` | 400, 409 already rated |
| GET | `/api/ratings/{id}` | Get one rating | 200 | 404 |
| GET | `/api/ratings/cake/{cakeId}` | Ratings for a cake (newest first, paged) | 200 | – |
| GET | `/api/ratings/cake/{cakeId}/summary` | `{ "cakeId": 10, "averageRating": 4.3, "ratingCount": 3 }` | 200 | – |
| DELETE | `/api/ratings/{id}` | Delete a rating | 204 | 404 |

## Notifications – `/api/notifications`

| Method | Path | Description | Success |
|---|---|---|---|
| GET | `/api/notifications?userId=alice` | In-app inbox (newest first, paged) | 200 |
| GET | `/api/notifications/order/{orderId}` | Notifications and delivery status for an order | 200 |

Notifications are created **only** by `ORDER_COMPLETED` events from Kafka. There is no create endpoint.

---

## How the OpenAPI files were exported

With a service running, save its spec:

```bash
curl -s http://localhost:8081/v3/api-docs -o docs/api/catalog-service.json
curl -s http://localhost:8082/v3/api-docs -o docs/api/order-service.json
curl -s http://localhost:8083/v3/api-docs -o docs/api/rating-service.json
curl -s http://localhost:8084/v3/api-docs -o docs/api/notification-service.json
```

The JSON files can be viewed at https://editor.swagger.io (File → Import file).
