FROM openjdk:8

WORKDIR /opt/shinyproxy

COPY target/shinyproxy-2.6.1.jar ./shinyproxy.jar

CMD ["java","-jar","shinyproxy.jar"]
