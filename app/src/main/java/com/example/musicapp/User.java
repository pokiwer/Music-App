package com.example.musicapp;

public class User {

    private String name, image, phone, birth;

    public User() {
    }

    public User(String name, String image, String phone, String birth) {
        this.name = name;
        this.image = image;
        this.phone = phone;
        this.birth = birth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }



    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }
}
