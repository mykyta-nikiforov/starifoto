<script setup>
import {ref} from "vue";
import arrowIcon from "@/assets/icons/heroicons-mini/arrow-up-tray.svg";

const props = defineProps({
  inputId: {
    type: String,
    required: true
  }
});

const active = ref(false);

const emit = defineEmits(['drop']);

const toggleActive = () => {
  active.value = !active.value;
};

const onDrop = (e) => {
  const file = e.dataTransfer.files[0];
  toggleActive(!!file);
  emit('drop', file);
};

</script>

<template>
  <section
      class="drop-zone-container"
      :class="{ 'active-dropzone': active }"
      @dragenter.prevent="toggleActive"
      @dragleave.prevent="toggleActive"
      @dragover.prevent
      @drop.prevent="onDrop"
  >

    <div class="upload-info">
      <img :src="arrowIcon" alt="Upload Icon"/>
      <span class="upload-description">Перетягни або завантаж файл</span>
      <span class="supported-formats">JPG або JPEG до 5 МБ</span>
    </div>

    <label :for="inputId" class="photomap-button photomap-button-transparent"> Обрати файл </label>
  </section>
</template>

<style scoped lang="less">
.drop-zone-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2rem;
  border: 2px dashed #D1D5DB;
  border-radius: 1.5rem;
  background-color: #fff;
  transition: all 0.3s ease;
  padding: 1rem;
  min-width: 280px;

  @media (min-width: 768px) {
    min-width: 400px;
  }


  input {
    display: none;
  }
}

.active-dropzone {
  background-color: #D1D5DBFF;
  border-color: #fff;
  color: #fff;

  label {
    background-color: #fff;
  }
}

.upload-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  img {
    width: 1.75rem;
    height: auto;
    margin-bottom: 1.5rem;
  }

  .upload-description {
    margin-bottom: 1rem;
    font-weight: 500;
  }

  .supported-formats {
    font-weight: 300;
    color: #6B7280;
  }

  span {
    font-size: 1rem;
  }
}

.drop-zone-file {
  display: none;
}
</style>