package share.database;

import java.util.List;
import share.models.*;

public class DatabaseRepo {
    /**
     * this is what the share.tests.client and servers communicate with when they want to access the share.tests.database
     */

    private IDatabase context;

    public DatabaseRepo(IDatabase context) {
        this.context = context;
    }

    public List<Feed> getFeeds(){return context.getFeeds();}

    public List<Chat> getChats(User u){return context.getChats(u);}

    public boolean saveMessage(Message msg){return context.saveMessage(msg);}

    public boolean saveChat(Chat c){return context.saveChat(c);}

    public User searchUser(String username){return context.searchUser(username);}

    public int saveUser(User u){return context.saveUser(u);}

    public User logIn(String email, String password){return context.logIn(email,password);}

    public boolean updateUser(User u){return context.updateUser(u);}

    public boolean savePost(Post p){return context.savePost(p);}

    public boolean updatePost(Post p){return context.updatePost(p);}

    public boolean deletePost(Post p){return context.deletePost(p);}

    public boolean addFriend(User u, User friend){return context.addFriend(u, friend);}
}
