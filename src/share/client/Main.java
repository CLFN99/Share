package share.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import share.server.IRemotePublisher;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main extends Application {
    private IRemotePublisher publisherListner;
    private Registry registry = null;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
        createClient("127.0.0.1",1099);
    }


    public static void main(String[] args) {
        launch(args);
    }

    private void createClient(String ipAddress, int portNumber) {
        //Locate registry at IP address and port number
        try {
            registry = LocateRegistry.getRegistry(ipAddress, portNumber);
            System.out.println("share.tests.client connected");
        } catch (RemoteException ex) {

            registry = null;
        }

        //init publisher
        try {
            publisherListner = (IRemotePublisher) registry.lookup("chatPublisher");
            System.out.println("publisher init");
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }
}
