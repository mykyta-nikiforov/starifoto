import type {AxiosPromise} from "axios";

export const useAddComment = (photoId: number, text: string): AxiosPromise<void> => {
    const {$axiosAuthenticated} = useNuxtApp();
    return $axiosAuthenticated.post(`/photo/${photoId}/comment`, {
        text: text
    });
}
