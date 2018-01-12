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
                f.setId(feedResult.getInt("feedId"));
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
                    c.setId(chatResult.getInt("chatId"));
                    chats.add(c);
                }
                else if(uB.getEmail().equals(u.getEmail())){
                    Chat c = new Chat(uA, u);
                    c.setId(chatResult.getInt("chatId"));
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
                getMessages.setInt(1, c.getId());
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
    public List<Chat> getChats() {
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
                    "INNER JOIN user b ON b.userId = cp.userBId\n";
            PreparedStatement getChat = conn.prepareStatement(getChatQuery);
            ResultSet chatResult = getChat.executeQuery();
            while(chatResult.next()){
                uA = logIn(chatResult.getString("a_email"), chatResult.getString("a_password"));
                uB = logIn(chatResult.getString("b_email"), chatResult.getString("b_password"));
                Chat c = new Chat(uA, uB);
                c.setId(chatResult.getInt("chatId"));
                chats.add(c);
            }
             chatResult.close();
            getChat.close();
            //then, get the chat's messages
            String getMessagesQuery = "select messageId, text, time, m.userId AS messageWriter from chat c\n" +
                    "INNER JOIN message m ON c.chatId = m.chatId \n" +
                    "where c.chatId = ?";
            PreparedStatement getMessages = conn.prepareStatement(getMessagesQuery);
            for(Chat c : chats){
                List<Message> messages = new ArrayList<>();
                getMessages.setInt(1, c.getId());
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
            insertMessage.setInt(3, msg.getChatId());
            insertMessage.setInt(4, msg.getUser().getId());
            insertMessage.execute();
            insertMessage.close();
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
                conn = DriverManager.getConnection("jdbc:mysql://studmysql01.fhict.local:3306/dbi365425", "dbi365425", "proftaaks3");
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
        } finally {

        }
        return false;
    }

    @Override
    public User searchUser(String username) {
        User u = null;
        try {

            if(conn.isClosed()){
                conn = DriverManager.getConnection("jdbc:mysql://studmysql01.fhict.local:3306/dbi365425", "dbi365425", "proftaaks3");
            }

            String searchQuery = "select * from user where username like concat(?,'%') or username like concat('%',?) or username like concat('%',?, '%')";
            PreparedStatement search = conn.prepareStatement(searchQuery);
            search.setString(1, username);
            search.setString(2, username);
            search.setString(3, username);
            ResultSet result = search.executeQuery();
            while(result.next()){
                u = new User(result.getString("username"),result.getString("password"),
                        result.getString("email"), result.getString("bio"));
                u.setId(result.getInt("userId"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return u;
    }

    @Override
    public int saveUser(User u) throws MySQLIntegrityConstraintViolationException {
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
                u.setFeed(getFeed(u));
                u.setFriends(getFriends(u));
            }


          //  conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return u;
    }

    @Override
    public boolean updateUser(User u) {
        try {
            if(conn.isClosed()){
                conn = DriverManager.getConnection("jdbc:mysql://studmysql01.fhict.local:3306/dbi365425", "dbi365425", "proftaaks3");
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
                conn = DriverManager.getConnection("jdbc:mysql://studmysql01.fhict.local:3306/dbi365425", "dbi365425", "proftaaks3");
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
                conn = DriverManager.getConnection("jdbc:mysql://studmysql01.fhict.local:3306/dbi365425", "dbi365425", "proftaaks3");
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
                conn = DriverManager.getConnection("jdbc:mysql://studmysql01.fhict.local:3306/dbi365425", "dbi365425", "proftaaks3");
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
    public Feed getFeed(User u) {
        Feed f = new Feed(u);
        try {

            if(conn.isClosed()){
                conn = DriverManager.getConnection("jdbc:mysql://studmysql01.fhict.local:3306/dbi365425", "dbi365425", "proftaaks3");
            }

            String getFeedQuery= "select u.username, u.userId, u.password, u.email, u.bio, f.feedId, p.postId, p.text, p.time from feed f \n" +
                    "                    inner join feed_posts fp on fp.feedId = f.feedId inner join post p on p.postId = fp.postId \n" +
                    "                    inner join user u on p.userId = u.userId\n" +
                    "                    where f.userId = ?";
            PreparedStatement getFeed = conn.prepareStatement(getFeedQuery);
            getFeed.setInt(1,u.getId());
            ResultSet result = getFeed.executeQuery();
            boolean hasPosts = false;
            while(result.next()){
                hasPosts = true;
                User writer = new User(result.getString("username"), result.getString("password"),
                        result.getString("email"), result.getString("bio"));
                writer.setId(result.getInt("userId"));
                Post p = new Post(result.getString("text"), writer);
                p.setId(result.getInt("postId"));
                f.getPosts().add(p);
                f.setId(result.getInt("feedId"));
            }
            getFeed.close();
            result.close();
            if(!hasPosts){
                String query = "select * from feed where userId = ?";
                PreparedStatement st = conn.prepareStatement(query);
                st.setInt(1, u.getId());
                ResultSet res = st.executeQuery();
                while(res.next()){
                    f.setId(res.getInt("feedId"));
                }
                res.close();
                st.close();;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return f;
    }

    @Override
    public List<User> getFriends(User u) {
        List<User> friends = new ArrayList<>();
        try {

            if(conn.isClosed()){
                conn = DriverManager.getConnection("jdbc:mysql://studmysql01.fhict.local:3306/dbi365425", "dbi365425", "proftaaks3");
            }

            String getFriendsQuery= "select u.userId, u.username, u.password, u.email, u.bio from user u \n" +
                    "                    inner join friends f on f.friendId = u.userId\n" +
                    "                    where f.userId = ? ";
            PreparedStatement getFriends = conn.prepareStatement(getFriendsQuery);
            getFriends.setInt(1, u.getId());
            ResultSet result = getFriends.executeQuery();
            while(result.next()){
                User friend = new User(result.getString("username"), result.getString("password"),
                        result.getString("email"), result.getString("bio"));
                friend.setId(result.getInt("userId"));
                friend.setFeed(getFeed(friend));
                friends.add(friend);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friends;
    }

    public Connection getConn(){return  conn;}

    @Override
    public void closeConn() throws SQLException {this.conn.close();}
}
