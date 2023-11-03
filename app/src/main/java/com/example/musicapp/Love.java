package com.example.musicapp;

public class Love {
    private int id, numSong;
    private String name;

    public Love() {
    }

    public Love(int id, int numSong, String name) {
        this.id = id;
        this.numSong = numSong;
        this.name = name;
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
}