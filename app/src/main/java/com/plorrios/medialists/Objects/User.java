package com.plorrios.medialists.Objects;

public class User {
    public String user_name;
    public String nickname;
    public String image;

    public User(String userName, String nickName) {
        user_name = userName;
        nickname = nickName;
    }

    public String getImage() {
        return image;
    }

    public String getNickname() {
        return nickname;
    }

    public String getUser_name() {
        return user_name;
    }
}