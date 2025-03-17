import {MAX_YEAR, MIN_YEAR} from "~/constants/yearFilterConfigs";

export function appendQueryParams(tags: string[], url: string, yearRange: number[]) {
    if (tags.length > 0) {
        url += `&tags=${tags.join(',')}`;
    }
    if (yearRange.length === 2 && !(yearRange[0] === MIN_YEAR && yearRange[1] === MAX_YEAR)) {
        url += `&yearRange=${yearRange.join(',')}`;
    }
    return url;
}