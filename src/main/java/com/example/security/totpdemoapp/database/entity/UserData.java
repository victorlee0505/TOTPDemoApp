package com.example.security.totpdemoapp.database.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "USERDATA")
public class UserData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USER_ID", length = 100, nullable = false)
    private String userId;

    @Column(name = "USER_PW", length = 15, nullable = false)
    private String userPassword;

    @Column(name = "USER_ENC_SECRET", length = 64, nullable = true)
    private String userEncryptedSecret;

    @Column(name = "USER_ENC_KEY", length = 44, nullable = true)
    private String userEncryptionKey;

    public UserData() {
    }

    public UserData(String userId, String userPassword) {
        this.userId = userId;
        this.userPassword = userPassword;
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserEncryptedSecret() {
        return userEncryptedSecret;
    }

    public void setUserEncryptedSecret(String userEncryptedSecret) {
        this.userEncryptedSecret = userEncryptedSecret;
    }

    public String getUserEncryptionKey() {
        return userEncryptionKey;
    }

    public void setUserEncryptionKey(String userEncryptionKey) {
        this.userEncryptionKey = userEncryptionKey;
    }

}
