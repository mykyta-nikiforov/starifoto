<script setup lang="ts">
import {computed, ref} from "vue";
import {TOOLTIP_MESSAGES} from "@/constants/messages";
import GeneralViewWrapper from "@/components/view/GeneralViewWrapper.vue";
import {useAuthStore} from "../store/authStore";
import {VALIDATION_RULES} from "../constants/validationRules";
import {VForm} from "vuetify/components";
import {useRouter} from "vue-router";
import {useSystemModalStore} from "../store/systemModalStore";
import LargeButton from "../components/buttons/LargeButton.vue";
import {useResetPassword} from "../composables/auth/resetPasswordConfirm";

const loading = ref(false);
const systemMessageModalStore = useSystemModalStore();
const router = useRouter();
const email = ref();

const form = ref<typeof VForm>();
const isFormValid = ref(false);
const isButtonActive = computed(() => {
  return isFormValid.value;
});

const onSubmitForm = () => {
  if (isFormValid.value) {
    loading.value = true;
    useResetPassword(email.value)
        .then(() => {
          systemMessageModalStore.setMessage("На вашу пошту відправлений лист для скидання паролю.");
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
              <label for="email" class="input-field-label">Електронна пошта</label>
              <v-text-field
                  id="email"
                  v-model="email"
                  placeholder="email@domain.ua"
                  :rules="[VALIDATION_RULES.REQUIRED, VALIDATION_RULES.EMAIL]"
              />
            </div>
            <div class="submit-container">
              <LargeButton
                  label="Підтвердити"
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