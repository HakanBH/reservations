package reservations.service;

import reservations.model.PendingAndRejectedReservation;
import reservations.model.Reservation;

import java.sql.Date;
import java.util.List;

/**
 * Created by Trayan_Muchev on 9/9/2016.
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

    //Pending and Rejected Reservations

    Reservation approveReservation(Integer id);

    List<PendingAndRejectedReservation> findAllPendingAndRejectedReservations();

    List<PendingAndRejectedReservation> findPendingReservationsByUserId(Integer userId);

    List<PendingAndRejectedReservation> findPendingReservationsByUserIdAndFacilityId(Integer userId, Integer facilityId);

    PendingAndRejectedReservation savePendingReservation(Reservation reservation);

    List<PendingAndRejectedReservation> findPendingReservationsByOwnerId(Integer ownerId);

}
