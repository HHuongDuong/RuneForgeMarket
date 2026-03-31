package com.example.runeforgemarket.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.runeforgemarket.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("""
        select u
        from User u
        where u.username = :username
        """)
    Optional<User> findUserByUsername(@Param("username") String username);

    @Query("""
        select u
        from User u
        where u.email = :email
        """)
    Optional<User> findUserByEmail(@Param("email") String email);

    @Query("""
        select (count(u) > 0)
        from User u
        where u.username = :username
        """)
    boolean userExistsByUsername(@Param("username") String username);

    @Query("""
        select (count(u) > 0)
        from User u
        where u.email = :email
        """)
    boolean userExistsByEmail(@Param("email") String email);
}
