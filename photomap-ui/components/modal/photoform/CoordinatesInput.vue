<script setup lang="ts">
import {defineEmits, ref} from 'vue';
import {formatCoordinates} from "@/utils/coordinateUtils";
import editPencilIcon from "@/assets/icons/pencil-square.svg";
import type {CoordinatesDTO} from "@/dto/Photo";

const props = defineProps({
  photo: {
    type: Object,
    required: true
  },
  areCoordinatesPresent: {
    type: Boolean,
    required: true
  }
});

const emit = defineEmits(['open-step']);

const onClick = () => {
  emit('open-step', 2);
};

const validationInput = ref();
const validationInputValue = ref(props.photo.coordinates);
const validationRules = [
  (v: CoordinatesDTO) => v.latitude && v.longitude ? true : 'Це обов\'язкове поле'
];
</script>

<template>
  <div
      @click="onClick"
      class="open-map-button">
    <span v-if="!props.areCoordinatesPresent">Обрати на карті</span>
    <div v-else class="location-coordinates">
            <span>
              {{
                formatCoordinates(props.photo.coordinates.latitude,
                    props.photo.coordinates.longitude)
              }}
            </span>
      <img :src="editPencilIcon" alt="Редагувати локацію"/>
    </div>
  </div>
  <v-input
      id="coordinates-input"
      v-model="validationInputValue"
      ref="validationInput"
      :rules="validationRules"
      type="hidden"/>
</template>

<style scoped lang="less">

.open-map-button {
  display: flex;
  align-items: center;
  height: 50px;
  padding: 0 1rem;
  border-radius: 8px;
  background-color: #F3F4F6;
  cursor: pointer;

  :deep(span) {
    color: #6B7280;
  }
}

:deep(.v-input__details) {
  padding-left: 16px;
}
</style>