package com.example.musicapp;

import java.io.Serializable;

public class Song implements Serializable {
    private int id, numListen, artist;
    private String name, title,image;

    public Song() {
    }

    public Song(int id, int numListen, int artist, String name, String title, String image) {
        this.id = id;
        this.numListen = numListen;
        this.artist = artist;
        this.name = name;
        this.title = title;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumListen() {
        return numListen;
    }

    public void setNumListen(int numListen) {
        this.numListen = numListen;
    }

    public int getArtist() {
        return artist;
    }

    public void setArtist(int artist) {
        this.artist = artist;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", numListen=" + numListen +
                ", artist=" + artist +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
