package com.vickyjha.chatter.Data;

import java.util.ArrayList;

public class MessageModelData {

    private String senderID;
    private String message;
    private ArrayList<String> mediaUrlList;
    private String messageId;
    private String time;

    public MessageModelData(String senderID, String message,String messageId ,ArrayList<String> mediaUrlList,String time) {
        this.senderID = senderID;
        this.message = message;
        this.messageId = messageId;
        this.mediaUrlList = mediaUrlList;
        this.time = time;
    }

    public String getName() {
        return senderID;
    }

    public void setName(String senderID) {
        this.senderID = senderID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public ArrayList<String> getMediaUrlList() {
        return mediaUrlList;
    }

    public void setMediaUrlList(ArrayList<String> mediaUrlList) {
        this.mediaUrlList = mediaUrlList;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
