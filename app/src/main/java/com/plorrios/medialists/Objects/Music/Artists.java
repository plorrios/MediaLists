package com.plorrios.medialists.Objects.Music;

public class Artists {

    private Artist[] items;

    public Artists(Artist[] artists){
        items = artists;
    }

    public Artist[] getItems() {
        return items;
    }

    public int getCount(){
        return items.length;
    }
}
