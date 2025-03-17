const mongoose = require('mongoose');

const PhotoPropertiesSchema = new mongoose.Schema({
    photoId: String,
    iconThumbUrl: String,
    tags: [String],
    yearStart: Number,
    yearEnd: Number
});

const PhotoFeatureSchema = new mongoose.Schema({
    geometry: {
        type: {
            type: String,
            enum: ['Point'],
            required: true
        },
        coordinates: {
            type: [Number],
            required: true
        }
    },
    type: {
        type: String,
        enum: ['Feature'],
        required: true
    },
    properties: PhotoPropertiesSchema
});

module.exports = mongoose.model('photo-features', PhotoFeatureSchema);
