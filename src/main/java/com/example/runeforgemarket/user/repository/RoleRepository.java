package com.example.runeforgemarket.user.repository;

import java.util.Optional;

import com.example.runeforgemarket.user.model.Role;
import com.example.runeforgemarket.user.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(RoleName name);
}
