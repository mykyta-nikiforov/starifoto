<script setup lang="ts">
import {computed, defineEmits, ref} from 'vue';
import {formatCoordinates} from "@/utils/coordinateUtils";
import {TOOLTIP_MESSAGES} from "@/constants/messages";
import LargeButton from "../../buttons/LargeButton.vue";

const emit = defineEmits(['open-step', 'error', 'submit-clicked', 'update-photo']);

const props = defineProps({
  isEdit: {
    type: Boolean,
    required: false,
    default: false
  },
  photo: {
    type: Object,
    required: true
  },
  activeStepNumber: {
    type: Number,
    required: true
  },
  areCoordinatesPresent: {
    type: Boolean,
    required: true
  },
  isFormValid: {
    type: Boolean,
    required: true
  },
  hasChanges: {
    type: Boolean,
    required: true
  }
});
const openStep = (stepNumber: number) => {
  emit('open-step', stepNumber);
};

const formattedCoordinates = computed(() => {
  const {latitude, longitude} = props.photo.coordinates;
  return formatCoordinates(latitude, longitude);
});

const isButtonActive = computed(() => {
  return props.isFormValid && (!props.isEdit || props.hasChanges);
});

const getTooltipMessage = computed(() => {
  if (!props.isFormValid) {
    return TOOLTIP_MESSAGES.NOT_FILLED_IN;
  } else if (!props.hasChanges) {
    return TOOLTIP_MESSAGES.NO_CHANGES;
  } else {
    return '';
  }
});

const isApproximateCoords = ref(props.photo.coordinates.isApproximate);

const onApproximateCoordsChanged = (newValue: boolean) => {
  emit('update-photo', {field: 'coordinates', value: {...props.photo.coordinates, isApproximate: newValue}});
};

const isButtonProcessing = ref(false);

const setButtonProcessing = (value: boolean) => {
  isButtonProcessing.value = value;
};

const onSubmitClick = () => {
  if (!isButtonProcessing.value) {
    emit('submit-clicked');
    isButtonProcessing.value = true;
  }
};

defineExpose({
  setButtonProcessing
});
</script>

<template>
  <div v-if="props.activeStepNumber === 1" class="modal-footer flex-end">
    <div class="submit-button-wrapper">
      <div>
        <LargeButton :label="isEdit ? 'Зберегти' : 'Завантажити'"
                     @click="onSubmitClick"
                     :disabled="!isButtonActive"
                     :loading="isButtonProcessing"
                     width='270px'
        />
        <v-tooltip
            v-if="!isButtonActive"
            activator="parent"
            location="top"
        >{{ getTooltipMessage }}
        </v-tooltip>
      </div>
    </div>
  </div>
  <div v-if="props.activeStepNumber === 2" class="modal-footer flex-space-between">
    <v-checkbox
        v-model="isApproximateCoords"
        label="Приблизна локація"
        hide-details
        @update:model-value="onApproximateCoordsChanged"
    />
    <div class="selected-coordinates">
        <span v-if="props.areCoordinatesPresent">
          {{ formattedCoordinates }}
        </span>
    </div>
    <LargeButton label="Обрати локацію"
                 @click="openStep(1)"
                 :disabled="!props.areCoordinatesPresent"
                 width='270px'
    ></LargeButton>
  </div>
</template>

<style scoped lang="less">
.modal-footer {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  min-height: 88px;
  width: 100%;
  padding: 0 2rem;

  @media (min-width: 600px) {
    flex-direction: row;
    align-items: center;
  }

  .photomap-button {
    min-height: 40px;
  }
}

.flex-end {
  justify-content: flex-end;
}

.flex-space-between {
  justify-content: space-between;
}

.selected-coordinates {
  @media (max-width: 1000px) {
    display: none;
  }
}

.submit-button-wrapper {
  width: 100%;
  display: flex;
  justify-content: end;
}
</style>