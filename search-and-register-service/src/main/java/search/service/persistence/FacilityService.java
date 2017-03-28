package search.service.persistence;


import search.model.Facility;
import search.model.FacilityType;

import java.util.List;

/**
 * Created by Trayan_Muchev on 7/21/2016.
 */
public interface FacilityService {

    Facility save(Facility facility);

    Facility findById(Integer id);

    List<Facility> deleteFacility(Integer id);

    List<Facility> findAll();

    List<Facility> userFavoriteFacilities(Integer userId);

    Facility updateFacility(Integer id, Facility facility);

    List<Facility> findByPrice(Integer price, Integer weekendPrice, Integer priceAfterFive);

    List<Facility> findByFacilityOwnerId(Integer facilityOwnerId);

    List<Facility> findByFacilityType(String type);

    List<Facility> findByWeekendHours(short startHour, short endHour);

    List<Facility> findByWeekdayHours(short startHour, short endHour);

    List<Facility> findAllOrderByRating();

    List<Facility> findByAddress(String city, String neighbourhood, String street);

    List<FacilityType> facilityTypes();

}
