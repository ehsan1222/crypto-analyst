# Crypto Analyst

### Getting start

#### Modules
* db (database common)
* kafka-common (kafka common)
* request (get crypto data and store in kafka)
* eval (get data from kafka and evaluate it)
* web (rest web service)

#### Prerequisites

* Java 11+
* Mysql
* Kafka

#### How do you test it!

* Download kafka and extract it.(suppose kafka_2.13-2.8.0 was downloaded and extracted in /opt)
* Start zookeeper 
  * `bin/zookeeper-server-start.sh config/zookeeper.properties`
* Start kafka server 
    * `bin/kafka-server-start.sh config/server.properties`
* In crypto-analyst directory run `./mvnw clean install`
* In **request** module run `../mvnw spring-boot:run` to get crypto infos and store in kafka.
* In **eval** module run `../mvnw spring-boot:run` to get data from kafka and evaluate it and add alert to database.
* In **web** module run `../mvnw spring-boot:run` to show alert data with json format.
