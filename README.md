# Microprofile demo

![logo](https://raw.githubusercontent.com/phillip-kruger/microprofile-demo/master/microprofile-logo.png)

## Presentation

See the presentation [Google Slides](http://bit.ly/mp-presentation), and the [talk](https://www.youtube.com/watch?v=aSEPxDPc-ag)

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
