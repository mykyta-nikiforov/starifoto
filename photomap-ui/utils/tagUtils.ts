import {trimAndLowerCase} from "@/utils/stringUtils";

export function formatTags(newTags: any[]): string[] {
    return newTags.map((tag) => {
        let result;
        if (typeof tag !== 'string' && tag.name) {
            result = tag.name;
        } else {
            result = tag;
        }
        return trimAndLowerCase(result);
    });
}
