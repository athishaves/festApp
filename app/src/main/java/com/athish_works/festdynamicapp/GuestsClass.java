package com.athish_works.festdynamicapp;

public class GuestsClass {
    private String imageURL;
    private String name;
    private String occupation;

    public GuestsClass() {
    }

    public GuestsClass(String imageURL, String name, String occupation) {
        this.imageURL = imageURL;
        this.name = name;
        this.occupation = occupation;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getName() {
        return name;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }
}
