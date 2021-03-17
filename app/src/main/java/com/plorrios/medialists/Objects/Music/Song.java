package com.plorrios.medialists.Objects.Music;

import androidx.annotation.NonNull;

public class Song {
    @NonNull
    private String id;
    private String name;
    private Album album;
    private Artist[] artists;
    private String preview_url;

    public Song(String Id, String Name, Album Album, Artist[] Artists, String Preview_url){
        id = Id;
        name = Name;
        album = Album;
        artists = Artists;
        preview_url = Preview_url;
    }

    public String getPreview() { return preview_url; }

    public Album getAlbum() { return album; }

    public Artist[] getArtists() { return artists; }

    public String getId() { return id; }

    public String getName() { return name; }

}
