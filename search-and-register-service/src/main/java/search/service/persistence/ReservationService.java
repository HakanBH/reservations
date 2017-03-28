package search.service.persistence;

import search.model.Reservation;

import java.sql.Date;
import java.util.List;

/**
 * Created by Trayan_Muchev on 7/22/2016.
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

    void delete(Integer reservationId);
}
