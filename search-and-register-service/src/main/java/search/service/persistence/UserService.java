package search.service.persistence;

import org.springframework.security.core.userdetails.UserDetailsService;
import search.exceptions.ExistingUserException;
import search.exceptions.UnexistingUserException;
import search.exceptions.WrongEmailOrPassword;
import search.exceptions.WrongPasswordException;
import search.model.Reservation;
import search.model.User;
import search.model.dto.ChangePasswordDTO;

import java.util.List;

/**
 * Created by Toncho_Petrov on 7/14/2016.
 */
public interface UserService extends UserDetailsService {

    User findById(int id);

    List<User> findAll();

    User save(User user) throws ExistingUserException;

    User updateUserById(Integer userToUpdate, User user);

    User findByEmailAndPassword(String email, String password) throws WrongEmailOrPassword;

    User findByEmail(String email);

    User findByGoogleEmail(String email);

    User findByFacebookEmail(String email);

    void deleteUser(int id);

    void deleteUser(User user);

    List<Reservation> getUserReservations(int userId);

    void changePassword(Integer id, String oldPassword, String newPassword, String confirmNewPassword) throws WrongPasswordException;

    void updateUserForgottenPassword(Integer id, ChangePasswordDTO passwordDTO) throws UnexistingUserException;
}
