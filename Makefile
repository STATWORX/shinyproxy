IMAGE = shinyproxy:2.6.1-oidc-extra
CONTAINER = shiny-proxy
NETWORK = cdck-net

THIS_FILE := $(realpath $(lastword $(MAKEFILE_LIST)))
THIS_FILE_DIR := $(shell dirname $(THIS_FILE))

build:
	DOCKER_BUILDKIT=1 docker build -f $(THIS_FILE_DIR)/Dockerfile \
                -t $(IMAGE) \
                --pull \
                --no-cache \
                $(THIS_FILE_DIR)

