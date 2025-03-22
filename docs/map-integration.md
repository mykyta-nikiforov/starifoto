# 🗺️ Map Integration

This document provides an overview of the map integration in the StariFoto project, including key features, technologies, and backend interactions.

## 🌟 Key Features

- **Server-Side Clusterization**: Efficiently clusters photos on the server side to improve map performance
- **Interactive Map**: Utilizes MapLibre for rendering interactive maps

## 🔍 Overview

The Node.js backend accesses MongoDB, where GeoJSONs of all the photos are stored. The client requests clusters for the current bounding box and zoom level.

### Cluster Requests
- **Endpoint**: `GET /cluster?bbox=30.33,46.19,30.35,46.19&zoom=16`
  - **Description**: Returns clusters and single-point data for the specified bounding box and zoom level.

### Cluster Leaves
- **Endpoint**: `GET /cluster/:clusterId/leaves?page=0&size=24&bbox=30.33,46.19,30.35,46.19`
  - **Description**: Retrieves the leaves of a cluster when a user clicks on it.

## 🖥️ Backend

- **Clusterization Library**: [Supercluster](https://github.com/mapbox/supercluster)
- **Database**: MongoDB for storing GeoJSON data

## 🌐 Frontend

- **Map Library**: [MapLibre](https://maplibre.org/)
- **Tile Server**: [MapTiler](https://www.maptiler.com/)
- **Geocoding**: [Google Geocoding API](https://developers.google.com/maps/documentation/geocoding/overview)

## 📊 Data Structure

### GeoJSON Structure

The GeoJSON data stored in MongoDB follows this structure:

```json
{
  "id": 123,
  "type": "Feature",
  "geometry": {
    "type": "Point",
    "coordinates": [28.112302973731488, 49.03427741140442]
  },
  "properties": {
    "photoId": 123,
    "iconThumbUrl": "https://storage.googleapis.com/...",
    "tags": ["архітектура", "люди"],
    "yearStart": 1900,
    "yearEnd": 1910
  }
}
```

Key points:
- Each document represents a single photo location
- The `geometry` field contains the photo's coordinates
- `properties` includes metadata like photo ID, thumbnail URL, tags, and year range
- MongoDB indexes for efficient querying:
  - `geometry.coordinates` - for spatial queries
  - `properties.tags` - for tag-based filtering
  - `properties.yearStart` and `properties.yearEnd` - for year range filtering

---

Made with ❤️ for Ukraine 🇺🇦 