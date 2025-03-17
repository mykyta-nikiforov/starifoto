<script setup lang="ts">
import {computed, defineEmits, ref, toRaw} from 'vue';
import ModalComponent from "@/components/modal/ModalComponent.vue";
import {VForm} from "vuetify/components";
import EditUserForm from "@/components/modal/user/EditUserForm.vue";
import {type UpdateUserPayload, type UserDTO} from "@/dto/User";
import {useUpdateUser} from "@/composables/user/updateUser";

const formDefaults = {
  username: '',
  email: '',
  rolesIds: []
}

const emit = defineEmits(['close', 'submit-success', 'error']);
const modal = ref();
const user = ref(structuredClone(formDefaults));
const userId = ref();

const originalUser = ref();
const openModal = (newUser: UserDTO) => {
  if (newUser) {
    userId.value = newUser.id;
    const userData = {
      username: newUser.username,
      email: newUser.email,
      rolesIds: newUser.roles.map(role => role.id)
    };
    user.value = structuredClone(toRaw(userData));
    originalUser.value = structuredClone(toRaw(userData));
  }
  modal.value.openModal();
};

const hasChanges = computed(() => {
  return JSON.stringify(user.value) !== JSON.stringify(originalUser.value);
});

const closeModal = () => {
  modal.value.setIsOpen(false);
  emit('close');
  // Reset form after modal animation finishes
  setTimeout(() => {
    user.value = structuredClone(formDefaults);
    userId.value = null;
  }, 200);
};

const onUploadSuccess = () => {
  closeModal();
  emit('submit-success');
};

const onUserUpdate = ({field, value}) => {
  if (user.value && user.value[field]) {
    user.value[field] = value;
  }
};

const form = ref();
const isFormValid = ref(false);

const submitForm = async () => {
  await form.value.validate();
  if (!isFormValid.value || !hasChanges.value) {
    return;
  }
  const payload: UpdateUserPayload = {
    username: user.value.username,
    email: user.value.email,
    rolesIds: user.value.rolesIds
  };
  try {
    useUpdateUser(userId.value, payload)
        .then(() => onUploadSuccess());
  } catch (error) {
    emit('error', error);
  }
};

const isButtonActive = computed(() => {
  return isFormValid.value && hasChanges.value;
});

defineExpose({
  openModal,
  closeModal
});
</script>

<template>
  <ModalComponent ref="modal" @close="closeModal">
    <template #header>
      <div class="modal-header">
        <div class="modal-title">
          Редагувати користувача
        </div>
        <span class="modal-close-button" @click="closeModal">&times;</span>
      </div>
    </template>
    <template #content>
      <div class="modal">
        <v-form ref="form" v-model="isFormValid">
          <EditUserForm
              :user="user"
              @update-user="onUserUpdate"
          />
        </v-form>
      </div>
    </template>
    <template #actions>
      <div class="footer">
        <button class="photomap-button"
                @click="submitForm"
                :class="{ 'button-disabled': !isButtonActive }"
        >
          {{ 'Зберегти' }}
        </button>
      </div>
    </template>
  </ModalComponent>
</template>

<style scoped lang="less">
.modal {
  overflow-y: auto;
  overflow-x: hidden;
  width: 100%;
  height: 100%;
}

.footer {
  width: 100%;
  display: flex;
  justify-content: end;

  .photomap-button {
    margin: 0.5rem 1rem;
  }
}
</style>