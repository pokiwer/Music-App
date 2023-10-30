package com.example.musicapp;

public class Album {
     private int resourceID;
     private String name,title;

    public Album(int resourceID, String title, String name) {
        this.resourceID = resourceID;
        this.title = title;
        this.name = name;
    }

    public int getResourceID() {
        return resourceID;
    }

    public void setResourceID(int resourceID) {
        this.resourceID = resourceID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
