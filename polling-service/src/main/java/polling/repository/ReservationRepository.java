package polling.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import polling.model.Reservation;

import java.sql.Date;
import java.util.List;

/**
 * Created by Toncho_Petrov on 1/4/2017.
 */
@RepositoryRestResource(exported = false)
public interface ReservationRepository extends CrudRepository<Reservation, Integer> {
    List<Reservation> findByUserId(Integer userId);

    List<Reservation> findByFacilityId(Integer facilityId);

    List<Reservation> findByDateAfter(Date date);

    List<Reservation> findByDateBefore(Date date);

    List<Reservation> findByDate(Date date);

    List<Reservation> findByUserIdAndFacilityId(Integer userId, Integer facilityId);

    List<Reservation> findByFacilityIdAndDateAfter(Integer facilityId, Date date);

    List<Reservation> findByUserIdAndDateAfter(Integer userId, Date date);
}
