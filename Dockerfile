FROM openjdk:23-slim-bookworm

WORKDIR /opt/shinyproxy

COPY target/shinyproxy-3.1.1-exec.jar ./shinyproxy.jar

CMD ["java","-jar","shinyproxy.jar"]