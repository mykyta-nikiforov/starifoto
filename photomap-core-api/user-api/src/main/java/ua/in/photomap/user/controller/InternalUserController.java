package ua.in.photomap.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.in.photomap.common.photo.model.dto.batch.GetUsersByIdsRequest;
import ua.in.photomap.common.photo.model.dto.batch.GetUsersByIdsResponse;
import ua.in.photomap.user.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/internal/user")
@Tag(name = "Internal User API", description = "Internal API for user service.")
public class InternalUserController {
    private final UserService userService;

    @Operation(description = "Get basic info by user ids. Used by photo-api to get comment author name.")
    @PostMapping("/basic-info")
    public GetUsersByIdsResponse getBasicInfo(
            @Parameter(description = "Users ids", required = true)
            @RequestBody GetUsersByIdsRequest request) {
        return userService.getBasicInfo(request.getIds());
    }
}
