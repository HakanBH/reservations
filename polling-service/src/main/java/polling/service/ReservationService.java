package polling.service;

import polling.model.Reservation;

import java.sql.Date;
import java.util.List;

/**
 * Created by Toncho_Petrov on 1/4/2017.
 */
public interface ReservationService {
    Reservation save(Reservation reservation);

    Reservation findById(Integer id);

    List<Reservation> findAll();

    List<Reservation> findByFacilityId(Integer facilityId);

    List<Reservation> findByUserId(Integer userId);

    List<Reservation> findByDateAfter(Date date);

    List<Reservation> findByDateBefore(Date date);

    List<Reservation> findByDate(Date date);

    List<Reservation> findByUserIdAndFacilityId(Integer userId, Integer facilityId);

    List<Reservation> findByFacilityIdAndDateAfter(Integer facilityId, Date date);

    List<Reservation> findByUserIdAndDateAfter(Integer userId, Date date);

    List<Reservation> delete(Integer reservationId);

}
