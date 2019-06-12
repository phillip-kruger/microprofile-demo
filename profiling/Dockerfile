FROM openjdk:8u212-b04

COPY target/profiling-thorntail.jar .
COPY application.properties .

CMD java ${JAVA_OPTS} -jar profiling-thorntail.jar -Djava.net.preferIPv4Stack=true