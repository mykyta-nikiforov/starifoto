<script setup lang="ts">
import {computed, nextTick, onMounted, onUnmounted, reactive, ref, watch} from 'vue';
import maplibregl, {type MapGeoJSONFeature} from 'maplibre-gl';
import Gallery from '@/components/view/map/GalleryView.vue';
import FilterPanel from "@/components/view/map/FilterPanel.vue";
import {PAGE_SIZE} from "@/constants/apiDefaults";
import {MAX_YEAR, MIN_YEAR} from "@/constants/yearFilterConfigs";
import {areEqualArrays, areEqualSets} from "@/utils/diffUtils";
import MenuBar from "@/components/header/MenuBar.vue";
import LoaderView from "@/components/loader/LoaderView.vue";
import {usePhotoViewStore} from "@/store/photoViewStore";
import {useFetchClusters} from "@/composables/map/fetchClusters";
import {useFetchClusterLeaves} from "@/composables/map/fetchClusterLeaves";
import type {PhotoGeoJSONFeature} from "@/dto/Photo";
import MapAddPhotoButtonContainer from "../view/map/MapAddPhotoButtonContainer.vue";
import {
  addClustersSource,
  addControlPanel,
  changeStyle,
  clearMarkers,
  fetchClustersForCurrentBboxAndZoom,
  getCurrentBbox,
  getMapStyleUrl,
  type MapData,
  updateMarkers
} from "../../utils/map";
import {MAP_STYLES, type MapStyle, UKRAINE_CENTER_COORDINATES} from "../../constants/map";
import StylePanel from "../view/map/StylePanel.vue";

const mapInstance = ref<maplibregl.Map>();
const mapData = reactive<MapData>
({
  clusters: [],
  markers: {}
});

const config = useRuntimeConfig();
const photos = ref<PhotoGeoJSONFeature[]>([]);
const clusterId = ref<number | null>(null);
const mapLoaded = ref(false);
const mapStyle = ref<MapStyle>(MAP_STYLES.STREETS);
const filterPanel = ref();
const mapStylePanel = ref();

const routeQuery = computed(() => useRouter().currentRoute.value.query);
const hasDefaultCenterCoordinated = () => {
  return !!routeQuery.value.lat && !!routeQuery.value.lng;
};

const mapCenterCoordinates = computed<[number, number]>(() => {
  return hasDefaultCenterCoordinated()
      ? [parseFloat(routeQuery.value.lng as string), parseFloat(routeQuery.value.lat as string)]
      : UKRAINE_CENTER_COORDINATES;
});

const mapDefaultZoom = computed<number>(() => {
  return hasDefaultCenterCoordinated()
      ? parseFloat(routeQuery.value.zoom as string) || 16
      : 5;
});

watch(() => useRouter().currentRoute.value.query, (newQuery) => {
  const currentRouteName = useRouter().currentRoute.value.name;
  if (currentRouteName === "map") {
    if (!!newQuery.lat && !!newQuery.lng) {
      clearGallery();
      if (mapInstance.value && hasDefaultCenterCoordinated()) {
        mapInstance.value.flyTo({center: mapCenterCoordinates.value, zoom: mapDefaultZoom.value, speed: 2});
      }
    } else if (!!newQuery.tag) {
      clearMarkers(mapData);
      clearGallery();
      clearFilters();
      tags.value = newQuery.tag ? [newQuery.tag as string] : [];
      filterPanel.value.openPanel();
      mapInstance.value?.easeTo({center: UKRAINE_CENTER_COORDINATES, zoom: mapDefaultZoom.value});
    }
  }
});

const clearFilters = () => {
  tags.value = [];
  yearRange.value = [MIN_YEAR, MAX_YEAR];
};

onMounted(() => {
  mapInstance.value = new maplibregl.Map({
    container: 'map-container',
    style: getMapStyleUrl(mapStyle.value.id, config.public.maptilerApiKey),
    center: mapCenterCoordinates.value,
    zoom: mapDefaultZoom.value,
    maxBounds: [
      [17, 44],
      [43, 53.2]
    ]
  });

  // Add control panel functionality when the map is ready
  addControlPanel(mapInstance.value, {
    navigationControlPosition: 'top-right',
    showCompass: true
  });
  addMapInitEventListeners(mapInstance.value);
});

