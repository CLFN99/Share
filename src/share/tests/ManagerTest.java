package share.tests;

import org.junit.jupiter.api.Test;
import share.database.Database;
import share.database.DatabaseRepo;
import share.models.*;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
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
            User u1 = manager.testLogin("celina@email.com", "1234");
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
        List<User> users = new ArrayList<>();
        try {
            users = manager.searchUser("celina");
            assertEquals("celina", users.get(0).getUsername());
            assertNotNull(users);
            //non existing user
            users = manager.searchUser("88");
            assertNull(users);
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
        //todo change users every execute
        initManager();
        //chat doesnt exist
        User u1 = manager.testLogin("billie@email.com", "1234");
        User u2 = manager.testLogin("amen@email.com", "1234");
        Chat c = new Chat(u1, u2);
        c.initManager(manager);
        int id = c.register();
        assertEquals(true, id != -1);
        assertEquals(true, c.getId() != -1);

        //trying to add existing chat
        int id2 = -1;
        try {
            id2 = manager.newChat(c);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        assertEquals(false, id2!=-1);
        try {
            repo.closeConn();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //send message in chat
    }

    @Test
    void updateUser() {
        initManager();
        //todo: change new bio on every execute
        User u = manager.testLogin("celina@email.com", "1234");
        String oldBio = u.getBio();
        String newBio = "yjrrjry";
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
        //todo change friends on every execute
        initManager();
        try {
            //they're not friends
            User u1 = manager.testLogin("alex@email.com", "1234");
            User friend = manager.testLogin("billie@email.com", "1234");
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
        initManager();
        //todo change users every execute
        User u1 = manager.testLogin("amen@email.com", "1234");
        User u2 = manager.testLogin("billie@email.com", "1234");
        Chat c = new Chat(u1, u2);
        c.initManager(manager);
        if(c.register() != -1){
            String text = "hey! how are you?";
            boolean success = manager.sendMessage(u2, text, c.getId());
            assertEquals(true, success);
            assertEquals(1, c.getMessages().size());
            assertEquals(text, c.getMessages().get(0).getText());
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
        User u1 = manager.testLogin("lienke@email.com", "1234");
        try {
            Post success = manager.newPost("neon too", u1);
            assertNotNull(success);
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
        initManager();
        User u1 = manager.testLogin("lienke@email.com", "1234");
        try {
            Post p = manager.newPost("neon too", u1);
            manager.updatePost(p, "pink flamingos");
            assertEquals("pink flamingos", p.getText());
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
    void deletePost() {
        initManager();
        User u1 = manager.testLogin("lienke@email.com", "1234");
       // u1.getFeed().initPublisher(manager.getPublisher());
        try {
            Post p = manager.newPost("ugh", u1);
            int size = u1.getFeed().getPosts().size();
            manager.deletePost(p);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            assertEquals((size - 1), u1.getFeed().getPosts().size());

        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            repo.closeConn();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}