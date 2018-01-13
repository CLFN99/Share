package share.models;

import share.interfaces.IMain;
import share.interfaces.IUser;
import share.server.IRemotePropertyListener;
import share.server.IRemotePublisher;

import java.io.Serializable;
import java.rmi.RemoteException;
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
    private transient IMain manager;
    private transient IRemotePublisher publisher;

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

    public void initManager(IMain manager){
        this.manager = (Manager) manager;
    }
    public void initPublisher(IRemotePublisher publisher){
        this.publisher = publisher;
        try {
            for(User u : friends){
                //subscribe user's feed to friends feed
                publisher.subscribePropertyListener(this.getFeed(), ("feed"+u.getFeed().getId()));

            }
            //subscribe user to his own feed
           // publisher.subscribePropertyListener(feed, ("feed"+feed.getId()));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
