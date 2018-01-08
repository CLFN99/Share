package interfaces;

import models.Post;

import java.util.List;

public interface IFeed {
    /**
     * refreshes the feed and gets all the post of the people the user follows
     * calls IMain.refreshFeed
     * @return the posts the server gave
     */
    List<Post> refresh(IMain manager);
}
