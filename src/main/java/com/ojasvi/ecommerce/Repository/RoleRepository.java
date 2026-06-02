package com.ojasvi.ecommerce.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ojasvi.ecommerce.Entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
