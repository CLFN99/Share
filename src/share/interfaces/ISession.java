package share.interfaces;

import share.models.User;

import java.rmi.Remote;

public interface ISession extends Remote {
    boolean logIn();
    boolean logOut();
    void setId(int id);
    int registerNewUser(User u);
}
