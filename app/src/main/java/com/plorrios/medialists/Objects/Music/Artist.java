package com.plorrios.medialists.Objects.Music;

public class Artist {

    private String id;
    private String name;
    private Image[] images;

    public Artist(String Name){
        name = Name;
    }

    public String getId() { return id; }

    public String getName() {
        return name;
    }

    public Image[] getImages() { return images; }

}
