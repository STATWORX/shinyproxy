TAG ?= pbi_v0.14.2
IMAGE = mseucdckdacr.azurecr.io/shinyproxy:$(TAG)
CONTAINER = shiny-proxy
NETWORK = cdck-net

THIS_FILE := $(realpath $(lastword $(MAKEFILE_LIST)))
THIS_FILE_DIR := $(shell dirname $(THIS_FILE))

package:
	mvn -U clean package -Dlicense.skip=true -DskipTests=true


build:
	docker build --platform=linux/amd64 -f $(THIS_FILE_DIR)/Dockerfile \
				--pull \
				-t $(IMAGE) \
				--build-arg PROXY_VERSION=$(TAG) \
				$(THIS_FILE_DIR)

run:
	docker run --rm \
	-p 8081:8081 \
	-v /var/run/docker.sock:/var/run/docker.sock \
	$(IMAGE)


push: check-registry
	docker tag $(IMAGE) $(REGISTRY)/$(IMAGE) 
	az acr login --name $(REGISTRY) || (az login && az acr login --name $(REGISTRY)) 
	docker push $(REGISTRY)/$(IMAGE)

check-registry:
ifndef REGISTRY 
	$(error REGISTRY is not set, use `make push REGISTRY=<registry>`) 
endif