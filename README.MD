#### To start the Kafka Broker in Machine
goto bin folder
```./zookeeper-server-start.sh ../config/zookeeper.properties```

#### Update server.properties
    listeners=PLAINTEXT://localhost:9092
    auto.create.topics.enable=false

```./kafka-server-start.sh ../config/server.properties```