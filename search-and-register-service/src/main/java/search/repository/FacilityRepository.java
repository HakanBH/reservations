package search.repository;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import search.model.Facility;

import java.util.List;

/**
 * Created by Trayan_Muchev on 7/21/2016.
 */
@Repository
public interface FacilityRepository extends CrudRepository<Facility, Integer> {

    @Query("select F from Facility F where (F.price<=?1 or F.weekendsPrice<=?2 or F.discountPrice<=?3)and F.isDeleted= ?4")
    List<Facility> findByPriceLessThan(Integer price, Integer weekendPrice, Integer priceAfterFive, boolean isDeleted);

    @Query("select F from Facility F inner join F.facilityOwner O where O.id = ?1 and F.isDeleted = ?2")
    List<Facility> findByFacilityOwnerId(Integer facilityOwnerId, boolean isDeleted);

    List<Facility> findByFacilityTypeType(String type);

    @Query("select F from Facility F inner join F.weekendHours W where W.startHour >= ?1 and W.endHour <= ?2 and F.isDeleted= ?3")
    List<Facility> findByWeekendHours(short startHour, short endHour, boolean iIsDeleted);

    @Query("select F from Facility F inner join F.weekdayHours W where W.startHour >= ?1 and W.endHour <= ?2 and F.isDeleted= ?3")
    List<Facility> findByWeekdayHours(short startHour, short endHour, boolean isDeleted);

    @Query("select F from Facility F inner join F.address A where A.city like ?1 and A.neighbourhood like ?2 and A.street like ?3 and F.isDeleted= ?4")
    List<Facility> findByAddress(String city, String neighbourhood, String street, boolean isDeleted);

    List<Facility> findAllByOrderByRatingDesc();

    @Query("select AVG(F.rating) from Facility F join F.facilityOwner O where O.id= ?1")
    Double findAvgOwnerRatingByFacility(Integer facilityOwnerId);

    @Query("select F from Facility F where F.isDeleted = ?1")
    List<Facility> findByIsDeleted(boolean facilityIsDeleted);


}
