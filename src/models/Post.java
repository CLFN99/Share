package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Post implements Serializable {
    private String text;
    private Date timeStamp;
    public static int maxChar;
    private String id;
    private User writer;

    public Post(String text, User writer){
        this.text = text;
        this.writer = writer;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public static int getMaxChar() {
        return maxChar;
    }

    public User getWriter() {
        return writer;
    }
}
