#!/bin/bash

file="docker-compose.yml"
mode="production"
if [[ "$1" == "dev" ]]; then
	file="docker-compose-dev.yml"
	mode="development"
elif [[ "$1" == "staging" ]]; then
	file="docker-compose-staging.yml"
	mode="staging"
fi

echo "Starting containers in $mode mode..."

docker-compose -f $file up -d


