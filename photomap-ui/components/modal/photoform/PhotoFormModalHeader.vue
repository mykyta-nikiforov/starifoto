<script setup lang="ts">
import {computed, defineEmits} from 'vue';
import arrowIcon from "@/assets/icons/heroicons-mini/arrow-left.svg";

const emit = defineEmits(['close', 'open-step']);

const props = defineProps({
  isEdit: {
    type: Boolean,
    required: false,
    default: false
  },
  activeStepNumber: {
    type: Number,
    required: true
  }
});

const title = computed(() => {
  return props.isEdit ? 'Редагувати фото' : 'Додати фото';
});

const openStep = (stepNumber: number) => {
  emit('open-step', stepNumber);
};

const closeModal = () => {
  emit('close');
};
</script>

<template>
  <div class="modal-header">
    <div class="modal-title">
      <div v-if="activeStepNumber === 1">{{ title }}</div>
      <div v-if="activeStepNumber === 2" class="step-2-active">
        <div class="step-1-title-wrapper"
             @click="openStep(1)">
          <img :src="arrowIcon"
               alt="Назад до опису фото"/>
          <span class="modal-title-step-1">{{ title }}</span>
        </div>
        <span class="modal-title-step-2"> / Обрати на карті</span>
      </div>
    </div>
    <span class="modal-close-button" @click="closeModal">&times;</span>
  </div>
</template>

<style scoped lang="less">
.modal-title {
  .step-2-active {
    display: flex;
    align-items: center;

    @media (max-width: 500px) {
      font-size: 1rem;
    }

    @media (min-width: 501px) and (max-width: 768px) {
      font-size: 1.25rem;
    }

    .step-1-title-wrapper {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      cursor: pointer;

      .modal-title-step-1 {
        color: #9CA3AF;
      }
    }

    .modal-title-step-2 {
      margin-left: 0.5rem;
    }
  }
}
</style>