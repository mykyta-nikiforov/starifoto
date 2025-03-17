import 'dotenv/config';
import express from 'express';
import clustersRoutes from './routes/clusters.mjs';
import {connectToDatabase, setupChangeStream} from "./db/index.mjs";
import {initializeSupercluster} from "./supercluster/index.mjs";
import cors from 'cors';
import mongoose from "mongoose";
import {MONGO_AUTH, MONGO_CONNECTION} from "./config/mongo.mjs";

mongoose.connection.on('connected', () => console.log('connected'));
mongoose.connection.on('open', () => console.log('open'));
mongoose.connection.on('reconnected', () => console.log('reconnected'));
mongoose.connection.on('disconnecting', () => console.log('disconnecting'));
mongoose.connection.on('close', () => console.log('close'));

let isEstablishingConnection = false;
const initDB = async () => {
    console.log("Initializing DB...")
    try {
        isEstablishingConnection = true;
        await connectToDatabase();
        initializeSupercluster();
        setupChangeStream();
    } catch (err) {
        console.error('Error on MongoDB connection init', err);
    } finally {
        isEstablishingConnection = false;
    }
};

initDB();

mongoose.connection.on('disconnected', () => {
    console.error('MongoDB disconnected event.');
    console.log('Mongo connection: ', MONGO_CONNECTION);
    console.log('Mongo auth: ', MONGO_AUTH);

    if (!isEstablishingConnection) {
        console.error('MongoDB disconnected event. Retry in 10 seconds...');
        setTimeout(initDB, 10000);
    }
});

const app = express();
app.use(cors({
    origin: process.env.CORS_ORIGIN
}));

app.use('/cluster', clustersRoutes());

// Health check endpoint
app.get('/health', (req, res) => {
    res.status(200).json({status: 'ok'});
});

app.use(express.json());

app.listen(process.env.PORT, () => {
    console.log(`Server running at http://localhost:${process.env.PORT}`);
});