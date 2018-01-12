package share.tests;

import org.junit.jupiter.api.Test;
import share.database.Database;
import share.database.DatabaseRepo;
import share.models.*;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ManagerTest {
    private Manager manager;
    private DatabaseRepo repo;

    void initManager(){
        try {
            manager = new Manager();
            repo = new DatabaseRepo(new Database());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    @Test
    void refreshFeed() {
        initManager();
        try {
            User u1 = repo.logIn("celina@email.com", "1234");
            Feed f = u1.getFeed();
            f.initManager(manager);
            //existing user
            assertNotNull(f.refresh());
            //non existing user / empty string
            assertNull(manager.refreshFeed(""));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            repo.closeConn();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void searchUser() {
        initManager();
        //existing user
        User u = null;
        try {
            u = manager.searchUser("celina");
            assertEquals("celina", u.getUsername());
            assertNotNull(u);
            //non existing user
            User u2 = manager.searchUser("88");
            assertNull(u2);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            repo.closeConn();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void newChat() {
        initManager();
        //chat doesnt exist
        User u1 = repo.logIn("marian@email.com", "1234");
        User u2 = repo.logIn("morgana@email.com", "1234");
        Chat c = new Chat(u1, u2);
        boolean success;
        try {
            success = manager.newChat(c);
            assertEquals(true, success);
            assertEquals(true, c.getId() != -1);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        //trying to add existing chat
        boolean test = false;
        try {
            test = manager.newChat(c);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        assertEquals(false, test);
        try {
            repo.closeConn();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void updateUser() {
        initManager();
        //todo: change new bio on every execute
        User u = repo.logIn("celina@email.com", "1234");
        String oldBio = u.getBio();
        String newBio = "Halloooo";
        u.changeBio(newBio);
        boolean success = false;
        try {
            success = manager.updateUser(u);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        assertEquals(true, success);
        assertNotEquals(oldBio, u.getBio());
        try {
            repo.closeConn();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void addFriend(){
        initManager();
        try {
            //they're not friends
            User u1 = repo.logIn("fleuri@email.com", "1234");
            User friend = repo.logIn("marian@email.com", "1234");
            List<User> friends = u1.getFriends();
            boolean success = manager.addFriend(u1, friend);
            assertEquals(true, success);
            assertEquals((friends.size()), u1.getFriends().size());
            //they are already friends
            boolean fail = manager.addFriend(u1,friend);
            assertEquals(false, fail);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            repo.closeConn();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void sendMessage() {
        //todo check this + make db nd repo 90+
        initManager();
        manager.removeAllChats();
        User u1 = repo.logIn("celina@email.com", "1234");
        User u2 = repo.logIn("test@email.com", "1234");
        Chat c = new Chat(u1, u2);
        try {
            if(manager.newChat(c)){
                String text = "heyooo";
                boolean success = manager.sendMessage(u1, text, c.getId());
                assertEquals(true, success);
                assertEquals(1, c.getMessages().size());
                assertEquals(text, c.getMessages().get(0).getText());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            repo.closeConn();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void newPost() {
        initManager();
        User u1 = repo.logIn("morgana@email.com", "1234");
        u1.initPublisher(manager.getPublisher());
        try {
            boolean success = manager.newPost("neon too", u1);
            assertEquals(true, success);
            if(success){
                //check if new post is in feed of writer
                //assertEquals(true, u1.getFeed().getPosts().contains());
                //check if new post is in feed of writer's friends
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            repo.closeConn();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void updatePost() {
    }

    @Test
    void deletePost() {
    }

}