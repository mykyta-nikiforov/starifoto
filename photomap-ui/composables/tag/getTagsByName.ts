import type {AxiosPromise} from "axios";
import type {TagDTO} from "~/dto/tag/Tag";

export const useGetTagsByName = (tagName: string): AxiosPromise<TagDTO[]> => {
    const {$axiosUnauthenticated} = useNuxtApp();
    return $axiosUnauthenticated.get<TagDTO[]>(`/photo/tag/search?name=${tagName}`);
}
