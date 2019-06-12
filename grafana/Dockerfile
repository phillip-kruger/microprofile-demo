FROM grafana/grafana

COPY dashboards/*.yml /etc/grafana/provisioning/dashboards/
COPY datasources/*.yml  /etc/grafana/provisioning/datasources/
COPY dashboards/*.json /var/lib/grafana/dashboards/

ADD grafana.ini /etc/grafana/grafana.ini