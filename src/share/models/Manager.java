package share.models;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import share.interfaces.IMain;
import share.interfaces.ISession;
import share.database.Database;
import share.database.DatabaseRepo;
import share.interfaces.*;
import share.server.IRemotePublisher;
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
    private List<User> users;
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
        //if(LocateRegistry.getRegistry("127.0.0.1", 1099) == null){
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("publisher", publisher);
            registry.rebind("manager", this);
            System.out.println("Server active");
        //}
        getData();
        activeChats = repo.getChats(users);
        //efe
    }

    public void getData(){
        System.out.println("getting data....");
        //get all users + their feed
        users = repo.getAllUsers();
        //get user's feeds post
        users = repo.getFeedPosts(users);
        // register feed properyt
        for(User u : users){
            try {
                publisher.registerProperty("feed"+u.getFeed().getId());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        //get users friends
        users = repo.getFriends(users);
        for(User u : users){
            u.initPublisher(publisher);
            feeds.add(u.getFeed());
        }
        System.out.println("done w getting data");
    }


    @Override
    public List<Post> refreshFeed(String email) throws RemoteException{
        for(Feed f : feeds){
            if(Objects.equals(f.getUser().getEmail(), email)){
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
    public Session addSession(Session session) throws RemoteException{
        if(session.getId() == -1){
            session.setId(createSessionId());
                for(User u : users){
                    if(session.getUser().getId() == u.getId()){
                        session.setUser(u);
                    }
                }
            this.activeSessions.add(session);
            return session;
        }
        return null;
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
            repo.saveChat(chat);
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
        try {
            if(repo.addFriend(u, friend)){
                u.addFriend(friend);
                friend.addFriend(u);
                return true;
            }
        } catch (MySQLIntegrityConstraintViolationException e) {
            e.printStackTrace();
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
                    repo.saveMessage(msg);
                    return true;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    @Override
    public Post newPost(String txt, User writer) throws RemoteException{
        Post p = new Post(txt, writer);
        if(repo.savePost(p) != -1){
            writer.getFeed().getPosts().add(p);
            //repo.savePost(p);
            try {
                publisher.inform("feed" + writer.getFeed().getId(), null, p);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return p;
        }
        return null;
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

        if(repo.deletePost(post)){
            post.getWriter().getFeed().getPosts().remove(post);
            try {
                publisher.inform("feed" + post.getWriter().getFeed().getId(), -1, post);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void newUser(User u) {
        this.users.add(u);
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

    public IRemotePublisher getPublisher(){return publisher;}

    public User testLogin(String email, String password){
        User user = repo.logIn(email, password);
        if(user != null){
            for(User u : users){
                if(user.getId() == u.getId()){
                    user = u;
                    return user;
                }
            }
        }
        return null;
    }
}
