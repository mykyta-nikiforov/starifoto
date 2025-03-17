package ua.in.photomap.common.rest.toolkit.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Privilege implements GrantedAuthority {
    private String name;

    @Override
    public String getAuthority() {
        return name;
    }
}
