package ua.in.photomap.common.photo.model.dto.email;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthTokenizedActionEmailDTO {
    @NotNull
    private String email;
    @NotNull
    private String token;
    @NotNull
    private String username;
}
