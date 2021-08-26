FROM openjdk:8

WORKDIR /opt/shinyproxy

COPY target/shinyproxy-2.3.2-oidc-k8s.jar ./shinyproxy.jar

CMD ["java","-jar","shinyproxy.jar"]
