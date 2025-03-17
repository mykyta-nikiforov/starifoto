<script setup lang="ts">
import {ref} from 'vue';
import * as gmapsLoader from '@googlemaps/js-api-loader';
const { Loader } = gmapsLoader;

const searchResults = ref();
const selectedItem = ref();
const searchQuery = ref();
const searchTimer = ref();
const geocoding = ref();

const emit = defineEmits(['coordinates-changed']);

const config = useRuntimeConfig();
const loader = new Loader({
  apiKey: config.public.googleMapsApiKey,
  version: 'weekly',
  libraries: ['geocoding'],
  language: 'uk'
})

onMounted(async () => {
  const {Geocoder} = await loader.importLibrary("geocoding");
  geocoding.value = new Geocoder();
})

const searchPlaces = async (searchQuery: string) => {
  geocoding.value.geocode({
    address: searchQuery,
    componentRestrictions: {
      country: 'UA'
    }
  }).then((response : any) => {
    if (response.results.length > 0) {
      const filteredResults = response.results.filter((result: any) => {
        // Check each result to see if it has an address component with 'locality' type
        return result.address_components.some((component: any) => component.types.includes('locality'));
      });
      searchResults.value = filteredResults;
    }
  }).catch((e: any) => {
    // No results
  });
};

const coordinateRegex = /^[-+]?([1-8]?\d(\.\d+)?|90(\.0+)?)\s*[,\w]\s*[-+]?(180(\.0+)?|((1[0-7]\d)|([1-9]?\d))(\.\d+)?)$/;
const latitudeRegex = /(\d+)\s?\°\s?(\d+)\s?\'\s?(\d{1,}(?:\.\d*)?)\"\s?(N|S|пн\.?\s?ш\.|пд\.?\s?ш\.)/;
const longitudeRegex = /(\d+)\s?\°\s?(\d+)\s?\'\s?(\d{1,}(?:\.\d*)?)\"\s?(E|W|сх\.?\s?д\.|зх\.?\s?д\.)/;

const convertDMSToDecimal = function convertDMSToDecimal(degrees: number, minutes: number, seconds: number, direction: string) {
  const dd = degrees + minutes / 60 + seconds / 3600;
  if (direction === 'S' || direction === 'W' || direction === 'пд. ш.' || direction === 'зх. д.') {
    return -dd;
  }
  return dd;
}

watch(searchQuery, (newVal) => {
  if (!newVal) {
    searchResults.value = [];
    return;
  }
  // For coordinates in format 50.4501, 30.5234
  if (coordinateRegex.test(newVal)) {
    searchResults.value = [];
    const match = newVal.match(coordinateRegex);
    if (match) {
      emit('coordinates-changed', [+match[4], +match[1]]);
    }
  }
  // For coordinates in format 50° 27' 0" N, 30° 31' 24" E
  else if (latitudeRegex.test(newVal) && longitudeRegex.test(newVal)) {
    searchResults.value = [];
    const latitudeMatch = newVal.match(latitudeRegex);
    const longitudeMatch = newVal.match(longitudeRegex);
    if (latitudeMatch && longitudeMatch) {
      const lat = convertDMSToDecimal(+latitudeMatch[1], +latitudeMatch[2], +latitudeMatch[3], latitudeMatch[4]);
      const lng = convertDMSToDecimal(+longitudeMatch[1], +longitudeMatch[2], +longitudeMatch[3], longitudeMatch[4]);
      emit('coordinates-changed', [lng, lat]);
    }
  }
  // For search query
  else {
    if (searchTimer.value) {
      clearTimeout(searchTimer.value);
      searchTimer.value = null;
    }
    searchTimer.value = setTimeout(() => {
      if (newVal && !!searchQuery.value) {
        searchPlaces(newVal);
      }
      searchTimer.value = null;
    }, 1000);
  }
});

watch(selectedItem, (newVal) => {
  if (newVal) {
    emit('coordinates-changed', [newVal.geometry.location.lng(), newVal.geometry.location.lat()]);
  }
});
</script>

<template>
  <div class="geo-search-input-wrapper">
    <v-autocomplete
        bg-color="white"
        class="geo-search-autocomplete"
        label="Адреса або координати"
        :items="searchResults"
        v-model="selectedItem"
        v-model:search="searchQuery"
        item-title="formatted_address"
        item-value="place_id"
        prepend-inner-icon="mdi-magnify"
        return-object
        no-filter
        hide-no-data
    ></v-autocomplete>
  </div>
</template>

<style scoped lang="less">
.geo-search-input-wrapper {
  width: 100%;

  @media (min-width: 576px) {
    width: 375px;
  }
}

.geo-search-autocomplete {

  :deep(.v-input__details) {
    display: none;
  }
}
</style>