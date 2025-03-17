<script setup lang="ts">

import {computed, ref, useSlots} from 'vue';
import MenuBar from "@/components/header/MenuBar.vue";
import {useSystemModalStore} from "@/store/systemModalStore";
import Footer from "@/components/footer/Footer.vue";

const props = defineProps({
  displayFooter: {
    type: Boolean,
    default: true
  },
  maxWidthWrapper: {
    type: Boolean,
    default: true
  }
});

const hasCustomHeader = computed(() => {
  return !!useSlots()['custom-header'];
});

const snackbarMessage = ref<string>();
const openSnackbar = (message: string) => {
  snackbar.value = true;
  snackbarMessage.value = message;
}

const snackbar = ref(false);

const onSnackbarClose = () => {
  snackbar.value = false;
}

const systemModalStore = useSystemModalStore();
const openSystemMessageModal = (message: string) => systemModalStore.setErrorMessage(message)

defineExpose({
  openSnackbar, openSystemMessageModal
});
</script>

<template>
  <v-snackbar
      v-model="snackbar">
    {{ snackbarMessage }}
    <template v-slot:actions>
      <v-btn
          icon="mdi-close"
          @click="onSnackbarClose"/>
    </template>
  </v-snackbar>
  <div class="header-wrapper">
    <MenuBar v-if="!hasCustomHeader"/>
    <slot name="custom-header"></slot>
  </div>
  <div class="general-view-wrapper view-wrapper">
    <div :class="`${maxWidthWrapper ? 'max-width-wrapper' : ''}`">
      <slot></slot>
    </div>
  </div>
  <div v-if="props.displayFooter"
       class="footer-wrapper">
    <Footer/>
  </div>
</template>

<style scoped>
.general-view-wrapper {
  display: flex;
  justify-content: center;
  width: 100%;
  min-height: 70vh;
  padding: 0 1rem 3rem;
  box-sizing: border-box;

  :deep(h1, h2) {
    font-family: 'e-Ukraine Head', sans-serif;
    font-style: normal;
    font-weight: 500;
    color: #111827;
  }

  :deep(h1) {
    margin: 1rem 0 2rem;
    font-size: 1.625rem;
    line-height: 2rem;

    @media (min-width: 768px) {
      font-size: 2.5rem;
      line-height: 3.5rem;
    }
  }

  :deep(h2) {
    font-size: 1.25rem;
    line-height: 1.875rem;
    margin: 2rem 0 2rem 0;

    @media (min-width: 768px) {
      font-size: 2rem;
      line-height: 2.75rem;
    }
  }
}
</style>