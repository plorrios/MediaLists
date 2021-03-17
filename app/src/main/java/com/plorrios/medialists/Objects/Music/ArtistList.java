package com.plorrios.medialists.Objects.Music;

public class ArtistList {

    private Artists artists;

    public ArtistList(Artists artistsitem){
        artists = artistsitem;
    }

    public Artists GetArtist()
    {return artists;}
    public int GetCount()
    {return artists.getCount();}
}
