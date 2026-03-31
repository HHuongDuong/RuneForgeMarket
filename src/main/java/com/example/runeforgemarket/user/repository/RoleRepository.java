package com.example.runeforgemarket.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.runeforgemarket.user.model.Role;
import com.example.runeforgemarket.user.model.RoleName;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    @Query("""
        select r
        from Role r
        where r.name = :name
        """)
    Optional<Role> findRoleByName(@Param("name") RoleName name);
}
