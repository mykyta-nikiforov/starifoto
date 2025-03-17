package ua.in.photomap.geojsongenerator.exception;

public class GeojsonRegenerationException extends RuntimeException {
    public GeojsonRegenerationException(String message) {
        super(message);
    }

    public GeojsonRegenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
