FROM openjdk:8-jre

RUN mkdir -p /opt/shinyproxy/
COPY target/shinyproxy-2.4.0-SNAPSHOT.jar /opt/shinyproxy/shinyproxy.jar
COPY application.yml /opt/shinyproxy/application.yml

WORKDIR /opt/shinyproxy/
CMD ["java", "-jar", "/opt/shinyproxy/shinyproxy.jar"]