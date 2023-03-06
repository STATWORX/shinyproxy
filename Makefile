TAG ?= 2.6
IMAGE = shinyproxy:$(TAG)
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

push: check-registry
	docker tag $(IMAGE) $(REGISTRY)/$(IMAGE) 
	az acr login --name $(REGISTRY) || (az login && az acr login --name $(REGISTRY)) 
	docker push $(REGISTRY)/$(IMAGE)



check-registry:
ifndef REGISTRY 
	$(error REGISTRY is not set) 
endif