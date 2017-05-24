package com.example.kalirajkalimuthu.hometown.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by kalirajkalimuthu on 4/8/17.
 */

@IgnoreExtraProperties
public class ChatUser {
    public String uid;
    public String email;
    public String nickname;
    public String firebaseToken;

    public ChatUser() {

    }

    public ChatUser(String uid,  String nickname, String email, String firebaseToken) {
        this.uid = uid;
        this.email = email;
        this.firebaseToken = firebaseToken;
        this.nickname = nickname;
    }
}