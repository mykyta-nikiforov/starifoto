import {ref} from 'vue';
import {defineStore} from 'pinia';

export const useLoaderStore = defineStore('loader', () => {
    const isLoaded = ref(false);

    function setIsLoaded(newIsLoaded: boolean) {
        isLoaded.value = newIsLoaded;
    }

    return {
        isLoaded,
        setIsLoaded
    }
});
