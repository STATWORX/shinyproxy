FROM openjdk:8

RUN apt-get update -y && apt-get upgrade -y  \
    && apt-get -f autoremove --purge && apt-get clean && rm -rf /var/lib/apt/lists/*

WORKDIR /opt/shinyproxy

COPY target/shinyproxy-2.6.1.jar ./shinyproxy.jar

CMD ["java","-jar","shinyproxy.jar"]
