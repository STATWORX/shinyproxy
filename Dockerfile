
FROM debian:bookworm-slim

ARG PROXY_VERSION
ARG userhomedir=/opt/shinyproxy

RUN apt-get update && apt-get upgrade -y && apt-get install -y --no-install-recommends wget apt-transport-https ca-certificates\
    && wget -O - https://packages.adoptium.net/artifactory/api/gpg/key/public | tee /etc/apt/keyrings/adoptium.asc \
    && echo "deb [signed-by=/etc/apt/keyrings/adoptium.asc] https://packages.adoptium.net/artifactory/deb $(awk -F= '/^VERSION_CODENAME/{print$2}' /etc/os-release) main" | tee /etc/apt/sources.list.d/adoptium.list \
    && apt-get update && apt-get install -y --no-install-recommends temurin-8-jre \
    && apt-get purge -y wget apt-transport-https \
    && apt-get autoremove -y \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/* 

# RUN useradd --create-home --home $userhomedir --no-log-init app && chmod 770 $userhomedir
# USER app
WORKDIR $userhomedir

# COPY --chmod=0755  target/shinyproxy-${PROXY_VERSION}*.jar shinyproxy.jar
COPY target/shinyproxy-${PROXY_VERSION}*.jar shinyproxy.jar

CMD ["java","-jar","shinyproxy.jar"]
