package share.database;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import share.models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database implements IDatabase {
    private Connection conn = null;

    public Database() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/share", "proftaak", "Proftaak34C");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Chat> getChats(List<User> users) {
        List<Chat> chats = new ArrayList<>();
        User uB = null;
        User uA = null;
        try{
            if(conn.isClosed()){
                conn = conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/share", "proftaak", "Proftaak34C");
            }
            //first, get the chat and its participants
            String chatQuery = "select * from chat c inner join chat_participants cp on cp.chatId = c.chatId";
            PreparedStatement getChats = conn.prepareStatement(chatQuery);
            ResultSet chatResult = getChats.executeQuery();
            while(chatResult.next()){
                Chat c;
                for(User u : users){
                    if(u.getId() == chatResult.getInt("userAId")){
                        uA = u;
                    }
                    else if(u.getId() == chatResult.getInt("userBId")){
                        uB = u;
                    }
                }
                if(uA != null && uB != null){
                    c = new Chat(uA, uB);
                    c.setId(chatResult.getInt("chatId"));
                    chats.add(c);
                }
            }

            //then, get the chat's messages
            String getMessagesQuery = "select messageId, text, time, m.userId AS messageWriter from chat c\n" +
                    "INNER JOIN message m ON c.chatId = m.chatId \n" +
                    "where c.chatId = ?";
            PreparedStatement getMessages = conn.prepareStatement(getMessagesQuery);
            for(Chat c : chats){
                List<Message> messages = new ArrayList<>();
                getMessages.setInt(1, c.getId());
                ResultSet msgResult = getMessages.executeQuery();
                uA = c.getUsers().get(0);
                uB = c.getUsers().get(1);
                while(msgResult.next()){
                    if(uA.getId() == msgResult.getInt("messageWriter") && uA != null){
                        Message msg = new Message(uA, msgResult.getString("text"),c.getId());
                        msg.setTimeStamp(msgResult.getString("time"));
                        msg.setId(msgResult.getInt("messageId"));
                        messages.add(msg);
                    }
                    else if(uB.getId() == msgResult.getInt("messageWriter") && uA != null){
                        Message msg = new Message(uB, msgResult.getString("text"),c.getId());
                        msg.setTimeStamp(msgResult.getString("time"));
                        msg.setId(msgResult.getInt("messageId"));
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
                conn = conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/share", "proftaak", "Proftaak34C");
            }
            String query = "INSERT INTO message (text, time, chatId, userId) VALUES (?, ?,?,?)";
            PreparedStatement insertMessage = conn.prepareStatement(query);
            insertMessage.setString(1, msg.getText());
            insertMessage.setString(2, msg.getTimeStamp());
            insertMessage.setInt(3, msg.getChatId());
            insertMessage.setInt(4, msg.getUser().getId());
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
            msg.setId(id);

            conn.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean saveChat(Chat c) {
        try {
            if(conn.isClosed()){
                conn = conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/share", "proftaak", "Proftaak34C");
            }
            String query = "INSERT INTO chat (chatId) VALUES (?)";
            PreparedStatement insertChat = conn.prepareStatement(query);
            insertChat.setInt(1, c.getId());
            insertChat.execute();
            insertChat.close();

            String participantsQuery = "insert into chat_participants (userAId, userBId, chatId) values (?, ?, ?)";
            PreparedStatement insertParticipants = conn.prepareStatement(participantsQuery);
            insertParticipants.setInt(1, c.getUsers().get(0).getId());
            insertParticipants.setInt(2, c.getUsers().get(1).getId());
            insertParticipants.setInt(3, c.getId());
            insertParticipants.execute();
            insertParticipants.close();
            conn.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<User> searchUser(String username) {
        List<User> users = new ArrayList<>();
        try {

            if(conn.isClosed()){
                conn = conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/share", "proftaak", "Proftaak34C");
            }

            String searchQuery = "select * from user where username like concat(?,'%') or username like concat('%',?) or username like concat('%',?, '%')";
            PreparedStatement search = conn.prepareStatement(searchQuery);
            search.setString(1, username);
            search.setString(2, username);
            search.setString(3, username);
            ResultSet result = search.executeQuery();
            while(result.next()){
                User u = new User(result.getString("username"),result.getString("password"),
                        result.getString("email"), result.getString("bio"));
                u.setId(result.getInt("userId"));
                users.add(u);
            }
            search.close();
            result.close();
            if(users.isEmpty()){
                return null;
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int saveUser(User u) throws MySQLIntegrityConstraintViolationException {
        try {
            if(conn.isClosed()){
                conn = conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/share", "proftaak", "Proftaak34C");
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

            String getFeedIdQuery = "SELECT last_insert_id();";
            PreparedStatement getFeedId = conn.prepareStatement(getFeedIdQuery);
            ResultSet resultFeed = getFeedId.executeQuery();
            int feedid = 0;
            while(resultFeed.next()){
                feedid = resultFeed.getInt("last_insert_id()");
            }
            getFeedId.close();
            resultFeed.close();
            u.getFeed().setId(feedid);

            conn.close();
            return id;

        } catch (SQLException e) {

            if(e instanceof MySQLIntegrityConstraintViolationException){
              return -2;
            }
            else{
                e.printStackTrace();
            }
        }
        return -1;
    }

    @Override
    public User logIn(String email, String password) {
        User u = null;
        try {

            if(conn.isClosed()){
                conn = conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/share", "proftaak", "Proftaak34C");
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
            //TODO: AFTER LOGIN IN ADDSESSION LOOP THROUGH ALL USERS AND SET SESSION USER TO ONE W SAME ID
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return u;
    }

    @Override
    public boolean updateUser(User u) {
        try {
            if(conn.isClosed()){
                conn = conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/share", "proftaak", "Proftaak34C");
            }
            String query = "update user set bio = ? where userId = ?";
            PreparedStatement updateUser = conn.prepareStatement(query);
            updateUser.setString(1, u.getBio());
            updateUser.setInt(2, u.getId());
            updateUser.execute();
            updateUser.close();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public int savePost(Post p) {
        try {
            if(conn.isClosed()){
                conn = conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/share", "proftaak", "Proftaak34C");
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
            return p.getId();
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return -1;
    }

    @Override
    public boolean updatePost(Post p) {
        try {
            if(conn.isClosed()){
                conn = conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/share", "proftaak", "Proftaak34C");
            }
            String query = "update post set text = ?, time = ? where post.postId = ?";
            PreparedStatement insertPost = conn.prepareStatement(query);
            insertPost.setString(1, p.getText());
            insertPost.setString(2, p.getTimeStamp());
            insertPost.setInt(3, p.getId());
            insertPost.execute();
            insertPost.close();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean deletePost(Post p) {
        try {
            if(conn.isClosed()){
                conn = conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/share", "proftaak", "Proftaak34C");
            }
            String feedQuery = "delete from feed_posts where postId = ?;";
            String postQuery =  "delete from post where postId = ?";
            PreparedStatement deletePostFromFeed = conn.prepareStatement(feedQuery);
            deletePostFromFeed.setInt(1, p.getId());
            deletePostFromFeed.execute();
            deletePostFromFeed.close();
            PreparedStatement deletePost = conn.prepareStatement(postQuery);
            deletePost.setInt(1, p.getId());
            deletePost.execute();
            deletePost.close();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean addFriend(User u, User friend) throws MySQLIntegrityConstraintViolationException{
        try {
            if(conn.isClosed()){
                conn = conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/share", "proftaak", "Proftaak34C");
            }
            String query = "INSERT INTO friends (userId, friendId) VALUES (?, ?)";
            String query2 = "INSERT INTO friends (userId, friendId) VALUES (?, ?)";
            PreparedStatement insertFriend = conn.prepareStatement(query);
            insertFriend.setInt(1, u.getId());
            insertFriend.setInt(2, friend.getId());
            insertFriend.execute();
            insertFriend.close();
            PreparedStatement insertFriend2 = conn.prepareStatement(query2);
            insertFriend2.setInt(1, friend.getId());
            insertFriend2.setInt(2, u.getId());
            insertFriend2.execute();
            insertFriend2.close();

            conn.close();
            return true;
        } catch (SQLException e) {
            if(e instanceof MySQLIntegrityConstraintViolationException){

                    throw new MySQLIntegrityConstraintViolationException("users are already friends!");

            }
            else{
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public List<User> getAllUsers() {
        //get all users + their feed
        //get user's feed
        //get users friends
        List<User> users = new ArrayList<>();
        try {
            if(conn.isClosed()){
                conn = conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/share", "proftaak", "Proftaak34C");
            }
            String userQuery = "SELECT f.feedId, f.userId, u.username, u.email, u.password, u.bio FROM feed f \n" +
                    "                    INNER JOIN user u ON f.userId = u.userId";
            PreparedStatement getUsers = conn.prepareStatement(userQuery);
            ResultSet userResult = getUsers.executeQuery();
            while(userResult.next()){
                User u = new User(userResult.getString("username"), userResult.getString("password"),
                        userResult.getString("email"), userResult.getString("bio"));
                u.setId(userResult.getInt("userId"));
                u.getFeed().setId(userResult.getInt("feedId"));
                users.add(u);
            }
            getUsers.close();
            userResult.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public Connection getConn(){return  conn;}

    @Override
    public List<User> getFeedPosts(List<User> users ){
        List<Post> posts;
        try {
            if(conn.isClosed()){
                conn = conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/share", "proftaak", "Proftaak34C");
            }
            String query = "SELECT f.feedId, f.userId, fp.postId, p.text, p.time FROM feed f \n" +
                    "                    INNER JOIN user u ON f.userId = u.userId  \n" +
                    "                    INNER JOIN feed_posts fp ON fp.feedId = f.feedId \n" +
                    "                    INNER JOIN post p ON p.postId = fp.postId\n" +
                    "                    where f.feedId = ?";
            PreparedStatement getPosts = conn.prepareStatement(query);
            for(User u : users){
                posts = new ArrayList<>();
                getPosts.setInt(1, u.getFeed().getId());
                ResultSet result = getPosts.executeQuery();
                while (result.next()){
                    for(User writer : users){
                        if(writer.getId() == result.getInt("userId")){
                            Post p = new Post(result.getString("text"), writer);
                            p.setId(result.getInt("postId"));
                            p.setTimeStamp(result.getString("time"));
                            posts.add(p);
                        }
                    }
                }
                u.getFeed().setPosts(posts);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    @Override
    public List<User> getFriends(List<User> users){
        //loop through user and get from friends where userId = ?
        try {
            if(conn.isClosed()){
                conn = conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/share", "proftaak", "Proftaak34C");
            }
            //get the friends and add them to list of friends AFTER U'VE GOTTEN FEED POSTS AND REGISTERED PROPERTY!
            String friendQuery = "select * from friends where userId = ?";
            PreparedStatement getFriends = conn.prepareStatement(friendQuery);

            for(User u : users){
                getFriends.setInt(1, u.getId());
                ResultSet friendResult = getFriends.executeQuery();
                while(friendResult.next()){
                        for(User friend : users){
                            if(friend.getId() == friendResult.getInt("friendId")){
                                u.addFriend(friend);
                            }
                        }
                }
            }
            getFriends.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    @Override
    public void closeConn() throws SQLException {this.conn.close();}
}
