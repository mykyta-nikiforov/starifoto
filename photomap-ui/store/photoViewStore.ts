import {ref} from 'vue';
import {defineStore} from 'pinia';

export const usePhotoViewStore = defineStore('photoViewStore', () => {
    const photoId = ref<number>();
    const photosIds = ref();
    const routeBack = ref('/');
    const showColorized = ref(false);
    const deletedPhotoId = ref<number | null>();

    function setPhotoId(id: number) {
        photoId.value = id;
    }

    function clearPhotoId() {
        photoId.value = undefined;
    }

    function setPhotosIds(ids: number[]) {
        photosIds.value = ids;
    }

    function addPhotosIds(ids: number[]) {
        photosIds.value = [...photosIds.value, ...ids];
    }

    function setRouteBack(route: string) {
        routeBack.value = route;
    }

    function clearPhotosIds() {
        photosIds.value = null;
    }

    function clear() {
        photoId.value = undefined;
        photosIds.value = [];
        routeBack.value = '/';
    }

    function setShowColorized(value: boolean) {
        showColorized.value = value;
    }

    function setDeletedPhotoId(id: number | null) {
        deletedPhotoId.value = id;
    }

    return {
        photoId,
        setPhotoId,
        clearPhotoId,
        photosIds,
        setPhotosIds,
        addPhotosIds,
        routeBack,
        setRouteBack,
        clearPhotosIds,
        clear,
        showColorized,
        setShowColorized,
        deletedPhotoId,
        setDeletedPhotoId
    }
});
