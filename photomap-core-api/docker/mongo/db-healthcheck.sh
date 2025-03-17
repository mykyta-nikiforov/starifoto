#!/bin/bash

MONGO_ROOT_USERNAME=${MONGO_INITDB_ROOT_USERNAME}
MONGO_ROOT_PASSWORD=${MONGO_INITDB_ROOT_PASSWORD}

# Check if the photomap database exists
mongo_output=$(mongosh --username "$MONGO_ROOT_USERNAME" --password "$MONGO_ROOT_PASSWORD" --eval 'db.getMongo().getDBNames().indexOf("photomap") >= 0')

if [[ "$mongo_output" == *"true"* ]]; then
  echo "Database exists"
  exit 0
else
  echo "Database does not exist"
  exit 1
fi
