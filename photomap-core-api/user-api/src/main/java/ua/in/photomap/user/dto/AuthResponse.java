package ua.in.photomap.user.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private UserDTO user;
    private String accessToken;
    private Long expiresIn;
    private String refreshToken;
    private Long refreshTokenExpiresIn;
}