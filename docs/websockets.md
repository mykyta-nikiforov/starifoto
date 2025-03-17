# 🔄 WebSocket Integration

This document provides an overview of the WebSocket integration in the StariFoto project, focusing on its role in providing real-time updates.

## 📜 Description

WebSockets are used to deliver real-time updates to clients, ensuring that users receive immediate feedback and notifications.

## 🔍 Overview

### Endpoint
- **`/ws`**: The main WebSocket endpoint for establishing connections

### Topics
- **`/topic/user/**`**: Used for sending updates on user profile pages

### Security
- **JWT Token**: Required in the `Authorization` header on the *CONNECT* message to establish a secure connection
- **XSRF Protection**: WebSocket connections are protected against Cross-Site Request Forgery (XSRF) attacks
  - Each WebSocket connection requires a valid XSRF token
  - The token must be included in the connection request headers
  - Tokens are validated against the server-side session

## 🔧 Implementation Details

- **Connection Handling**: WebSocket connections are managed by the [notification-api](backend.md#notification-api), which handles incoming connections and message routing
- **Message Broadcasting**: Messages are broadcasted to all connected clients subscribed to relevant topics

## 🔒 Security Considerations

- **Authorization**: Ensures that only authenticated users can establish WebSocket connections

## Connection Flow
1. Client establishes WebSocket connection to `/ws` endpoint
2. Connection includes JWT token for authentication
3. Connection includes XSRF token in headers for protection against CSRF attacks
4. Server validates both tokens
5. On successful validation, WebSocket connection is established

---

Made with ❤️ for Ukraine 🇺🇦 