#!/bin/bash

if [ "$1" = "fe" ]; then
    cd frontend
    npm start
    cd ..
else
    cd backend
    mvn clean install -DskipTests
    docker compose up -d --build
    cd ..
    cd frontend
    npm start
    cd ..
fi