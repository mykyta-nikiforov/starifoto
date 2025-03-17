export const MONGO_CONNECTION = process.env.MONGO_CONNECTION;
export const MONGO_AUTH = {
    authSource: process.env.MONGO_AUTH_SOURCE,
    user: process.env.MONGO_USERNAME,
    pass: process.env.MONGO_PASSWORD
};