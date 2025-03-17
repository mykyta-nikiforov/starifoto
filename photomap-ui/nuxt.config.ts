// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
    site: {
        name: 'СтаріФото.Укр | Старі фото України на карті, світлини, фотографії'
    },
    devtools: {enabled: true},
    css: [
        '~/assets/main.css',
        'vuetify/lib/styles/main.sass',
        '@mdi/font/css/materialdesignicons.min.css',
        'maplibre-gl/dist/maplibre-gl.css'
    ],
    build: {
        transpile: ['vuetify'],
    },
    modules: [
        // ...
        '@pinia/nuxt',
        '@nuxtjs/seo',
        'nuxt-gtag'
    ],
    runtimeConfig: {
        public: {
            googleClientId: process.env.NUXT_PUBLIC_GOOGLE_CLIENT_ID || '',
            baseApiUrl: process.env.NUXT_PUBLIC_BASE_API_URL || '',
            browserBaseApiUrl: process.env.NUXT_PUBLIC_BROWSER_BASE_API_URL || '',
            timeout: process.env.NUXT_PUBLIC_TIMEOUT || '',
            clusterApiRoot: process.env.NUXT_PUBLIC_CLUSTER_API_URL || '',
            wsUrl: process.env.NUXT_PUBLIC_WS_URL || '',
            nodeEnv: process.env.NUXT_PUBLIC_NODE_ENV || '',
            clientUrl: process.env.NUXT_PUBLIC_CLIENT_URL || '',
            googleMapsApiKey: process.env.NUXT_PUBLIC_GOOGLE_MAPS_API_KEY || '',
            maptilerApiKey: process.env.NUXT_PUBLIC_MAPTILER_API_KEY || '',
        }
    },
    plugins: [
        '~/plugins/vuetify',
        '~/plugins/axios',
        '~/plugins/vue3-google-login'
    ],
    devServer: {
        port: 5173
    },
    sitemap: {
        exclude: ["/users", "/admin", "/settings"],
        sources: ['/api/photoSitemap'],
    },
    gtag: {
        id: 'G-855B9WP393'
    }
})