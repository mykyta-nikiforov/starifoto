import type {AxiosPromise} from "axios";
import type {CommentDTO} from "~/dto/photo/Comment";
import type {PaginatedResponse} from "~/dto/PaginatedResponse";

export const useGetCommentsByPhotoId = (photoId: number, page?: number, size?: number): AxiosPromise<PaginatedResponse<CommentDTO>> => {
    const {$axiosUnauthenticated} = useNuxtApp();
    return $axiosUnauthenticated.get(`/photo/${photoId}/comment`, {
        params: {
            page: page,
            size: size
        }
    });
}
