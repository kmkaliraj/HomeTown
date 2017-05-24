package com.example.kalirajkalimuthu.hometown.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by kalirajkalimuthu on 4/8/17.
 */

@IgnoreExtraProperties
public class Chat {
    public String sender;
    public String senderMail;
    public String receiver;
    public String receiverMail;
    public String senderUid;
    public String receiverUid;
    public String message;
    public long timestamp;

    public Chat() {
    }

    public Chat(String sender,String receiver, String senderMail, String receiverMail,String senderUid, String receiverUid, String message, long timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.senderMail = senderMail;
        this.receiverMail = receiverMail;
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.message = message;
        this.timestamp = timestamp;
    }


}