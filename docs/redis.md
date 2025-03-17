# ⚡ Redis Integration

This document provides an overview of the Redis integration in the StariFoto project, focusing on its roles in caching and Pub/Sub messaging.

## 📜 Description

Redis is used for both caching and Pub/Sub messaging to enhance performance and enable real-time notifications.

## 🗄️ Caching

The [api-gateway](backend.md#api-gateway) uses Redis caching to reduce the need for SQL queries during authentication and authorization processes.

### Caches
- **isEnabledUser**: Caches whether a user is enabled
- **isNonLockedUser**: Caches whether a user is non-locked
- **getUserPrivileges**: Caches user privileges

- **Key**: All caches use `userId` as the key.
- **Eviction**: When a user's status or privileges change, the [user-api](backend.md#user-api) evicts the user's cached values to ensure data consistency.

## 📢 Pub/Sub

Redis Pub/Sub is used to notify all [notification-api](backend.md#notification-api) pods to send WebSocket messages to subscribers.

### Topic
- **`user-photo-notification`**: Used for broadcasting notification events

### Workflow
- **Publish**: When a notification should be sent, the event data is published to the `user-photo-notification` topic on Redis.
- **Subscribe**: Each notification-api pod subscribes to this topic. Upon receiving a message, each pod processes it and sends notifications via WebSocket to all connected clients.

---

Made with ❤️ for Ukraine 🇺🇦 