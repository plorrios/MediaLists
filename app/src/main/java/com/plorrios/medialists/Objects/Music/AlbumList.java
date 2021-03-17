package com.plorrios.medialists.Objects.Music;

import com.plorrios.medialists.Objects.Music.Albums;

public class AlbumList {


    private Albums albums;

    public AlbumList(Albums Albums){
        albums = Albums;
    }

    public Albums GetAlbum()
    {return albums;}
    public int GetCount()
    {return albums.getCount();}
}
