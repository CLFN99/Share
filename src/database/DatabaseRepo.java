package database;

import models.*;

import java.util.List;

public class DatabaseRepo {
    /**
     * this is what the client and servers communicate with when they want to access the database
     */

    private IDatabase context;

    public DatabaseRepo(IDatabase context) {
        this.context = context;
    }

    public List<Feed> getFeeds(){
        return context.getFeeds();
    }
}
