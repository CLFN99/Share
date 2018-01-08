package models;

import database.Database;
import database.DatabaseRepo;
import interfaces.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class Manager extends UnicastRemoteObject implements IMain {
    /**
     * this class performs all the methods for the server
     * specification of the methods can be found in IMain
     */

    private List<ISession> activeSessions;
    private List<Chat> activeChats;
    private List<Feed> feeds;
    private DatabaseRepo repo;
    /**
     * instantiate all lists
     * @throws RemoteException
     */
    public Manager() throws RemoteException {
        this.activeChats = new ArrayList<>();
        this.activeSessions = new ArrayList<>();
        this.feeds = new ArrayList<>();
        this.repo = new DatabaseRepo(new Database());
    }

    public void getFeeds(){
        this.feeds = repo.getFeeds();
    }

    @Override
    public List<Post> refreshFeed(String email) {
        getFeeds();
        for(Feed f : feeds){
            if(f.getUser().getEmail() == email){
                return f.getPosts();
            }
        }
        return null;
    }

    @Override
    public User searchUser(String username) {
        return null;
    }

    @Override
    public boolean addSession(Session session) {
        return false;
    }

    @Override
    public boolean removeSession(Session session) {
        return false;
    }

    @Override
    public boolean newChat(User userA, User userB) {
        return false;
    }

    @Override
    public void syncUser(User user) {

    }

    @Override
    public boolean sendMessage(String txt, String chatId) {
        return false;
    }

    @Override
    public boolean newPost(String txt, String email) {
        return false;
    }

    @Override
    public void updatePost(Post post, String text) {

    }

    @Override
    public void deletePost(Post post) {

    }
}
