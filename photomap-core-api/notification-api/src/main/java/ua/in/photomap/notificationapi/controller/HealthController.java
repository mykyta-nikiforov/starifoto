package ua.in.photomap.notificationapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
@Tag(name = "Health API", description = "API for checking service health.")
public class HealthController {

    @GetMapping("/readiness")
    @Operation(summary = "Check readiness of the service")
    public ResponseEntity<?> readiness() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/liveness")
    @Operation(summary = "Check liveness of the service")
    public ResponseEntity<?> liveness() {
        return ResponseEntity.ok().build();
    }
}
