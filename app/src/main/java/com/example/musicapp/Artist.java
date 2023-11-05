package com.example.musicapp;

public class Artist {
    private int id, numSong, numFollow;
    private String name, image;

    public Artist() {
    }

    public Artist(int id, int numSong, int numFollow, String name, String image) {
        this.id = id;
        this.numSong = numSong;
        this.numFollow = numFollow;
        this.name = name;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumSong() {
        return numSong;
    }

    public void setNumSong(int numSong) {
        this.numSong = numSong;
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

    public int getNumFollow() {
        return numFollow;
    }

    public void setNumFollow(int numFollow) {
        this.numFollow = numFollow;
    }
}