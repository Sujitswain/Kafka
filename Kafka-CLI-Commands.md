# Kafka CLI Commands Reference

This document lists all the essential Kafka CLI commands used for managing topics, producing messages, and consuming messages.

## Prerequisites
- Kafka is running (e.g., via Docker on localhost:9092)
- Commands assume `/opt/kafka/bin/` path (adjust if using different installation)

## Topic Management

### Create a Topic
```bash
/opt/kafka/bin/kafka-topics.sh --create --topic <topic-name> --bootstrap-server localhost:9092 --partitions <num-partitions> --replication-factor <replication-factor>
```
Example:
```bash
/opt/kafka/bin/kafka-topics.sh --create --topic test-topic --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```

### Delete a Topic
```bash
/opt/kafka/bin/kafka-topics.sh --delete --topic <topic-name> --bootstrap-server localhost:9092
```
Example:
```bash
/opt/kafka/bin/kafka-topics.sh --delete --topic test-topic --bootstrap-server localhost:9092
```

### List Topics
```bash
/opt/kafka/bin/kafka-topics.sh --list --bootstrap-server localhost:9092
```

### Describe a Topic
```bash
/opt/kafka/bin/kafka-topics.sh --describe --topic <topic-name> --bootstrap-server localhost:9092
```
Example:
```bash
/opt/kafka/bin/kafka-topics.sh --describe --topic test-topic --bootstrap-server localhost:9092
```

## Producing Messages

### Basic Producer
```bash
/opt/kafka/bin/kafka-console-producer.sh --topic <topic-name> --bootstrap-server localhost:9092
```
Example:
```bash
/opt/kafka/bin/kafka-console-producer.sh --topic test-topic --bootstrap-server localhost:9092
```

### Producer with Key Support
```bash
/opt/kafka/bin/kafka-console-producer.sh --topic <topic-name> --bootstrap-server localhost:9092 --property "parse.key=true" --property "key.separator=:"
```
Example:
```bash
/opt/kafka/bin/kafka-console-producer.sh --topic multi-part-topic --bootstrap-server localhost:9092 --property "parse.key=true" --property "key.separator=:"
```

## Consuming Messages

### Basic Consumer (from beginning)
```bash
/opt/kafka/bin/kafka-console-consumer.sh --topic <topic-name> --bootstrap-server localhost:9092 --from-beginning
```
Example:
```bash
/opt/kafka/bin/kafka-console-consumer.sh --topic test-topic --bootstrap-server localhost:9092 --from-beginning
```

### Consumer with Consumer Group
```bash
/opt/kafka/bin/kafka-console-consumer.sh --topic <topic-name> --bootstrap-server localhost:9092 --group <group-name> --from-beginning
```
Example:
```bash
/opt/kafka/bin/kafka-console-consumer.sh --topic cg-topic --bootstrap-server localhost:9092 --group group-A --from-beginning
```

### Consumer with Detailed Output (partition, offset, value)
```bash
/opt/kafka/bin/kafka-console-consumer.sh --topic <topic-name> --bootstrap-server localhost:9092 --from-beginning --property print.value=true --property print.partition=true --property print.offset=true
```
Example:
```bash
/opt/kafka/bin/kafka-console-consumer.sh --topic multi-part-topic --bootstrap-server localhost:9092 --from-beginning --property print.value=true --property print.partition=true --property print.offset=true
```

### Consumer with Group and Detailed Output
```bash
/opt/kafka/bin/kafka-console-consumer.sh --topic <topic-name> --bootstrap-server localhost:9092 --group <group-name> --from-beginning --property print.value=true --property print.partition=true --property print.offset=true
```
Example:
```bash
/opt/kafka/bin/kafka-console-consumer.sh --topic cg-topic --bootstrap-server localhost:9092 --group group-A --from-beginning --property print.value=true --property print.partition=true --property print.offset=true
```

## Consumer Group Management

### Describe a Consumer Group
This command provides detailed information about a consumer group, including its members, assigned partitions, current offsets, and lag (how many messages are yet to be consumed).

```bash
/opt/kafka/bin/kafka-consumer-groups.sh --bootstrap-server <bootstrap-server> --describe --group <group-name>
```
Example:
```bash
/opt/kafka/bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group group-A
```
