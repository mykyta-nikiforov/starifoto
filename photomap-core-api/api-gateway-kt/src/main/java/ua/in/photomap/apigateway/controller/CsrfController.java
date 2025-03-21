package ua.in.photomap.apigateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CsrfController {

    @GetMapping("/api/csrf")
    public Map<String, String> csrf() {
        return Map.of("result", "csrf");
    }
}
