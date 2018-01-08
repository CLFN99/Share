package models;

import interfaces.IUser;

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

    public void addFriend(User u){this.friends.add(u);}

    @Override
    public void changeBio(String txt) {
        this.bio = bio;
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


    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setId(int id){this.id = id;}
    public int getId(){return id;}
}