// Cleanup the map instance when the component is unmounted
onUnmounted(() => {
  if (mapInstance.value) {
    mapInstance.value.remove();
  }
});

const tags = ref<string[]>(routeQuery.value.tag ? [routeQuery.value.tag as string] : []);
const onSearchTagsChanged = (newTags: string[]) => {
  tags.value = newTags;
  fetchAndDisplayClusters();
}

const yearRange = ref<number[]>([MIN_YEAR, MAX_YEAR]);
const onYearRangeChanged = (newRange: number[]) => {
  yearRange.value = newRange;
  fetchAndDisplayClusters();
}

const onStyleChanged = (newStyle: MapStyle) => {
  mapStyle.value = newStyle;
  if (mapInstance.value) {
    changeStyle(mapInstance.value, getMapStyleUrl(newStyle.id, config.public.maptilerApiKey));
  }
}

const requestInProgress = ref(false);
const latestParams = reactive<{ bbox: number[], tags: string[], yearRange: number[] }>({
  bbox: [],
  tags: [],
  yearRange: [MIN_YEAR, MAX_YEAR]
});
const showLoader = ref(false);

async function fetchClusters(bbox: number[], zoom: number, tags: string[], yearRange: number[]) {
  latestParams.bbox = bbox;
  latestParams.tags = tags;
  latestParams.yearRange = yearRange;
  try {
    const response = await useFetchClusters(bbox, zoom, tags, yearRange);
    return response.data.clusters;
  } catch (error) {
    console.error('Error fetching clusters:', error);
    return [];
  }
}

function areMapParamsChanged() {
  return mapInstance.value && !areEqualSets(new Set(latestParams.bbox), new Set(getCurrentBbox(mapInstance.value)))
      || !areEqualSets(new Set(latestParams.tags), new Set(tags.value))
      || !areEqualArrays(latestParams.yearRange, yearRange.value);
}

async function fetchAndDisplayClusters() {
  if (!mapInstance.value) return;
  const clustersSource: maplibregl.GeoJSONSource | undefined
      = mapInstance.value.getSource('clusters') as maplibregl.GeoJSONSource | undefined;
  if (clustersSource && !requestInProgress.value) {
    requestInProgress.value = true;
    const loaderTimeout = setTimeout(() => {
      showLoader.value = true;
    }, 5000);
    try {
      mapData.clusters = await fetchClusters(getCurrentBbox(mapInstance.value), mapInstance.value.getZoom(), tags.value, yearRange.value);
      clustersSource.setData({
        type: 'FeatureCollection',
        features: mapData.clusters,
      });
      clearMarkers(mapData); // Clear old markers
      updateMarkers(mapData, mapInstance.value); // Place new markers
    } catch (error) {
      console.error('Error fetching clusters:', error);
    } finally {
      clearTimeout(loaderTimeout);  // Clear the components.loader timeout
      showLoader.value = false;
      requestInProgress.value = false;
      if (areMapParamsChanged()) {
        await fetchAndDisplayClusters();
      }
    }
  }
}

async function initClusters() {
  if (mapInstance.value) {
    mapData.clusters = await fetchClustersForCurrentBboxAndZoom(mapInstance.value, (bbox, zoom) =>
        fetchClusters(bbox, zoom, tags.value, yearRange.value));
    addClustersSource(mapInstance.value, mapData);
    clearMarkers(mapData);
    updateMarkers(mapData, mapInstance.value);
  }
}

const isFilterHiddenOnMove = () => {
  return window.innerWidth < 600;
};

