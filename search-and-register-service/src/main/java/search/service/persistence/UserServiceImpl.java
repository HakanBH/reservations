package search.service.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import search.exceptions.ExistingUserException;
import search.exceptions.UnexistingUserException;
import search.exceptions.WrongEmailOrPassword;
import search.exceptions.WrongPasswordException;
import search.model.*;
import search.model.dto.ChangePasswordDTO;
import search.repository.RoleRepository;
import search.repository.UserRepository;

import java.io.File;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Created by Toncho_Petrov on 7/14/2016.
 */
@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {
    //    public static final String IMAGES_FOLDER = "C:" + File.separator + "images" + File.separator;
    public static final String IMAGES_FOLDER = "/opt/reservations/backend/images/";
    public static final String WRONG_EMAIL_OR_PASSWORD = "Wrong email or password!";
    public static final String EXISTING_USER_ERROR_MSG = "User with this email already exists";
    private static final boolean NOT_DELETED_FIELDS = FALSE;

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AddressService addressService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private CommentService commentService;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User findById(int id) {
        return userRepository.findOne(id);
    }

    public List<User> findAll() {
        return userRepository.findByIsDeleted(NOT_DELETED_FIELDS);
    }

    @Override
    public User save(User user) throws ExistingUserException {
        if (userRepository.findByEmail(user.getEmail()) == null) {
            if (user.getAddress() != null) {
                addressService.save(user.getAddress());
            }
            user.setRole(roleRepository.findByRoleType(RoleType.ROLE_USER));

            String hashedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(hashedPassword);

            File folderPath = new File(IMAGES_FOLDER + user.getEmail());
            folderPath.mkdirs();

            return userRepository.save(user);
        } else {
            throw new ExistingUserException(EXISTING_USER_ERROR_MSG);
        }
    }

    @Override
    public User findByEmailAndPassword(String email, String password) throws WrongEmailOrPassword {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new WrongEmailOrPassword(WRONG_EMAIL_OR_PASSWORD);
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new WrongEmailOrPassword(WRONG_EMAIL_OR_PASSWORD);
        }
        return user;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User findByGoogleEmail(String email) {
        return userRepository.findByGoogleEmail(email);
    }

    @Override
    public User findByFacebookEmail(String email) {
        return userRepository.findByFacebookEmail(email);
    }

    @Override
    public User updateUserById(Integer id, User user) {
        User userToUpdate = userRepository.findOne(id);
        if (userToUpdate != null) {
            if (user.getAddress() != null) {
                Address newAddress = addressService.updateAddress(userToUpdate.getAddress(), user.getAddress());
                userToUpdate.setAddress(newAddress);
            }

            userToUpdate.setFirstName(user.getFirstName());
            userToUpdate.setLastName(user.getLastName());
            userToUpdate.setPhoneNumber(user.getPhoneNumber());
            if (user.getVerified() != null) {
                userToUpdate.setVerified(user.getVerified());
            }

            userRepository.save(userToUpdate);
        }
        return userToUpdate;
    }

    @Override
    public void deleteUser(int id) {
        User entity = userRepository.findOne(id);
        List<Comment> comments = commentService.findByUserId(id);
        List<Reservation> reservations = reservationService.findByUserId(id);
        entity.setDeleted(TRUE);
        userRepository.save(entity);
        for (Comment comment : comments) {
            commentService.deleteComment(comment.getId());
        }
        for (Reservation reservation : reservations) {
            reservationService.delete(reservation.getId());
        }
    }

    @Override
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    @Override
    public List<Reservation> getUserReservations(int userId) {
        return reservationService.findByUserId(userId);
    }

    @Override
    public void changePassword(Integer id, String oldPassword, String newPassword, String confirmNewPassword) throws WrongPasswordException {
        User user = userRepository.findOne(id);
        if (passwordEncoder.matches(oldPassword, user.getPassword()) && newPassword.equals(confirmNewPassword)) {
            String hashedNewPass = passwordEncoder.encode(newPassword);
            user.setPassword(hashedNewPass);
            updateUserById(user.getId(), user);
        } else {
            throw new WrongPasswordException("The given password is incorrect.");
        }
    }

    @Override
    public void updateUserForgottenPassword(Integer id, ChangePasswordDTO passwordDTO) throws UnexistingUserException {
        User user = userRepository.findOne(id);
        if (user != null) {
            if (passwordDTO.getNewPassword().equals(passwordDTO.getConfirmPassword())) {
                String hashedNewPass = passwordEncoder.encode(passwordDTO.getNewPassword());
                user.setPassword(hashedNewPass);
                updateUserById(user.getId(), user);
            } else {
                throw new WrongPasswordException("Password mismatch. Confirm password is not equals to the new password!");
            }
        } else {
            throw new UnexistingUserException("Current user does not exists");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByEmail(username);
    }
}
