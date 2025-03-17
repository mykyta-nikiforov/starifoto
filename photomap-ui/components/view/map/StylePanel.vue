<script setup lang="ts">

import {type PropType, ref, watch} from 'vue';
import {MAP_STYLES, type MapStyle} from "../../../constants/map";

const props = defineProps({
  style: {
    type: Object as PropType<MapStyle>,
    required: true
  }
});

const styleSelected = ref<MapStyle>(props.style);

const emit = defineEmits(['style-changed']);

watch(() => props.style, (newValue) => {
  if (newValue.id !== styleSelected.value.id) {
    styleSelected.value = newValue;
  }
});

watch(styleSelected, (newValue) => {
  emit('style-changed', newValue);
});

const showStylePanel = ref(false);

const openPanel = () => {
  showStylePanel.value = true;
}

const closePanel = () => {
  showStylePanel.value = false;
}

const changeStyle = () => {
  styleSelected.value = styleSelected.value.id === MAP_STYLES.STREETS.id ? MAP_STYLES.SATELLITE : MAP_STYLES.STREETS;
}

const iconToShow = computed(() => styleSelected.value.id === MAP_STYLES.STREETS.id ? MAP_STYLES.SATELLITE.icon : MAP_STYLES.STREETS.icon);

const isPanelOpen = computed(() => showStylePanel.value);

defineExpose({
  openPanel,
  closePanel,
  isPanelOpen
});
</script>

<template>
  <div class="map-style-wrapper disable-dbl-tap-zoom">
    <div class="button-wrapper">
      <img :src="iconToShow" alt="styleSelected.name" @click="changeStyle"/>
    </div>
  </div>
</template>

<style scoped lang="less">
.map-style-wrapper {
  position: absolute;
  bottom: 1rem;
  left: 1rem;
  z-index: 50;
}

.button-wrapper {

  overflow: hidden;
  cursor: pointer;
  height: 48px;

  img {
    width: 48px;
    border: 1px solid white;
    border-radius: 7px;
  }

  img:hover {
    opacity: 0.9;
  }
}
</style>