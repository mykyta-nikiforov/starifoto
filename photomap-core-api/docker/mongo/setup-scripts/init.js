rs.status();

const photoDb = db.getSiblingDB('{{MONGO_PHOTOMAP_DB_NAME}}');
photoDb.createUser({
    user: '{{MONGO_PHOTOMAP_USERNAME}}',
    pwd: '{{MONGO_PHOTOMAP_PASSWORD}}',
    roles: [
        {role: "readWrite", db: "{{MONGO_PHOTOMAP_DB_NAME}}"}
    ]
});

photoDb.auth('{{MONGO_PHOTOMAP_USERNAME}}', '{{MONGO_PHOTOMAP_PASSWORD}}');
photoDb.createCollection('photo-features');