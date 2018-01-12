package share.models;

import share.database.Database;
import share.database.DatabaseRepo;
import share.interfaces.IMain;
import share.interfaces.ISession;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;

public class SessionManager extends UnicastRemoteObject implements ISession {
    /**
     * this manages sessions
     * is called by client application, started in LoginServer application
     * it creates a registry on port 8099
     * also creates users and saves them
     * logs user in and out
     */

    private IMain manager;
    private DatabaseRepo repo = new DatabaseRepo(new Database());
    private Registry managerRegistry;

    public SessionManager() throws RemoteException{
        Registry registry = LocateRegistry.createRegistry(8099);
        System.out.println("Server active");
        registry.rebind("sessionManager", this);
        System.out.println("session manager bound");
        connectToMain();

    }

    /**
     * connects to the main server and gets the manager
     * @throws RemoteException
     */

    private void connectToMain() throws RemoteException{
        managerRegistry = LocateRegistry.getRegistry("127.0.0.1", 1099);
        System.out.println("connected to main server");
        try {
            manager = (IMain) managerRegistry.lookup("manager");
            System.out.println("bound to manager");
        } catch (NotBoundException e) {
            e.printStackTrace();
            manager = null;
        }
    }

    /**
     * verifies input in database and creates user
     * creates new session
     * adds said session to list of active sessions on manager
     * @return
     */
    @Override
    public User logIn(String username, String password){
        User u = repo.logIn(username, password);
        if(u != null){
            Session s = new Session(u);
            try {
                if(manager.addSession(s)){
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return u;
        }

        return null;
    }

    /**
     * removes session
     * @param session session to remove
     * @return true if succeeded, false if something went wrong
     */
    @Override
    public boolean logOut(Session session) {
        try {
            if(manager.removeSession(session)){
                session.setEndTime(new Date());
                session.setActive(false);
                return true;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public int registerNewUser(User u) {
        int id = repo.saveUser(u);
        return id;
    }
}