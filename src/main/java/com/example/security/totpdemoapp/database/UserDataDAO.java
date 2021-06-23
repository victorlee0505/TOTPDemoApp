package com.example.security.totpdemoapp.database;

import com.example.security.totpdemoapp.database.entity.UserData;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDataDAO extends JpaRepository<UserData, Long> {
    
    UserData findTopByUserId(String userId);
}
