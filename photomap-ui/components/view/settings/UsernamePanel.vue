<script setup lang="ts">
import {computed, ref, watch} from "vue";
import {TOOLTIP_MESSAGES} from "@/constants/messages";
import {useUpdateCurrentUserInfo} from "@/composables/user/updateCurrentUserInfo";

const props = defineProps({
  user: {
    type: Object,
    required: true
  }
});

const username = ref(props.user.username);
const oldUsername = ref(props.user.username);

watch(() => props.user, () => {
  username.value = props.user.username;
  oldUsername.value = props.user.username;
});

const emit = defineEmits(['submit-success', 'error']);

const updateUserInfo = () => {
  useUpdateCurrentUserInfo({username: username.value})
      .then(() => {
        emit('submit-success');
      })
      .catch((error) => emit('error', error.response.data.message));
}

const canBeSubmitted = computed(() => {
  return username.value && oldUsername.value !== username.value;
});

const tooltipMessage = computed(() => {
  if (!username.value) {
    return TOOLTIP_MESSAGES.NOT_FILLED_IN;
  } else if (oldUsername.value === username.value) {
    return TOOLTIP_MESSAGES.NO_CHANGES;
  }
  return TOOLTIP_MESSAGES.EMPTY;
});

</script>

<template>
  <div class="tab-container">
    <form class="settings-form" @submit.prevent="canBeSubmitted ? updateUserInfo() : null">
      <div class="input-field">
        <label for="newUsername" class="input-field-label">Ім'я користувача</label>
        <input id="newUsername" type="text" v-model="username" placeholder="Введіть ім'я"/>
      </div>
      <div class="submit-container">
        <button class="photomap-button"
                type="submit"
                :class="{ 'button-disabled': !canBeSubmitted }">
          Зберегти
        </button>
        <v-tooltip
            v-if="!canBeSubmitted"
            activator="parent"
            location="top"
        >{{ tooltipMessage }}
        </v-tooltip>
      </div>
    </form>
  </div>
</template>