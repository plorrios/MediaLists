package com.plorrios.medialists.Objects.Music;

public class Songs {

    private Song[] items;

    public Songs(Song[] songs){
        items = songs;
    }

    public Song[] getItems() {
        return items;
    }

    public int getCount(){
        return items.length;
    }
}
