package com.plorrios.medialists.Objects.Games;

public class APIPublishersList {
    int count;
    String previous;
    String next;
    Publisher[] results;

    public int getCount() {
        return count;
    }

    public Publisher[] getPublishers(){
        return results;
    }
}
