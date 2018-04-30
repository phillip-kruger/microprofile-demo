# Microprofile demo

Applications to demonstrate MicroProfile 

## Health 1.0 API

[Spec](https://github.com/eclipse/microprofile-health/releases/tag/1.0)

## Metrics 1.1 API
[Spec](https://github.com/eclipse/microprofile-metrics/releases/tag/1.1)

## Prometheus and Grafana

Configure in :

    /etc/prometheus/prometheus.yml

Start as system control process:

    sudo systemctl start prometheus.service
    sudo systemctl start grafana.service

View consoles:

* [prometheus](http://localhost:9090/)
* [grafana](http://localhost:3000/)