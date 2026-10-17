Let's start from the beginning and set up 3 Kafka brokers on your local machine using KRaft mode. I'll explain the steps in order, including which properties to change, when to generate the Cluster ID, and when to start each broker.

The key idea is that you'll run three separate Kafka server processes, all belonging to the same cluster.

## Step 1: Create three configuration files

In your Kafka installation, go to the `config` directory.

Copy your existing `server.properties` file twice and rename the files:

```
config/
├── server-1.properties
├── server-2.properties
└── server-3.properties
```

Each file configures one Kafka node.

## Step 2: Configure the three brokers

For this example, each node will act as both a broker and a controller. This is convenient for learning locally, although dedicated controllers are often preferred for production deployments.

### Broker 1 — `server-1.properties`

```
node.id=1
process.roles=broker,controller

listeners=PLAINTEXT://:9092,CONTROLLER://:9093
advertised.listeners=PLAINTEXT://localhost:9092

log.dirs=/tmp/kafka-logs-1

controller.listener.names=CONTROLLER
controller.quorum.voters=1@localhost:9093,2@localhost:9095,3@localhost:9097

inter.broker.listener.name=PLAINTEXT
listener.security.protocol.map=CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT
```

### Broker 2 — `server-2.properties`

```
node.id=2
process.roles=broker,controller

listeners=PLAINTEXT://:9094,CONTROLLER://:9095
advertised.listeners=PLAINTEXT://localhost:9094

log.dirs=/tmp/kafka-logs-2

controller.listener.names=CONTROLLER
controller.quorum.voters=1@localhost:9093,2@localhost:9095,3@localhost:9097

inter.broker.listener.name=PLAINTEXT
listener.security.protocol.map=CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT
```

### Broker 3 — `server-3.properties`

```
node.id=3
process.roles=broker,controller

listeners=PLAINTEXT://:9096,CONTROLLER://:9097
advertised.listeners=PLAINTEXT://localhost:9096

log.dirs=/tmp/kafka-logs-3

controller.listener.names=CONTROLLER
controller.quorum.voters=1@localhost:9093,2@localhost:9095,3@localhost:9097

inter.broker.listener.name=PLAINTEXT
listener.security.protocol.map=CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT
```

These are example configurations for a static KRaft quorum. Keep any other required settings appropriate for your Kafka version.

## Step 3: Generate one Cluster ID

Run this command once from your Kafka installation directory.

Linux/macOS:

```
bin/kafka-storage.sh random-uuid
```

Windows:

```
bin\windows\kafka-storage.bat random-uuid
```

Copy the generated ID. All three brokers must belong to this same cluster.

## Step 4: Format the storage for each broker

Use the same Cluster ID for all three nodes, but specify each node's own configuration file.

Linux/macOS:

```
bin/kafka-storage.sh format \
  -t YOUR_CLUSTER_ID \
  -c config/server-1.properties
```

Repeat for brokers 2 and 3, changing the configuration file each time.

Windows:

```
bin\windows\kafka-storage.bat format -t YOUR_CLUSTER_ID -c config\server-1.properties
```

Repeat with `server-2.properties` and `server-3.properties`.

Replace `YOUR_CLUSTER_ID` with the actual generated ID. Format only fresh storage directories; don't reformat an existing cluster's data.

## Step 5: Start all three brokers

Open three separate terminal windows.

Terminal 1 — Broker 1

```
bin/kafka-server-start.sh config/server-1.properties
```

Terminal 2 — Broker 2

```
bin/kafka-server-start.sh config/server-2.properties
```

Terminal 3 — Broker 3

```
bin/kafka-server-start.sh config/server-3.properties
```

On Windows, use `bin\windows\kafka-server-start.bat` with the relevant configuration path.

Wait until all three servers start successfully.

## Step 6: Verify the cluster with a topic

Create a topic with three partitions and a replication factor of three.

```
bin/kafka-topics.sh --create \
  --topic orders \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 3
```

Then inspect the partition assignments:

```
bin/kafka-topics.sh --describe \
  --topic orders \
  --bootstrap-server localhost:9092
```

You should see three partitions, with each partition having three replicas distributed across the three brokers.

## The entire process at a glance

1. Copy `server.properties` into three configuration files.
2. Configure unique node IDs, ports and storage directories.
3. Keep the controller quorum configuration consistent.
4. Generate one Cluster ID.
5. Format each node's storage using that same ID.
6. Start all three brokers in separate terminals.
7. Create a replicated topic and verify it.

One thing to check before running these commands: Which Kafka version are you using, and are you on Windows? The exact storage-formatting and startup commands can vary slightly by Kafka version.