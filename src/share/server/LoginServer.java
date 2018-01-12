package share.server;

import javafx.application.Application;
import javafx.stage.Stage;
import share.models.SessionManager;

import java.applet.Applet;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static javafx.application.Application.launch;

public class LoginServer extends Applet
{
    public static void main(String[] args) {
        try {
            SessionManager sm = new SessionManager();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        launch(args);
    }


}
