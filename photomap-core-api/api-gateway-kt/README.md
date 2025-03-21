# 🚀 PhotoMap API Gateway (Kotlin)

Spring Cloud Gateway implementation in Kotlin with reactive approach.

## 🛠 Tech Stack

- **Kotlin** 
- **Spring Boot & Cloud**
  - Spring Cloud Gateway
  - Spring WebFlux
  - Spring Security
- **Redis** for caching
- **PostgreSQL** for user data
- **JWT** authentication

## 🌟 Key Features

1. **Gateway Routes**
   - user-api (`/api/auth/**`, `/api/user/**`)
   - photo-api (`/api/photo/**`)
   - geojson-generator (`/api/geojson/**`)
   - notification-api (`/api/notification/**`)

2. **Security**
   - JWT authentication filter
   - CSRF token validation
   - Cookie security configuration
   - CORS with allowed origin (client URL)

3. **Logging**
   - MDC context for request tracking
   - Request/Response logging
   - Performance metrics

4. **Caching**
   - Redis integration
   - User privileges caching
   - Authentication state caching


## 🚀 Quick Start

1. **Environment Variables**
```bash
JWT_SECRET=your-jwt-secret
SWAGGER_USERNAME=your-username
SWAGGER_PASSWORD=your-password
PHOTOMAP_CLIENT_URL=http://localhost:5173
```

2. **Run Application**
```bash
./gradlew bootRun
```

3. **Build Docker Image**
```bash
docker build -t api-gateway .
```

## 📚 API Documentation

- Swagger UI: `/swagger-ui.html`
- API Docs: `/doc/api-gateway/v3/api-docs`

---
Made with ❤️ for Ukraine 🇺🇦 