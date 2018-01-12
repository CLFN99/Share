package share.interfaces;

import share.models.Session;
import share.models.User;

import java.rmi.Remote;

public interface ISession extends Remote {
    User logIn(String username, String password);
    boolean logOut(Session session);
    int registerNewUser(User u);
}
