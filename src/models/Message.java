package models;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements Serializable {
    private User user;
    private String timeStamp;
    private String chatId;
    private String text;

    public Message(User user, String text, String chatId){
        this.user = user;
        this.text = text;
        this.chatId = chatId;
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date time = new Date();
        timeStamp = dateFormat.format(time);
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getChatId() {
        return chatId;
    }

    public User getUser() {
        return user;
    }

    public String getText() {
        return text;
    }

}
