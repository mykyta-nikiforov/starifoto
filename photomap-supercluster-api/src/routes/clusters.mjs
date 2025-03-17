import express from 'express';
import {getClusterIndexByTags} from "../supercluster/index.mjs";
import {PAGE_SIZE} from "../constant/index.mjs";

const createClusterRoutes = () => {
    const router = express.Router();

    router.get('/', async (req, res) => {
        const {bbox, zoom, tags, yearRange} = req.query;
        const index = await getClusterIndexByTags(bbox, tags, yearRange, false);
        if (!index) {
            return res.status(500).json({error: 'Supercluster index is not initialized.'});
        }

        if (!bbox || !zoom) {
            return res.status(400).json({error: 'bbox and zoom parameters are required.'});
        }

        const bboxArray = bbox.split(',').map(Number); // Convert the bbox string to an array of numbers
        const [minLng, minLat, maxLng, maxLat] = bboxArray;

        const clusters = index.getClusters([minLng, minLat, maxLng, maxLat], Number(zoom)); // Ensure zoom is a number

        // For each cluster, add the "iconThumbUrl" property based on the first leaf's value
        clusters.forEach(cluster => {
            if (cluster.properties.cluster) {
                const leaves = index.getLeaves(cluster.properties.cluster_id, 1); // Get the first leaf
                if (leaves.length > 0) {
                    const leafData = leaves[0].properties;
                    if (leafData) {
                        cluster.properties.iconThumbUrl = leafData.iconThumbUrl;
                    }
                }
            }
        });

        res.json({clusters});
    });

    router.get('/:clusterId/leaves', async (req, res) => {
        const {page = 0, size = PAGE_SIZE, tags, yearRange, bbox} = req.query;
        const index = await getClusterIndexByTags(bbox, tags, yearRange, true);
        if (!index) {
            return res.status(500).json({error: 'Supercluster index is not initialized.'});
        }

        const {clusterId} = req.params;

        if (!clusterId) {
            return res.status(400).json({error: 'clusterId is required.'});
        }

        const offset = page * size;
        try {
            const leaves = index.getLeaves(Number(clusterId), Number(size), Number(offset));
            res.json({leaves});
        } catch (error) {
            res.status(400).json({error: 'Invalid clusterId.'});
        }
    });
    return router;
}

export default createClusterRoutes;