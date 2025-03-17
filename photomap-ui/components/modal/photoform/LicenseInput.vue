<script setup lang="ts">

import {VALIDATION_RULES} from "@/constants/validationRules";
import {ref, watch} from "vue";
import {LICENSES} from "@/constants/licenses";

const emit = defineEmits(['update:modelValue']);

const props = defineProps({
  modelValue: {
    type: Number,
    required: true
  }
});

const licenseSelected = ref(props.modelValue);

const onLicenseChanged = (licenseId: number) => {
  licenseSelected.value = licenseId;
};

watch(licenseSelected, (newValue) => {
  emit('update:modelValue', newValue);
});
</script>

<template>
  <label for="license" class="input-field-label">Ліцензія</label>
  <v-select
      placeholder="Оберіть ліцензію для фото"
      :items="Object.values(LICENSES)"
      :item-value="'id'"
      :item-title="'name'"
      :model-value="licenseSelected"
      :rules="[VALIDATION_RULES.REQUIRED]"
      @update:model-value="onLicenseChanged"/>
</template>

<style scoped lang="less">

</style>