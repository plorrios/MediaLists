package com.plorrios.medialists.Objects.Games;

import android.media.Image;

public class PublishersList {
    private String Title;
    private Image icon;
    private Publisher[] publishers;


    public PublishersList(Image icon){

    }

    public PublishersList(Publisher[] mpublishers){
        publishers = mpublishers;
    }

    public String getTitle() {
        return Title;
    }

    public Image getIcon() {
        return icon;
    }

    public Publisher[] getPublishers() {
        return publishers;
    }

}

