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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
       // if(LocateRegistry.getRegistry("127.0.0.1", 1099) == null){
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("publisher", publisher);
            registry.rebind("manager", this);
            System.out.println("Server active");
        //}
        getData();
        activeChats = repo.getChats(users);
        for(Chat c : activeChats){
            publisher.registerProperty("chat"+c.getId());
        }
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
                publisher.registerProperty("user"+u.getId());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        //get users friends
        users = repo.getFriends(users);
        for(User u : users){
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
    public List<User> searchUser(String username) throws RemoteException{
        List<User> users = repo.searchUser(username);
        return users;
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
                session = s;
                in = true;
                break;
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
    public int newChat(Chat chat) throws RemoteException{
        if(chat.getId() == -1){
            repo.saveChat(chat);
            this.activeChats.add(chat);
            try {
                publisher.registerProperty("chat" + chat.getId());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return chat.getId();
        }
        return -1;
    }

    @Override
    public boolean updateUser(User user) throws RemoteException{
        if(repo.updateUser(user)){
            publisher.inform(("user"+user.getId()), null, user);
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
                publisher.inform(("user"+u.getId()), null, u);
                publisher.inform(("user"+friend.getId()), null, friend);
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
                    repo.saveMessage(msg);
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
    public Post newPost(String txt, User writer) throws RemoteException{
        Post p = new Post(txt, writer);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date time = new Date();
        p.setTimeStamp(dateFormat.format(time));
        if(repo.savePost(p) != -1){
            writer.getFeed().getPosts().add(p);
            refreshFeeds(p);
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
            refreshFeeds(post);
        }
    }

    @Override
    public void deletePost(Post post) throws RemoteException{
        if(repo.deletePost(post)){
            boolean in = false;
            for(Post p : post.getWriter().getFeed().getPosts()){
                if(p.getId() == post.getId()){
                    in = true;
                    post = p;
                    break;
                }
            }
            if(in){
                for(User friend : post.getWriter().getFriends()){
                    friend.getFeed().getPosts().remove(post);
                }
                post.getWriter().getFeed().getPosts().remove(post);
                refreshFeeds(post);
            }
//            try {
//               // publisher.inform("feed" + post.getWriter().getFeed().getId(), -1, post);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
        }
    }
    private void refreshFeeds(Post post){
    for(Feed f : feeds){
        if(Objects.equals(f.getUser().getEmail(), post.getWriter().getEmail())){
            f.setPosts(post.getWriter().getFeed().getPosts());
            break;
        }
        for(User friend : post.getWriter().getFriends()){
            if(f.getUser().getEmail().equals(friend.getEmail())){
                f.setPosts(post.getWriter().getFeed().getPosts());
            }
        }
    }
}
    @Override
    public void newUser(User u) {
        this.users.add(u);
        try {
            publisher.registerProperty("user"+u.getId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Chat> getChats(int id) throws RemoteException {
        List<Chat> userChats = new ArrayList<>();
        for(Chat c : activeChats){
            if(c.getUsers().get(0).getId() == id || c.getUsers().get(1).getId() == id){
                userChats.add(c);
            }
        }
        return userChats;
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

    @Override
    public IRemotePublisher getPublisher() throws RemoteException{return publisher;}

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
