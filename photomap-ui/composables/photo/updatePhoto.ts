import type {AxiosPromise} from "axios";
import type {PhotoDTO} from "~/dto/Photo";
import type {UploadPhotoPayload} from "~/dto/photo/UpdatePhoto";
import {buildEditFormData} from "~/utils/photoApiUtils";

export const useUpdatePhoto = (photoId: number, request: UploadPhotoPayload): AxiosPromise<PhotoDTO> => {
    const {$axiosAuthenticated} = useNuxtApp();
    const formData = buildEditFormData(request);
    return $axiosAuthenticated.put('/photo/' + photoId, formData);
}