import type {UploadPhotoPayload} from "~/dto/photo/UpdatePhoto";

export function buildEditFormData(request: UploadPhotoPayload): FormData {
    const formData = new FormData();
    if (request.file) {
        formData.append('files', request.file);
    }
    if (request.colorizedFile) {
        formData.append('files', request.colorizedFile);
    }
    formData.append('metadata', new Blob([JSON.stringify(request.metadata)],
        {
            type: "application/json"
        }));
    return formData;
}