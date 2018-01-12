package share.tests;

import share.database.Database;
import share.models.*;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {
    private Database db = new Database();
    @Test
    public void getFeeds() throws Exception {

        List<Feed> feeds = db.getFeeds();
        boolean size = false;
        if(feeds.size() > 0){
            size = true;
        }
        assertEquals(feeds.size() != 0, size);
        db.closeConn();
    }

    @Test
    public void saveMessage() throws Exception {

        User u1 = db.logIn("celina@email.com", "1234");
        User u2 = db.logIn("test@email.com", "1234");
        Chat c = new Chat(u1, u2);
        c.setId(1);
        Message msg = new Message(u1, "do you love cats?", c.getId());
        boolean succeeded = db.saveMessage(msg);
        assertEquals(true, succeeded);
        db.closeConn();
    }

    @Test
    public void searchUser() throws Exception {

        User u = db.searchUser("c");
        assertEquals(true, u != null);
        assertEquals(true, u.getUsername().contains("c"));
        db.closeConn();
    }

    @Test
    public void saveUser() throws Exception {
        User u = new User("celina", "1234", "celina@email.com", "Hoi! Ik heet celina");

        int id = db.saveUser(u);
        assertEquals(id != -1, id != -1);

        User u2 = new User("celina", "1234", "celina@email.com", "Hoi! Ik heet celina");
        int idd = db.saveUser(u2);
        assertEquals(-2, idd);
        db.closeConn();

    }

    @Test
    public void logIn() throws Exception {

        User u = db.logIn("test@email.com", "1234");
        assertEquals("test@email.com", u.getEmail());
        User u2 = db.logIn("doijoefj", "");
        assertEquals(null, u2   );
        db.closeConn();
    }

    @Test
    public void updateUser() throws Exception {

        User u = db.logIn("celina@email.com", "1234");
        u.changeBio("heyooo");
        boolean succeeded = db.updateUser(u);
        assertEquals(true, succeeded);
        db.closeConn();
    }

    @Test
    public void savePost() throws Exception {

        User u = db.logIn("test@email.com", "1234");
        Post p = new Post("I LOVE CATS", u);
        int id = db.savePost(p);
        assertEquals(true, id!=-1);
        db.closeConn();
    }

    @Test
    public void updatePost() throws Exception {

        User u = db.logIn("celina@email.com", "1234");
        Post p = new Post("nothing is real", u);
        db.savePost(p);
        p.setText("NOTHING IS REAALL!!");
        boolean success = db.updatePost(p);
        assertEquals(true, success);
        db.closeConn();
    }

    @Test
    public void deletePost() throws Exception {

        User u = db.logIn("celina@email.com", "1234");
        Post p = new Post("nothing is real", u);
        db.savePost(p);

        boolean success = db.deletePost(p);
        assertEquals(true, success);
        db.closeConn();
    }

    @Test
    public void addFriend() throws Exception {

        User u1 = db.logIn("celina@email.com", "1234");
        User u2 = db.logIn("test@email.com", "1234");
        boolean succeeded = db.addFriend(u1, u2);
        assertEquals(true, succeeded);
        db.closeConn();
    }

    @Test
    public void getConn() throws Exception {
        assertEquals(db.getConn() != null, true);
        db.closeConn();
    }

    @Test
    public void saveChat() throws Exception{

        User u1 = db.logIn("celina@email.com", "1234");
        User u2 = db.logIn("test@email.com", "1234");
        Chat c = new Chat(u1, u2);
        c.setId(1);
        boolean succeeded = db.saveChat(c);
        assertEquals(true, succeeded);
        db.closeConn();
    }

    @Test
    public void getChat() throws Exception {

        User user = db.logIn("celina@email.com", "1234");
        List<Chat> chats = db.getChats(user);
        boolean size = false;
        if(chats.size() > 0){
            size = true;
        }
        assertEquals(true, size);
        db.closeConn();
    }
}