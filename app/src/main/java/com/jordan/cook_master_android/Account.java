package com.jordan.cook_master_android;

public class Account {

    private int id;
    private String name;
    private String email;
    private String image;

    public Account(int id, String name, String email, String image) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getImage() {
        return image;
    }

    @Override
    public String toString() {
        return name;
    }

}
