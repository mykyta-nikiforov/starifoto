import SatelliteIcon from '@/assets/icons/map/sat.jpg';
import MapIcon from '@/assets/icons/map/map.jpg';

export const UKRAINE_CENTER_COORDINATES: [number, number] = [30.234, 48.557];

export interface MapStyle {
    id: string;
    icon: string;
}

export const MAP_STYLES : {[key: string]: MapStyle} = {
    STREETS: {
        id: "1e99ad7d-09af-48be-be45-469b931c3771",
        icon: MapIcon
    },
    SATELLITE: {
        id: "d4870643-228a-49b2-bb9d-ab53a5c54237",
        icon: SatelliteIcon
    }
}