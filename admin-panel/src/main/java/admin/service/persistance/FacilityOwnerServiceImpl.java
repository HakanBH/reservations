package admin.service.persistance;

import admin.exceptions.ExistingUserException;
import admin.model.*;
import admin.repository.FacilityOwnerRepository;
import admin.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;

import static java.lang.Boolean.TRUE;

/**
 * Created by Hakan_Hyusein on 7/27/2016.
 */
@Service("facilityOwnerService")
@Transactional
public class FacilityOwnerServiceImpl implements FacilityOwnerService {

    @Autowired
    FacilityOwnerRepository facilityOwnerRepository;
    @Autowired
    private AddressService addressService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private CommentService commentService;
    @Autowired
    private FacilityService facilityService;
    @Autowired
    private ReservationService reservationService;

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public FacilityOwner save(FacilityOwner owner) throws ExistingUserException {
        if (facilityOwnerRepository.findByEmail(owner.getEmail()) != null) {
            throw new ExistingUserException("User with this email already exists");
        }
        if (owner.getAddress() != null) {
            addressService.save(owner.getAddress());
        }
        owner.setRole(roleRepository.findByRoleType(RoleType.ROLE_FACILITY_OWNER));

        String hashedPassword = passwordEncoder.encode(owner.getPassword());
        owner.setPassword(hashedPassword);

        File folderPath = new File(UserServiceImpl.IMAGES_FOLDER + File.separator + owner.getEmail());
        folderPath.mkdirs();

        return facilityOwnerRepository.save(owner);
    }

    @Override
    public FacilityOwner findById(int id) {
        return facilityOwnerRepository.findOne(id);
    }

    @Override
    public List<FacilityOwner> findAll() {
        return (List<FacilityOwner>) facilityOwnerRepository.findAll();
    }

    @Override
    public FacilityOwner updateOwner(FacilityOwner owner) {
        FacilityOwner entity = facilityOwnerRepository.findOne(owner.getId());
        if (entity != null) {
            if (owner.getAddress() != null) {
                addressService.updateAddress(owner.getAddress());
            }

            entity.setFirstName(owner.getFirstName());
            entity.setLastName(owner.getLastName());
            entity.setPhoneNumber(owner.getPhoneNumber());
            entity.setEmail(owner.getEmail());
            entity.setWebsite(owner.getWebsite());
            entity.setRating(owner.getRating());

            facilityOwnerRepository.save(entity);
        }

        return entity;
    }

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
    public List<FacilityOwner> deleteList(List<Integer> ownersId) {
        for (Integer id : ownersId) {
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
        return (List<FacilityOwner>) facilityOwnerRepository.findAll();
    }


    @Override
    public List<FacilityOwner> findAllByOrderByRatingDesc() {
        return facilityOwnerRepository.findAllByOrderByRatingDesc();
    }
}
