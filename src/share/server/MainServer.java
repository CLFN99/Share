package share.server;

import share.interfaces.IMain;
import share.interfaces.*;
import share.models.Manager;

import java.applet.Applet;
import java.rmi.RemoteException;

import static javafx.application.Application.launch;

public class MainServer extends Applet {
    public static void main(String[] args) {
        try {
            IMain manager = new Manager();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


}
