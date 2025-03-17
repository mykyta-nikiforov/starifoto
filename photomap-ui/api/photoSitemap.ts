import type {PhotoSitemapDataDTO} from "~/dto/Photo";

export interface SitemapElement {
    loc: string;
    lastmod: string,
    images: {
        loc: string
    }[]
}

export default defineSitemapEventHandler(async (e) => {
    const pageSize = 1000;
    let page = 0;
    let totalPages = 1;
    const allPaths: SitemapElement[] = [];

    while (page < totalPages) {
        const response = await fetch(`${process.env.NUXT_PUBLIC_BASE_API_URL}/api/photo/all/sitemap-data?page=${page}&size=${pageSize}`);
        const data = await response.json();
        totalPages = data.totalPages; // Ensure your API provides this information

        data.content.forEach((photo: PhotoSitemapDataDTO) => {
            allPaths.push({
                loc: `/photo/${photo.id}`,
                lastmod: new Date(photo.updatedAt).toISOString(),
                images: [
                    {
                        loc: photo.url
                    }
                ]
            });
        });
        page++;
    }
    return allPaths;
});
