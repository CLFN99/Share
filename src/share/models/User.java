package share.models;

import share.interfaces.IUser;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable, IUser {
    private String username;
    private String password;
    private List<User> friends;
    private String email;
    private String bio;
    private Feed feed;
    private int id;

    public User(String username, String password, String email, String bio){
        this.username = username;
        this.password = password;
        this.email = email;
        this.bio = bio;
        this.friends = new ArrayList<>();
        this.feed = new Feed(this);
    }

    public List<User> getFriends() {
        return friends;
    }

    public void addFriend(User u){
        this.friends.add(u);
    }

    @Override
    public void changeBio(String txt) {
        this.bio = txt;
    }

    public String getPassword() {
        return password;
    }

    public String getBio() {
        return bio;
    }

    public Feed getFeed() {
        return feed;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public void setId(int id){this.id = id;}
    public int getId(){return id;}
    public void setFriends(List<User> friends){
        this.friends = friends;
    }

}
