package com.vit.club_manager.repository;


import com.vit.club_manager.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Integer> {
    // This lets us check if a role already exists so we don't create duplicates
    boolean existsByRoleName(String roleName);

    Roles findByRoleName(String roleName);
}