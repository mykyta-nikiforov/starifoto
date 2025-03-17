import {ref} from 'vue';
import {defineStore} from 'pinia';

export const useSystemSnackbarStore = defineStore('system-snackbar-store', () => {
    const message = ref<string>();

    function setMessage(newMessage: string, fixed: boolean = false) {
        message.value = newMessage;
    }

    function clear() {
        message.value = undefined;
    }

    return {
        message,
        setMessage,
        clear
    }
});
