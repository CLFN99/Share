package share.interfaces;

import share.models.Post;

import java.util.List;

public interface IFeed {
    /**
     * refreshes the feed and gets all the post of the people the user follows
     * calls IMain.refreshFeed
     * @return the posts the share.tests.server gave
     */
    List<Post> refresh(IMain manager);
}
