<script setup lang="ts">

import {ref, watch} from 'vue';
import YearRange from "@/components/common/YearRange.vue";
import {formatTags} from "@/utils/tagUtils";

const props = defineProps({
  tags: {
    type: Array<string>,
    required: true
  }
});

const tagsSelected = ref<string[]>(props.tags);
const minYear = ref(1852);
const maxYear = ref(2000);
const selectedYearRange = ref([minYear.value, maxYear.value]);

const emit = defineEmits(['tags-changed', 'year-range-updated']);

watch(() => props.tags, (newValue) => {
  tagsSelected.value = newValue;
});

const onYearRangeUpdated = (newRange: number[]) => {
  selectedYearRange.value = newRange;
}

watch(selectedYearRange, (newValue) => {
  emit('year-range-updated', newValue);
});

watch(tagsSelected, (newValue) => {
  if (newValue !== props.tags) {
    emit('tags-changed', formatTags(newValue));
  }
});

const onTagRemoved = (tag: string) => {
  tagsSelected.value = tagsSelected.value.filter((t) => t !== tag);
}

const showFilterPanel = ref(false);

const yearRange = ref();
const reset = () => {
  tagsSelected.value = [];
  selectedYearRange.value = [minYear.value, maxYear.value];
  yearRange.value.reset();
}

const openPanel = () => {
  showFilterPanel.value = true;
}

const closePanel = () => {
  showFilterPanel.value = false;
}

const isPanelOpen = computed(() => showFilterPanel.value);

defineExpose({
  openPanel,
  closePanel,
  isPanelOpen
});
</script>

<template>
  <div class="filter-wrapper disable-dbl-tap-zoom">
    <v-btn
        @click="showFilterPanel = !showFilterPanel"
        class="filter-button"
        :icon="showFilterPanel ? 'mdi-filter-off-outline' : 'mdi-filter-menu'"
    />
    <div class="filter-panel-container" v-if="showFilterPanel">
      <div class="panel-title">Фільтрація</div>
      <div class="filter-title">За тегами:</div>
      <v-combobox
          v-model="tagsSelected"
          chips
          multiple
          label="Напр., «люди», «авто»"
      >
        <template v-slot:chip="tag">
          <v-chip :text="tag.item.value"
                  label
                  closable
                  size="large"
                  @click:close="onTagRemoved(tag.item.value)"
          />
        </template>
      </v-combobox>
      <div>
        <div>За роками:</div>
        <YearRange
            ref="yearRange"
            :value="selectedYearRange"
            :minYear="minYear"
            :maxYear="maxYear"
            @year-range-updated="onYearRangeUpdated"
        />
      </div>
      <div class="map-filter-buttons-wrapper">
        <v-btn
            @click="reset">
          Скинути
        </v-btn>
      </div>
    </div>
  </div>
</template>

<style scoped lang="less">
.filter-wrapper {
  position: absolute;
  top: 1rem;
  left: 1rem;
  z-index: 51;
}

.filter-panel-container {
  width: 250px;
  margin-top: 0.5rem;
  padding: 1rem;
  background-color: white;
  border-radius: 0.5rem;
  font-family: 'e-Ukraine', sans-serif;
  font-size: 1rem;

  .panel-title {
    font-family: 'e-Ukraine Head', sans-serif;
    font-weight: bold;
    margin-bottom: 0.75rem;
  }

  .filter-title {
    margin: 0.5rem 0;
  }

  .map-filter-buttons-wrapper {
    margin-top: 1rem;
    display: flex;
    flex-direction: row-reverse;
  }
}
</style>