<script setup lang="ts">
import {ref, watch} from 'vue';
import {isEmpty} from "@/utils/stringUtils";
import {formatTags} from "@/utils/tagUtils";
import {useGetTagsByName} from "@/composables/tag/getTagsByName";

const props = defineProps({
  tags: {
    type: Array,
    required: true
  },
  rules: {
    type: Array,
    required: false,
    default: () => []
  }
});

const tagsSelected = ref(props.tags);
const searchTag = ref();
const selectItems = ref();
const searchTimer = ref();

const onSearch = (search: string) => {
  searchTag.value = search;
}

const emit = defineEmits(['tags-changed']);

watch(tagsSelected, (newValue) => {
  emit('tags-changed', formatTags(newValue));
  searchTag.value = null;
  selectItems.value = [];
});

watch(searchTag, async (newSearchString) => {
  if (isEmpty(newSearchString)) {
    selectItems.value = [];
  } else {
    try {
      await delayedSearchTags(newSearchString);
    } catch (error) {
      console.error("Error fetching tags:", error);
    }
  }
});

const searchTags = async (newSearchString: string) => {
  if (!isEmpty(newSearchString)) {
    try {
      const response = await useGetTagsByName(newSearchString);
      if (searchTag.value === newSearchString) {
        selectItems.value = response.data
            .filter((tag) => {
              return !tagsSelected.value.find((t) => t.name === tag.name);
            });
      }
    } catch (error) {
      console.error("Error fetching tags:", error);
    }
  }
}

const delayedSearchTags = async (newSearchString: string) => {
  if (!searchTimer.value) {
    await searchTags(newSearchString);
    searchTimer.value = setTimeout(() => {
      searchTimer.value = null;
    }, 1000);
  } else {
    setTimeout(() => {
      searchTags(newSearchString);
    }, 1000);
  }
}
</script>

<template>
  <v-combobox
      v-model="tagsSelected"
      :items="selectItems"
      item-title="name"
      item-value="name"
      @update:search="onSearch"
      @click:clear="searchTag = null"
      chips
      placeholder="Введіть тег"
      multiple
      :hide-no-data="true"
      :rules="props.rules"
  >
    <template v-slot:chip="tag">
      <v-chip :text="tag.item.value"
              label
              closable
              size="large"
              @click:close="tagsSelected = tagsSelected.filter((t) => t !== tag.item.value)"
      />
    </template>
  </v-combobox>
</template>