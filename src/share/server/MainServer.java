package share.server;

import share.interfaces.IMain;
import share.interfaces.*;
import javafx.application.Application;
import javafx.stage.Stage;
import share.models.Manager;

import java.rmi.RemoteException;

import static javafx.application.Application.launch;


public class MainServer extends Application {
    public static void main(String[] args) {
        try {
            IMain manager = new Manager();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {

    }

}
