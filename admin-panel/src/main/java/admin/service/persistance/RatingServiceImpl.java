package admin.service.persistance;

import admin.model.Rating;
import admin.repository.FacilityOwnerRepository;
import admin.repository.FacilityRepository;
import admin.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Trayan_Muchev on 8/9/2016.
 */
@Service("ratingService")
@Transactional
public class RatingServiceImpl implements RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private FacilityOwnerRepository facilityOwnerRepository;

    @Override
    public double findAvgByFacility(Integer facilityId) {
        return ratingRepository.findAvgByFacility(facilityId);
    }

    @Override
    public void save(Rating rating) {
        if (rating != null) {
            if (ratingRepository.findByUserIdAndFacilityId(rating.getUser().getId(), rating.getFacility().getId()) != null) {
                Rating entity = ratingRepository.findByUserIdAndFacilityId(rating.getUser().getId(), rating.getFacility().getId());
                entity.setRating(rating.getRating());
                ratingRepository.save(entity);
            } else {
                ratingRepository.save(rating);
            }
            rating.getFacility().setRating(ratingRepository.findAvgByFacility(rating.getFacility().getId()));
            facilityRepository.save(rating.getFacility());
            rating.getFacility().getFacilityOwner().setRating(facilityRepository.findAvgOwnerRatingByFacility(rating.getFacility().getFacilityOwner().getId()));
            System.out.println(facilityRepository.findAvgOwnerRatingByFacility(rating.getFacility().getFacilityOwner().getId()));
            facilityOwnerRepository.save(rating.getFacility().getFacilityOwner());
        }
    }

}
