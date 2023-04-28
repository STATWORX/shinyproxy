IMAGE = shinyproxy:2.6.1-oidc-extra
CONTAINER = shiny-proxy
NETWORK = cdck-net
TARGET = target/shinyproxy-2.6.1.jar

THIS_FILE := $(realpath $(lastword $(MAKEFILE_LIST)))
THIS_FILE_DIR := $(shell dirname $(THIS_FILE))

all: build_jar run_jar

build_jar:
	mvn -U clean install

run_jar:
	java -jar $(TARGET)

deps:
	mvn dependency:tree

inspect:
	jar tvf $(TARGET)

package:
	mvn clean package -f pom.xml && java -jar $(TARGET)

build:
	DOCKER_BUILDKIT=1 docker build -f $(THIS_FILE_DIR)/Dockerfile \
                -t $(IMAGE) \
                --pull \
                --no-cache \
                $(THIS_FILE_DIR)