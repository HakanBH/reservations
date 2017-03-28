package search.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import search.model.Rating;

/**
 * Created by Trayan_Muchev on 8/9/2016.
 */
public interface RatingRepository extends CrudRepository<Rating, Integer> {

    @Query("select AVG(R.rating) from Rating R join R.facility F  where F.id=?")
    double findAvgByFacility(Integer facilityId);

    Rating findByUserIdAndFacilityId(Integer userId, Integer facilityId);
}
