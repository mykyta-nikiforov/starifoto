import maplibregl, {type ControlPosition} from "maplibre-gl";

export interface MapData {
    clusters: any[];
    markers: any
}

export const getCurrentBbox = (mapInstance: maplibregl.Map | undefined) => {
    if (mapInstance) {
        return mapInstance.getBounds().toArray().flat();
    } else return [];
};

export function buildMarker() {
    return new maplibregl.Marker(
        {
            className: 'selected-location-marker',
            color: '#B64038'
        }
    );
}

export function clearMarkers(mapData: MapData) {
    for (const id in mapData.markers) {
        if (mapData.markers[id]) {
            mapData.markers[id].remove();
        }
    }
    mapData.markers = {};
}

export function updateMarkers(mapData: MapData, mapInstance: maplibregl.Map) {
    const newMarkers: any = {};
    const features = mapData.clusters;
    for (const feature of features) {
        const props = feature.properties;
        const coords = feature.geometry.coordinates;
        const id = props.cluster ? props.cluster_id : `photo-${props.photoId}`;
        const iconThumbUrl = props.iconThumbUrl;
        const pointCount = props.point_count_abbreviated;

        let marker = mapData.markers[id];
        if (!marker) {
            marker = new maplibregl.Marker({
                element: createMarkerElement(iconThumbUrl, pointCount),
            }).setLngLat(coords);
            newMarkers[id] = marker;
            marker.addTo(mapInstance);
        } else {
            newMarkers[id] = marker;
        }
    }
    // Update the markers data with the new markers
    mapData.markers = newMarkers;
}

function createMarkerElement(iconThumbUrl: string, pointsAmount: number) {
    const html = `
    <div class="marker-icon" style="background-image: url(&quot;${iconThumbUrl}&quot;);">
      <div class="cluster-foot ${pointsAmount ? 'visible' : ''}">
        <span class="cluster-count">${pointsAmount}</span>
      </div>
    </div>`;
    const el = document.createElement('div');
    el.innerHTML = html;
    return el;
}

export function addClustersSource(mapInstance: maplibregl.Map, mapData: MapData) {
    const width = 40;
    const bytesPerPixel = 4;
    const data = new Uint8Array(width * width * bytesPerPixel);

    // Fill the data array with green pixels (RGBA format, 4 bytes per pixel)
    for (let i = 0; i < data.length; i += bytesPerPixel) {
        data[i] = 0;       // Red channel (0 for no red)
        data[i + 1] = 255; // Green channel (255 for full green)
        data[i + 2] = 0;   // Blue channel (0 for no blue)
        data[i + 3] = 1; // Alpha channel (transparent)
    }
    mapInstance.addImage('custom-square-icon', {width: width, height: width, data: data});


    mapInstance.addSource('clusters', {
        type: 'geojson',
        data: {
            type: 'FeatureCollection',
            features: mapData.clusters,
        },
        cluster: true,
        clusterMaxZoom: 23,
        clusterRadius: 10,
    });

    mapInstance.addLayer({
        id: 'cluster-layer',
        type: 'symbol',
        source: 'clusters',
        filter: ['has', 'point_count'],
        layout: {
            'icon-image': 'custom-square-icon',
            'icon-allow-overlap': true
        }
    });

    mapInstance.addLayer({
        id: "unclustered-point",
        type: "symbol",
        source: "clusters",
        filter: ['!', ['has', 'point_count']],
        layout: {
            'icon-image': 'custom-square-icon',
            'icon-allow-overlap': true
        }
    });
}

export function changeStyle(mapInstance: maplibregl.Map, style: string) {
    mapInstance.setStyle(style, {
        transformStyle(previous, next) {
            const savedSources: any = {};
            if (previous?.sources["clusters"]) {
                savedSources["clusters"] = previous.sources["clusters"];
            }
            const savedLayers = previous!.layers
                .filter((l) => l.id === "cluster-layer" || l.id === "unclustered-point");
            return {
                ...next,
                sources: {
                    ...next.sources,
                    ...savedSources,
                },
                layers: next.layers.concat(savedLayers),
            };
        },
    });
}

export interface ControlPanelOptions {
    navigationControlPosition?: ControlPosition;
    showCompass?: boolean;
}

export function addControlPanel(mapInstance: maplibregl.Map,
                                {navigationControlPosition = 'top-right', showCompass = false}: ControlPanelOptions) {
    if (mapInstance) {
        mapInstance.addControl(new maplibregl.NavigationControl(
            {showCompass: showCompass}
        ), navigationControlPosition);
        mapInstance.addControl(
            new maplibregl.GeolocateControl({
                positionOptions: {
                    enableHighAccuracy: true
                },
                trackUserLocation: true
            }),
            navigationControlPosition
        );
    }
}

export async function fetchClustersForCurrentBboxAndZoom(mapInstance: maplibregl.Map,
                                                         callback: (bbox: number[], zoom: number) => Promise<any[]>) {
    if (!mapInstance) return [];
    const bbox = mapInstance.getBounds().toArray().flat();
    const zoom = mapInstance.getZoom();
    return await callback(bbox, zoom);
}

const MAPTILER_BASE_URL = 'https://api.maptiler.com/maps';

export const getMapStyleUrl = (styleId: string, apiKey: string): string => {
    return `${MAPTILER_BASE_URL}/${styleId}/style.json?key=${apiKey}`;
};