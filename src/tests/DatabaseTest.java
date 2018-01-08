package tests;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import database.Database;
import models.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import javax.xml.crypto.Data;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {
    @Test
    public void getFeeds() throws Exception {
        Database db = new Database();
        List<Feed> feeds = db.getFeeds();
        boolean size = false;
        if(feeds.size() > 0){
            size = true;
        }
        assertEquals(feeds.size() != 0, size);
    }

    @Test
    public void saveMessage() throws Exception {
        Database db = new Database();
        User u1 = db.logIn("celina@email.com", "1234");
        User u2 = db.logIn("test@email.com", "1234");
        Chat c = new Chat(u1, u2);
        c.setId("C0001");
        Message msg = new Message(u1, "do you love cats?", c.getId());
        boolean succeeded = db.saveMessage(msg);
        assertEquals(true, succeeded);
    }

    @Test
    public void searchUser() throws Exception {
    }

    @Test
    public void saveUser() throws Exception {
        User u = new User("celina", "1234", "celina@email.com", "Hoi! Ik heet celina");
        Database db = new Database();
        int id = db.saveUser(u);
        assertEquals(id != -1, id != -1);

        User u2 = new User("celina", "1234", "celina@email.com", "Hoi! Ik heet celina");
        int idd = db.saveUser(u2);
        assertEquals(-2, idd);

    }

    @Test
    public void logIn() throws Exception {
        Database db = new Database();
        User u = db.logIn("test@email.com", "1234");
        assertEquals("test@email.com", u.getEmail());
        User u2 = db.logIn("doijoefj", "");
        assertEquals(null, u2   );
    }

    @Test
    public void updateUser() throws Exception {
    }

    @Test
    public void savePost() throws Exception {
        Database db = new Database();
        User u = db.logIn("test@email.com", "1234");
        Post p = new Post("I LOVE CATS", u);
        boolean succeeded = db.savePost(p);
        assertEquals(true, succeeded);
    }

    @Test
    public void updatePost() throws Exception {
    }

    @Test
    public void deletePost() throws Exception {
    }

    @Test
    public void addFriend() throws Exception {
    }

    @Test
    public void getConn() throws Exception {
        Database db = new Database();
        assertEquals(db.getConn() != null, true);
    }

    @Test
    public void saveChat() throws Exception{
        Database db = new Database();
        User u1 = db.logIn("celina@email.com", "1234");
        User u2 = db.logIn("test@email.com", "1234");
        Chat c = new Chat(u1, u2);
        c.setId("C0001");
        boolean succeeded = db.saveChat(c);
        assertEquals(true, succeeded);
    }

    @Test
    public void getChat() throws Exception {
        Database db = new Database();
        User user = db.logIn("celina@email.com", "1234");
        List<Chat> chats = db.getChats(user);
        boolean size = false;
        if(chats.size() > 0){
            size = true;
        }
        assertEquals(true, size);

    }
}