package com.plorrios.medialists.Objects.Games;

public class Platform {
    int id;
    String name;
    String slug;
    Integer games_count;
    String image_background;
    String description;
    String image;
    int year_start;
    int year_end;
    String platform;

    public String getName(){ return name; }

}
/*"platforms":
        [{"platform":{"id":18,"name":"PlayStation 4","slug":"playstation4","image":null,"year_end":null,"year_start":null,"games_count":4654,"image_background":"https://media.rawg.io/media/games/4be/4be6a6ad0364751a96229c56bf69be59.jpg"},"released_at":"2020-09-17","requirements":null},
        {"platform":{"id":1,"name":"Xbox One","slug":"xbox-one","image":null,"year_end":null,"year_start":null,"games_count":3233,"image_background":"https://media.rawg.io/media/games/15c/15c95a4915f88a3e89c821526afe05fc.jpg"},"released_at":"2020-09-17","requirements":null},
        {"platform":{"id":4,"name":"PC","slug":"pc","image":null,"year_end":null,"year_start":null,"games_count":218590,"image_background":"https://media.rawg.io/media/games/7fa/7fa0b586293c5861ee32490e953a4996.jpg"},"released_at":"2020-09-17","requirements":null}]
        */