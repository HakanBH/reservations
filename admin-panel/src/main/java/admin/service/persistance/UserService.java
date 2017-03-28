package admin.service.persistance;

import admin.exceptions.ExistingUserException;
import admin.exceptions.WrongEmailOrPassword;
import admin.model.Reservation;
import admin.model.User;

import java.util.List;

/**
 * Created by Toncho_Petrov on 7/14/2016.
 */
public interface UserService {

    User findById(int id);

    List<User> findAll();

    User save(User user) throws ExistingUserException;

    User updateUserById(Integer userToUpdate, User user);

    User findByEmailAndPassword(String email, String password) throws WrongEmailOrPassword;

    User findByEmail(String email);

    User findByGoogleEmail(String email);

    User findByFacebookEmail(String email);

    void deleteUser(int id);

    List<User> deleteList(List<Integer> usersId);

    void deleteUser(User user);

    List<Reservation> getUserReservations(int userId);

    String updateUserPassword(Integer id, String oldPassword, String newPassword, String confirmNewPassword);

    public String updateUserForgottenPassword(String email, String newPassword);


}
