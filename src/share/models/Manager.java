package share.models;

import share.interfaces.IMain;
import share.interfaces.ISession;
import share.database.Database;
import share.database.DatabaseRepo;
import share.interfaces.*;
import share.server.Publisher;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Manager extends UnicastRemoteObject implements IMain {
    /**
     * this class performs all the methods for the share.tests.server
     * specification of the methods can be found in IMain
     */

    private List<Session> activeSessions;
    private List<Chat> activeChats;
    private List<Feed> feeds;
    private DatabaseRepo repo;
    private Publisher publisher;

    /**
     * instantiate all lists
     * @throws RemoteException
     */
    public Manager() throws RemoteException {
        this.activeChats = new ArrayList<>();
        this.activeSessions = new ArrayList<>();
        this.feeds = new ArrayList<>();
        this.repo = new DatabaseRepo(new Database());
        publisher = new Publisher();

        publisher.registerProperty("chat");
        Registry registry = LocateRegistry.createRegistry(1099);
        registry.rebind("publisher", publisher);
        registry.rebind("manager", this);
        System.out.println("Server active");
    }

    public void getFeeds(){
        this.feeds = repo.getFeeds();
    }

    @Override
    public List<Post> refreshFeed(String email) throws RemoteException{
        getFeeds();
        for(Feed f : feeds){
            if(f.getUser().getEmail() == email){
                return f.getPosts();
            }
        }
        return null;
    }

    @Override
    public User searchUser(String username) throws RemoteException{
        User u = repo.searchUser(username);
        return u;
    }

    @Override
    public boolean addSession(Session session) throws RemoteException{
        if(session.getId() == -1){
            session.setId(createSessionId());
            this.activeSessions.add(session);
        }
        return false;
    }

    @Override
    public boolean removeSession(Session session) throws RemoteException{
        boolean in = false;
        int id = session.getId();
        for(Session s : activeSessions){
            if(Objects.equals(s.getId(), session.getId())){
                in = true;
            }
        }
        if(in){
            this.activeSessions.remove(session);
            changeSessionId(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean newChat(Chat chat) throws RemoteException{
        if(chat.getId() == -1){
            chat.setId(createChatId());
            this.activeChats.add(chat);
            try {
                publisher.registerProperty("chat" + chat.getId());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean updateUser(User user) throws RemoteException{
        if(repo.updateUser(user)){
            return true;
        }
        return false;
    }

    @Override
    public boolean addFriend(User u, User friend) throws RemoteException {
        if(repo.addFriend(u, friend)){
            u.addFriend(friend);
            friend.addFriend(u);
            return true;
        }
        return false;
    }

    @Override
    public boolean sendMessage(User u, String txt, int chatId) {
        Message msg = new Message(u, txt, chatId);
        for(Chat c : activeChats){
            if(c.getId() == chatId){
                try {
                    publisher.inform("chat" + c.getId(), null, msg);
                    return true;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    @Override
    public boolean newPost(String txt, User writer) throws RemoteException{
        Post p = new Post(txt, writer);
        if(repo.savePost(p) != -1){
            writer.getFeed().getPosts().add(p);
            try {
                publisher.inform("feed" + writer.getFeed().getId(), null, p);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public void updatePost(Post post, String text) throws RemoteException {
        Post prevValue = post;
        post.setText(text);
        if(repo.updatePost(post)){
            post.getWriter().getFeed().getPosts().remove(post);
            post.getWriter().getFeed().getPosts().add(post);
            try {
                publisher.inform("feed" + post.getWriter().getFeed().getId(), prevValue, post);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void deletePost(Post post) throws RemoteException{
        repo.deletePost(post);
        if(repo.updatePost(post)){
            post.getWriter().getFeed().getPosts().remove(post);
            try {
                publisher.inform("feed" + post.getWriter().getFeed().getId(), -1, post);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void changeSessionId(int id){
        for(Session s : activeSessions){
            if(s.getId() > id){
                s.setId(s.getId() - 1);
            }
        }
    }

    private int createSessionId(){
        int id = activeSessions.size() + 1;
        return id;
    }

    private int createChatId(){
        int id = activeChats.size() + 1;
        return id;
    }

    public void removeAllChats(){this.activeChats = new ArrayList<>();}
}
