package share.tests;

import org.junit.jupiter.api.Test;
import share.database.Database;
import share.database.DatabaseRepo;
import share.models.*;

import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.*;

class SessionManagerTest {
    private Manager manager;
    private DatabaseRepo repo;
    private SessionManager sessionManager;

    void initManager(){
        try {
            manager = new Manager();
            sessionManager = new SessionManager();
            repo = new DatabaseRepo(new Database());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Test
    void logIn() {
        initManager();
        //user exists
        User u = sessionManager.logIn("celina@email.com", "1234");
        assertNotNull(u);
        assertNotNull(u.getFeed());
        assertNotNull(u.getFriends());
        assertEquals(true, (u.getId() >= 0));
        assertEquals(true, sessionManager.getSession(u).isActive());
        assertNotNull(sessionManager.getSession(u).getBeginTime());
        //user doesnt exist
        User u2 = sessionManager.logIn("dfief", "");
        assertNull(u2);

    }

    @Test
    void logOut() {
        initManager();
        //user exists
        User u = sessionManager.logIn("celina@email.com", "1234");
        Session s = sessionManager.getSession(u);
        sessionManager.logOut(s);
        assertEquals(false, s.isActive());
        assertNotNull(s.getEndTime());

    }

    @Test
    void registerNewUser() {
        initManager();
        User u = new User("Billie Holiday", "1234", "billie@email.com", "Gloomy Sunday");
        int id = sessionManager.registerNewUser(u);
        assertEquals(true, (id >= 0));
        int id2 = sessionManager.registerNewUser(u);
        assertEquals(-2, id2);
    }

}