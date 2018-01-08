package tests;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import database.Database;
import models.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {
    @Test
    public void getFeeds() throws Exception {
        Database db = new Database();
        List<Feed> feeds = db.getFeeds();
        assertEquals(feeds.size() != 0, feeds.size());
    }

    @Test
    public void saveMessage() throws Exception {
    }

    @Test
    public void searchUser() throws Exception {
    }

    @Test
    public void saveUser() throws Exception {
        User u = new User("emil", "1234", "test@email.com", "Hoi! Ik heet emil");
        Database db = new Database();
        int id = db.saveUser(u);
        assertEquals(id != -1, id != -1);

        User u2 = new User("emil", "1234", "test@email.com", "Hoi! Ik heet emil");
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
}