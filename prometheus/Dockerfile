FROM prom/prometheus
COPY prometheus.yml /etc/prometheus/prometheus.yml
CMD [ "sh", "-c", "envsubst < /etc/prometheus/prometheus.yml.template > /etc/prometheus/prometheus.yml && /bin/prometheus --config.file=/etc/prometheus/prometheus.yml" ]
