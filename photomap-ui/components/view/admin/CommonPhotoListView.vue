<script setup>
import {defineEmits, ref} from "vue";
import NoContent from "@/components/common/NoContent.vue";
import {NO_DATA} from "@/constants/messages";
import PhotoMetadata from "@/components/view/photo/PhotoMetadata.vue";
import {parseMarkupToHtml} from "@/utils/markup.ts";

const props = defineProps({
  photos: {
    type: Array,
    required: true
  },
  pageTitle: {
    type: String,
    required: true
  },
  page: {
    type: Number,
    required: true
  },
  totalPages: {
    type: Number,
    required: true
  }
});

const emit = defineEmits(['page-changed']);

const pageSelected = ref(props.page);
const pageChanged = (newPage) => {
  emit('page-changed', newPage);
};
</script>

<template>
  <div class="view-container">
      <h1>{{ pageTitle }}</h1>
    <div class="photos-container-wrapper">
      <div class="photos-container" v-if="photos.length > 0">
        <div v-for="(photo, index) in photos"
             :key="photo.id"
             class="photo-row"
        >
          <div class="photo-row-inner">
            <div class="photo-container">
              <NuxtLink :to="`/photo/${photo.id}`">
                <img :src="photo.url" :alt="photo.title">
              </NuxtLink>
            </div>
            <div>
              <div class="photo-title">{{ photo.title }}</div>
              <div class="photo-description" v-html="parseMarkupToHtml(photo.description)"></div>
              <PhotoMetadata :photo="photo"
                             :showColorizedSwitcher="false"
              />
            </div>
          </div>
        </div>
        <v-pagination :length="totalPages"
                      :start="1"
                      v-model="pageSelected"
                      @update:model-value="pageChanged"/>
      </div>
      <div v-else>
        <NoContent :message="NO_DATA.NO_PHOTOS"/>
      </div>
    </div>
  </div>
</template>

<style lang="less" scoped>
.title-container {
  background-color: #f0f0f0;
  padding: 20px;
  border-radius: 5px;
}

.photo-row {
  position: relative;

  &:nth-child(odd) {
    background-color: white;
  }

  &:nth-child(even) {
    background-color: #F9FAFB;

    .photo-details-row {
      &:nth-child(odd) {
        background-color: #F9FAFB !important;
      }

      &:nth-child(even) {
        background-color: white !important;
      }
    }
  }
}

.photo-row-inner {
  display: flex;
  gap: 1rem;
  padding: 1rem;
  flex-direction: column;

  @media (min-width: 768px) {
    flex-direction: row;
  }
}

.photo-container {
  flex: 1;
  max-width: 500px;

  @media (min-width: 768px) {
    max-width: 300px;
  }
}

.photo-container img {
  min-width: 300px;
  max-width: 100%;
  height: auto;
  max-height: 500px;
  object-fit: cover;
  border-radius: 5px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.photo-details {
  flex: 2;

  .photo-details-table {
    display: flex;
    flex-direction: column;
    gap: 1rem;
    margin-bottom: 1rem;

    .photo-details-row {
      display: flex;
      gap: 1rem;

      &:nth-child(odd) {
        background-color: white;
      }

      &:nth-child(even) {
        background-color: #F9FAFB;
      }

      label {
        width: 100px;
        font-weight: bold;
      }
    }
  }
}

.photos-container-wrapper {
  margin-top: 1.5rem;
}

.photos-container {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.photo-buttons {
  display: flex;
  gap: 1rem;
}

.photo-tag {
  border: 1px solid #D1D5DB;
  border-radius: 14px;
  padding: 3px 11px 3px 11px;
  margin: 0 4px 4px 0;
  color: #111827;
  font-size: 0.875rem;
  line-height: 1.25rem;
  background-color: white;
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  padding: 0.25rem 0;
}

.photo-geo-map {
  height: 150px;
  width: 100%;

}

.map-and-buttons-container {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  min-width: 250px;
}

.view-container {
  padding: 0 1.5rem;
}

.photo-title {
  margin: 1rem 0;
  font-weight: 500;
  font-size: 1.5rem;
  line-height: 2rem;
}

.photo-description {
  margin: 0.6rem 0;
}
</style>