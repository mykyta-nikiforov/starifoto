<script setup lang="ts">
import {computed, ref} from "vue";
import {TOOLTIP_MESSAGES} from "@/constants/messages";
import GeneralViewWrapper from "@/components/view/GeneralViewWrapper.vue";
import {useAuthStore} from "../store/authStore";
import {VALIDATION_RULES} from "../constants/validationRules";
import {VForm} from "vuetify/components";
import {useRoute, useRouter} from "vue-router";
import {useResetPasswordConfirm} from "../composables/auth/resetPassword";
import {useSystemModalStore} from "../store/systemModalStore";
import LargeButton from "../components/buttons/LargeButton.vue";

const loading = ref(false);
const route = useRoute();
const systemMessageModalStore = useSystemModalStore();
const router = useRouter();
const form = ref<typeof VForm>();
const isFormValid = ref(false);
const isButtonActive = computed(() => {
  return isFormValid.value;
});

const password = ref();

const onSubmitForm = () => {
  const {token} = route.query;
  if (isFormValid && token && typeof token === 'string') {
    loading.value = true;
    const base64EncodedPassword = btoa(password.value);

    useResetPasswordConfirm(token, base64EncodedPassword)
        .then(() => {
          systemMessageModalStore.setMessage("Пароль скинуто! Тепер ви можете увійти.");
          router.push({name: 'login'});
        })
        .catch(() => {
          systemMessageModalStore.setErrorMessage("Пароль не скинуто, трапилася помилка.");
          router.push({name: 'login'});
        })
        .finally(() => {
          loading.value = false;
        })
  }
}

definePageMeta
({
  middleware: [
    function () {
      if (useAuthStore().isLoggedIn) {
        return navigateTo('/user/' + useAuthStore().user?.id);
      }
    }
  ],
});

useHead({
  title: 'Скинути пароль',
});
</script>

<template>
  <GeneralViewWrapper ref="viewWrapper">
    <div class="reset-password-container">
      <div class="reset-password-form-container">
        <h1>Скинути пароль</h1>
        <hr class="divider-line"/>
        <div class="email-password-login-container">
          <v-form ref="form" v-model="isFormValid" class="auth-form" @submit.prevent="onSubmitForm">
            <div class="input-field">
              <label for="password" class="input-field-label">Новий пароль</label>
              <v-text-field
                  id="password"
                  v-model="password"
                  placeholder="Ваш пароль"
                  type="password"
                  :rules="[VALIDATION_RULES.REQUIRED, VALIDATION_RULES.MIN_CHARS_8]"
              />
            </div>
            <div class="submit-container">
              <LargeButton
                  label="Скинути"
                  :disabled="!isButtonActive"
                  :loading="loading"
                  @click="onSubmitForm"
                  width='300px'
              />
              <v-tooltip
                  v-if="!isButtonActive"
                  activator="parent"
                  location="top"
              >{{ TOOLTIP_MESSAGES.NOT_FILLED_IN }}
              </v-tooltip>
            </div>
          </v-form>
        </div>
        <div class="sign-in-footer">
          <slot name="form-footer"></slot>
        </div>
      </div>
    </div>
  </GeneralViewWrapper>
</template>

<style lang="less" scoped>
.reset-password-container {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.reset-password-form-container {
  width: 400px;
  display: flex;
  justify-content: center;
  flex-direction: column;
  align-items: center;
}

.submit-container {
  margin-top: 2rem;
}
</style>