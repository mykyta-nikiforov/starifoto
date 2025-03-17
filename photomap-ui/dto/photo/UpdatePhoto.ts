export interface UpdatePhotoPayload {
    title: string;
    description: string;
    source: string;
    author: string;
    licenseId: number;
    tags: Array<string>;
    coordinates: GeoPoint;
    yearRange: YearRange;
    changedImageTypes: string[];
}

export interface UploadPhotoPayload {
    metadata: UpdatePhotoPayload;
    // TODO refactor, maybe just have an array of files
    file: File;
    colorizedFile?: File;
}

export interface GeoPoint {
    latitude: number;
    longitude: number;
}

export interface YearRange {
    start: number;
    end: number;
}