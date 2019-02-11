#!/usr/bin/env bash
./mvnw clean package -DskipTests
docker build -t marugoto .
docker run -it --rm -p 8080:8080 marugoto