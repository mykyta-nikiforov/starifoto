<script setup lang="ts">
import {computed, ref, watch} from "vue";
import {TOOLTIP_MESSAGES} from "@/constants/messages";
import {useUpdateCurrentUserPassword} from "@/composables/user/updateCurrentUserPassword";

const props = defineProps({
  user: {
    type: Object,
    required: true
  }
});

const hasPassword = ref(props.user.hasPassword);
const newPassword = ref('');
const oldPassword = ref('');

const emit = defineEmits(['submit-success', 'error']);

const isPasswordFieldsFilled = computed(() => {
  return newPassword.value && (hasPassword.value ? oldPassword.value : true);
});

watch(() => props.user, () => {
  hasPassword.value = props.user.hasPassword;
});

const updatePassword = () => {
  const base64EncodedOldPassword = btoa(oldPassword.value);
  const base64EncodedNewPassword = btoa(newPassword.value);

  useUpdateCurrentUserPassword({
    oldPassword: base64EncodedOldPassword,
    newPassword: base64EncodedNewPassword
  })
      .then(() => {
        emit('submit-success');
      })
      .catch((error) => emit('error', error.response.data.message))
      .finally(() => {
        newPassword.value = '';
        oldPassword.value = '';
      });
}
</script>

<template>
  <div class="tab-container">
    <form class="settings-form" @submit.prevent="isPasswordFieldsFilled ? updatePassword() : null">
      <div v-if="hasPassword" class="input-field">
        <label for="oldPassword" class="input-field-label">Актуальний пароль</label>
        <input id="oldPassword" type="password" v-model="oldPassword" placeholder="Актуальний пароль"/>
      </div>
      <div class="input-field">
        <label for="newPassword" class="input-field-label">Новий пароль</label>
        <input id="newPassword" type="password" v-model="newPassword" placeholder="Новий пароль"/>
      </div>
      <div class="submit-container">
        <button class="photomap-button"
                type="submit"
                :class="{ 'button-disabled': !isPasswordFieldsFilled }">Зберегти
        </button>
        <v-tooltip
            v-if="!isPasswordFieldsFilled"
            activator="parent"
            location="top"
        >{{ TOOLTIP_MESSAGES.NOT_FILLED_IN }}
        </v-tooltip>
      </div>
    </form>
  </div>
</template>