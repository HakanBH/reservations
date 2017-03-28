package search.service.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import search.model.*;
import search.repository.FacilityRepository;
import search.repository.FacilityTypeRepository;
import search.repository.WorkingHoursRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Created by Trayan_Muchev on 7/21/2016.
 */

@Service("facilityService")
@Transactional
public class FacilityServiceImpl implements FacilityService {
    private static final boolean NOT_DELETED_FIELDS = FALSE;

    @Autowired
    private FacilityRepository facilityRepository;
    @Autowired
    private WorkingHoursRepository workingHoursRepository;
    @Autowired
    private FacilityTypeRepository facilityTypeRepository;
    @Autowired
    private AddressService addressService;
    @Autowired
    private FacilityOwnerService facilityOwnerService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private PictureService pictureService;

    @Override
    public Facility save(Facility facility) {
        setWorkingHours(facility, facility);

        if (facility.getFacilityType() != null) {
            FacilityType facilityType = facilityTypeRepository.findByType(facility.getFacilityType().getType());
            if (facilityType == null) {
                facilityTypeRepository.save(facility.getFacilityType());
            } else {
                facility.setFacilityType(facilityType);
            }
        }

        if (facility.getAddress() != null) {
            addressService.save(facility.getAddress());
        }

        Facility entity = facilityRepository.save(facility);
        FacilityOwner owner = facilityOwnerService.findById(entity.getFacilityOwner().getId());
        File folderPath = new File(UserServiceImpl.IMAGES_FOLDER + owner.getEmail() + File.separator + entity.getId());
        folderPath.mkdirs();

        return entity;
    }

    @Override
    public Facility findById(Integer id) {
        Facility facility = facilityRepository.findOne(id);
        facility.setReservations(reservationService.findByFacilityId(id));
        facility.setComments(commentService.findByFacilityId(id));
        return facility;
    }

    @Override
    public List<Facility> deleteFacility(Integer id) {
        Facility entity = facilityRepository.findOne(id);
        List<Reservation> reservations = reservationService.findByFacilityId(id);
        List<Comment> comments = commentService.findByFacilityId(id);
        List<Picture> pictures = pictureService.findByFacilityId(id);
        entity.setDeleted(TRUE);
        facilityRepository.save(entity);
        for (Reservation reservation : reservations) {
            reservationService.delete(reservation.getId());
        }
        for (Comment comment : comments) {
            commentService.deleteComment(comment.getId());
        }
        for (Picture picture : pictures) {
            pictureService.delete(picture.getId());
        }
        return facilityRepository.findByFacilityOwnerId(entity.getFacilityOwner().getId(), NOT_DELETED_FIELDS);
    }

    @Override
    public List<Facility> findAll() {
        List<Facility> facilities = facilityRepository.findByIsDeleted(NOT_DELETED_FIELDS);
        for (Facility facility : facilities) {
            if (facility.getPictures() == null || facility.getPictures().isEmpty()) {
                List<Picture> pics = new ArrayList<>();
                Picture pic = pictureService.findOne(1);
                pics.add(pic);
                facility.setPictures(pics);
            }
            facility.setReservations(reservationService.findByFacilityId(facility.getId()));
            facility.setComments(commentService.findByFacilityId(facility.getId()));
        }
        return facilities;
    }

    @Override
    public List<Facility> userFavoriteFacilities(Integer userId) {
        List<Facility> facilities = facilityRepository.findByIsDeleted(NOT_DELETED_FIELDS);
        for (Facility facility : facilities) {
            if (facility.getPictures() == null || facility.getPictures().isEmpty()) {
                List<Picture> pics = new ArrayList<>();
                Picture pic = pictureService.findOne(1);
                pics.add(pic);
                facility.setPictures(pics);
            }
            facility.setReservations(reservationService.findByFacilityId(facility.getId()));
            facility.setComments(commentService.findByFacilityId(facility.getId()));
            List<Reservation> reservations = reservationService.findByUserIdAndFacilityId(userId, facility.getId());
            facility.setNumberOfUserReservations(reservations.size());
            facility.getNumberOfUserReservations();

        }
        return facilities;
    }

    @Override
    public Facility updateFacility(Integer facilityToUpdate, Facility facility) {
        Facility entity = facilityRepository.findOne(facilityToUpdate);
        if (entity != null) {
            if (facility.getAddress() != null) {
                Address newAddress = addressService.updateAddress(entity.getAddress(), facility.getAddress());
                facility.setAddress(newAddress);
            }

            if (facility.getComments() != null) {
                Comment c = facility.getComments().get(facility.getComments().size() - 1);
                User user = c.getUser();
                c.setUser(user);
                c.setFacility(facility);
                commentService.save(c);
            }
            setWorkingHours(facility, entity);
            entity.setFacilityType(facility.getFacilityType());
            entity.setPrice(facility.getPrice());
            entity.setWeekendsPrice(facility.getWeekendsPrice());
            entity.setDiscountPrice(facility.getDiscountPrice());
            entity.setDescription(facility.getDescription());
            entity.setName(facility.getName());
            return facilityRepository.save(entity);
        }
        return entity;
    }

