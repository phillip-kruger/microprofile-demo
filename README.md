# Microprofile demo

----
[![Build Status](https://travis-ci.com/phillip-kruger/microprofile-demo.svg?branch=master)](https://travis-ci.com/phillip-kruger/microprofile-demo)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.phillip-kruger/microprofile-demo/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.phillip-kruger/microprofile-demo)
[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://raw.githubusercontent.com/phillip-kruger/microprofile-demo/master/LICENSE.txt)
----

![logo](https://raw.githubusercontent.com/phillip-kruger/microprofile-demo/master/microprofile-logo.png)

## Presentations

If any one is looking for a talk to give at your local meetups or conferences you are welcome to use all or parts of this demo and presentation. 
You can make it your own or use it as is. You are also welcome to improve the talk and demo and contribute back.
([reference](https://groups.google.com/forum/#!topic/microprofile/Zv6clnF6-GY))

### Known presentations

* [Java Cloud Conference](http://j-sa.co/) [Google Slides](http://bit.ly/mp-presentation), [Video](https://www.youtube.com/watch?v=aSEPxDPc-ag) - by [Phillip Kruger](https://twitter.com/phillipkruger) (July 2018)
* [DevConfCZ](https://devconf.info/cz) [Google Slides](https://docs.google.com/presentation/d/1Bub410JeNWZJZYNVUkYjp500zv6goaCpAifGSYjJA3s/edit#slide=id.p) - by [Martin Stefanko](https://twitter.com/xstefank) (January 2019)
* ... add you name here and do a PR :)


## Implementations

The demo runs on

<img src="https://raw.githubusercontent.com/phillip-kruger/microprofile-demo/master/thorntail.png" alt="thorntail" width="200px"/>
<img src="https://raw.githubusercontent.com/phillip-kruger/microprofile-demo/master/payara.jpeg" alt="payara" width="200px"/>
<img src="https://raw.githubusercontent.com/phillip-kruger/microprofile-demo/master/openliberty.png" alt="openliberty" width="200px"/>

## Dependencies

This demo use [microprofile-extentions](https://github.com/microprofile-extensions) extensively.

## High level use case

<img src="https://raw.githubusercontent.com/phillip-kruger/microprofile-demo/master/high_level.png" alt="highlevel" width="100%"/>

## Getting started.

### Prerequisite

To make the example easier we use an internal H2 Database and internal Elasticsearch server, so no need to install any datastore.

You can, however, use MySQL if you prefer:

#### MySQL / Maria (Optional)

You need a MySQL/Maria Database installed and running on your PC.

So something like this:
    
    sudo pacman -S mariadb
    sudo systemctl start mariadb.service

You need to create a Database and Database User in Maria:

    mysql -u root -p < membership/init.sql

You also need to change the datasource definition in web.xml and change the driver in pom.xml

#### Prometheus and Grafana (Optional)

To see the metrics in action (i.e. more than just the raw output) you will need a Prometheus and Grafana server.

So something like this:

    packer -S prometheus
    sudo pacman -Ss grafana
    sudo systemctl start prometheus.service
    sudo systemctl start grafana.service

Also make sure **prometheus** is configured in 

    /etc/prometheus/prometheus.yml

(look at [prometheus.yml](prometheus.yml) as an example. You can also use the [grafana.json](grafana.json) for the grafana dashboards)

### Source code
The then demo code on your PC:

    git clone https://github.com/phillip-kruger/microprofile-demo.git
    cd microprofile-demo
    mvn clean install

### Running

#### Manually
Build and start **Membership service**

    cd membership/
    mvn clean install -Prun

Build and start **Profiling service**

    cd profiling/
    mvn clean install -Prun

Build and start **User service**

    cd user/
    mvn clean install -Prun
    
#### Docker compose
Alternatively you can use docker and docker compose to run the whole project in a single command:

    mvn clean install
    docker-compose up --build

### Static Web Demo page

There is a static HTML Demo page, you can go to any of

* http://localhost:7080/profiling
* http://localhost:8080/membership
* http://localhost:9080/user

You can generate a token in the [User Service](http://localhost:9080/user) to be used in the other services.

You can find some test users in the OpenLiberty configuration - [server.xml](https://github.com/phillip-kruger/microprofile-demo/blob/master/user/src/main/openliberty/config/server.xml)

## Sample

<img src="https://raw.githubusercontent.com/phillip-kruger/microprofile-demo/master/sample.gif" alt="sample" width="100%"/>
