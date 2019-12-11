IMAGE = shinyproxy:2.4.1-SNAP
CONTAINER = shiny-proxy
NETWORK = df-net

REMOTE_FILE_SHARE =

THIS_FILE := $(realpath $(lastword $(MAKEFILE_LIST)))
THIS_FILE_DIR := $(shell dirname $(THIS_FILE))


build:
	DOCKER_BUILDKIT=1 docker build -f $(THIS_FILE_DIR)/Dockerfile \
                -t $(IMAGE) \
                $(THIS_FILE_DIR)/..


run:
	docker run -d \
                --name ${CONTAINER} \
                -p 8080:8080 \
		-v $(THIS_FILE_DIR)/../application_mongodb.yml:/opt/shinyproxy/application.yml \
		-v /var/run/docker.sock:/var/run/docker.sock \
                --net ${NETWORK} \
                ${IMAGE}

stop:
	docker stop ${CONTAINER}

rm: stop
	docker rm ${CONTAINER}

package:
	docker save -o $(THIS_FILE_DIR)/../target/$(TAR) $(IMAGE)
	cd $(THIS_FILE_DIR)/.. && tar cfz target/$(PACKAGE) target/$(TAR) docker/Makefile
	rm $(THIS_FILE_DIR)/../target/$(TAR)

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
