package models;

import interfaces.*;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Session extends UnicastRemoteObject implements Serializable, ISession  {

    private String id;
    private Date beginTime;
    private Date endTime;
    private boolean active;
    private User user;
    private IMain manager;

    public Session(User user) throws RemoteException {
        super();
        this.user = user;
        manager = new Manager();
        logIn();
    }

    @Override
    public boolean logIn() {
        if(manager.addSession(this)){
            this.beginTime = new Date();
            this.active = true;
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public boolean logOut() {
        if(manager.removeSession(this)){
            this.endTime = new Date();
            this.active = false;
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
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
}
