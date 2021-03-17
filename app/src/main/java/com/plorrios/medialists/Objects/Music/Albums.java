package com.plorrios.medialists.Objects.Music;

public class Albums {

    private Album[] items;

    public Albums(Album[] Albums){
        items = Albums;
    }

    public Album[] getItems() {
        return items;
    }

    public int getCount(){
        return items.length;
    }
}
