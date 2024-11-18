package com.project.template.repositories;

import com.project.template.domain.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Set<Role> findRolesByRoleNameIn(List<String> roles);
    boolean findRoleByRoleName(String roleName);
}
