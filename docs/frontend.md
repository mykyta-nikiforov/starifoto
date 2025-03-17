# 🎨 Frontend Architecture

This document provides an overview of the StariFoto frontend architecture, including frameworks, libraries, and key features.

## 🌐 Technology Stack

### Framework
- **Vue 3**: JavaScript framework for building user interfaces
- **Nuxt 3**: Framework for creating Vue.js applications with server-side rendering (SSR)

### Rendering
- **Server-Side Rendering (SSR)**: Utilizes Nuxt 3's SSR capabilities for improved SEO and performance

### Routing
- **Vue Router**: Official router for Vue.js, used for navigating between pages

### State Management
- **Pinia**: State management library for Vue.js, providing a simpler and more intuitive API than Vuex

### SEO
- **Nuxt SEO**: Module for managing SEO metadata and improving search engine visibility

### Map Integration
- **Maplibre**: Open-source mapping library for integrating interactive maps

## 📂 Project Structure

- **Components**: Reusable Vue components for building the UI
- **Pages**: Directory for page components, each corresponding to a route
- **Store**: Pinia stores for managing application state
- **Assets**: Static assets such as images and styles

## 🚀 Key Features

- **Interactive Map**: Displays geolocated historical photos of Ukraine
- **Responsive Design**: Optimized for both desktop and mobile devices
- **Dynamic Routing**: Seamless navigation between different sections of the application
- **SEO Optimization**: Enhanced search engine visibility through SSR and Nuxt SEO

## 🔧 Development and Deployment

- **Local Development**: Use `npm run dev` to start the development server
- **Production Build**: Use `npm run build` to generate a production-ready build
- **Deployment**: Deployed on DigitalOcean using Docker and Docker Compose

---

Made with ❤️ for Ukraine 🇺🇦 