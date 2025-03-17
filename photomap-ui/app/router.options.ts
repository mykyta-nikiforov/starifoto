import type {RouterConfig} from '@nuxt/schema'
import PhotoView from "~/components/pages/PhotoView.vue";
import MapView from "~/components/pages/MapView.vue";
import UserPhotosView from "~/components/pages/UserPhotosView.vue";
import ConfirmEmailView from "~/components/view/auth/ConfirmEmailView.vue";

function keepDefaultView(to: any, from: any) {
    if (from.matched.length) {
        to.matched[0].components.default = from.matched[0].components.default;
        to.meta.backgroundViewParams = from.meta.backgroundViewParams || from.params;
    }
}

export default <RouterConfig>{
    // https://router.vuejs.org/api/interfaces/routeroptions.html#routes
    routes: (_routes) => [
        ..._routes,
        {
            path: '/photo/:photoId',
            name: 'photo',
            components: {
                modal: PhotoView
            },
            beforeEnter: [keepDefaultView]
        },
        {
            path: '/',
            name: 'map',
            component: MapView
        },
        {
            path: '/user/:userId',
            name: 'user',
            component: UserPhotosView
        },
        {
            path: '/confirm-email',
            components: {
                modal: ConfirmEmailView
            },
            props: {
                modal: route => ({token: route.query.token})
            }
        }
    ],
}