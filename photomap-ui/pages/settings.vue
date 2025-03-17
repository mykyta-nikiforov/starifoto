<script setup lang="ts">
import {onMounted, ref} from "vue";
import GeneralViewWrapper from "@/components/view/GeneralViewWrapper.vue";
import {useSystemModalStore} from "@/store/systemModalStore";
import UsernamePanel from "@/components/view/settings/UsernamePanel.vue";
import PasswordPanel from "@/components/view/settings/PasswordPanel.vue";
import {useGetCurrentUserInfo} from "@/composables/user/getCurrentUserInfo";

const activeTab = ref('account');
const hasPassword = ref(false);
const newPassword = ref('');
const oldPassword = ref('');
const viewWrapper = ref();
const user = ref();

const getUserInfo = () => {
  useGetCurrentUserInfo()
      .then((response) => {
        user.value = response.data;
        hasPassword.value = response.data.hasPassword;
      })
      .catch((e) => useSystemModalStore().setErrorMessage(e.response.data.message));
}

const onSuccessSubmit = () => {
  viewWrapper.value.openSnackbar("Зміни збережено.");
  getUserInfo();
}

const onError = (message: string) => {
  viewWrapper.value.openSystemMessageModal(message);
}

onMounted(() => {
  getUserInfo();
})

definePageMeta({
  rolesPermitted: ['User', 'Moderator', 'Admin']
});
</script>

<template>
  <client-only>
    <GeneralViewWrapper ref="viewWrapper">
      <div class="user-settings-container">
        <div class="user-settings-form-container">
          <h1>Налаштування</h1>
          <div class="tab-menu">
            <button :class="{ active: activeTab === 'account' }" @click="activeTab = 'account'">Акаунт</button>
            <button :class="{ active: activeTab === 'password' }" @click="activeTab = 'password'">Пароль</button>
          </div>
          <div v-if="user">
            <UsernamePanel
                v-if="activeTab === 'account'"
                :user="user"
                @submit-success="onSuccessSubmit"
                @error="onError"
            />
            <PasswordPanel
                v-if="activeTab === 'password'"
                :user="user"
                @submit-success="onSuccessSubmit"
                @error="onError"
            />
          </div>
        </div>
      </div>
    </GeneralViewWrapper>
  </client-only>
</template>

<style lang="less" scoped>
.user-settings-container {
  width: 100%;
  display: flex;
  justify-content: start;
  align-items: center;
  padding: 0 1.5rem;

  .user-settings-form-container {
    width: 400px;
    display: flex;
    justify-content: center;
    flex-direction: column;
    align-items: start;

    .form-group {
      margin-bottom: 1rem;
    }
  }
}

:deep(.tab-container) {
  width: 300px;

  .settings-form {
    width: 100%;
    display: flex;
    flex-direction: column;
    gap: 1rem;
  }
}

.submit-container {
  display: flex;
  flex-direction: column;
  gap: 1rem;

  .success-message {
    text-align: right;
  }
}

.tab-menu {
  display: flex;
  margin-bottom: 20px;

  button {
    flex: 1;
    padding: 10px;
    cursor: pointer;
    background: none;
    border: none;
    border-bottom: 2px solid transparent;

    &.active {
      border-bottom: 2px solid #007bff;
    }
  }
}
</style>