package com.example.admin.sinch;

public class modelUser {
    String name, num, email, password, image, status, id;

    public modelUser(String id, String name) {
        this.id= id;
        this.name= name;
    }

    public modelUser(String name, String num, String email, String password, String image, String status, String id) {
        this.name = name;
        this.num = num;
        this.email = email;
        this.password = password;
        this.image = image;
        this.status = status;
        this.id= id;
    }

    public modelUser() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
