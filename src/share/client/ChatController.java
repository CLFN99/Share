package share.client;

import share.models.Chat;
import share.models.Message;
import share.server.IRemotePropertyListener;
import share.server.IRemotePublisher;

import java.beans.PropertyChangeEvent;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ChatController extends UnicastRemoteObject implements IRemotePropertyListener {
    private ChatScreenController screenController;
    private Chat chat;
    private IRemotePublisher publisher;

    public ChatController(IRemotePublisher publisher, ChatScreenController screenController, Chat chat) throws RemoteException{
        this.publisher = publisher;
        this.chat = chat;
        this.publisher = publisher;
        this.screenController = screenController;
        try {
            publisher.subscribePropertyListener(this, ("chat"+ chat.getId()));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent var1) throws RemoteException {
        chat.getMessages().add((Message) var1.getNewValue());
        screenController.setMessages(chat.getMessages());
    }

    public void unsubscribe(){
        try {
            publisher.unsubscribeRemoteListener(this, ("chat"+ chat.getId()) );
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
