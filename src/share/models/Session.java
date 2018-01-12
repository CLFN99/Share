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

public class Session implements Serializable {

    private int id;
    private Date beginTime;
    private Date endTime;
    private boolean active;
    private User user;


    public Session(User u) {
        this.user = u;
        this.id = -1;
        this.beginTime = new Date();
        this.active = true;
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

    public void setEndTime(Date time){this.endTime = time;}

    public void setId(int id){this.id = id;}

    public void setActive(boolean active) {
        this.active = active;
    }

}
