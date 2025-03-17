import mongoose from 'mongoose';
import { MONGO_CONNECTION, MONGO_AUTH } from '../config/mongo.mjs';
import PhotoFeature from "./PhotoFeature.js";
import {initializeSupercluster} from "../supercluster/index.mjs";

export const connectToDatabase = () => {
    console.log("Connecting to database");
    return connectWithRetry();
};

const connectWithRetry = async () => {
    console.log("Inside connectWithRetry")
    return new Promise((resolve, reject) => {
        mongoose.connect(MONGO_CONNECTION, MONGO_AUTH)
            .then(() => {
            console.log("Connected to MongoDB");
            resolve();
        }).catch((error) => {
            console.error("Error connecting to MongoDB, retrying in 10 seconds...", error);
            setTimeout(() => {
                connectWithRetry().then(resolve).catch(reject);
            }, 10000);
        });
    });
};





export function setupChangeStream() {
    console.log("Setting up change stream...");
    let debounceTimer;

    const changeStream = PhotoFeature.watch();
    console.log("Change stream opened");
    changeStream.on('change', async (data) => {
        if (debounceTimer) {
            clearTimeout(debounceTimer);
        }
        console.log("Change detected in MongoDB, updating supercluster in 10 seconds...")
        debounceTimer = setTimeout(async () => {
            await initializeSupercluster();
        }, 10000);
    });

    changeStream.on('error', (error) => {
        console.error('Error in MongoDb change stream', error);
    });

    changeStream.on('close', () => {
        console.warn('MongoDb change stream closed');
    });
}
