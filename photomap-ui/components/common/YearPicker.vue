<script setup lang="ts">
import VueDatePicker from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css';
import {type PropType, ref} from "vue";
import {MAX_YEAR, MIN_YEAR} from "@/constants/yearFilterConfigs";

const props = defineProps({
  value: {
    type: [Number, null] as PropType<number | null>,
    required: true
  },
  minYear: {
    type: Number,
    required: false,
    default: MIN_YEAR
  },
  maxYear: {
    type: Number,
    required: false,
    default: MAX_YEAR
  }
});

const minYearFormatted = ref(props.minYear.toString());
const maxYearFormatted = ref(props.maxYear.toString());
const year = ref(props.value);

const PRE_SELECT_YEAR = new Date('1930-01-01');

const emit = defineEmits(['year-updated']);

const onYearUpdated = (newYear: number) => {
  year.value = newYear;
  emit('year-updated', newYear);
}

const onCleared = () => {
  year.value = null;
  emit('year-updated', null);
}
</script>

<template>
  <VueDatePicker
      :model-value="year"
      :year-picker="true"
      :text-input="true"
      :utc="true"
      :min-date="minYearFormatted"
      :max-date="maxYearFormatted"
      :start-date="PRE_SELECT_YEAR"
      :year-range="[minYear, maxYear]"
      :auto-apply="true"
      input-class-name="field-input-text"
      @update:model-value="onYearUpdated"
      @cleared="onCleared"

  />
</template>

<style scoped lang="less">
:deep(.dp__input),
:deep(.dp__overlay) {
  font-family: 'e-Ukraine Head', sans-serif;
  font-weight: normal;
}

:deep(.dp__input) {
  padding-left: 2.5rem;
}
</style>