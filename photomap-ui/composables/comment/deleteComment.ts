import type {AxiosPromise} from "axios";

export const useDeleteComment = (photoId: number, commentId: number): AxiosPromise<void> => {
    const {$axiosAuthenticated} = useNuxtApp();
    return $axiosAuthenticated.delete(`/photo/${photoId}/comment/${commentId}`);
}
