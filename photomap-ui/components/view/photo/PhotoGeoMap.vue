<script setup lang="ts">
import {onMounted, ref} from "vue";
import maplibregl, {Marker} from "maplibre-gl";
import {buildMarker, getMapStyleUrl} from "../../../utils/map";
import {MAP_STYLES} from "../../../constants/map";

const props = defineProps({
  latitude: {
    type: Number,
    required: true
  },
  longitude: {
    type: Number,
    required: true
  }
});

const mapInstance = ref();
let marker: Marker;

const containerId = ref(`photo-geo-map`);
const config = useRuntimeConfig();

onMounted(() => {
  mapInstance.value = new maplibregl.Map({
    container: containerId.value,
    style: getMapStyleUrl(MAP_STYLES.STREETS.id, config.public.maptilerApiKey),
    center: [props.longitude, props.latitude],
    zoom: 14,
    minZoom: 7,
    maxBounds: [
      [17, 44],
      [43, 53.2]
    ]
  });

  marker = buildMarker()
      .setLngLat({lng: props.longitude, lat: props.latitude})
      .addTo(mapInstance.value);
});

watch(() => props.latitude, (newLatitude) => {
  if (!!marker) {
    marker.setLngLat({lng: props.longitude, lat: newLatitude});
    // new center
    mapInstance.value.setCenter({lng: props.longitude, lat: newLatitude});
  }
});
</script>

<template>
  <div :id="containerId" class="photo-map-container"></div>
</template>

<style scoped lang="less">
.photo-map-container {
  height: 100%;
  width: 100%;
  min-height: 200px;
  min-width: 100px;
}
</style>