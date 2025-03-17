import vue3GoogleLogin from 'vue3-google-login'

export default defineNuxtPlugin({
    name: 'vue3-google-login',
    async setup(nuxtApp) {
        const config = useRuntimeConfig();
        nuxtApp.vueApp.use(vue3GoogleLogin, {
            clientId: config.public.googleClientId
        });
    },
});
