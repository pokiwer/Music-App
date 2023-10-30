package com.example.musicapp;

public class Love {
    private int resourceID;
    private String name,numSong;

    public Love(int resourceID, String name, String numSong) {
        this.resourceID = resourceID;
        this.name = name;
        this.numSong = numSong;
    }

    public int getResourceID() {
        return resourceID;
    }

    public void setResourceID(int resourceID) {
        this.resourceID = resourceID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumSong() {
        return numSong;
    }

    public void setNumSong(String numSong) {
        this.numSong = numSong;
    }
}
