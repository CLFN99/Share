package interfaces;

import java.rmi.Remote;

public interface ISession extends Remote {
    boolean logIn();
    boolean logOut();
    void setId(String id);
}
