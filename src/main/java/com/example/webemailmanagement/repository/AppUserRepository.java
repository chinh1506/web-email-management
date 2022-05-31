package com.example.webemailmanagement.repository;

import com.example.webemailmanagement.model.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser,Long> {
    @Query("select e from AppUser e where e.userName=?1")
    public AppUser findUserAccount(String userName);
}
