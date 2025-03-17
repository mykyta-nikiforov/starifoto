# 📊 Kafka Integration

This document provides an overview of the Kafka integration in the StariFoto project, focusing on its role in event queueing for GeoJSON regeneration.

## 📜 Description

Kafka is used for event queueing, specifically for regenerating GeoJSON data in MongoDB when photos are updated.

## 🔍 Overview

### Topics
- **`photo-add-topic`**: Handles events when a new photo is added
- **`photo-delete-topic`**: Handles events when a photo is deleted
- **`photo-update-topic`**: Handles events when a photo is updated

### Workflow
- **Sender**: The [photo-api](backend.md#photo-api) sends messages to the appropriate Kafka topic whenever a photo is added, deleted, or updated.
- **Listener**: The [geojson-generator](backend.md#geojson-generator) listens to these topics and updates the GeoJSON records in MongoDB accordingly.

## 🛠️ Implementation Details

- **Producer**: Implemented in the Photo API to send messages to Kafka topics
- **Consumer**: Implemented in the GeoJSON Generator to listen for messages and trigger updates
- **Scalability**: Kafka's distributed nature allows for high throughput and scalability, ensuring that the system can handle a large number of photo updates efficiently

---

Made with ❤️ for Ukraine 🇺🇦 