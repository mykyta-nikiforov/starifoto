<script setup lang="ts">
import {defineEmits, ref} from 'vue';
import type {PhotoDTO} from "../../../dto/Photo";

const emit = defineEmits(['close', 'continue']);

defineProps({
  photos: {
    type: Array<PhotoDTO>,
    required: true
  }
});

const isOpened = ref(true);

const onContinue = () => {
  emit('continue');
}
const onClose = () => {
  emit('close');
}
</script>

<template>
  <v-dialog
      v-model="isOpened"
      width="auto"
      :close-on-back="false"
      :persistent="true"
  >
    <v-card
        max-width="950"
        :prepend-icon="'mdi-alert-circle-outline'"
        :text="''"
        :title="'Перевірка на дублікат'"
    >
      <v-card-text>
        <p>Можливо, ця фотографія вже є на сайті. Будь ласка, перевірте, чи це не дублікат.</p>
        <p>Якщо ви завантажуєте інше фото, натисніть на кнопку «Не дублікат».</p>
      </v-card-text>
      <div class="images-container">
        <div
            class="image-wrapper"
            v-for="photo in photos"
            :key="photo.id"
        >
          <a :href="'/photo/' + photo.id" target="_blank">

          <img :src="photo.url" :alt="photo.title"/>
          <div class="photo-title">{{photo.title}}</div>
          </a>
        </div>
      </div>
      <template v-slot:actions>
        <v-btn
            class="ms-auto"
            text="Не дублікат"
            @click="onContinue"
        ></v-btn>
        <v-btn
            class="ms-auto"
            text="Дублікат"
            @click="onClose"
        ></v-btn>
      </template>
    </v-card>
  </v-dialog>
</template>

<style scoped lang="less">
.images-container {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  justify-content: center;
  align-content: center;
  margin: 2rem 0;
  padding: 0 1rem;

  @media (min-width: 500px) {
    flex-direction: row;
    gap: 1.5rem;
  }
}

.image-wrapper {
  width: 100%;

  @media (min-width: 500px) {
    max-width: 300px;
  }

  img {
    width: 100%;
    height: auto;
  }

  a {
    color: black;
  }

  .photo-title {
    text-align: center;
    margin-top: 0.5rem;
    text-overflow: ellipsis;;
  }
}
</style>