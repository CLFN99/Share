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
    //TODO: REGISTER TO SESSION MANAGER

    private List<Session> activeSessions;
    private List<Chat> activeChats;
    private List<Feed> feeds;
    private DatabaseRepo repo;
    private Publisher publisher;
     bb  n

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
        registry.rebind("chatPublisher", publisher);
        registry.rebind("manager", this);
        System.out.println("Server active");
    }

    private void connectToLoginServer(){

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
        User u = repo.searchUser(username);
        return u;
    }

    @Override
    public boolean addSession(Session session) {
        if(session.getId() == -1){
            session.setId(createSessionId());
            this.activeSessions.add(session);
        }
        return false;
    }

    @Override
    public boolean removeSession(Session session) {
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
    public boolean newChat(Chat chat) {
        if(chat.getId() == -1){
            chat.setId(createChatId());
            this.activeChats.add(chat);
            return true;
        }
        return false;
    }

    @Override
    public void syncUser(User user) {
        repo.saveUser(user);
    }

    @Override
    public boolean sendMessage(User u, String txt, int chatId) {
        Message msg = new Message(u, txt, chatId);
        for(Chat c : activeChats){
            if(c.getId() == chatId){
                c.getMessages().add(msg);
                try {
                    publisher.inform("chat", null, msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    @Override
    public boolean newPost(String txt, String email) {
        return false;
    }

    @Override
    public void updatePost(Post post, String text) {
        post.setText(text);
        repo.updatePost(post);
        //TODO PUSH THIS TO FEEDS OF ALL THE FRIENDS
    }

    @Override
    public void deletePost(Post post) {
        repo.deletePost(post);
        //TODO PUSH THIS TO FEEDS OF ALL THE FRIENDS
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
}
