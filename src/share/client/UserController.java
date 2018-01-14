package share.client;

import com.sun.org.apache.bcel.internal.generic.IREM;
import share.models.Post;
import share.models.User;
import share.server.IRemotePropertyListener;
import share.server.IRemotePublisher;

import java.beans.PropertyChangeEvent;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class UserController extends UnicastRemoteObject implements IRemotePropertyListener {
    private User currentUser;
    private IRemotePublisher publisher;
    private MainScreenController controller;

    public UserController(IRemotePublisher publisher, MainScreenController controller, User currentUser) throws RemoteException{
        this.publisher = publisher;
        this.controller = controller;
        this.currentUser = currentUser;
        try {
            //subscribing to changes to itself
            publisher.subscribePropertyListener(this, "user"+currentUser.getId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent var1) throws RemoteException {
        if(var1.getNewValue().getClass() == User.class){
            User u = (User) var1.getNewValue();
            currentUser.changeBio(u.getBio());
            currentUser.setFriends(u.getFriends());
            controller.setUser(currentUser);
        }
    }

    public void unsubscribe(){
        try {
            publisher.unsubscribeRemoteListener(this, ("user"+ currentUser.getId()) );
            for(User u : currentUser.getFriends()){
                //subscribe user's feed to friends feed
                publisher.unsubscribeRemoteListener(this, ("feed"+u.getFeed().getId()));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
