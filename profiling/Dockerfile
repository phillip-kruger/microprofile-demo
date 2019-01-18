FROM openjdk:8u181

COPY target/profiling-thorntail.jar .

CMD java ${JAVA_OPTS} -jar profiling-thorntail.jar -Djava.net.preferIPv4Stack=true

