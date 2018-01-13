package share.interfaces;

import share.models.Session;
import share.models.User;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ISession extends Remote {
    User logIn(String username, String password) throws RemoteException;
    boolean logOut(Session session) throws RemoteException;
    int registerNewUser(User u) throws RemoteException;
    Session getSession(User u) throws RemoteException;
}
