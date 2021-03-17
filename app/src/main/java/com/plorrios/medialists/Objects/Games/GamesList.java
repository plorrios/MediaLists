package com.plorrios.medialists.Objects.Games;

import android.media.Image;

import com.plorrios.medialists.Objects.Music.Song;

public class GamesList {

    private String Title;
    private Image icon;
    private Game[] games;


    public GamesList(Image icon){

    }

    public GamesList(Game[] mgames){
        games = mgames;
    }

    public String getTitle() {
        return Title;
    }

    public Image getIcon() {
        return icon;
    }

    public Game[] getGames() {
        return games;
    }

}
