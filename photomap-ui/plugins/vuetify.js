import {createVuetify} from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'

export default defineNuxtPlugin(nuxtApp => {

    const myCustomTheme = {
        colors: {
            primary: '#B64038'
        },
    }

    const vuetify = createVuetify({
        ssr: true,
        components,
        directives,
        theme: {
            defaultTheme: 'myCustomTheme',
            themes: {
                myCustomTheme,
            },
        }
    })

    nuxtApp.vueApp.use(vuetify)
})