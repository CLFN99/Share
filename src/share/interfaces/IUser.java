package share.interfaces;

import share.models.User;

public interface IUser {
    void addFriend(User u);
    void changeBio(String txt);
}
