<script setup lang="ts">
import {defineEmits, type PropType, ref} from "vue";
import NoContent from "@/components/common/NoContent.vue";
import ConfirmDialog from "@/components/modal/ConfirmDialog.vue";
import {CONFIRMATION_MESSAGES, TITLES} from "@/constants/messages";
import EditUserModal from "@/components/modal/user/EditUserModal.vue";
import {useAuthStore} from "@/store/authStore";
import {useUpdateIsEnabled} from "@/composables/user/updateUserIsEnabled";
import type {UserDTO} from "@/dto/User";

const emit = defineEmits(['page-changed', 'edit-success', 'error']);

const props = defineProps({
  users: {
    type: Array as PropType<UserDTO[]>,
    required: true
  },
  page: {
    type: Number,
    required: true
  },
  totalPages: {
    type: Number,
    required: true
  },
});

const pageSelected = ref(props.page);

const pageChanged = (newPage: number) => {
  emit('page-changed', newPage);
};

const editModal = ref();
const editUser = (user: UserDTO) => {
  if (editModal.value) {
    editModal.value.openModal(user);
  }
};

const toggleIsEnabled = (photoId: number, newValue: boolean) => {
  confirm.value.open(newValue ? TITLES.UNBLOCK_USER : TITLES.BLOCK_USER, CONFIRMATION_MESSAGES.ARE_YOU_SURE, {color: '#B64038'})
      .then((confirm: boolean) => {
        if (confirm) {
          updateIsEnabled(photoId, newValue);
        }
      })
};

const updateIsEnabled = (userId: number, newValue: boolean) => {
  useUpdateIsEnabled(userId, newValue)
      .then(() => {
        emit('edit-success');
      })
      .catch((error) => {
        emit('error', error.response.data.message);
      });
};

const onEditSuccess = () => {
  emit('edit-success');
};

const confirm = ref();
const authStore = useAuthStore();
</script>

<template>
  <div class="page-container">
    <ConfirmDialog ref="confirm"></ConfirmDialog>
    <div>
      <EditUserModal ref="editModal"
                     @submit-success="onEditSuccess"/>
      <table class="user-list-table">
        <tr>
          <th>Ім'я</th>
          <th>Пошта</th>
          <th>Час реєстрації</th>
          <th>Редагувати</th>
          <th>Активний</th>
        </tr>
        <tr v-for="user in users" :key="user.id">
          <td><NuxtLink :to="'/user/' + user.id">{{ user.username }}</NuxtLink></td>
          <td>{{ user.email }}</td>
          <td>{{ user.createdAt }}</td>
          <td>
            <v-btn class="edit-button" icon="mdi-pencil" @click="editUser(user)"/>
          </td>
          <td>
            <v-btn v-if="user.id !== authStore.user?.id"
                   class="checkbox-outline"
                   :icon="user.isEnabled ? 'mdi-checkbox-outline' : 'mdi-minus-box-outline'"
                   @click="toggleIsEnabled(user.id, !user.isEnabled)"/>
          </td>
        </tr>
      </table>
    </div>
    <div v-if="users.length === 0">
      <NoContent message="Немає користувачів"/>
    </div>
    <div>
      <v-pagination :length="totalPages"
                    :start="1"
                    v-model="pageSelected"
                    @update:model-value="pageChanged"/>
    </div>
  </div>
</template>

<style scoped lang="less">
.page-container {
  width: 100%;
  padding: 16px 25px 0 25px;
}

.user-list-table {
  width: 100%;
  border-collapse: collapse;

  th {
    border: 1px solid #D1D5DB;
    padding: 0.5rem;
    text-align: left;
  }

  td {
    border: 1px solid #D1D5DB;
    padding: 0.5rem;
  }

  .edit-button {
    color: #4B5563;
  }

  .checkbox-outline {
    color: #4B5563;
  }
}
</style>