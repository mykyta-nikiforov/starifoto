package ua.in.photomap.notificationapi.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import ua.in.photomap.common.rest.toolkit.exception.ResourceNotFoundException;

public class MarshalUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String JACKSON_DATETIME_MODULE_NAME = "jackson-datatype-jsr310";

    static {
        MAPPER.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);

        //add module that works with java.time package
        final Module dateTimeModule = ObjectMapper.findModules().stream()
                .filter(module -> module.getModuleName().equals(JACKSON_DATETIME_MODULE_NAME))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Failed to start project. Jackson DataTime module not found"));
        MAPPER.registerModule(dateTimeModule);
    }

    public static ObjectMapper getMapper() {
        return MAPPER;
    }
}
