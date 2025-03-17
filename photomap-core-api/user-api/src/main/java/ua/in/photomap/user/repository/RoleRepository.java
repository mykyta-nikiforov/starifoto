package ua.in.photomap.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.in.photomap.user.model.Role;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query("SELECT COUNT(r.id) FROM Role r WHERE r.id IN :ids")
    Long countAllByIds(List<Long> ids);
}
