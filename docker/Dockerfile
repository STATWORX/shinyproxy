FROM openjdk:8-jre

COPY target/shinyproxy-2.3.3.jar /opt/shinyproxy/shinyproxy.jar
COPY application.yml /opt/shinyproxy/application.yml

WORKDIR /opt/shinyproxy

CMD ["java", "-jar", "shinyproxy.jar"]
