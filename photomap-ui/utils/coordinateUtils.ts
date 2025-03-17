export function formatCoordinates(latitude: number, longitude: number): string {
    // Convert latitude and longitude to the desired format
    const latDegrees = Math.floor(latitude);
    const latMinutes = Math.floor((latitude - latDegrees) * 60);
    const latSeconds = ((latitude - latDegrees - latMinutes / 60) * 3600).toFixed(2);
    const lngDegrees = Math.floor(longitude);
    const lngMinutes = Math.floor((longitude - lngDegrees) * 60);
    const lngSeconds = ((longitude - lngDegrees - lngMinutes / 60) * 3600).toFixed(2);

    // Construct the formatted string
    const formattedLat = `${latDegrees}° ${latMinutes}' ${latSeconds}" пн. ш.`;
    const formattedLng = `${lngDegrees}° ${lngMinutes}' ${lngSeconds}" сх. д.`;

    return `${formattedLat}, ${formattedLng}`;
}
