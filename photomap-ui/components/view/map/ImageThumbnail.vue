<script setup lang="ts">
import {defineProps, type PropType, ref} from 'vue'
import Loader from '@/components/loader/LoaderGrape.vue';
import type {PhotoGalleryPropertiesDTO} from "@/dto/Photo";

const props = defineProps({
  image: {
    type: Object as PropType<PhotoGalleryPropertiesDTO>,
    required: true
  },
  showColorized: {
    type: Boolean,
    required: false,
    default: false
  }
});

const loaded = ref(false);

const imageWidthInPx = 300;
const ratio = imageWidthInPx / props.image.width;
const imageHeightInPx = props.image.height * ratio;

const imageContainerStyle = {
  width: `${imageWidthInPx}px`,
  height: `${imageHeightInPx}px`,
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  position: 'relative',
  background: 'rgb(55, 65, 81, 0.5)',
  borderRadius: '8px',
  margin: '10px 0'
};


const handleImageLoad = () => {
  loaded.value = true;
};
</script>

<template>
  <div :style="imageContainerStyle" class="image-container">
    <div v-if="!loaded">
      <Loader/>
    </div>

    <img
        v-show="loaded"
        :src="(showColorized && props.image.colorizedUrl) ? props.image.colorizedUrl : props.image.url"
        :title="props.image.title"
        :alt="props.image.title"
        @load="handleImageLoad"
        @click="$emit('click')"
    />
  </div>
</template>


<style scoped>
img {
  width: 100%;
  height: 100%;
  border-radius: 8px;
  object-fit: cover;
  vertical-align: bottom;
}

.image-container {
  cursor: pointer;
}
</style>
