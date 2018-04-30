# Microprofile demo

Applications to demonstrate MicroProfile 

## Prometheus and Grafana

Configure in :

    /etc/prometheus/prometheus.yml

Start as system control process:

    sudo systemctl start prometheus.service
    sudo systemctl start grafana.service

View consoles:

* [prometheus](http://localhost:9090/)
* [grafana](http://localhost:3000/)