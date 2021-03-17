package com.plorrios.medialists.Objects.Music;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Album {

    private String id;
    private String name;
    private Image[] images;
    private Artist[] artists;

    public Album(String Name, String url){
        name = Name;
        Image image = new Image(url);
        ArrayList<Image> Images = new ArrayList<>();
        Images.add(image);
        images = (Image[]) Images.toArray(new Image[Images.size()]);
    }

    public Image[] getImages() {
        return images;
    }

    public String getName() { return name; }

    public Artist[] getArtists() { return artists; }

    public String getId() {
        return id;
    }
}
