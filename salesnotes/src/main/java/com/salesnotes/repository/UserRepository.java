package com.salesnotes.repository;

import com.salesnotes.entity.User;
import com.salesnotes.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsernameIgnoreCase(String username);
    Optional<User> findByEmailIgnoreCase(String email);
    boolean existsByUsernameIgnoreCase(String username);
    boolean existsByEmailIgnoreCase(String email);
    List<User> findByRole(UserRole role);
    List<User> findByActive(Boolean active);
    List<User> findByOrganisationId(String organisationId);
    List<User> findByFullnameContainingIgnoreCaseOrUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String fullname, String username, String email
    );
}
