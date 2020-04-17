package com.athish_works.festdynamicapp;

public class User {
    String name, gmail;

    public User() {
    }

    public User(String name, String gmail) {
        this.name = name;
        this.gmail = gmail;
    }

    public String getName() {
        return name;
    }

    public String getGmail() {
        return gmail;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }
}