    @Override
    public List<Facility> findByPrice(Integer price, Integer weekendPrice, Integer priceAfterFive) {
        List<Facility> facilities = facilityRepository.findByPriceLessThan(price, weekendPrice, priceAfterFive, NOT_DELETED_FIELDS);
        for (Facility facility : facilities) {
            facility.setReservations(reservationService.findByFacilityId(facility.getId()));
            facility.setComments(commentService.findByFacilityId(facility.getId()));
        }
        return facilities;
    }

    @Override
    public List<Facility> findByFacilityOwnerId(Integer facilityOwnerId) {
        List<Facility> facilities = facilityRepository.findByFacilityOwnerId(facilityOwnerId, NOT_DELETED_FIELDS);
        for (Facility facility : facilities) {
            if (facility.getPictures() == null || facility.getPictures().isEmpty()) {
                List<Picture> pics = new ArrayList<>();
                Picture pic = pictureService.findOne(1);
                pics.add(pic);
                facility.setPictures(pics);
            }
            facility.setReservations(reservationService.findByFacilityId(facility.getId()));
            facility.setComments(commentService.findByFacilityId(facility.getId()));
        }
        return facilities;
    }

    @Override
    public List<Facility> findByFacilityType(String type) {
        List<Facility> facilities = facilityRepository.findByFacilityTypeType(type);
        for (Facility facility : facilities) {
            facility.setReservations(reservationService.findByFacilityId(facility.getId()));
            facility.setComments(commentService.findByFacilityId(facility.getId()));
        }
        return facilities;
    }

    @Override
    public List<Facility> findByWeekendHours(short startHour, short endHour) {
        List<Facility> facilities = facilityRepository.findByWeekendHours(startHour, endHour, NOT_DELETED_FIELDS);
        for (Facility facility : facilities) {
            facility.setReservations(reservationService.findByFacilityId(facility.getId()));
            facility.setComments(commentService.findByFacilityId(facility.getId()));
        }
        return facilities;
    }

    @Override
    public List<Facility> findByWeekdayHours(short startHour, short endHour) {
        List<Facility> facilities = facilityRepository.findByWeekdayHours(startHour, endHour, NOT_DELETED_FIELDS);
        for (Facility facility : facilities) {
            facility.setReservations(reservationService.findByFacilityId(facility.getId()));
            facility.setComments(commentService.findByFacilityId(facility.getId()));
        }
        return facilities;
    }

    @Override
    public List<Facility> findAllOrderByRating() {
        List<Facility> facilities = facilityRepository.findAllByOrderByRatingDesc();
        for (Facility facility : facilities) {
            facility.setReservations(reservationService.findByFacilityId(facility.getId()));
            facility.setComments(commentService.findByFacilityId(facility.getId()));
        }
        return facilities;
    }

    @Override
    public List<Facility> findByAddress(String city, String neighbourhood, String street) {
        List<Facility> facilities = facilityRepository.findByAddress(city, neighbourhood, street, NOT_DELETED_FIELDS);
        for (Facility facility : facilities) {
            facility.setReservations(reservationService.findByFacilityId(facility.getId()));
            facility.setComments(commentService.findByFacilityId(facility.getId()));
        }
        return facilities;
    }

    @Override
    public List<FacilityType> facilityTypes() {
        return (List<FacilityType>) facilityTypeRepository.findAll();
    }


    private void setWorkingHours(Facility facility, Facility entity) {
        if (facility.getWeekdayHours() != null) {
            WorkingHours weekdayHours = workingHoursRepository.findByStartHourAndEndHour(facility.getWeekdayHours().getStartHour(),
                    facility.getWeekdayHours().getEndHour());
            if (weekdayHours == null) {
                entity.setWeekdayHours(workingHoursRepository.save(facility.getWeekdayHours()));
            } else {
                entity.setWeekdayHours(weekdayHours);
            }
        }

        if (facility.getWeekendHours() != null) {
            WorkingHours weekendHours = workingHoursRepository.findByStartHourAndEndHour(facility.getWeekendHours().getStartHour(),
                    facility.getWeekendHours().getEndHour());
            if (weekendHours == null) {
                entity.setWeekendHours(workingHoursRepository.save(facility.getWeekendHours()));
            } else {
                entity.setWeekendHours(weekendHours);
            }
        }
    }
}
