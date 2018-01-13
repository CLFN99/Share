package share.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import share.interfaces.*;
import share.server.IRemotePublisher;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main extends Application {
    private IRemotePublisher publisherListner;
    private Registry mainRegistry = null;
    private Registry sessionRegistry = null;
    private IMain manager;
    private ISession sessionManager;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
        createClient("127.0.0.1",1099);
        manager.searchUser("d");
    }


    public static void main(String[] args) {
        launch(args);
    }

    private void createClient(String ipAddress, int portNumber) {
        //Locate registry at IP address and port number
        try {
            mainRegistry = LocateRegistry.getRegistry(ipAddress, portNumber);
            System.out.println("client connected w main server");
            sessionRegistry = LocateRegistry.getRegistry(ipAddress, 8099);
            System.out.println("client connected w login server");
        } catch (RemoteException ex) {
            mainRegistry = null;
            sessionRegistry = null;
        }

        //init publisher
        try {
            publisherListner = (IRemotePublisher) mainRegistry.lookup("publisher");
            System.out.println("publisher init");
            manager = (IMain) mainRegistry.lookup("manager");
            System.out.println("manager init");

            sessionManager = (ISession) sessionRegistry.lookup("sessionManager");
            System.out.printf("session manager init");
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }
}
