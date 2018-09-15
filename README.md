# Microprofile demo

![logo](https://raw.githubusercontent.com/phillip-kruger/microprofile-demo/master/microprofile-logo.png)

## Presentation

See the presentation [Google Slides](http://bit.ly/mp-presentation), and the [talk](https://www.youtube.com/watch?v=aSEPxDPc-ag)

## Sample

<img src="https://raw.githubusercontent.com/phillip-kruger/microprofile-demo/master/sample.gif" alt="sample" width="100%"/>

## Implementations

The demo runs on

<img src="https://raw.githubusercontent.com/phillip-kruger/microprofile-demo/master/wildfly-swarm.png" alt="wildfly-swarm" width="200px"/>
<img src="https://raw.githubusercontent.com/phillip-kruger/microprofile-demo/master/payara.jpeg" alt="payara" width="200px"/>
<img src="https://raw.githubusercontent.com/phillip-kruger/microprofile-demo/master/openliberty.png" alt="openliberty" width="200px"/>

## Dependencies

This demo use [microprofile-extentions](https://github.com/phillip-kruger/microprofile-extentions) extensively.

## Getting started.

### Prerequisite
You need a MySQL/Maria DB and Elasticsearch servers installed and running on your PC.
To test the Metrics you need Prometheus and Grafana

So something like this:

    sudo systemctl start mariadb.service
    sudo systemctl start elasticsearch.service

and:

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
Build and start **Membership service**

    cd membership/
    mvn clean install -Ppayara-micro-run

Build and start **Profiling service**

    cd profiling/
    mvn clean install -Prun

Build and start **User service**

    cd user/
    mvn clean install -Prun

Opening up the ```index.html``` in ```static/public_html``` will give you the demo screen
