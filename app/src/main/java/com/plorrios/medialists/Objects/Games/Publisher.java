package com.plorrios.medialists.Objects.Games;

public class Publisher {

    int id;
    String name;
    String slug;
    Integer games_count;
    String image_background;
    String description;

    public String getName(){ return name; }

    public String getBackgroundImage() {
        return image_background;
    }

}
