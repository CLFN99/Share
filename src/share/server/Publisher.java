package share.server;

import java.beans.PropertyChangeEvent;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Publisher extends UnicastRemoteObject implements IRemotePublisher {
    private Map<String, List<IRemotePropertyListener>> propertyListeners;
    private ExecutorService pool;

    public Publisher() throws RemoteException {
        this.propertyListeners = Collections.synchronizedMap(new HashMap());
        this.propertyListeners.put(null, Collections.synchronizedList(new ArrayList()));
        this.pool = Executors.newFixedThreadPool(10);
    }

    public void registerProperty(String property)  throws RemoteException{
        if (property.equals("")) {
            throw new RuntimeException("a property cannot be an empty string");
        } else if (!this.propertyListeners.containsKey(property)) {
            this.propertyListeners.put(property, Collections.synchronizedList(new ArrayList()));
        }
    }

    public void subscribePropertyListener(IRemotePropertyListener listener, String property) throws RemoteException {
        (this.propertyListeners.get(property)).add(listener);
    }

    public void inform(String property, Object oldValue, Object newValue)  throws RemoteException{
        ArrayList listenersToBeInformed = new ArrayList();
        if (property != null) {
            listenersToBeInformed.addAll(this.propertyListeners.get(property));
           // listenersToBeInformed.addAll(this.propertyListeners.get(null));
        } else {
            List<String> keyset = new ArrayList(this.propertyListeners.keySet());
            Iterator var6 = keyset.iterator();

            while(var6.hasNext()) {
                String key = (String)var6.next();
                listenersToBeInformed.addAll((Collection)this.propertyListeners.get(key));
            }
        }

        Iterator var9 = listenersToBeInformed.iterator();

        while(var9.hasNext()) {
            IRemotePropertyListener listener = (IRemotePropertyListener)var9.next();
            PropertyChangeEvent event = new PropertyChangeEvent(this, property, oldValue, newValue);
            Publisher.InformListenerRunnable informListenerRunnable = new Publisher.InformListenerRunnable(listener, event);
            this.pool.execute(informListenerRunnable);
        }
    }


    private class InformListenerRunnable implements Runnable {
        IRemotePropertyListener listener;
        PropertyChangeEvent event;

        public InformListenerRunnable(IRemotePropertyListener listener, PropertyChangeEvent event) {
            this.listener = listener;
            this.event = event;
        }

        public void run() {
                try {
                    listener.propertyChange(this.event);
                } catch (RemoteException var3) {
                    System.out.println("nope");
                }
        }
    }
}
