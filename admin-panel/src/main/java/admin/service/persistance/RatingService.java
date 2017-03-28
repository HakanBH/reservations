package admin.service.persistance;


import admin.model.Rating;

/**
 * Created by Trayan_Muchev on 8/9/2016.
 */
public interface RatingService {
    double findAvgByFacility(Integer facilityId);

    void save(Rating rating);

}
