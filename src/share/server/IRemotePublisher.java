package share.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRemotePublisher extends Remote{
    void registerProperty(String property) throws RemoteException;

    void subscribePropertyListener(IRemotePropertyListener listener, String property) throws RemoteException;

    void inform(String property, Object oldValue, Object newValue) throws RemoteException;

}
