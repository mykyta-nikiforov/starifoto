<script setup lang="ts">
import {defineEmits, onMounted, ref, watch} from 'vue';
import {VALIDATION_RULES} from "@/constants/validationRules";
import {useGetRoles} from "@/composables/auth/getRoles";

const props = defineProps({
  user: {
    type: Object,
    required: true
  }
});

const rolesAvailable = ref();

onMounted(() => {
  useGetRoles().then((response) => {
    rolesAvailable.value = response.data;
  });
});

const rolesSelected = ref([]);
watch(rolesSelected, (newValue) => {
  onUserUpdate({field: 'rolesIds', value: newValue});
});

const emit = defineEmits(['update-user']);

const onUsernameInput = (username: string) => {
  onUserUpdate({field: 'username', value: username});
};
const onEmailInput = (email: string) => {
  onUserUpdate({field: 'email', value: email});
};
const onRolesInput = (rolesIds: number[]) => {
  onUserUpdate({field: 'rolesIds', value: rolesIds});
};

// @ts-ignore
const onUserUpdate = ({field, value}) => {
  emit('update-user', {field, value});
};

const username = ref(props.user.username);
const email = ref(props.user.email);
</script>

<template>
  <div class="photo-modal-content">
    <div class="user-metadata">
      <v-row dense>
        <v-col
            cols="12"
            md="4"
            sm="6"
        >
          <div class="input-field">
            <label for="username" class="input-field-label">Ім'я</label>
            <v-text-field
                id="username"
                v-model="username"
                :rules="[VALIDATION_RULES.REQUIRED]"
                :validation-value="username"
                @input="onUsernameInput($event.target.value)"
            />
          </div>
        </v-col>
        <v-col
            cols="12"
            md="4"
            sm="6"
        >
          <div class="input-field">
            <label for="email" class="input-field-label">Email</label>
            <v-text-field
                id="email"
                v-model="email"
                :rules="[VALIDATION_RULES.REQUIRED]"
                :validation-value="email"
                @input="onEmailInput($event.target.value)"
            />
          </div>
        </v-col>
        <v-col
            cols="12"
            md="4"
            sm="6"
        >
          <div class="input-field">
            <label for="roles" class="input-field-label">Email</label>
            <v-combobox
                id="roles"
                :model-value="props.user.rolesIds"
                multiple
                label="Ролі"
                :items="rolesAvailable"
                item-title="name"
                item-value="id"
                :rules="[VALIDATION_RULES.REQUIRED]"
                :return-object="false"
                @update:modelValue="onRolesInput"
            ></v-combobox>
          </div>
        </v-col>
      </v-row>

    </div>
  </div>
</template>

<style scoped lang="less">
.photo-modal-content {
  margin-top: 1rem;
  width: 100%;
  display: flex;
  padding-bottom: 2rem;
  gap: 2rem;

  @media (max-width: 1024px) {
    flex-direction: column;
  }
}

.user-metadata {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 2rem;
  align-self: center;
}
</style>