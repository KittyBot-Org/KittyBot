#!/bin/bash

file="docker-compose.yml"
mode="production"
if [[ "$1" == "dev" ]]; then
	file="docker-compose-dev.yml"
	mode="development"
fi

echo "Starting containers in $mode mode..."

docker-compose -f $file up -d


