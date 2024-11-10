package com.example.project1.Model;

public class User {
    private int idUser;
    private String username;
    private String password;
    private int idRole;

    public User() {
    }

    public User(int idUser, String username, String password, int idRole) {
        this.idUser = idUser;
        this.username = username;
        this.password = password;
        this.idRole = idRole;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getIdRole() {
        return idRole;
    }

    public void setIdRole(int idRole) {
        this.idRole = idRole;
    }
}
