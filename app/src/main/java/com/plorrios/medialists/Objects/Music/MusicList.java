package com.plorrios.medialists.Objects.Music;

public class MusicList {

    private Songs tracks;

    public MusicList(Songs songs){
        tracks = songs;
    }

    public Songs GetMusic()
    {return tracks;}
    public int GetCount()
    {return tracks.getCount();}
}
