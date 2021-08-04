# Crypto Analyst   
[![Build Status](https://travis-ci.com/ehsan1222/crypto-analyst.svg?branch=develop)](https://travis-ci.com/ehsan1222/crypto-analyst)
[![codecov](https://codecov.io/gh/ehsan1222/crypto-analyst/branch/develop/graph/badge.svg?token=1OG0LVPVIM)](https://codecov.io/gh/ehsan1222/crypto-analyst)
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
* In mysql
  * create **crypto** DB `CREATE DATABASE crypto;`
  * create **crypto** user `CREATE USER 'crypto'@'localhost' IDENTIFIED BY 'password';`
  * grant all privileges to crypto user `GRANT ALL PRIVILEGES ON crypto.* TO 'crypto'@'localhost';'` 
  * create **alert** table `CREATE TABLE alert(
    id int auto_increment,
    rule varchar(45),
    market varchar(45),
    price double,
    close_date timestamp,
    primary key(id)
    );`

* In crypto-analyst directory run `./mvnw clean install`
* In **request** module run `../mvnw spring-boot:run` to get crypto infos and store in kafka.
* In **eval** module run `../mvnw spring-boot:run` to get data from kafka and evaluate it and add alert to database.
* In **web** module run `../mvnw spring-boot:run` to show alert data with json format.
