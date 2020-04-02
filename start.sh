#!/bin/bash

file="docker-compose.yml"
mode="production"
if [[ "$1" == "dev" ]]; then
	file="docker-compose-dev.yml"
	mode="development"
fi

echo "Starting Docker Containers in $mode..."

docker-compose -f $file up -d


