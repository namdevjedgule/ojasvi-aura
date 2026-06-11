package com.ojasvi.ecommerce.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ojasvi.ecommerce.Entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByMobile(String mobile);

    Boolean existsByEmail(String email);

    Boolean existsByMobile(String mobile);

    Long countByRoleRoleName(String roleName);

    long countByRole_IdAndIsActiveTrue(Long roleId);

    List<User> findByRole_IdAndIsActiveTrue(Long roleId);

}
