<script setup lang="ts">
import {ref} from "vue";

const emit = defineEmits(['year-range-updated']);

const props = defineProps({
  value: {
    type: Array<number>,
    required: true
  },
  minYear: {
    type: Number,
    required: true
  },
  maxYear: {
    type: Number,
    required: true
  }
});

const selectedYearRange = ref(props.value);

const onYearRangeUpdated = (newRange: number[]) => {
  emit('year-range-updated', newRange);
}
const reset = () => {
  selectedYearRange.value = [props.minYear, props.maxYear];
}
defineExpose({
  reset
});
</script>

<template>
  <div class="year-range-container">
    <div class="selected-year-ranges">
      <div>{{selectedYearRange[0]}}</div>
      <div>{{selectedYearRange[1]}}</div>
    </div>
    <v-range-slider
        v-model="selectedYearRange"
        :max="props.maxYear"
        :min="props.minYear"
        step="1"
        strict
        track-fill-color="#B64038"
        track-color="#D1D5DB"
        @end="onYearRangeUpdated"
    ></v-range-slider>
  </div>
</template>

<style scoped lang="less">
:deep(.v-slider.v-input--horizontal .v-slider-track__fill) {
  height: var(--v-slider-track-size);
}

:deep(.v-slider-thumb) {
  color: #B64038;
}

.selected-year-ranges {
  display: flex;
  justify-content: space-between;
  margin: 1rem 0;
  font-weight: lighter;
}

.year-range-container {
  width: 100%;
}

:deep(.v-input__details) {
  display: none;
}
</style>