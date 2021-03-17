package com.plorrios.medialists.Objects.Games;

import android.media.Image;

public class DevelopersList {
    private String Title;
    private Image icon;
    private Developer[] developers;


    public DevelopersList(Image icon){

    }

    public DevelopersList(Developer[] mdevelopers){
        developers = mdevelopers;
    }

    public String getTitle() {
        return Title;
    }

    public Image getIcon() {
        return icon;
    }

    public Developer[] getDevs() {
        return developers;
    }

}
