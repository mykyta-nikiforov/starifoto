<script setup lang="ts">
import type {PhotoDetailsDTO} from "@/dto/Photo";
import {computed, type PropType, watch} from "vue";
import {convertUrlToHtmlLink, parseMarkupToHtml} from "@/utils/markup";
import {VALID_URL} from "@/constants/regex";
import {usePhotoViewStore} from "../../../store/photoViewStore";
import {LICENSES} from "../../../constants/licenses";
import userProfileIcon from "../../../assets/icons/profile.svg";
import {formatToDate} from "../../../utils/dateUtils";

const props = defineProps({
  photo: {
    type: Object as PropType<PhotoDetailsDTO>,
    required: true
  },
  showColorizedSwitcher: {
    type: Boolean,
    required: false,
    default: true
  }
});

const emit = defineEmits(['colorized-switched']);

const yearLabel = computed(() => {
  if (props.photo.yearRange.start === props.photo.yearRange.end) {
    return 'Рік';
  } else {
    return 'Роки';
  }
});

const yearValue = computed(() => {
  if (props.photo.yearRange.start === props.photo.yearRange.end) {
    return props.photo.yearRange.start;
  } else {
    return `${props.photo.yearRange.start}—${props.photo.yearRange.end}`;
  }
});

const sourceAsHtml = computed(() => {
  if (VALID_URL.test(props.photo.source)) {
    return convertUrlToHtmlLink(props.photo.source);
  } else {
    return parseMarkupToHtml(props.photo.source);
  }
});

const authorAsHtml = computed(() => {
  if (!!props.photo.author) {
    return parseMarkupToHtml(props.photo.author);
  }
});

const licence = computed(() => {
  return Object.values(LICENSES)
      .find(license => license.id === props.photo.licenseId);
});

watch(() => usePhotoViewStore().showColorized, (newValue) => {
  emit('colorized-switched', newValue);
});
</script>

<template>
  <div class="photo-details">
    <div class="photo-details-table">
      <div class="photo-details-row">
        <label>{{ yearLabel }}</label>
        <div>{{ yearValue }}</div>
      </div>
      <div class="photo-details-row">
        <label>Автор</label>
        <div v-if="photo.author" v-html="authorAsHtml"/>
        <div v-else>Невідомий</div>
      </div>
      <div class="photo-details-row">
        <label>Теги</label>
        <div class="tags-container">
          <div v-for="tag in photo.tags" :key="tag" class="photo-tag">
            <NuxtLink :to="{name: 'map', query: {tag: tag}}">
              {{ tag }}
            </NuxtLink>
          </div>
        </div>
      </div>
      <div class="photo-details-row">
        <label>Ліцензія</label>
        <div class="licence-row"><span>{{ photo.licenseName }}</span>
          <a v-if="licence"
             :href="licence.link"
             target="_blank">
            <img :src="licence.icon" width="20" height="20">
          </a>
        </div>
      </div>
      <div v-if="props.showColorizedSwitcher && !!props.photo.colorizedUrl"
           class="photo-details-row">
        <label class="colorized-label">
          Кольоризоване фото
        </label>
        <v-switch
            v-model="usePhotoViewStore().showColorized"
            inset
            color="#B64038"
            hide-details
        />
      </div>
      <div class="photo-details-row">
        <label>Джерело</label>
        <div v-html="sourceAsHtml"/>
      </div>
      <div class="photo-details-row">
        <label><small>Завантажено</small></label>
        <div class="uploaded-info">
          <div class="uploaded-info-row">
            <v-icon icon="mdi-calendar-month" color="#6B7280" size="22"/>
            <small>
                {{ formatToDate(photo.createdAt) }}
            </small>
          </div>
          <div class="uploaded-info-row">
            <img :src="userProfileIcon"/>
            <small>
              <NuxtLink :to="{name: 'user', params: {userId: photo.userId}}">
                {{ photo.userName }}
              </NuxtLink>
            </small>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<style lang="less" scoped>
.photo-details {
  flex: 2;

  a {
    color: black;
  }

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
        width: 150px;
        font-weight: bold;
        flex-shrink: 0;
      }

      :deep(.v-switch__track) {
        opacity: 1;
        background-color: #E5E7EB;
      }
    }
  }
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

  a {
    text-decoration: none;
  }
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  padding: 0.25rem 0;
}

.colorized-label {
  margin: auto 0;
}

.licence-row {
  display: flex;
  flex-direction: column;
  gap: 1rem;

  @media (min-width: 500px) {
    flex-direction: row;
  }

  a {
    display: flex;
    align-items: center;
  }
}

.uploaded-info {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;

  .uploaded-info-row {
    display: flex;
    gap: 0.75rem;
    align-items: center;
  }

  img {
    width: 18px;
    margin: 0 2px;
  }

  @media (min-width: 1000px) {
    //flex-direction: row;
  }
}
</style>