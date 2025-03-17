<script setup lang="ts">
// @ts-ignore
import {VueFlexWaterfall} from 'vue-flex-waterfall';
import {computed, nextTick, onMounted, onUnmounted, reactive, ref, watch} from 'vue';
import ImageThumbnail from '@/components/view/map/ImageThumbnail.vue'
import {WATERFALL_BREAK_AT} from "@/constants/waterfallConfigs";
import {LOAD_MORE_STATUSES, PAGE_SIZE} from "@/constants/apiDefaults";
import {usePhotoViewStore} from "@/store/photoViewStore";
import type {PhotoGeoJSONFeature} from "@/dto/Photo";
import {useFetchClusterLeaves} from "@/composables/map/fetchClusterLeaves";

const currentPage = ref(0);
const galleryContainer = ref();
const photosContainer = ref();
const photoThumbsRefs = reactive<{ [key: number]: Element }>({});

const emit = defineEmits(['nextPageLoaded', 'closeGallery']);

const props = defineProps({
  photos: {
    type: Array<PhotoGeoJSONFeature>,
    required: true
  },
  clusterId: {
    type: Number,
    required: true
  },
  tags: {
    type: Array<string>,
    required: true
  },
  yearRange: {
    type: Array<number>,
    required: true
  },
  bbox: {
    type: Function,
    required: true
  }
});

const photoViewStore = usePhotoViewStore();
const isPhotoModalOpened = ref(false);
const loadMoreStatus = ref(LOAD_MORE_STATUSES.IDLE);

onMounted(() => {
  galleryContainer.value.addEventListener('scroll', checkScrollEnd);
  photoViewStore.setPhotosIds(props.photos.map(photo => photo.properties.photoId));
  photoViewStore.setRouteBack('/');
});

const scrollToPhoto = (photoId: number) => {
  const scrollToPhotoRef = photoThumbsRefs[photoId];
  if (scrollToPhotoRef) {
    nextTick(() => {
      scrollToPhotoRef.scrollIntoView({block: 'center'});
    });
  }
};


function updatePhotoThumbsRefs(el: Element, photoId: number) {
  if (!!el && !!photoId) {
    photoThumbsRefs[photoId] = el;
  }
}

const loadData = () => {
  loadMoreStatus.value = LOAD_MORE_STATUSES.LOADING;
  useFetchClusterLeaves(props.bbox(), props.clusterId, ++currentPage.value, PAGE_SIZE, props.tags,
      props.yearRange)
      .then((response) => {
        emit('nextPageLoaded', response.data.leaves);
        photoViewStore.addPhotosIds(response.data.leaves.map(photo => photo.properties.photoId));
        loadMoreStatus.value = response.data.leaves.length < PAGE_SIZE ?
            LOAD_MORE_STATUSES.NO_MORE : LOAD_MORE_STATUSES.IDLE
      });
};

const handleGalleryClick = (event: MouseEvent) => {
  // Check if the clicked element is not a descendant of an element with the class 'image-container' or 'modal-gallery-photo'
  if (!isPhotoModalOpened.value && event.target instanceof HTMLElement) {
    const isColorizedSwitchClicked = event.target.closest('.colorized-switch');
    const isImageContainerClicked = event.target.closest('.image-container');
    const isModalGalleryPhotoClicked = event.target.closest('.modal-gallery-photo');
    if (!isImageContainerClicked && !isModalGalleryPhotoClicked && !isColorizedSwitchClicked) {
      closeGallery();
    }
  }
};

const checkScrollEnd = () => {
  if (loadMoreStatus.value === LOAD_MORE_STATUSES.IDLE
      && photosContainer.value.getBoundingClientRect().bottom - 500 < window.innerHeight) {
    loadData();
  }
};

watch(() => isPhotoModalOpened.value, (isPhotoModalOpened) => {
  if (!isPhotoModalOpened && props.photos.length === 1) {
    closeGallery();
  }
});

watch(() => photoViewStore.photoId, (newPhotoId) => {
  if (newPhotoId) {
    scrollToPhoto(newPhotoId);
  }
});

watch(() => photoViewStore.deletedPhotoId, (deletedPhotoId) => {
  if (deletedPhotoId) {
    photoViewStore.clearPhotosIds();
    emit('closeGallery')
    photoViewStore.setDeletedPhotoId(null);
  }
});

const showColorizedButton = computed(() => {
  return props.photos.some(photo => !!photo.properties.colorizedUrl);
});

const closeGallery = () => {
  emit('closeGallery');
};
onUnmounted(() => {
  if (galleryContainer.value) {
    galleryContainer.value.removeEventListener('scroll', checkScrollEnd);
  }
  photoViewStore.clearPhotosIds();
});
</script>

<template>
  <div class="gallery"
       ref="galleryContainer"
       v-if="props.photos.length > 0"
       @click="handleGalleryClick"
  >
    <div class="gallery-panel">
      <v-switch
          v-if="showColorizedButton"
          v-model="photoViewStore.showColorized"
          inset
          color="#FFD431"
          hide-details
          label="Кольоризовані фото (якщо є)"
          class="colorized-switch"
      />
      <div class="close-button" @click="closeGallery"></div>
    </div>
    <div ref="photosContainer"
         class="gallery-waterfall-container">
      <VueFlexWaterfall
          align-content="center"
          col="4"
          col-spacing="32"
          :break-at="WATERFALL_BREAK_AT"
      >
        <div v-for="feature in props.photos"
             :key="feature.properties.photoId"
             :ref="(el) => updatePhotoThumbsRefs(el as Element, feature.properties.photoId)">
          <NuxtLink :to="`/photo/${feature.properties.photoId}`">
            <ImageThumbnail :image="feature.properties" :showColorized="photoViewStore.showColorized"/>
          </NuxtLink>
        </div>
      </VueFlexWaterfall>
    </div>
  </div>
</template>

<style scoped lang="less">
.gallery {
  position: fixed;
  top: 0;
  left: 0;
  height: 100vh;
  width: 100vw;
  margin: 0 auto;
  overflow: auto;
  background-color: rgba(0, 0, 0, 0.8);
  z-index: 100;
}

.gallery-waterfall-container {
  width: 100vw;
  padding: 5rem 1.5rem 0 1.5rem;
}

.colorized-switch {
  max-width: 380px;
  color: white;

  :deep(.v-switch__track) {
    opacity: 1;
    background-color: #5d5d5d;
  }
}
</style>
