IMAGE = shinyproxy:2.3.3
CONTAINER = shinyproxy
NETWORK = data-science-net

REMOTE_FILE_SHARE =

REGISTRY ?= registry.statworx

# Name of exported image-tar
TAR := $(shell echo $(IMAGE) | sed 's/:/_/g' | sed 's/\//_/g')-image.tar

# Name of exported package
PACKAGE := $(shell echo $(IMAGE) | sed 's/:/_/g' | sed 's/\//_/g').tar.gz

THIS_FILE := $(realpath $(lastword $(MAKEFILE_LIST)))
THIS_FILE_DIR := $(shell dirname $(THIS_FILE))


check:
	docker-check image ${IMAGE}
	docker-check network ${NETWORK}

build:
	DOCKER_BUILDKIT=1 docker build -f $(THIS_FILE_DIR)/Dockerfile \
		-t $(IMAGE) \
		$(THIS_FILE_DIR)/..

run:
	docker run -d \
		--name ${CONTAINER} \
		--net ${NETWORK} \
		-v $(THIS_FILE_DIR)/../application_mlflow.yml:/opt/shinyproxy/application.yml \
		-p 8080:8080 \
		${IMAGE}

stop:
	docker stop ${CONTAINER}

rm: stop
	docker rm ${CONTAINER}

package:
	docker save -o $(THIS_FILE_DIR)/../target/$(TAR) $(IMAGE)
	cd $(THIS_FILE_DIR)/.. && tar cfz target/$(PACKAGE) target/$(TAR) docker/Makefile source/resources  
	rm $(THIS_FILE_DIR)/../target/$(TAR)

push:
	docker tag $(IMAGE) $(REGISTRY)/$(IMAGE)
	docker push $(REGISTRY)/$(IMAGE)

publish:
	cp $(THIS_FILE_DIR)/../target/$(PACKAGE) $(REMOTE_FILE_SHARE)/$(PACKAGE)

load:
	docker load -i $(THIS_FILE_DIR)/../target/$(TAR)

help:
	$(info Use `make check` to check if environment is ready)
	$(info Use `make build` to build image)
	$(info Use `make run` to start container)
	$(info Use `make stop` to stop container)
	$(info Use `make rm` to remove container)
	$(info Use `make package` to pack resources for deployment)
	$(info Use `make load` to load image at deployment environment)
