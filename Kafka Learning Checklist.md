# Kafka Learning Checklist

## Phase 1: Setup

1. Install Apache Kafka
2. Run Kafka (KRaft mode, no ZooKeeper)
3. Verify Kafka is running

Used Docker
    docker run -d --name kafka -p 9092:9092 ^
    -e KAFKA_PROCESS_ROLES=broker,controller ^
    -e KAFKA_NODE_ID=1 ^
    -e KAFKA_CONTROLLER_QUORUM_VOTERS=1@localhost:9093 ^
    -e KAFKA_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093 ^
    -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 ^
    -e KAFKA_CONTROLLER_LISTENER_NAMES=CONTROLLER ^
    apache/kafka:latest

# Line-by-line explanation

## 1. Start container
docker run -d --name kafka -p 9092:9092

### Meaning:

* `docker run` → start container
* `-d` → run in background
* `--name kafka` → container name = kafka
* `-p 9092:9092` → expose Kafka to your machine

Result:
Kafka available at: localhost:9092

## 2. Node identity
-e KAFKA_NODE_ID=1
This Kafka server gets ID = 1

## 3. Roles (VERY IMPORTANT)
-e KAFKA_PROCESS_ROLES=broker,controller

This node does 2 jobs:

* **broker** → stores data (topics, partitions)
* **controller** → manages cluster decisions

One machine = data + brain

## 4. Controller quorum (cluster coordination)
-e KAFKA_CONTROLLER_QUORUM_VOTERS=1@localhost:9093

This defines:
* who is the controller
* how Kafka agrees on cluster state

“Node 1 is controller at localhost:9093”

## 5. Listeners (ports Kafka uses internally)
-e KAFKA_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093
 Kafka opens 2 ports:

| Port | Purpose                           |
| ---- | --------------------------------- |
| 9092 | Producer/Consumer traffic         |
| 9093 | Internal controller communication |


## 6. Advertised listener (VERY IMPORTANT)
-e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092

This tells clients:

“Connect to Kafka at localhost:9092”
Without this:
* producers/consumers won’t connect properly

## 7. Controller listener name
-e KAFKA_CONTROLLER_LISTENER_NAMES=CONTROLLER
Use CONTROLLER port (9093) for cluster coordination

## 8. Image
apache/kafka:latest

# Final simple mental model

Docker runs Kafka container
   ↓
Broker handles data (topics, messages)
   ↓
Controller manages cluster decisions
   ↓
Clients connect via port 9092

# One-line summary
This command starts a single-node Kafka server with broker + controller roles, exposes it on port 9092, and configures how producers/consumers connect.

Stop the container

    docker stop kafka

## Phase 2: Kafka CLI 

4. Create a topic

    docker start kafka

    docker ps

    docker exec -it kafka bash

    (if not work for creating topic use prefix - /opt/kafka/bin/)

    CREATE TOPIC    : /opt/kafka/bin/kafka-topics.sh --create --topic test-topic --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
    UPDATE TOPIC    : kafka-topics.sh --alter --topic test-topic --partitions 3 --bootstrap-server localhost:9092

    DELETE TOPIC    : /opt/kafka/bin/kafka-topics.sh --delete --topic test-topic --bootstrap-server localhost:9092
    LIST TOPIC      : /opt/kafka/bin/kafka-topics.sh --list --bootstrap-server localhost:9092

    CREATE PRODUCER : /opt/kafka/bin/kafka-console-producer.sh --topic test-topic --bootstrap-server localhost:9092
    CREATE CONSUMER : /opt/kafka/bin/kafka-console-consumer.sh --topic test-topic --from-beginning --bootstrap-server localhost:9092


5. List topics
6. Describe a topic
7. Produce messages (CLI producer)
8. Consume messages (CLI consumer)

## Phase 3: Topics & Partitions

9. Create topic with multiple partitions
10. Send messages and observe distribution
11. Understand ordering within partitions

## Phase 4: Consumer Groups

12. Run multiple consumers with same `group.id`
13. Observe load balancing (no duplication)
14. Run consumers with different `group.id`
15. Observe duplication across groups

## Phase 5: Offsets & Commit

16. Consume messages and stop consumer
17. Restart and observe offset behavior
18. Learn:

* `earliest` vs `latest`
* auto commit vs manual commit

## Phase 6: Keys & Partitioning

19. Send messages **with key**
20. Send messages **without key**
21. Observe:

* Same key → same partition
* Ordering behavior

## Phase 7: Rebalance

22. Start multiple consumers
23. Stop one consumer
24. Add a new consumer
25. Observe partition reassignment

## Phase 8: Brokers & Replication

26. Create topic with replication factor
27. Understand leader & follower
28. Learn fault tolerance behavior

## Phase 9: Basic Configs (Light Learning)

29. Producer configs:

* `acks` (0, 1, all)
* retries
* serializers

30. Consumer configs:

* `auto-offset-reset`
* `enable-auto-commit`
* `max.poll.interval.ms`

## Phase 10: Java Kafka (No Spring)

31. Create simple producer (Java)
32. Create simple consumer (Java)
33. Send & receive messages programmatically

## Phase 11: Spring Boot Kafka

34. Add Kafka dependency
35. Configure `group-id`
36. Use `@KafkaListener`
37. Send JSON messages
38. Build producer API
39. Build consumer service

## Phase 12: Advanced

40. Manual offset commit
41. Retry handling
42. Error handling
43. Idempotent processing