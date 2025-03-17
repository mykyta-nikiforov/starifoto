#!/bin/bash

echo "Starting replica set initialization"

# Wait until mongo1 is available
until mongosh --username ${MONGO_ROOT_USERNAME} --password ${MONGO_ROOT_PASSWORD} --host mongo1 --eval "print(\"waiting for connection...\")" > /dev/null 2>&1
do
    sleep 2
done

echo "Connection established"

# Check if the replica set is already initialized
IS_RS_INITIALIZED=$(mongosh --username ${MONGO_ROOT_USERNAME} --password ${MONGO_ROOT_PASSWORD} --host mongo1 --eval "rs.status().ok" --quiet)
echo 'IS_RS_INITIALIZED: ' $IS_RS_INITIALIZED
if [ $IS_RS_INITIALIZED -eq 1 ]; then
    echo "Replica set already initialized. Skipping initialization."
else
    echo "Creating replica set"

    mongosh --username ${MONGO_ROOT_USERNAME} --password ${MONGO_ROOT_PASSWORD} --host mongo1 <<EOF
    var config = {
        "_id": "dbrs",
        "version": 1,
        "members": [
            { "_id": 1, "host": "mongo1:27017", "priority": 2 },
            { "_id": 2, "host": "mongo2:27017", "priority": 1 },
            { "_id": 3, "host": "mongo3:27017", "priority": 1 }
        ]
    };
    rs.initiate(config);
EOF

    DELAY=25

    echo "Waiting for ${DELAY} seconds for replica set configuration to be applied"
    sleep $DELAY

    # Run additional initialization

    sed -e "s/{{MONGO_PHOTOMAP_USERNAME}}/${MONGO_PHOTOMAP_USERNAME}/g" \
        -e "s/{{MONGO_PHOTOMAP_PASSWORD}}/${MONGO_PHOTOMAP_PASSWORD}/g" \
        -e "s/{{MONGO_PHOTOMAP_DB_NAME}}/${MONGO_PHOTOMAP_DB_NAME}/g" \
        /scripts/init.js > /scripts/init-processed.js

    mongosh --username ${MONGO_ROOT_USERNAME} --password ${MONGO_ROOT_PASSWORD} --host mongo1 < /scripts/init-processed.js

    # Remove the processed file
    rm /scripts/init-processed.js
fi
