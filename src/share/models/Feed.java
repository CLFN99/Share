package share.models;

import share.interfaces.IFeed;
import share.interfaces.IMain;
import share.server.IRemotePropertyListener;
import share.server.IRemotePublisher;

import java.beans.PropertyChangeEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class Feed implements IFeed, IRemotePropertyListener {
    private List<Post> posts;
    private User user;
    private int Id;
    private IMain manager;
    private IRemotePublisher publisher;

    /**
     * creates new feed
     * @param user the user to which the feed belongs
     */
    public Feed(User user){
        posts = new ArrayList<>();
        this.user = user;

        try {
            publisher.subscribePropertyListener(this, "feed" + this.Id);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public void setId(int id) {
        Id = id;
    }

    public int getId() {
        return Id;
    }

    /**
     * gets the list of posts of the feed
     * @return this.posts
     */
    public List<Post> getPosts(){return this.posts;}

    /**
     * sets the list of post
     * @param posts the posts obtained from the share.tests.database
     */
    public void setPosts(List<Post> posts){
        this.posts = posts;
    }
    /**
     * gets the user the feed belongs to
     * @return this.user
     */
    public User getUser(){return this.user;}

    /**
     * refreshes the feed
     * cals IMain.refreshFeed
     * adds the posts the share.tests.server provided to this.posts
     * @return the list of posts
     */
    @Override
    public List<Post> refresh(IMain manager) {
        try {
            this.posts = manager.refreshFeed(this.user.getEmail());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return posts;
    }

    public void initManager(IMain manager){
        this.manager = manager;
    }
    public void initPublisher(IRemotePublisher publisher){this.publisher = publisher;}

    @Override
    public void propertyChange(PropertyChangeEvent var1) throws RemoteException {
        //if the post has been changed
        if(var1.getOldValue() != null){
            Post post = (Post) var1.getNewValue();
            for(Post p : this.posts){
                if(p.getId() == post.getId()){
                    p.setText(post.getText());
                }
            }
        }
        //if there's a new post
        else if(var1.getOldValue() == null && (int)var1.getOldValue() != -1){
            this.posts.add((Post)var1.getNewValue());
        }
        else if((int)var1.getOldValue() == -1){
            this.posts.remove(var1.getNewValue());
        }

    }
}
