FROM openjdk:25-slim-bookworm

WORKDIR /opt/shinyproxy

# Update glibc to fix CVE-2025-0395
RUN apt-get update && \
    apt-get install -y --only-upgrade libc6=2.36-9+deb12u10 && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

COPY target/shinyproxy-3.1.1-exec.jar ./shinyproxy.jar

CMD ["java","-jar","shinyproxy.jar"]