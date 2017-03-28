package admin.service.persistance;

import admin.model.*;
import admin.repository.FacilityRepository;
import admin.repository.FacilityTypeRepository;
import admin.repository.WorkingHoursRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Created by Trayan_Muchev on 7/21/2016.
 */

@Service("facilityService")
@Transactional
public class FacilityServiceImpl implements FacilityService {
    private static final boolean NOT_DELETED_FIELDAS = FALSE;

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
    public Facility save(Facility facility) throws Exception {
        setWorkingHours(facility, facility);

        if (facility.getFacilityOwner() != null) {
            if (facilityOwnerService.findById(facility.getFacilityOwner().getId()) == null) {
                throw new Exception("Invalid facility owner id=" + facility.getFacilityOwner().getId()
                        + "Facility Owner with such id does not exist.");
            }
        }

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
        return facilityRepository.findOne(id);
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
        return facilityRepository.findByFacilityOwnerId(entity.getFacilityOwner().getId());
    }

    @Override
    public List<Facility> deleteList(List<Integer> facilitiesId) {
        for (Integer id : facilitiesId) {
            deleteFacility(id);
        }
        return (List<Facility>) facilityRepository.findAll();
    }


    @Override
    public List<Facility> findAll() {
        return (List<Facility>) facilityRepository.findAll();
    }

    @Override
    public Facility updateFacilityById(Integer facilityToUpdate, Facility facility) {
        Facility entity = facilityRepository.findOne(facilityToUpdate);
        if (entity != null) {
            if (facility.getAddress() != null) {
                if (entity.getAddress() != null) {
                    Address address = addressService.save(facility.getAddress());
                    entity.setAddress(address);
                } else {
                    addressService.updateAddress(facility.getAddress());
                }
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
            entity.setDeleted(facility.isDeleted());
            return facilityRepository.save(entity);
        }
        return entity;
    }

    @Override
    public List<Facility> findByPrice(Integer price, Integer weekendPrice, Integer priceAfterFive) {
        return facilityRepository.findByPriceLessThan(price, weekendPrice, priceAfterFive);
    }

    @Override
    public List<Facility> findByFacilityOwnerId(Integer facilityOwnerId) {
        return facilityRepository.findByFacilityOwnerId(facilityOwnerId);
    }

    @Override
    public List<Facility> findByFacilityType(String type) {
        return facilityRepository.findByFacilityTypeType(type);
    }

    @Override
    public List<Facility> findByWeekendHours(short startHour, short endHour) {
        return facilityRepository.findByWeekendHours(startHour, endHour);
    }

    @Override
    public List<Facility> findByWeekdayHours(short startHour, short endHour) {
        return facilityRepository.findByWeekdayHours(startHour, endHour);
    }

    @Override
    public List<Facility> findAllOrderByRating() {
        return facilityRepository.findAllByOrderByRatingDesc();
    }

    @Override
    public List<Facility> findByAddress(String city, String neighbourhood, String street) {
        return facilityRepository.findByAddress(city, neighbourhood, street);
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
