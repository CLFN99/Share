package share.server;


import java.beans.PropertyChangeEvent;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.EventListener;

public interface IRemotePropertyListener extends EventListener, Remote {
    void propertyChange(PropertyChangeEvent var1) throws RemoteException;
}
