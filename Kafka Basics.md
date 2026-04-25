# Kafka

# Apache Kafka — Notes & Hands-on

## What is Kafka?

Apache Kafka is a **open-source, distributed, event streaming platform** used to build real-time data pipelines and streaming applications.

In simple terms:

* It lets services **send (produce)** and **receive (consume)** messages.
* It is designed for **high throughput, scalability, and fault tolerance**.

## Core Components of Kafka

* **Producer** → Sends data to Kafka
* **Consumer** → Reads data from Kafka
* **Topic** → Logical stream of messages
* **Partition** → Physical split of a topic (for parallelism)
* **Broker** → Kafka server that stores data
* **Consumer Group** → Group of consumers working together
* **Offset** → Position of a message in a partition


# What is a Broker?

In Apache Kafka:

 **Broker = one Kafka server (one running instance)**

Broker = 1 machine / 1 Kafka process

✔ Stores some partitions
✔ Handles read/write for those partitions

# What is a Cluster?

 **Cluster = group of multiple brokers working together**

Cluster = Broker1 + Broker2 + Broker3 + ...

✔ Data is distributed across them
✔ Provides scalability + fault tolerance

# Simple Picture

Kafka Cluster

Broker 1     Broker 2     Broker 3
   │             │             │
  P0            P1            P2

One topic is split and spread across brokers

# How it works

### 1. Producer sends message
* Producer → Kafka Cluster

### 2. Kafka decides partition
* Message → Partition P1

### 3. Partition belongs to a broker
* Partition P1 → Broker 2 (Leader)
* Message stored in Broker 2

### 4. Replication happens
* Leader (Broker 2) → Followers (Broker 1, Broker 3)
* ✔ Copies data to other brokers

### 5. Consumer reads
* Consumer → Broker 2 (Leader)
* Always reads from leader

# Old Way: ZooKeeper (you used earlier)

Apache ZooKeeper was used for:

* Broker coordination
* Leader election
* Metadata management

### Flow with ZooKeeper

ZooKeeper ←→ Kafka Brokers

## ZooKeeper keeps:

* which broker is alive
* who is leader
* cluster metadata

# New Way: KRaft (No ZooKeeper)

Now Kafka handles everything internally using:
KRaft = Kafka Raft Metadata mode

## How KRaft works

* Some brokers act as **controllers**
* They manage:

  * metadata
  * leader election
  * cluster state

### Roles in KRaft

Controller node → manages cluster  
Broker node → stores data  

# Key Difference
| Feature     | ZooKeeper Mode       | KRaft Mode |
| ----------- | -------------------- | ---------- |
| Dependency  | External (ZooKeeper) | Internal   |
| Complexity  | Higher               | Simpler    |
| Performance | Slower metadata ops  | Faster     |
| Setup       | Harder               | Easier     |

# Final Mental Model
Broker = one server (stores data)
Cluster = group of brokers

Old:
Kafka + ZooKeeper

New:
Kafka (self-managed using KRaft)

# One-line clarity
Cluster = multiple brokers working together to store and serve data reliably

### Consumer Group

* One service = one consumer group
* Multiple instances = multiple consumers in the group
* Messages are shared within the group

### Consumer Group (group.id)

* In Apache Kafka, a consumer group is identified by a group.id.
* A unique identifier for a consumer group
* Represents one service
* All instances of that service use the same group.id

### Partition

* Enables parallel processing
* Ordering is guaranteed only within a partition

### Offset

* Tracks how much data is consumed
* Stored per **consumer group**

## How Kafka Works (Simple)

1. Producer sends message → Topic
2. Kafka stores message in a partition
3. Each message gets an offset (position)
4. Consumer group subscribes to topic (group.id)
5. Kafka assigns partitions to consumers
6. Consumer polls (reads) messages
7. Messages are fetched
8. Consumer processes the message
9. After success → offset is committed
10. Kafka marks message as “processed” for that group
11. Next read starts from next offset


