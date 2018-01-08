package database;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import models.*;

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
    public List<Chat> getChats(User u) {
        List<Chat> chats = new ArrayList<>();
        User uB = null;
        User uA = null;
        try{
            if(conn.isClosed()){
                conn = DriverManager.getConnection("jdbc:mysql://studmysql01.fhict.local:3306/dbi365425", "dbi365425", "proftaaks3");
            }
            //first, get the chat and its participants
            String getChatQuery = "Select c.chatId,  userAId, a.username as a_username, a.email as a_email, a.password as a_password, a.bio as a_bio,\n" +
                    "userBId, b.username as b_username, b.email as b_email, b.password as b_password, b.bio as b_bio from chat c\n" +
                    "INNER JOIN chat_participants cp ON cp.chatId = c.chatId \n" +
                    "INNER JOIN user a ON a.userId = cp.userAId\n" +
                    "INNER JOIN user b ON b.userId = cp.userBId\n" +
                    "where a.userId = ? or b.userId = ?;";
            PreparedStatement getChat = conn.prepareStatement(getChatQuery);
            getChat.setInt(1, u.getId());
            getChat.setInt(2, u.getId());
            ResultSet chatResult = getChat.executeQuery();
            while(chatResult.next()){
                uA = logIn(chatResult.getString("a_email"), chatResult.getString("a_password"));
                uB = logIn(chatResult.getString("b_email"), chatResult.getString("b_password"));
                if(uA.getEmail().equals(u.getEmail())){
                    Chat c = new Chat(u, uB);
                    c.setId(chatResult.getString("chatId"));
                    chats.add(c);
                }
                else if(uB.getEmail().equals(u.getEmail())){
                    Chat c = new Chat(uA, u);
                    c.setId(chatResult.getString("chatId"));
                    chats.add(c);
                }
            }
           // chatResult.close();
            getChat.close();
            //then, get the chat's messages
            String getMessagesQuery = "select messageId, text, time, m.userId AS messageWriter from chat c\n" +
                    "INNER JOIN message m ON c.chatId = m.chatId \n" +
                    "where c.chatId = ?";
            PreparedStatement getMessages = conn.prepareStatement(getMessagesQuery);
            for(Chat c : chats){
                List<Message> messages = new ArrayList<>();
                getMessages.setString(1, c.getId());
                ResultSet msgResult = getMessages.executeQuery();

                while(msgResult.next()){
                    if(uA.getId() == msgResult.getInt("messageWriter") && uA != null){
                        Message msg = new Message(uA, msgResult.getString("text"),c.getId());
                        messages.add(msg);
                    }
                    else if(uB.getId() == msgResult.getInt("messageWriter") && uA != null){
                        Message msg = new Message(uB, msgResult.getString("text"),c.getId());
                        messages.add(msg);
                    }
                }
                c.setMessages(messages);
            }

            conn.close();

        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return chats;
    }

    @Override
    public boolean saveMessage(Message msg) {
        try {
            if(conn.isClosed()){
                conn = DriverManager.getConnection("jdbc:mysql://studmysql01.fhict.local:3306/dbi365425", "dbi365425", "proftaaks3");
            }
            String query = "INSERT INTO message (text, time, chatId, userId) VALUES (?, ?,?,?)";
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
    public boolean saveChat(Chat c) {
        try {
            if(conn.isClosed()){
                conn = DriverManager.getConnection("jdbc:mysql://studmysql01.fhict.local:3306/dbi365425", "dbi365425", "proftaaks3");
            }
            String query = "INSERT INTO chat (chatId) VALUES (?)";
            PreparedStatement insertChat = conn.prepareStatement(query);
            insertChat.setString(1, c.getId());
            insertChat.execute();
            insertChat.close();

            String participantsQuery = "insert into chat_participants (userAId, userBId, chatId) values (?, ?, ?)";
            PreparedStatement insertParticipants = conn.prepareStatement(participantsQuery);
            insertParticipants.setInt(1, c.getUsers().get(0).getId());
            insertParticipants.setInt(2, c.getUsers().get(1).getId());
            insertParticipants.setString(3, c.getId());
            insertParticipants.execute();
            insertParticipants.close();
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
                u.setId(result.getInt("userId"));
                break;
            }

            validateUser.close();
            if(u!= null){
                String feedQuery = "select * from feed where userId = ?";
                PreparedStatement getFeed = conn.prepareStatement(feedQuery);
                getFeed.setInt(1, u.getId());
                ResultSet feedResult = getFeed.executeQuery();
                while(feedResult.next()){
                    Feed f = new Feed(u);
                    u.getFeed().setId(feedResult.getInt("feedId"));
                    break;
                }
                getFeed.close();

            }


          //  conn.close();

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
            if(conn.isClosed()){
                conn = DriverManager.getConnection("jdbc:mysql://studmysql01.fhict.local:3306/dbi365425", "dbi365425", "proftaaks3");
            }
            String query = "INSERT INTO post(text, time, userId) VALUES ( ?,?,?)";
            PreparedStatement insertPost = conn.prepareStatement(query);
            insertPost.setString(1, p.getText());
            insertPost.setString(2, p.getTimeStamp());
            insertPost.setInt(3, p.getWriter().getId());
            insertPost.execute();
            insertPost.close();
            String selectQuery = "select last_insert_id();";
            PreparedStatement getId = conn.prepareStatement(selectQuery);
            ResultSet resultSet = getId.executeQuery();
            while(resultSet.next()){
                p.setId(resultSet.getInt("last_insert_id()"));
            }
            //insert the just now inserted post id to feed id of user who posted it and his friends
            String feedQuery = "insert into feed_posts (feedId, postId) values (?, ?)";
            PreparedStatement insertFeedPosts = conn.prepareStatement(feedQuery);
            insertFeedPosts.setInt(1, p.getWriter().getFeed().getId());
            insertFeedPosts.setInt(2, p.getId());
            insertFeedPosts.execute();

            for(User friend : p.getWriter().getFriends()){
                insertFeedPosts.setInt(1, friend.getFeed().getId());
                insertFeedPosts.setInt(2, p.getId());
                insertFeedPosts.execute();
            }

            insertFeedPosts.close();
            conn.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
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
        try {
            if(conn.isClosed()){
                conn = DriverManager.getConnection("jdbc:mysql://studmysql01.fhict.local:3306/dbi365425", "dbi365425", "proftaaks3");
            }
            String query = "INSERT INTO chat (chatId) VALUES (?)";
            PreparedStatement insertFriend = conn.prepareStatement(query);

            insertFriend.execute();
            insertFriend.close();

            conn.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

        }
        return false;
    }

    public Connection getConn(){return  conn;}
}
