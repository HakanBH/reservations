package admin.service.persistance;

import admin.exceptions.ExistingUserException;
import admin.exceptions.WrongEmailOrPassword;
import admin.model.Comment;
import admin.model.Reservation;
import admin.model.RoleType;
import admin.model.User;
import admin.repository.ReservationRepository;
import admin.repository.RoleRepository;
import admin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;

import static java.lang.Boolean.TRUE;

/**
 * Created by Toncho_Petrov on 7/14/2016.
 */
@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {
    public static final String IMAGES_FOLDER = "/opt/reservations/backend/images/";

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AddressService addressService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private RoleRepository roleRepository;

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User findById(int id) {
        return userRepository.findOne(id);
    }

    @Override
    public List<User> findAll() {
        return (List<User>) userRepository.findAll();
    }

    @Override
    public User save(User user) throws ExistingUserException {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new ExistingUserException("User with this email already exists");
        }
        if (user.getAddress() != null) {
            addressService.save(user.getAddress());
        }
        user.setRole(roleRepository.findByRoleType(RoleType.ROLE_USER));

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        File folderPath = new File(IMAGES_FOLDER + user.getEmail());
        folderPath.mkdirs();

        return userRepository.save(user);
    }

    @Override
    public User findByEmailAndPassword(String email, String password) throws WrongEmailOrPassword {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new WrongEmailOrPassword("Wrong email or password!");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new WrongEmailOrPassword("Wrong email or password!");
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
                if (userToUpdate.getAddress() != null) {
                    user.getAddress().setId(userToUpdate.getAddress().getId());
                }
                addressService.updateAddress(user.getAddress());
            }

            userToUpdate.setAddress(user.getAddress());
            userToUpdate.setFirstName(user.getFirstName());
            userToUpdate.setLastName(user.getLastName());
            userToUpdate.setPhoneNumber(user.getPhoneNumber());
            userToUpdate.setDeleted(user.isDeleted());

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
    public List<User> deleteList(List<Integer> usersId) {
        for (Integer id : usersId) {
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
        return (List<User>) userRepository.findAll();
    }


    @Override
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    @Override
    public List<Reservation> getUserReservations(int userId) {
        return reservationRepository.findByUserId(userId);
    }

    @Override
    public String updateUserPassword(Integer id, String oldPassword, String newPassword, String confirmNewPassword) {
        User user = userRepository.findOne(id);
        if (passwordEncoder.matches(oldPassword, user.getPassword()) && newPassword.equals(confirmNewPassword)) {
            String hashedNewPass = passwordEncoder.encode(newPassword);
            user.setPassword(hashedNewPass);
            updateUserById(user.getId(), user);
            return "password change success";
        } else {
            return "password change fail!";
        }
    }

    @Override
    public String updateUserForgottenPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email);
        String hashedNewPass = passwordEncoder.encode(newPassword);
        user.setPassword(hashedNewPass);
        updateUserById(user.getId(), user);
        return "password change succsess";

    }
}