## Data Processing Models

Kafka supports multiple patterns:

* **Event-driven architecture**
* **Pub/Sub messaging**
* **Stream processing** (using Kafka Streams)
* **Log aggregation**

## Common Use Cases

* Order & payment systems
* Real-time analytics
* Logging and monitoring
* Event-driven microservices
* Real-time location tracking (ride/cab apps)

## Kafka vs RabbitMQ

| Feature           | Kafka                | RabbitMQ                          |
| ----------------- | -------------------- | --------------------------------- |
| Model             | Distributed log      | Message queue                     |
| Throughput        | Very high            | Moderate                          |
| Message retention | Yes                  | Usually deleted after consumption |
| Use case          | Streaming, analytics | Task queues                       |
| Scaling           | Partition-based      | Queue-based                       |

## Benefits of Kafka

* High throughput
* Horizontally scalable
* Fault-tolerant (replication)
* Durable message storage
* Supports real-time processing

---

# Kafka Rules & Guarantees

### 1. Partition → Consumer Rule

* One partition is consumed by only **one consumer in a group**

### 2. Consumer Group Isolation

* Each group gets **all messages independently**

### 3. Ordering Guarantee

* Ordering only within a partition
* No guarantee across partitions

### 4. Parallelism Rule

* Max parallelism = number of partitions

### 5. Scaling Behavior

* Consumers < partitions → one consumer handles multiple
* Consumers > partitions → some consumers idle

### 6. Offset Rule

* Offset is tracked per:
  * partition
  * consumer group

* One Topic (orders)
    [0] Order Created  
    [1] Order Confirmed  
    [2] Order Paid  
    [3] Order Shipped  
    [4] Order Delivered  

* Order Service (group: order-service)
    This service cares about ALL order events

    Processes:
    ✔ Created  
    ✔ Confirmed  
    ✔ Paid  
    ✔ Shipped  
    ✔ Delivered  

    So its offset moves like:
    offset = 4  (processed everything)

* Payment Service (group: payment-service)
   This service cares ONLY about payment

    Processes:
    ->  Order Paid  
    X Created → ignore  
    X Confirmed → ignore  
    X Shipped → ignore  
    X Delivered → ignore  

    * READ → (decide) → PROCESS or IGNORE → MOVE OFFSET
        * Payment Service flow
            Read [0] Created → ignore → move offset  
            Read [1] Confirmed → ignore → move offset  
            Read [2] Paid → process → move offset  
            Read [3] Shipped → ignore → move offset  
            Read [4] Delivered → ignore → move offset  

* Kafka does not filter messages — service does


### 7. Delivery Guarantee

* Default = **at-least-once**
* Can cause duplicates

### 8. Rebalance Rule

* Happens when consumers join/leave
* Partitions get reassigned

#### Rebalance in Kafka

#### What is Rebalance?

Rebalance is the process where Apache Kafka redistributes partitions among consumers in a consumer group.
Rebalance = Stop → Reassign partitions → Result

#### When does rebalance happen?

* New consumer joins the group
* Existing consumer crashes or leaves
* Consumer stops polling (timeout)
* Number of partitions changes

### What happens during rebalance?

1. Consumers stop consuming
2. Partitions are revoked
3. Kafka reassigns partitions
4. Consumers resume processing

### Problems during rebalance

* Temporary pause in processing
* Increased latency
* Possible duplicate processing

### Why duplicates can happen?

Read message → Rebalance before commit → Re-read by another consumer

### Key takeaway

* Rebalance is necessary for scaling and fault tolerance
* But frequent rebalances can hurt performance

## Static vs Dynamic Membership

### Dynamic Membership (Default)

* Consumers join without fixed identity
* Kafka assigns a temporary ID
* On restart → treated as a new consumer

Restart → New identity → Rebalance triggered

### Static Membership

