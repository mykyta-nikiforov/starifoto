import {ref} from 'vue';
import {defineStore} from 'pinia';

export const useWebSocketStore = defineStore('ws-connection', () => {
    const isConnected = ref(false);

    function setConnected(newValue: boolean) {
        isConnected.value = newValue;
    }

    return {
        isConnected: isConnected,
        setConnected
    }
});
