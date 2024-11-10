package com.example.project1.Model;

public class Profile {
    private int idProfile;
    private int idUser;
    private String name;
    private String email;
    private String phoneNumber;
    private int idStatus;
    private byte[] photo;  // Lưu ảnh dưới dạng mảng byte

    public Profile() {
    }

    public Profile(int idProfile, int idUser, String name, String email, String phoneNumber, int idStatus, byte[] photo) {
        this.idProfile = idProfile;
        this.idUser = idUser;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.idStatus = idStatus;
        this.photo = photo;
    }

    public int getIdProfile() {
        return idProfile;
    }

    public void setIdProfile(int idProfile) {
        this.idProfile = idProfile;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getIdStatus() {
        return idStatus;
    }

    public void setIdStatus(int idStatus) {
        this.idStatus = idStatus;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }
}
