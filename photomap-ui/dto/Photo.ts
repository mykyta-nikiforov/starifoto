export interface PhotoDTO {
    id: number;
    title: string;
    url: string;
    colorizedUrl?: string; // Optional since there might not be a colorized image
    description: string;
    author: string;
    source: string;
    licenseId: number;
    licenseName: string;
    userId: number;
    tags: string[];
    createdAt: Date
    updatedAt: Date;
    coordinates: CoordinatesDTO;
    yearRange: YearRangeDTO;
}

export interface YearRangeDTO {
    start: number;
    end: number;
}

export interface CoordinatesDTO {
    latitude: number;
    longitude: number;
    isApproximate: boolean;
}

export interface PhotoDetailsDTO extends PhotoDTO {
    userName: string;
}

export interface PhotoSitemapDataDTO {
    id: number;
    updatedAt: Date;
    url: string;
}

export interface PhotoGeoJSONFeature {
    properties: PhotoGalleryPropertiesDTO;
}

export interface PhotoGalleryPropertiesDTO {
    photoId: number;
    title: string;
    url: string;
    colorizedUrl?: string; // Optional since there might not be a colorized image
    width: number;
    height: number;
}

export interface AdminPhotoDTO extends PhotoDTO {
    isDeleted?: boolean;
}