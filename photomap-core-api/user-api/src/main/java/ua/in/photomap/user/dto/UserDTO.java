package ua.in.photomap.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.in.photomap.user.model.Provider;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private Provider provider;
    private List<RoleDTO> roles;
    private Boolean hasPassword;
    private Boolean isEnabled;
    private LocalDateTime createdAt;
}
