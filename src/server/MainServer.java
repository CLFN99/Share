package server;

import interfaces.*;
import models.Manager;

import java.rmi.RemoteException;

import static javafx.application.Application.launch;


public class MainServer {
    public static void main(String[] args) {
        launch(args);
        try {
            IMain manager = new Manager();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    //TODO: create server, registry and such, make method for connecting clients and such
}
