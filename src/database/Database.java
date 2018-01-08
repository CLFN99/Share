package database;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import models.Feed;
import models.Message;
import models.Post;
import models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database implements IDatabase {
    private Connection conn = null;

    public Database() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://studmysql01.fhict.local:3306/dbi365425", "dbi365425", "proftaaks3");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Feed> getFeeds() {
        List<Feed> feeds = new ArrayList<>();
        try {
            if(conn.isClosed()){
                conn = DriverManager.getConnection("jdbc:mysql://studmysql01.fhict.local:3306/dbi365425", "dbi365425", "proftaaks3");
            }
            String friendQuery = "SELECT fu.username, fu.email, fu.password, fu.bio FROM user u " +
                    "INNER JOIN friends f ON f.userId = u.userId " +
                    "INNER JOIN user fu ON fu.userId = f.friendId ";
            PreparedStatement selectFriends = conn.prepareStatement(friendQuery);
            ResultSet friendResult = selectFriends.executeQuery();


            String feedQuery = "SELECT f.feedId, f.userId, u.username, u.email, u.password, u.bio, fp.postId, p.text, p.time FROM feed f " +
                    "INNER JOIN user u ON f.userId = u.userId " +
                    "INNER JOIN feed_posts fp ON fp.feedId = f.feedId " +
                    "INNER JOIN post p ON p.postId = fp.postId ";
            PreparedStatement selectFeeds = conn.prepareStatement(feedQuery);
            ResultSet feedResult = selectFeeds.executeQuery();

            while (feedResult.next()) {
                User u = new User(feedResult.getString("username"), feedResult.getString("password"), feedResult.getString("email"),feedResult.getString("bio"));

                while(friendResult.next()){
                    User friend = new User(friendResult.getString("username"), friendResult.getString("password"), friendResult.getString("email"),friendResult.getString("bio"));
                    u.addFriend(friend);
                }

                Feed f = new Feed(u);
                feeds.add(f);
            }

            selectFeeds.close();
            feedResult.close();
            conn.close();

            return feeds;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean saveMessage(Message msg) {
        try {
            String query = "INSERT INTO message (text, time, chatId, userId) VALUES (?, ?,?,?,?)";
            PreparedStatement insertMessage = conn.prepareStatement(query);
            insertMessage.setString(1, msg.getText());
            insertMessage.setString(2, msg.getTimeStamp());
            insertMessage.setString(3, msg.getChatId());
            insertMessage.setInt(4, msg.getUser().getId());
            insertMessage.execute();
            insertMessage.close();
            conn.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

        }
        return false;
    }

    @Override
    public User searchUser(String username) {
        return null;
    }

    @Override
    public int saveUser(User u) {
        try {
            if(conn.isClosed()){
                conn = DriverManager.getConnection("jdbc:mysql://studmysql01.fhict.local:3306/dbi365425", "dbi365425", "proftaaks3");
            }
            String query = "INSERT INTO user (username, email, password, bio) VALUES (?,?,?,?);";
                    ;
            PreparedStatement insertMessage = conn.prepareStatement(query);
            insertMessage.setString(1, u.getUsername());
            insertMessage.setString(2, u.getEmail());
            insertMessage.setString(3, u.getPassword());
            insertMessage.setString(4, u.getBio());
            insertMessage.execute();
            insertMessage.close();

            String getIdQuery = "SELECT last_insert_id();";
            PreparedStatement getId = conn.prepareStatement(getIdQuery);
            ResultSet result = getId.executeQuery();
            int id = 0;
            while(result.next()){
                id = result.getInt("last_insert_id()");
            }
            getId.close();
            result.close();


            String feedQuery = "insert into feed ( userId) values (?);";
            PreparedStatement insertFeed = conn.prepareStatement(feedQuery);
            insertFeed.setInt(1, id);
            insertFeed.execute();
            insertFeed.close();
            conn.close();
            return id;

        } catch (SQLException e) {
            e.printStackTrace();
            if(e instanceof MySQLIntegrityConstraintViolationException){
                return -2;
            }
        }
        return -1;
    }

    @Override
    public User logIn(String email, String password) {
        User u = null;
        try {

            if(conn.isClosed()){
                conn = DriverManager.getConnection("jdbc:mysql://studmysql01.fhict.local:3306/dbi365425", "dbi365425", "proftaaks3");
            }

            String query = "SELECT * FROM user WHERE email = ? AND password = ?;";

            PreparedStatement validateUser = conn.prepareStatement(query);
            validateUser.setString(1, email);
            validateUser.setString(2, password);
            ResultSet result = validateUser.executeQuery();

            while(result.next()){
                u = new User(result.getString("username"),password, email, result.getString("bio"));
                break;
            }
            validateUser.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return u;
    }

    @Override
    public boolean updateUser(User u) {
        return false;
    }

    @Override
    public boolean savePost(Post p) {
        try {
            String query = "INSERT INTO post(text, time, chatId, userId) VALUES (?, ?,?,?,?)";
            PreparedStatement insertMessage = conn.prepareStatement(query);
            insertMessage.setString(1, p.getText());
            insertMessage.setString(2, msg.getTimeStamp());
            insertMessage.setString(3, msg.getChatId());
            insertMessage.setInt(4, msg.getUser().getId());
            insertMessage.execute();
            insertMessage.close();
            conn.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

        }
        return false;
    }

    @Override
    public boolean updatePost(Post p) {
        return false;
    }

    @Override
    public boolean deletePost(Post p) {
        return false;
    }

    @Override
    public boolean addFriend(User u, User friend) {
        return false;
    }

    public Connection getConn(){return  conn;}
}
