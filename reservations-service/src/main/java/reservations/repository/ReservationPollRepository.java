package reservations.repository;

import reservations.model.Reservation;

import java.util.List;

public interface ReservationPollRepository {

    List<Reservation> getReservation();

    void addReservation(Reservation reservation);

    void deleteReservation(Reservation reservation);
}
