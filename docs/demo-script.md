# Demo script (about 5 minutes)

A walkthrough of the end-to-end flow: **browse → filter → basket → checkout → notification → rating**,
plus two short failure scenarios that show the cloud-native behaviour.

## Before the demo
- [ ] The stack is running (Docker Compose **or** Kubernetes with port-forwards)
- [ ] http://localhost:8080 (web UI) and http://localhost:8025 (Mailpit) are open in two browser tabs
- [ ] A terminal is ready (for `kubectl` / `docker compose` commands)
- [ ] Use a **new user ID** (for example `demo1`), so the basket and ratings start empty

## Part 1 – Architecture (30 s)
Show the architecture diagram in the README:
- one **API Gateway** as the single entry point;
- **4 services**, each with its **own database**;
- **REST** where an answer is needed now (Order → Catalog);
- **Kafka** for the order-completed event (Order → Notification).

## Part 2 – Customer journey (2 min)

| Step | Action in the UI | What to point out |
|---|---|---|
| 1 | Enter user `demo1`, email `demo1@example.com` → **Load user** | Empty basket, no orders |
| 2 | Filter **Category = PASTRY**, then **Max ₹ = 500**, sort **Price high → low** | Catalog Service: dynamic filters plus pagination |
| 3 | **Add** Chocolate Truffle ×1 and Mango Mousse ×2 | Order Service fetched the **real price from Catalog** |
| 4 | Change Mango Mousse to **3**, then remove one item and add it back | Basket update and remove, totals recalculated |
| 5 | **Checkout** | 201: the order appears as **PLACED**, and the basket empties (one transaction) |
| 6 | Wait about 1 s: **Notifications** shows **SENT**. Open the **Mailpit** tab | Event travelled **Order → Kafka → Notification → SMTP** asynchronously |
| 7 | **Rate** Chocolate Truffle ★★★★★. Then try to rate it again | Average updates. A second rating gives **409** (one per user) |

## Part 3 – Resilience (1.5 min)

**A. A dependency is down (fault isolation)**
```bash
kubectl scale deployment catalog-service --replicas=0      # or: docker compose stop catalog-service
```
- Adding to the basket shows *"Catalog service is currently unavailable"* (**503**, a clean error, no hang).
- **My orders** and **Notifications** still load: other services are unaffected, and orders use price snapshots.
```bash
kubectl scale deployment catalog-service --replicas=1      # or: docker compose start catalog-service
```

**B. Self-healing (Kubernetes)**
```bash
kubectl delete pod -l app=order-service
kubectl get pods -w
```
Kubernetes immediately starts a replacement pod. It only receives traffic once the **readiness probe** passes.

## Part 4 – Behind the scenes (1 min)
- Gateway logs with request IDs: `kubectl logs deploy/api-gateway --tail=10`
- An order event published: `kubectl logs deploy/order-service --tail=20 | findstr Published`
- Health and metrics: `/actuator/health`, `/actuator/prometheus`
- The Kafka consumer group (lag = 0):
  ```bash
  kubectl exec deploy/kafka -- /opt/kafka/bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group notification-service
  ```

## Closing (15 s)
Key design points: database per service, snapshots instead of cross-service joins, events
published only after commit, an idempotent consumer with retries and a dead-letter topic,
and probes, self-healing and config through ConfigMaps and Secrets in Kubernetes.

---

## Screenshot checklist (save in `docs/screenshots/`)

| File name | What to capture |
|---|---|
| `01-frontend-after-checkout.png` | Web UI: empty basket, order **PLACED**, notification **SENT** |
| `02-mailpit-email.png` | The confirmation email opened in Mailpit |
| `03-kubectl-get-pods.png` | `kubectl get pods`: all 8 pods `1/1 Running` |
| `04-swagger-catalog.png` | Swagger UI of the Catalog Service |
| `05-kubectl-get-svc.png` | `kubectl get svc` |
| `06-self-healing.png` | `kubectl get pods -w` while a deleted pod is replaced |
| `07-postman-e2e-run.png` | Postman runner: E2E Journey all green |
| `08-validation-error.png` | A 400 ProblemDetail with an `errors` map (Postman) |
| `09-catalog-down-503.png` | The UI or Postman showing the 503 when Catalog is scaled to 0 |
| `10-kafka-consumer-group.png` | Consumer group describe output (partitions, offsets, lag) |
| `11-prometheus-metrics.png` | `/actuator/prometheus` output |
| `12-gateway-logs.png` | Gateway access log lines with `requestId` |
