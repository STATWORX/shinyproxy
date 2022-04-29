FROM openjdk:8

WORKDIR /opt/shinyproxy

COPY target/shinyproxy-2.6.0.jar ./shinyproxy.jar

CMD ["java","-jar","shinyproxy.jar"]
