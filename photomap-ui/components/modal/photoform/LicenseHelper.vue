<script setup lang="ts">
import {computed, type PropType, ref, watch} from 'vue';
import {FAMILY_ARCHIVE_SOURCE_NAME, LICENSES} from "@/constants/licenses";
import {VALIDATION_RULES} from "@/constants/validationRules";

const emit = defineEmits(['license-changed', 'params-updated', 'update-photo']);

const props = defineProps({
  licenseId: {
    type: [Number, null] as PropType<number | null>,
    required: true
  },
  licenseHelperParams: {
    type: Object,
    required: true
  }
});

const licenseType = ref(props.licenseHelperParams.licenseType);
const copyrightStatus = ref(props.licenseHelperParams.copyrightStatus);

const selectedLicenseId = ref(props.licenseId);

watch(selectedLicenseId, (newValue) => {
  emit('license-changed', newValue);
});

const selectedLicense = computed(() => {
  return Object.values(LICENSES)
      .find(license => license.id === selectedLicenseId.value);
});

watch(licenseType, (newValue) => {
  emit('params-updated', {licenseType: newValue, copyrightStatus: null});
});

watch(copyrightStatus, (newValue) => {
  emit('params-updated', {licenseType: licenseType.value, copyrightStatus: newValue});
});

const onLicenseTypeChange = (value: string) => {
  copyrightStatus.value = null;
  if (value === 'family-archive') {
    selectedLicenseId.value = LICENSES.CREATIVE_COMMONS_SA_BY.id;
    emit('update-photo', {field: 'source', value: FAMILY_ARCHIVE_SOURCE_NAME});
  } else {
    selectedLicenseId.value = null;
    emit('update-photo', {field: 'source', value: ''});
  }
};

const onCopyrightStatusChange = (value: string) => {
  if (value === 'public-domain') {
    selectedLicenseId.value = LICENSES.PUBLIC_DOMAIN.id;
  } else if (value === 'fair-use') {
    selectedLicenseId.value = LICENSES.FAIR_USE.id;
  } else if (value === 'cc-sa') {
    selectedLicenseId.value = LICENSES.CREATIVE_COMMONS_SA_BY.id;
  }
};
</script>

<template>
  <label class="input-field-label">Ліцензія</label>
  <div class="radio-groups-container">
    <v-radio-group
        v-model="licenseType"
        :rules="[VALIDATION_RULES.REQUIRED]"
        @update:modelValue="onLicenseTypeChange">
      <v-radio
          label="Це фото з мого архіву, і кожен може його вільно використовувати"
          value="family-archive"></v-radio>
      <v-radio
          label="Це чиясь робота"
          value="someone-else"></v-radio>
    </v-radio-group>
    <v-divider v-if="licenseType === 'someone-else'"/>
    <div v-if="licenseType === 'someone-else'" class="license-additional">
      <v-radio-group
          v-model="copyrightStatus"
          :rules="[VALIDATION_RULES.REQUIRED]"
          @update:modelValue="onCopyrightStatusChange">
        <v-radio
            label="Авторські права на фото минули (70 років після смерті автора або після публікації, якщо автор невідомий)"
            value="public-domain"></v-radio>
        <v-radio
            label="Ця робота опублікована під ліцензією Creative Commons Share-Alike"
            value="cc-sa"></v-radio>
        <v-radio
            label="Авторські права не минули. Добропорядне використання"
            value="fair-use"></v-radio>
      </v-radio-group>
    </div>
  </div>

  <v-divider/>
  <div class="message-explained" v-if="selectedLicense">
    <div class="icon-wrapper">
      <a :href="selectedLicense.link" target="_blank">
        <img :src="selectedLicense.icon"
             class="license-icon"
             :alt="selectedLicense.name"/>
      </a>
    </div>
    <span>{{ selectedLicense.description }}</span>
  </div>
</template>


<style scoped lang="less">
.radio-groups-container {
  margin-bottom: 1rem;

  :deep(.v-label) {
    font-weight: 300;
  }

  :deep(.v-selection-control-group) {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
  }
}

.message-explained {
  display: flex;
  margin-top: 1rem;
  padding-left: 16px;
  font-size: 0.8rem;

  .icon-wrapper {
    min-width: 80px;
    display: flex;
    justify-content: end;
    padding-right: 1.5rem;
  }
}

.license-additional {
  margin-top: 1rem;
  margin-left: 1rem;
  background: rgba(0, 0, 0, 0.03);
  padding: 1rem;
}

.license-icon {
  height: 24px;
  vertical-align: middle;
}
</style>