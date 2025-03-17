# 🏗️ Backend Architecture

This document provides a comprehensive overview of the StariFoto backend architecture, including databases, caching, messaging, services, and APIs.

## 📊 Technology Stack

### Databases
- **PostgreSQL 15**: Primary relational database for user and photo data
- **MongoDB**: NoSQL database for GeoJSON data storage

### Caching & Messaging
- **Redis**: 
  - In-memory data structure store used for caching
  - Pub/Sub for broadcasting notifications to all service instances
    - Ensures notifications are sent to all clients even when multiple instances of Notification API are running
    - Critical for load balancing and high availability across pods

### Event Streaming
- **Kafka**: Used for asynchronous communication between microservices

### Real-Time Communication
- **WebSocket**: Enables real-time notifications

## 🔌 Microservices Architecture

The backend follows a microservices architecture with the following components:

### API Gateway
- **Responsibilities**: 
  - Authentication and authorization
  - Request routing to appropriate backend services
  - API security
- **Technologies**: 
  - Spring Cloud Gateway
  - JWT (JSON Web Tokens)
- **Endpoints**: Routes all API requests to appropriate microservices

#### Security Features
- **JWT Authentication**: Validates JWT tokens for authenticated requests
- **XSRF Protection**: Implements Cross-Site Request Forgery protection
  - Generates and provides XSRF tokens through a dedicated endpoint
  - Validates XSRF tokens for all state-changing requests (POST, PUT, DELETE)
- **CORS**: Configures Cross-Origin Resource Sharing

### User API
- **Responsibilities**:
  - User registration and login
  - Email confirmation
  - JWT token refreshing
  - User management (admin functionality)
- **Technologies**:
  - JWT for secure authentication
  - Google OAuth 2.0 for social login
- **Database**: PostgreSQL 15 (`user_service` schema)
- **Database migration**: Flyway
- **Endpoints**: 
  - `/api/auth/*` - Authentication operations
  - `/api/user/*` - User management operations

### Photo API
- **Responsibilities**:
  - Photo management (upload, update, delete)
  - Comment functionality
  - Photo metadata handling
- **Database**: PostgreSQL 15 (`photo_service` schema)
- **Database migration**: Flyway
- **Storage**: GCP Cloud Storage for image files
- **Event Publishing**: Sends Kafka events when photos change
- **Endpoints**: `/api/photo/*`

### Notification API
- **Responsibilities**:
  - Real-time notifications via WebSocket
  - Email notifications
- **Technologies**:
  - WebSocket for real-time updates
  - SMTP for email delivery
- **Endpoint**: `/api/notification/ws`

### GeoJSON Generator
- **Responsibilities**:
  - Generates GeoJSON data for photos and stores it in MongoDB
- **Databases**:
  - PostgreSQL for photo data
  - MongoDB for GeoJSON storage
- **Event Consumption**: Subscribes to Kafka events for photo updates

### Shared Libraries (photomap-common)
- **Components**:
  - **cache**: Common Redis configurations
  - **dto**: Shared Data Transfer Objects
  - **rest-toolkit**: Authentication filters, logging utilities, and common tools

## 🌐 Additional Backend Services

### PhotoMap Supercluster (Node.js)
- **Responsibilities**:
  - Server-side clustering of photos for map visualization
  - Optimized rendering of large numbers of map points
- **Technologies**:
  - Node.js
  - [Supercluster](https://github.com/mapbox/supercluster) library
- **Database**: MongoDB (reads GeoJSON)
- **Endpoints**:
  - `GET /cluster` - Returns clustered points
  - `GET /cluster/:clusterId/leaves` - Returns points within a cluster

## 📚 API Documentation

The API is documented using Swagger, providing an interactive interface for exploring and testing the endpoints.

- **Swagger UI**: Available in the development environment
- **Authentication**: Basic authentication required to access Swagger UI
- **Endpoints Documented**: All API endpoints with request/response examples

## 🔒 Security

- **JWT Authentication**: Secure token-based authentication
- **HTTPS**: All communications are encrypted
- **Role-Based Access Control**: Different permissions for users, moderators, and admins
- **XSRF Protection**: Cross-Site Request Forgery protection implemented in:
  - API Gateway for all state-changing HTTP requests
  - WebSocket connections in notification-api
- **CORS**: Only allows requests from trusted frontend domains

## 🔄 Communication Flow

1. Client requests are sent to the API Gateway
2. API Gateway authenticates and routes to appropriate microservice
3. Microservices process requests and interact with databases
4. For GeoJSON regeneration, Photo API publishes events to Kafka which are consumed by the GeoJSON Generator
5. For notifications:
   - Microservices trigger Notification API via direct REST calls
   - Notification API uses Redis Pub/Sub topics to broadcast events to all Notification API instances
   - Each Notification API instance sends updates to connected clients via WebSockets

---

Made with ❤️ for Ukraine 🇺🇦 