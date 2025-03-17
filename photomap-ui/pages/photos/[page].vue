<script setup lang="ts">
import {ref} from "vue";
import {PAGE_SIZE} from "../../constants/apiDefaults";
import GeneralViewWrapper from "../../components/view/GeneralViewWrapper.vue";
import {TITLES} from "../../constants/messages";
import {useGetPhotosPage} from "../../composables/photo/getPhotosPage";
import CommonPhotoListView from "../../components/view/admin/CommonPhotoListView.vue";
import {type PhotoDetailsDTO} from "../../dto/Photo";

const photos = useState<PhotoDetailsDTO[]>('photos');
const totalPages = useState<number>('total-pages');
const route = useRoute();
const router = useRouter();
const page = ref(Number(route.params.page));

const getPhotos = async () => {
  await useGetPhotosPage(page.value - 1, PAGE_SIZE)
      .then((response) => {
        photos.value = response.data.content;
        totalPages.value = response.data.totalPages;
      });
}

const updatePage = (newPage: number) => {
  if (newPage >= 0 && newPage <= totalPages.value) {
    router.push(`/photos/${newPage}`);
    page.value = newPage;
    getPhotos();
    window.scrollTo({top: 0});
  }
}

await callOnce(async () => {
  await getPhotos();
});

useSeoMeta
({
  description: 'Список старих фотографій, завантажених на сайт'
});

useHead({
  title: `Усі фото — сторінка ${page.value}`
});
</script>

<template>
  <client-only>
    <GeneralViewWrapper ref="viewWrapper">
      <CommonPhotoListView
          :photos="photos"
          :page-title="TITLES.ALL_PHOTOS"
          :page="page"
          :total-pages="totalPages"
          @page-changed="(newPage) => updatePage(newPage)"
      />
    </GeneralViewWrapper>
  </client-only>
</template>

<style lang="less" scoped>

.photo-buttons {
  display: flex;
  justify-content: end;
}

</style>