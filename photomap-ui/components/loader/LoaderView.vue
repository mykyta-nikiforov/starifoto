<script setup lang="ts">

import LoaderGrape from "@/components/loader/LoaderGrape.vue";

import {ref, watch} from "vue";

const props = defineProps({
  displayed: {
    type: Boolean,
    default: false
  },
  message: {
    type: String,
    required: false,
    default: 'Завантаження...'
  },
  background: {
    type: String,
    required: false,
    default: 'rgba(0, 0, 0, 0.6)'
  }
});

const overlayDisplayed = ref(props.displayed);

const styles = ref({
  background: props.background
});

watch(() => props.displayed, (newVal) => {
  overlayDisplayed.value = newVal;
});
</script>

<template>

  <div>
    <v-overlay
        v-model="overlayDisplayed"
        class="map-loader-overlay"
        :style="styles"
        :close-on-back="false"
        persistent
    >
      <div class="loader-wrapper">
        <LoaderGrape :message="message"/>
      </div>
    </v-overlay>
  </div>
</template>

<style scoped lang="less">
.map-loader-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  // black
  display: flex;
  justify-content: center;
  flex-direction: column;
  align-items: center;
  font-size: 20px;
  z-index: 1000;
  width: 100vw;
  height: 100vh;
}

.loader-wrapper {
  display: flex;
  justify-content: center;
  flex-direction: column;
  align-items: center;
  width: 100%;
  height: 100%;
  min-height: 300px;
}
</style>