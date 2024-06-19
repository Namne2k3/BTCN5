package com.bookstore.repository;

import com.bookstore.dto.ReqResUser;
import com.bookstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface IUserRepository extends JpaRepository<User, Integer> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user_role (user_id, role_id) VALUE(?1, ?2)", nativeQuery = true)
    void addRoleToUser(Long userId, Long roleId);

    @Query("SELECT u.id FROM User u WHERE u.username = ?1")
    Long getUserIdByUserName(String username);

    @Query("SELECT u FROM User u WHERE u.username = ?1")
    User findByUsername(String username);

    @Query(value = "SELECT r.name FROM role r INNER JOIN user_role ur ON r.id = ur.role_id WHERE ur.user_id = ?1", nativeQuery = true)
    String[] getRoleOfUser(Long userId);

    @Query("SELECT u FROM User u WHERE u.email = ?1")
    User getUserByEmail(String email);

//    @Query(value = "SELECT u.id, u.username, u.email,u.name, r.name as role_name FROM user u JOIN user_role ur ON u.id = ur.user_id JOIN role r ON ur.role_id = r.id;", nativeQuery = true)
    @Query("SELECT u FROM User u JOIN FETCH u.roles")
    List<User> getAllUserS();

}
