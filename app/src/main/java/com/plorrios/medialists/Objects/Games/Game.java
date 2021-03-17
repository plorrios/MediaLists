package com.plorrios.medialists.Objects.Games;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Game implements Serializable {

    //añadir @SerializeName cuando nombre de variable sea distinto al nombre en el json

    private int id;
    @NonNull
    private String slug;
    @NonNull
    private String name;
    @NonNull
    private String nameOriginal;
    @NonNull
    private String description;

    private int metacritic;
    private String released;
    private boolean tba;

    private Platforms[] platforms;
    private Developer[] developers;


    private String updated;
    private String background_image;
    private String backgroundImageAdditional;
    private String website;

    @NonNull
    private Double rating;
    private int rating_top;
    private Ratings[] ratings;
    private Reactions reactions;
    private int added;
    //private AddedByStatus addedByStatus;
    private int playtime;
    private int screenshotsCount;
    private int moviesCount;
    private int creatorsCount;
    private int achievementsCount;
    private String parentAchievementsCount;
    private String reddit_url;
    private String redditName;
    private String redditDescription;
    private String redditLogo;
    private int redditCount;
    private String twitchCount;
    private String youtubeCount;
    private String reviewsTextCount;
    private int ratingsCount;
    private int suggestionsCount;
    private String[] alternativeNames;
    private String metacritic_url;
    private int parentsCount;
    private int additionsCount;
    private int gameSeriesCount;


    private int list;

    public Game(int id, @NonNull String name, @NonNull String Image) {
        this.id = id;
        this.name = name;
        this.background_image = Image;
    }

    public Game() {
        this.id = -1;
        this.name = "";
        this.description = "";
    }

    @NonNull
    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getDescription() { return description; }

    public String getReleaseDate() { return released; }

    public String getBackgroundImage() { return background_image; }

    public String getSlug() { return slug; }

    public String getRedditURL() { return reddit_url; }

    public String getMetacriticURL() { return metacritic_url; }

    public String toString() {
        return "Id: " + this.id + "\nName: " + this.name + "\nDescription: " + this.description
                + "\nBackground image: " + this.background_image;
    }

    public Platforms[] getPlatfroms(){ return platforms; }
    public Developer[] getDevelopers() {
        return developers;
    }
}