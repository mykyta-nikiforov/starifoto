import type {AxiosPromise} from "axios";
import type {PhotoDTO} from "~/dto/Photo";
import type {UploadPhotoPayload} from "~/dto/photo/UpdatePhoto";

export const useUploadPhoto = (request: UploadPhotoPayload, queryParams: {
    similarValidationSkip?: boolean
} = {}): AxiosPromise<PhotoDTO> => {
    const {$axiosAuthenticated} = useNuxtApp();

    const formData = new FormData();
    formData.append('files', request.file);
    if (request.colorizedFile) {
        formData.append('files', request.colorizedFile);
    }
    formData.append('metadata', new Blob([JSON.stringify(request.metadata)],
        {
            type: "application/json"
        }));
    return $axiosAuthenticated.post('/photo', formData, {params: queryParams});
}
