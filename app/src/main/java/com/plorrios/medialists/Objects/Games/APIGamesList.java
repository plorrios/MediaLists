package com.plorrios.medialists.Objects.Games;

public class APIGamesList {


    private int count;
    private String next;
    private String previous;
    private Game[] results;

    public Game[] GetGames()
    {return results;}
    public int GetCount()
    {return count;}

    public APIGamesList(Game[] games){
        count = games.length;
        results = games;
    }

    public void changeGames(Game[] games){results = games;}

}
