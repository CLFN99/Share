package share.database;

import java.sql.SQLException;
import java.util.List;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import share.models.*;

public class DatabaseRepo {
    /**
     * this is what the share.tests.client and servers communicate with when they want to access the share.tests.database
     */

    private IDatabase context;

    public DatabaseRepo(IDatabase context) {
        this.context = context;
    }

    //public List<Feed> getFeeds(){return context.getFeeds();}

   // public List<Chat> getChats(User u){return context.getChats(u);}

    public boolean saveMessage(Message msg){return context.saveMessage(msg);}

    public boolean saveChat(Chat c){return context.saveChat(c);}

    public List<User> searchUser(String username){return context.searchUser(username);}

    public int saveUser(User u)throws MySQLIntegrityConstraintViolationException{
       return context.saveUser(u);
    }

    public User logIn(String email, String password){return context.logIn(email,password);}

    public boolean updateUser(User u){return context.updateUser(u);}

    public int savePost(Post p){return context.savePost(p);}

    public boolean updatePost(Post p){return context.updatePost(p);}

    public boolean deletePost(Post p){return context.deletePost(p);}

    public boolean addFriend(User u, User friend) throws MySQLIntegrityConstraintViolationException {return context.addFriend(u, friend);}

    public List<Chat> getChats(List<User> users){return context.getChats(users);}

    public List<User> getAllUsers(){return context.getAllUsers();}
    public List<User> getFeedPosts(List<User> users){return context.getFeedPosts(users);}
    public List<User> getFriends(List<User> users){return context.getFriends(users);}

    public void closeConn() throws SQLException {context.closeConn();}
}
