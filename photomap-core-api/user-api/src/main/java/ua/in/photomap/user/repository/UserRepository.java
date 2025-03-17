package ua.in.photomap.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ua.in.photomap.common.photo.model.dto.UserBasicDTO;
import ua.in.photomap.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u " +
            "join fetch u.roles r " +
            "join fetch r.privileges " +
            "where u.email = :email")
    Optional<User> findByEmailJoinPrivileges(String email);

    @EntityGraph(value = "user-with-roles")
    @Query("select u from User u")
    Page<User> findAllFetchRoles(Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("update User u set u.isEnabled = :isEnabled where u.id = :userId")
    void updateIsEnabled(Long userId, Boolean isEnabled);

    @Modifying(clearAutomatically = true)
    @Query("update User u set u.username = :username where u.id = :userId")
    void updateUserName(Long userId, String username);

    Boolean existsByEmail(String email);

    @Modifying(clearAutomatically = true)
    @Query("update User u set u.password = :encryptedPassword where u.id = :userId")
    void updatePassword(Long userId, String encryptedPassword);

    @Query("select new ua.in.photomap.common.photo.model.dto.UserBasicDTO(u.id, u.username) " +
            "from User u where u.id in :ids")
    List<UserBasicDTO> findAllBasicByIdIn(List<Long> ids);
}
