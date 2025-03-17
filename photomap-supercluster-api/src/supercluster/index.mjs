import Supercluster from 'supercluster';
import PhotoFeature from "../db/PhotoFeature.js";

const mapSelectParams = {
    geometry: 1,
    type: 1,
    'properties.photoId': 1,
    'properties.iconThumbUrl': 1
};

const leavesSelectParams = {
    geometry: 1,
    type: 1,
    'properties.photoId': 1
};

const superclusterConfig = {
    radius: 60,
    maxZoom: 23
};

let mapIndex;
let leavesIndex;
export const getPhotos = async (isLeaves = false) => {
    return PhotoFeature.find({}, null, {lean: true})
        .select(isLeaves ? leavesSelectParams : mapSelectParams);
};

export const initializeSupercluster = async () => {
    console.log("Initializing supercluster...")
    const photos = await getPhotos(false);

    mapIndex = new Supercluster(superclusterConfig);
    mapIndex.load(photos);

    leavesIndex = new Supercluster(superclusterConfig);
    const leaves = await getPhotos(true);
    leavesIndex.load(leaves);
    console.log("Supercluster initialized")
};

export const getClusterIndexByTags = async (bbox, tags, yearRange, isLeaves = false) => {
    if (!tags && !yearRange) {
        return isLeaves ? leavesIndex : mapIndex;
    }
    const query = {};
    if (bbox) {
        const [minLng, minLat, maxLng, maxLat] = bbox.split(',').map(Number);
        query['geometry.coordinates'] = {
            $geoWithin: {
                $box: [[minLng, minLat], [maxLng, maxLat]]
            }
        };
    }
    if (tags) {
        const tagsArray = tags.includes(',') ? tags.split(',') : [tags];
        if (tagsArray.length > 0) {
            query['properties.tags'] = {$all: tagsArray};
        }
    }
    if (yearRange) {
        const yearsArray = yearRange ? yearRange.split(',') : [];
        const yearStart = yearsArray.length > 0 ? Number(yearsArray[0]) : null;
        const yearEnd = yearsArray.length > 1 ? Number(yearsArray[1]) : null;
        if (yearStart !== null && yearEnd !== null) {
            query['$or'] = [
                {'properties.yearStart': {$lte: yearEnd, $gte: yearStart}},
                {'properties.yearEnd': {$gte: yearStart, $lte: yearEnd}},
                {'properties.yearStart': {$lte: yearStart}, 'properties.yearEnd': {$gte: yearEnd}}
            ];
        }
    }
    const resultIndex = new Supercluster(superclusterConfig);
    const selectParams = isLeaves ? leavesSelectParams : mapSelectParams;
    resultIndex.load(await PhotoFeature.find(query, null, {lean: true}).select(selectParams));
    return resultIndex;
};