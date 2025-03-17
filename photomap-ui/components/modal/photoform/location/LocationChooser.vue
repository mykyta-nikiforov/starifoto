<script setup lang="ts">
import {onMounted, reactive, ref} from "vue";
import maplibregl, {type MapMouseEvent, Marker} from "maplibre-gl";
import GeoSearchInput from "./GeoSearchInput.vue";
import {useFetchClusters} from "../../../../composables/map/fetchClusters";
import {
  addClustersSource,
  addControlPanel,
  buildMarker,
  changeStyle,
  clearMarkers,
  fetchClustersForCurrentBboxAndZoom,
  getCurrentBbox,
  type MapData,
  updateMarkers
} from "../../../../utils/map";
import {areEqualSets} from "../../../../utils/diffUtils";
import StylePanel from "../../../view/map/StylePanel.vue";
import {MAP_STYLES, type MapStyle} from "../../../../constants/map";
import { getMapStyleUrl } from '@/utils/map';

const mapData = reactive<MapData>
({
  clusters: [],
  markers: {}
});

let marker: Marker;
const mapInstance = ref();
const mapStyle = ref<MapStyle>(MAP_STYLES.STREETS);

const props = defineProps({
  isEdit: {
    type: Boolean,
    required: true
  },
  coordinates: {
    type: Object,
    required: false,
    default: null
  },
  photoId: {
    type: Number,
    required: false
  },
  getMapCenter: {
    type: Function,
    required: false,
    default: () => [30.234, 48.557]
  },
  getMapZoom: {
    type: Function,
    required: false,
    default: () => 5
  }
});

const emit = defineEmits(['coordinates-changed']);
const config = useRuntimeConfig();

const getDefaultCoordinates: () => [number, number] = () => {
  if (!!props.coordinates && props.coordinates.latitude && props.coordinates.longitude) {
    return [props.coordinates.longitude, props.coordinates.latitude];
  } else {
    return props.getMapCenter();
  }
};

const getDefaultZoom: () => number = () => {
  if (!!props.coordinates && props.coordinates.latitude && props.coordinates.longitude) {
    return 15;
  } else return props.getMapZoom();
};

onMounted(() => {
  mapInstance.value = new maplibregl.Map({
    container: 'location-chooser-map',
    style: getMapStyleUrl(mapStyle.value.id, config.public.maptilerApiKey),
    center: getDefaultCoordinates(),
    zoom: getDefaultZoom(),
    maxBounds: [
      [17, 44],
      [43, 53.2]
    ],
    pitchWithRotate: false,
    dragRotate: false
  });

  addControlPanel(mapInstance.value, {
    navigationControlPosition: "bottom-right",
    showCompass: false
  });
  if (!props.isEdit) {
    initMapClusters(mapInstance.value);
  }

  const {latitude, longitude} = props.coordinates;
  if (latitude != null && longitude != null) {
    marker = buildMarker()
        .setLngLat({lng: longitude, lat: latitude})
        .addTo(mapInstance.value);
  }

  mapInstance.value.on('click', (e: MapMouseEvent) => {
    const coordinates = e.lngLat;

    if (marker) {
      marker.remove();
    }

    marker = buildMarker()
        .setLngLat(coordinates)
        .addTo(mapInstance.value);
    emit('coordinates-changed', coordinates.lng, coordinates.lat);
  });
});

const requestInProgress = ref(false);
const latestParams = reactive<{ bbox: number[] }>({
  bbox: []
});

async function fetchAndDisplayClusters() {
  if (!mapInstance.value) return;
  if (mapInstance.value.getZoom() < 10) {
    clearMarkers(mapData);
    return;
  }
  const clustersSource: maplibregl.GeoJSONSource | undefined
      = mapInstance.value.getSource('clusters') as maplibregl.GeoJSONSource | undefined
  if (clustersSource && !requestInProgress.value) {
    requestInProgress.value = true;

    try {
      latestParams.bbox = getCurrentBbox(mapInstance.value);
      mapData.clusters = await fetchClustersForCurrentBboxAndZoom(mapInstance.value,
          (bbox, zoom) => fetchClusters(bbox, zoom))
      if (props.photoId) {
        mapData.clusters = mapData.clusters.filter(feature => feature.properties.cluster ? true : feature.properties.photoId !== props.photoId);
      }

      clustersSource.setData({
        type: 'FeatureCollection',
        features: mapData.clusters,
      });
      clearMarkers(mapData);
      updateMarkers(mapData, mapInstance.value);
    } catch (error) {
      console.error('Error fetching clusters:', error);
    } finally {
      requestInProgress.value = false;
      if (areMapParamsChanged()) {
        await fetchAndDisplayClusters();
      }
    }
  }
}

function initMapClusters(map: maplibregl.Map) {
  map.on('load', () => {
    addClustersSource(map, mapData);
    fetchAndDisplayClusters();
  });

  let throttleTimer: NodeJS.Timeout | null;
  map.on('moveend', () => {
    if (!throttleTimer) {
      // If throttleTimer is not set, call fetchAndDisplayClusters immediately
      fetchAndDisplayClusters();
      throttleTimer = setTimeout(() => {
        throttleTimer = null; // Reset throttleTimer after the interval has passed
        if (areMapParamsChanged()) {
          fetchAndDisplayClusters();
        }
      }, 1000);
    }
  });
}

async function fetchClusters(bbox: number[], zoom: number) {
  latestParams.bbox = bbox;
  try {
    const response = await useFetchClusters(bbox, zoom);
    return response.data.clusters;
  } catch (error) {
    console.error('Error fetching clusters:', error);
    return [];
  }
}

const onStyleChanged = (newStyle: MapStyle) => {
  mapStyle.value = newStyle;
  if (mapInstance.value) {
    changeStyle(mapInstance.value, getMapStyleUrl(newStyle.id, config.public.maptilerApiKey));
  }
}

function areMapParamsChanged() {
  return mapInstance.value && !areEqualSets(new Set(latestParams.bbox), new Set(getCurrentBbox(mapInstance.value)));
}

const onSearchChoosen = (coordinates: number[]) => {
  if (marker) {
    marker.remove();
  }

  marker = buildMarker()
      .setLngLat({lng: coordinates[0], lat: coordinates[1]})
      .addTo(mapInstance.value);
  mapInstance.value.flyTo({center: coordinates, zoom: 15, speed: 2});
  emit('coordinates-changed', coordinates[0], coordinates[1]);
};
</script>

<template>
  <div class="location-chooser-wrapper">
    <div id="location-chooser-map"></div>
    <GeoSearchInput class="geo-search-input"
                    @coordinates-changed="onSearchChoosen"/>
    <StylePanel
        ref="mapStylePanel"
        :style="mapStyle"
        @styleChanged="onStyleChanged"/>
  </div>
</template>

<style scoped lang="less">
.location-chooser-wrapper {
  position: relative;
}

#location-chooser-map {
  width: 100%;
  min-height: 40vh;

@media (min-height: 300px) {
    min-height: 50vh;
  }

  @media (min-height: 600px) {
    min-height: 60vh;
  }

  @media (min-height: 800px) {
    min-height: 70vh;
  }
}

.geo-search-input {
  position: absolute;
  top: 1rem;
  z-index: 200;
  padding: 0 1rem;

  @media (min-width: 576px) {
    top: 2rem;
    padding: 0 2rem;
  }
}
</style>

<style>
.selected-location-marker {
  z-index: 1;
}
</style>