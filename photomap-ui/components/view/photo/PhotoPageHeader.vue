<script setup lang="ts">
import arrowLeftIcon from "@/assets/icons/heroicons-mini/arrow-left.svg";
import arrowRightIcon from "@/assets/icons/heroicons-mini/arrow-right.svg";
import xMarkIcon from "@/assets/icons/heroicons-mini/x-mark.svg";
import {computed, onUnmounted, ref, watch} from "vue";
import {usePhotoViewStore} from "@/store/photoViewStore";

const currentPhotoIndex = ref(0);

const props = defineProps({
  photosIds: {
    type: Array,
    required: false,
    default: () => []
  },
  enableKeyboardNavigation: {
    type: Boolean,
    required: false,
    default: true
  }
});

const emit = defineEmits(['photoChanged', 'close']);

onMounted(() => {
  if (props.enableKeyboardNavigation && props.photosIds.length > 1) {
    document.addEventListener('keydown', handleKeydown);
  }
});

const hasOtherPhotos = computed(() => props.photosIds.length > 1);

const photoViewStore = usePhotoViewStore();
watch(() => photoViewStore.photoId, (newPhotoId) => {
  const foundIndex = props.photosIds.findIndex(photo => photo === newPhotoId);
  if (foundIndex !== -1) {
    currentPhotoIndex.value = foundIndex;
  }
});

watch(() => props.enableKeyboardNavigation, (newVal) => {
  if (newVal && hasOtherPhotos.value) {
    document.addEventListener('keydown', handleKeydown);
  } else {
    document.removeEventListener('keydown', handleKeydown);
  }
});

const handleKeydown = (event: KeyboardEvent) => {
  switch (event.key) {
    case 'ArrowLeft':
      showPreviousPhoto();
      break;
    case 'ArrowRight':
      showNextPhoto();
      break;
  }
};

onUnmounted(() => {
  if (props.enableKeyboardNavigation && hasOtherPhotos.value) {
    document.removeEventListener('keydown', handleKeydown);
  }
});

const showNextPhoto = () => {
  currentPhotoIndex.value = (currentPhotoIndex.value + 1) % props.photosIds.length;
  navigateToPhoto(currentPhotoIndex.value);
};

const showPreviousPhoto = () => {
  currentPhotoIndex.value = (currentPhotoIndex.value - 1 + props.photosIds.length) % props.photosIds.length;
  navigateToPhoto(currentPhotoIndex.value);
};

const navigateToPhoto = (index: number) => {
  const photoId = props.photosIds[index];
  emit('photoChanged', photoId);
};

const close = () => {
  emit('close');
};
</script>
<template>
  <header class="header-container">
    <nav class="disable-dbl-tap-zoom">
      <div class="links-container-left">
        <div
            v-if="hasOtherPhotos"
            v-on:click="showPreviousPhoto">
          <img :src="arrowLeftIcon" alt="Попереднє фото"/>
          <span>Попереднє фото</span>
        </div>
      </div>
      <div class="links-container-right" :class="!hasOtherPhotos ? 'flex-end' : ''">
        <div
            v-if="hasOtherPhotos"
            v-on:click="showNextPhoto">
          <span>Наступне фото</span>
          <img :src="arrowRightIcon" alt="Наступне фото"/>
        </div>
        <div v-on:click="close">
          <img :src="xMarkIcon"
               alt="Закрити"/>
          <span>Назад</span>
        </div>
      </div>
    </nav>
  </header>
</template>

<style lang="less" scoped>
.header-container {
  display: flex;
  width: 100%;
  height: 100%;
  min-height: inherit;
  justify-content: center;
  align-items: center;
  border-bottom: 1px solid #d1d5db;
  border-top: 1px solid #d1d5db;

  font-family: e-Ukraine, sans-serif;
  font-weight: 300;
  font-size: 1rem;
  line-height: 1.5;
  user-select: none;

  nav {
    display: flex;
    height: 1px;
    width: 100%;
    min-height: inherit;
    justify-content: space-between;
    max-width: 1600px;
    align-items: center;

    .links-container-right,
    .links-container-left {
      display: flex;
      width: 50%;
      height: 100%;
      align-items: center;

      div {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 44px;
        height: 100%;
        gap: 0.7rem;
        white-space: nowrap;
        cursor: pointer;

        @media (min-width: 576px) {
          width: 64px;
        }

        @media (min-width: 1000px) {
          width: 240px;
        }

        span {
          display: none;

          @media (min-width: 1000px) {
            display: inline-block;
          }
        }
      }
    }

    .links-container-right {
      justify-content: space-between;

      div {
        border-left: 1px solid #d1d5db;
        border-right: 1px solid #d1d5db;
      }

      @media (max-width: 1300px) {
        div:last-child {
          border: none;
        }
      }
    }

    .flex-end {
      justify-content: flex-end;
    }

    .links-container-left {
      justify-content: flex-end;

      div {
        border-left: 1px solid #d1d5db;
      }
    }
  }
}
</style>