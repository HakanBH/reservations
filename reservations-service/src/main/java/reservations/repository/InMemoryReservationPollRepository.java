package reservations.repository;

import org.springframework.stereotype.Repository;
import reservations.model.Reservation;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Created by Toncho_Petrov on 1/3/2017.
 */
@Repository
public class InMemoryReservationPollRepository implements ReservationPollRepository {

    private List<Reservation> reservations = new CopyOnWriteArrayList<Reservation>();

    public List<Reservation> getReservation() {
        if (this.reservations.isEmpty()) {
            return Collections.<Reservation>emptyList();
        }
        List<Reservation> reservationList = this.reservations.subList(this.reservations.size() - 1, this.reservations.size());
        return reservationList;
    }

    @Override
    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
    }

    @Override
    public void deleteReservation(Reservation reservation) {
        reservations.remove(reservation);
    }
}
