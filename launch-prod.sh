#!/usr/bin/env bash

# Launch script for Abc-Map
# You can modify environment vars to modify site behavior

. ./setenv.sh

export SAFRANLICES_ACTIVE_PROFILE=prod

./mvnw spring-boot:run
