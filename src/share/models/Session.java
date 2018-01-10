package share.models;

import share.interfaces.IMain;
import share.interfaces.ISession;
import share.database.Database;
import share.database.DatabaseRepo;
import share.interfaces.*;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;

public class Session extends UnicastRemoteObject implements Serializable, ISession {

    private int id;
    private Date beginTime;
    private Date endTime;
    private boolean active;
    private User user;
    private String email;
    private String password;
    private transient IMain manager;
    private transient DatabaseRepo repo = new DatabaseRepo(new Database());

    public Session(String email, String password) throws RemoteException {
        super();
        this.email = email;
        this.password = password;
        this.id = -1;
        logIn();
    }

    @Override
    public boolean logIn() {
        this.user = repo.logIn(email, password);
        this.beginTime = new Date();
        this.active = true;
        if(this.user != null){
            try {
                if(manager.addSession(this)){
                    return true;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean logOut() {
        try {
            if(manager.removeSession(this)){
                this.endTime = new Date();
                this.active = false;
                return true;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int registerNewUser(User u) {
        int id = repo.saveUser(u);
        return id;
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public boolean isActive() {
        return active;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void initManager(IMain manager){
        this.manager = (Manager) manager;
    }
}
