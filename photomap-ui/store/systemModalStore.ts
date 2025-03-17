import {ref} from 'vue';
import {defineStore} from 'pinia';

export const useSystemModalStore = defineStore('system-modal-store', () => {
    const message = ref<string>();
    const isFixed = ref<boolean>();
    const isError = ref<boolean>();

    function setErrorMessage(newMessage: string, fixed: boolean = false) {
        if (isFixed.value && !fixed) return;
        message.value = newMessage;
        isFixed.value = fixed;
        isError.value = true;
    }

    function setMessage(newMessage: string, fixed: boolean = false) {
        if (isFixed.value && !fixed) return;
        message.value = newMessage;
        isFixed.value = fixed;
        isError.value = false;
    }

    function clear() {
        message.value = undefined;
        isFixed.value = false;
        isError.value = false;
    }

    return {
        message,
        isError,
        setMessage,
        setErrorMessage,
        clear
    }
});
