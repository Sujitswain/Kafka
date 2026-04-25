# Kafka Learning Checklist

## Phase 1: Setup

1. Install Apache Kafka
2. Run Kafka (KRaft mode, no ZooKeeper)
3. Verify Kafka is running

## Phase 2: Kafka CLI 

4. Create a topic
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