package ua.in.photomap.common.rest.toolkit.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class JwtAuthenticatedUser implements UserDetails {
    private Long id;
    private String email;
    private List<String> privileges;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return privileges.stream()
                .map(Privilege::new)
                .toList();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public boolean isModeratorOrAdmin() {
        return privileges.contains("ROLE_MODERATOR") || privileges.contains("ROLE_ADMIN");
    }
}
