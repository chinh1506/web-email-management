package com.example.webemailmanagement.repository;

import com.example.webemailmanagement.model.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AppRoleRepository extends JpaRepository<AppRole,Long> {
    @Query("select ur.appRole.roleName from UserRole ur where ur.appUser.userId=?1")
    public List<String> getRoleNames(Long userId);
}
