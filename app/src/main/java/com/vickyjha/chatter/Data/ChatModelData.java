package com.vickyjha.chatter.Data;

public class ChatModelData implements java.io.Serializable{

    private String chatId;
    private String imageId;
    private String title;

    public ChatModelData(String chatId, String imageId, String title) {
        this.chatId = chatId;
        this.imageId = imageId;
        this.title = title;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
