package admin.repository;


import admin.model.Facility;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Trayan_Muchev on 7/21/2016.
 */
@Repository
public interface FacilityRepository extends CrudRepository<Facility, Integer> {

    @Query("select F from Facility F where F.price<=? or F.weekendsPrice<=? or F.discountPrice<=?")
    List<Facility> findByPriceLessThan(Integer price, Integer weekendPrice, Integer priceAfterFive);

    List<Facility> findByFacilityOwnerId(Integer facilityOwnerId);

    List<Facility> findByFacilityTypeType(String type);

    @Query("select F from Facility F inner join F.weekendHours W where W.startHour >= :startHour and W.endHour <= :endHour")
    List<Facility> findByWeekendHours(@Param("startHour") short startHour, @Param("endHour") short endHour);

    @Query("select F from Facility F inner join F.weekdayHours W where W.startHour >= :startHour and W.endHour <= :endHour")
    List<Facility> findByWeekdayHours(@Param("startHour") short startHour, @Param("endHour") short endHour);

    @Query("select F from Facility F inner join F.address A where A.city like :city and A.neighbourhood like :neighbourhood and A.street like :street")
    List<Facility> findByAddress(@Param("city") String city, @Param("neighbourhood") String neighbourhood, @Param("street") String street);

    List<Facility> findAllByOrderByRatingDesc();

    @Query("select AVG(F.rating) from Facility F join F.facilityOwner O where O.id= ?1")
    Double findAvgOwnerRatingByFacility(Integer facilityOwnerId);


}
