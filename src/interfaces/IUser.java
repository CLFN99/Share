package interfaces;

import models.User;

public interface IUser {
    void addFriend(User u);
    void changeBio(String txt);
}
