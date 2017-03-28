package polling.repository;

import polling.model.Reservation;

import java.util.List;

/**
 * Created by Toncho_Petrov on 1/3/2017.
 */

public interface ReservationPollRepository {

    List<Reservation> getReservation();

    void addReservation(Reservation reservation);

    void deleteReservation(Reservation reservation);
}
