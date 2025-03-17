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

## 🌐 Frontend

- **Map Library**: [MapLibre](https://maplibre.org/)
- **Tile Server**: [MapTiler](https://www.maptiler.com/)
- **Geocoding**: [Google Geocoding API](https://developers.google.com/maps/documentation/geocoding/overview)

## 🖥️ Node.js Backend

- **Clusterization Library**: [Supercluster](https://github.com/mapbox/supercluster)
- **Database**: MongoDB for storing GeoJSON data

---

Made with ❤️ for Ukraine 🇺🇦 