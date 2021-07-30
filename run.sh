
# Run zookeeper
/opt/kafka_2.13-2.8.0/bin/zookeeper-server-start.sh config/zookeeper.properties

# Run kafka
/opt/kafka_2.13-2.8.0/bin/kafka-server-start.sh config/server.properties

# Create 'crypto' topic
/opt/kafka_2.13-2.8.0/bin/kafka-topics.sh --create --zookeeper \
localhost:2181 --replication-factor 1 --partitions 1 --topic crypto

# Describe 'crypto' topic
/opt/kafka_2.13-2.8.0/bin/kafka-topics.sh --describe --topic \
crypto --bootstrap-server localhost:9092