* Consumer has a fixed identity using:

properties
group.instance.id=payment-1

### Behavior with static membership

* Restart does NOT create a new identity
* Kafka recognizes the same consumer
* Avoids unnecessary full rebalance

### Difference

| Feature          | Dynamic Membership | Static Membership           |
| ---------------- | ------------------ | --------------------------- |
| Identity         | Temporary          | Fixed (`group.instance.id`) |
| Restart behavior | New consumer       | Same consumer               |
| Rebalance        | Frequent           | Reduced                     |
| Stability        | Lower              | Higher                      |

### When to use static membership

* Long-running microservices
* Systems where rebalance is expensive
* Stable deployments (fixed instances)
* Static membership reduces unnecessary rebalances by keeping consumer identity stable.

## Summary

* Rebalance = partition redistribution in a group
* Happens on join/leave/failure
* Can cause pauses and duplicates
* Static membership helps reduce unnecessary rebalances


### 9. Retention Rule

* Messages stored based on time/size
* Not deleted immediately after consumption

### 10. Producer Partitioning

* Same key → same partition
* No key → round-robin

## Kafka Partitions & Message Key

In Apache Kafka, **partitions** and **keys** control:

* how data is distributed
* how ordering is maintained

## What is a Partition?

* A **topic is split into partitions**
* Each partition is an **ordered log**

Topic: orders

Partition 0 → [A, B, C]
Partition 1 → [D, E]
Partition 2 → [F, G]

## Why partitions?

* Enable **parallel processing**
* Improve **scalability**
* Allow multiple consumers to work together

## What is a Message Key?

When producing a message:
The **key decides which partition the message goes to**


## How Kafka chooses partition

### Case 1: Key is present

partition = hash(key) % number_of_partitions

✔ Same key → always same partition
✔ Maintains order for that key

### Case 2: No key

Round-robin distribution

✔ Messages spread evenly
X No ordering guarantee

## Example (VERY IMPORTANT)

### Messages:

OrderId = 101 → "Order Created"
OrderId = 101 → "Order Paid"
OrderId = 102 → "Order Created"

### With key = OrderId

Partition 0:
[Order101 Created, Order101 Paid]

Partition 1:
[Order102 Created]

✔ Order 101 events stay in order
✔ Correct processing

### Without key

Partition 0:
[Order101 Created]

Partition 1:
[Order101 Paid]

X Order breaks
X System becomes inconsistent

## Why key is critical

Key ensures:

* **Ordering per entity**
* Correct business logic
* No race conditions

## Real-world usage

| Use case          | Key       |
| ----------------- | --------- |
| Orders            | orderId   |
| Payments          | paymentId |
| Users             | userId    |
| Drivers (cab app) | driverId  |

## Example: Driver location

Key = driverId

✔ All updates for one driver → same partition
✔ Location updates stay in order

## Important rules

* Ordering guaranteed **only within a partition**
* Key controls partition → controls ordering
* Bad key choice → uneven load (hot partition)

## Interview tips

Key is used to maintain ordering for related events by ensuring they go to the same partition.

## Mental Model
Topic → Partitions → Messages
             ↑
           Key decides placement

## Summary

* Partition = unit of parallelism
* Key = controls partition assignment
* Same key → same partition → ordered processing
* No key → random distribution → no ordering

### 11. Fault Tolerance

* Data replicated across brokers

### 12. Pull Model

* Consumers pull data (not pushed)

### 13. Offset Commit

* Auto or manual commit
* Controls reliability

### 14. Processing Behavior

* Same group → no duplication
* Different groups → duplication

## Mental Model

Topic → Data stream  
Partition → Parallel lanes  
Consumer Group → Service  
Consumer → Instance  
Offset → Progress tracker  

## Real-world Best Practices

* One service = one consumer group
* Choose partitions based on scale
* Always use key if ordering matters
* Handle duplicate messages
* Do NOT mix different services in same group