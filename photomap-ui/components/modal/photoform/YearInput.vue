<script setup lang="ts">
import {computed, defineEmits, ref, watch} from 'vue';
import YearRange from "@/components/common/YearRange.vue";
import YearPicker from "@/components/common/YearPicker.vue";
import {MAX_YEAR, MIN_YEAR} from "@/constants/yearFilterConfigs";

const props = defineProps({
  yearRange: {
    type: Object,
    required: true
  },
  isEdit: {
    type: Boolean,
    required: true
  }
});

const emit = defineEmits(['update-photo']);

const isRangeYear = ref(props.yearRange.start !== props.yearRange.end);
const exactYear = ref(props.yearRange.start);

watch(exactYear, (newValue: number) => {
  emitYearRangeChanged([newValue, newValue]);
});

watch(isRangeYear, (newValue: boolean) => {
  const currentYearRange = props.yearRange;
  if (newValue && currentYearRange.start && currentYearRange.start === currentYearRange.end) {
    const newStart = Math.max(exactYear.value - 10, MIN_YEAR);
    const newEnd = Math.min(exactYear.value + 10, MAX_YEAR);
    emitYearRangeChanged([newStart, newEnd]);
  } else if (!newValue && currentYearRange.start) {
    exactYear.value = Math.round((currentYearRange.start + currentYearRange.end) / 2);
  }
  validationInput.value?.resetValidation();
});

const yearRange = computed(() => {
  if (props.yearRange.start && props.yearRange.end) {
    return [props.yearRange.start, props.yearRange.end]
  } else return [];
});

const onYearUpdated = (newValue: any) => {
  exactYear.value = newValue;
};

const emitYearRangeChanged = (newRange: number[]) => {
  validationInput.value?.resetValidation();
  emit('update-photo', {field: 'yearRange', value: {start: newRange[0], end: newRange[1]}});
};

const validationInput = ref();
const validationInputValue = ref(props.yearRange);
watch(() => props.yearRange, (newValue) => validationInputValue.value = newValue);

const rules = [
  (v) => {
    if ((isRangeYear.value && (!v.start || !v.end)) || (v.start === MIN_YEAR && v.end === MAX_YEAR)) {
      return 'Вкажіть вужчий проміжок років'
    } else {
      return v.start && v.end ? true : 'Це обов\'язкове поле';
    }
  }
];
</script>

<template>
  <label for="year" class="input-field-label">Рік</label>
  <div class="year-container" :class="isRangeYear ? 'year-container-flex-column' : ''">
    <div class="year-selector-container">
      <YearPicker
          v-if="!isRangeYear"
          :value="exactYear"
          :minYear="MIN_YEAR"
          :maxYear="MAX_YEAR"
          @year-updated="onYearUpdated"
      />
      <div v-else>
        <small>{{props.isEdit ? 'Приблизні роки створення фото:' : 'У такому разі вкажіть приблизні роки:'}}</small>
        <YearRange
            :value="yearRange.length > 0 ? yearRange : [MIN_YEAR, MAX_YEAR]"
            :minYear="MIN_YEAR"
            :maxYear="MAX_YEAR"
            @year-range-updated="emitYearRangeChanged"
        />
      </div>
      <v-input
          ref="validationInput"
          :model-value="validationInputValue"
          :multiple="false"
          id="validation-input"
          class="validation-input"
          :rules="rules"
      />
    </div>
    <v-checkbox
        v-model="isRangeYear"
        label="Я не знаю точний рік"
        class="year-checkbox"
    />
  </div>
</template>

<style scoped lang="less">
.year-container {
  display: flex;
  justify-content: space-between;
  flex-shrink: 0;

  .year-selector-container {
    width: 50%
  }
}

.year-container-flex-column {
  flex-direction: column;

  .year-selector-container {
    width: 100%
  }
}

.year-checkbox {
  :deep(.v-selection-control__wrapper) {
    margin: 0 0.5rem;
  }

  :deep(.v-input__details) {
    display: none;
  }
}

.year-container-flex-column {
  :deep(.v-selection-control__wrapper) {
    margin-left: 0;
  }
}

.validation-input {
  :deep(.v-messages__message) {
    padding-inline: 16px;
  }
}

:deep(.v-slider__container) {
  margin: 0 0.5rem;
}
</style>