function addMapInitEventListeners(map: maplibregl.Map) {
  map.on('click', 'cluster-layer', handleClusterClick);
  map.on('click', 'unclustered-point', handleClusterClick);
  map.on('load', () => {
    mapLoaded.value = true;
    nextTick(() => {
      if (routeQuery.value.tag) {
        filterPanel.value?.openPanel();
      }
    });
    initClusters();
  });

  let throttleTimer: NodeJS.Timeout | null;
  map.on('moveend', () => {
    if (!throttleTimer
        && !displayGallery.value  // Do not fetch clusters if the gallery is open and viewport was changed
    ) {
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

  map.on('move', () => {
    useRouter().replace({query: {}});
  });

  map.on('movestart', () => {
    if (isFilterHiddenOnMove() && filterPanel.value.isPanelOpen) {
      filterPanel.value.closePanel();
    }
  })
}

function handleClusterClick(event: maplibregl.MapLayerMouseEvent) {
  if (event.features) {
    openClickedFeatures(event.features);
  }
}

function openClickedFeatures(features: MapGeoJSONFeature[]) {
  if (features && features.length > 0 && mapInstance.value) {
    const selectedFeature = features[0];
    if (selectedFeature.properties.cluster) {
      const selectedClusterId = selectedFeature.properties.cluster_id;
      useFetchClusterLeaves(mapInstance.value.getBounds().toArray().flat(),
          selectedClusterId, 0, PAGE_SIZE, tags.value, yearRange.value)
          .then((leaves) => {
            photos.value = leaves.data.leaves;
          });
      clusterId.value = selectedClusterId;
    } else {
      photoViewStore.setPhotosIds([selectedFeature.properties.photoId]);
      photoViewStore.setRouteBack('/');
      useRouter().push(`/photo/${selectedFeature.properties.photoId}`)
    }
  }
}

const photoViewStore = usePhotoViewStore();
watch(() => photoViewStore.photoId, (newVal) => {
  if (!newVal && photoViewStore.photosIds?.length === 1) {
    photoViewStore.clearPhotosIds();
  }
})

const onNextPageLoaded = (newPhotos: PhotoGeoJSONFeature[]) => {
  addPhotos(newPhotos);
};

const addPhotos = (newPhotos: PhotoGeoJSONFeature[]) => {
  photos.value.push(...newPhotos)
};

const onCloseGallery = () => {
  clearGallery();
  // in case viewport was changed while gallery was opened
  if (!areEqualSets(new Set(latestParams.bbox), new Set(getCurrentBbox(mapInstance.value)))) {
    fetchAndDisplayClusters();
  }
};

const clearGallery = () => {
  photos.value = [];
  clusterId.value = null;
};

const currentBbox = () => {
  return getCurrentBbox(mapInstance.value);
};

const getMapCenter = () => {
  return mapInstance.value?.getCenter().toArray();
};

const getMapZoom = () => {
  return mapInstance.value?.getZoom();
};

const displayGallery = computed(() => {
  return photos.value.length > 0;
});
</script>

<template>
  <div class="header-wrapper">
    <MenuBar/>
  </div>
  <main class="view-wrapper">
    <div id="map-container">
      <div v-if="mapLoaded">
        <FilterPanel
            ref="filterPanel"
            :tags="tags"
            :yearRange="yearRange"
            @tagsChanged="onSearchTagsChanged"
            @yearRangeUpdated="onYearRangeChanged"
        />
        <StylePanel
            ref="mapStylePanel"
            :style="mapStyle"
            @styleChanged="onStyleChanged"/>
        />
        <MapAddPhotoButtonContainer
            :getMapCenter="getMapCenter"
            :getMapZoom="getMapZoom"
        />
      </div>
    </div>
    <LoaderView
        :displayed="showLoader"
        :message="'Завантажуємо! Ду-у-у-уже багато фото! %)'"/>
    <Gallery v-if="displayGallery && clusterId"
             :photos="photos"
             :clusterId="clusterId"
             :tags="tags"
             :yearRange="yearRange"
             :bbox="currentBbox"
             @nextPageLoaded="onNextPageLoaded"
             @closeGallery="onCloseGallery"
    />
  </main>
</template>

<style scoped lang="less">
:deep(.maplibregl-ctrl-top-right .maplibregl-ctrl) {
  margin: 1rem 1rem 0 0;
}
</style>