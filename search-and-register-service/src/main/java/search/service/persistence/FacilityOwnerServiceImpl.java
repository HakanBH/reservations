package search.service.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import search.exceptions.ExistingUserException;
import search.exceptions.WrongEmailOrPassword;
import search.model.*;
import search.repository.FacilityOwnerRepository;
import search.repository.RoleRepository;

import java.io.File;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Created by Hakan_Hyusein on 7/27/2016.
 */
@Service("facilityOwnerService")
@Transactional
public class FacilityOwnerServiceImpl implements FacilityOwnerService {
    private static final boolean NOT_DELETED_FIELDAS = FALSE;

    @Autowired
    FacilityOwnerRepository facilityOwnerRepository;
    @Autowired
    private AddressService addressService;
    @Autowired
    private FacilityService facilityService;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private RoleRepository roleRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public FacilityOwner save(FacilityOwner owner) throws ExistingUserException {
        if (facilityOwnerRepository.findByEmail(owner.getEmail()) == null) {
            if (owner.getAddress() != null) {
                addressService.save(owner.getAddress());
            }
            owner.setRole(roleRepository.findByRoleType(RoleType.ROLE_FACILITY_OWNER));

            String hashedPassword = passwordEncoder.encode(owner.getPassword());
            owner.setPassword(hashedPassword);

            File folderPath = new File(UserServiceImpl.IMAGES_FOLDER + File.separator + owner.getEmail());
            folderPath.mkdirs();

            return facilityOwnerRepository.save(owner);
        } else {
            throw new ExistingUserException(UserServiceImpl.EXISTING_USER_ERROR_MSG);
        }
    }

    @Override
    public FacilityOwner findById(int id) {
        return facilityOwnerRepository.findOne(id);
    }

//    @Override
//    public List<FacilityOwner> findAll() {
//        return (List<FacilityOwner>) facilityOwnerRepository.findAll();
//    }


    public List<FacilityOwner> findAll() {
        return facilityOwnerRepository.findByIsDeleted(NOT_DELETED_FIELDAS);
    }

    @Override
    public FacilityOwner updateOwner(FacilityOwner owner) {
        FacilityOwner entity = facilityOwnerRepository.findOne(owner.getId());
        if (entity != null) {
            if (owner.getAddress() != null) {
                Address newAddress = addressService.updateAddress(entity.getAddress(), owner.getAddress());
                owner.setAddress(newAddress);
            }

            entity.setFirstName(owner.getFirstName());
            entity.setLastName(owner.getLastName());
            entity.setPhoneNumber(owner.getPhoneNumber());
            entity.setEmail(owner.getEmail());
            entity.setWebsite(owner.getWebsite());
            entity.setRating(owner.getRating());
            if (owner.getVerified() != null) {
                entity.setVerified(owner.getVerified());
            }

            facilityOwnerRepository.save(entity);
        }

        return entity;
    }

    @Override
    public FacilityOwner findByEmail(String email) {
        return facilityOwnerRepository.findByEmail(email);
    }

//    @Override
//    public void deleteUser(int id) {
//        facilityOwnerRepository.delete(id);
//    }

    @Override
    public void deleteUser(int id) {

        FacilityOwner entity = facilityOwnerRepository.findOne(id);
        List<Facility> facilities = facilityService.findByFacilityOwnerId(entity.getId());
        List<Reservation> reservations = reservationService.findByUserId(entity.getId());
        List<Comment> comments = commentService.findByUserId(id);
        entity.setDeleted(TRUE);
        facilityOwnerRepository.save(entity);
        for (Facility facility : facilities) {
            facilityService.deleteFacility(facility.getId());
        }
        for (Reservation reservation : reservations) {
            reservationService.delete(reservation.getId());
        }
        for (Comment comment : comments) {
            commentService.deleteComment(comment.getId());
        }
    }

    @Override
    public List<FacilityOwner> findAllByOrderByRatingDesc() {
        return facilityOwnerRepository.findAllByOrderByRatingDesc();
    }

    @Override
    public FacilityOwner findByEmailAndPassword(String email, String password) throws WrongEmailOrPassword {
        FacilityOwner user = facilityOwnerRepository.findByEmail(email);
        if (user == null) {
            throw new WrongEmailOrPassword("Wrong email or password!");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new WrongEmailOrPassword("Wrong email or password!");
        }
        return user;
    }
}
