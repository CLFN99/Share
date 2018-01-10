package share.models;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Post implements Serializable {
    private String text;
    private String timeStamp;
    public final static int maxChar = 140;
    private int id;
    private User writer;

    public Post(String text, User writer){
        this.text = text;
        this.writer = writer;
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date time = new Date();
        timeStamp = dateFormat.format(time);
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public User getWriter() {
        return writer;
    }
}
