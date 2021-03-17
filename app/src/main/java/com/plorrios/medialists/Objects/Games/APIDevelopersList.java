package com.plorrios.medialists.Objects.Games;

public class APIDevelopersList {

    int count;
    String previous;
    String next;
    Developer[] results;

    public int getCount() {
        return count;
    }

    public Developer[] getDevs(){ return results; }
}
