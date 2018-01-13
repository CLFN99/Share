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
        createClient("127.0.0.1",1099);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LoginScreen.fxml"));
        Parent root1 = null;
        root1 = (Parent) fxmlLoader.load();
        LoginController controller = fxmlLoader.getController();
        controller.setManagers(this.manager, this.sessionManager);
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        stage.show();

//        FXMLLoader loader = getClass().getResource("LoginScreen.fxml");
//        Parent root = FXMLLoader.load(getClass().getResource("LoginScreen.fxml"));
//        LoginController controller =
//        primaryStage.setTitle("Login");
//        primaryStage.setScene(new Scene(root, 275, 300));
//        primaryStage.show();

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
