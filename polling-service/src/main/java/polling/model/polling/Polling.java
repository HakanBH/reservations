package polling.model.polling;

import polling.model.Reservation;

/**
 * Created by Toncho_Petrov on 1/10/2017.
 */
public class Polling {

    private Reservation newReservation;
    private Reservation deletedReservation;

    public Polling() {
    }

    public Reservation getNewReservation() {
        return newReservation;
    }

    public void setNewReservation(Reservation newReservation) {
        this.newReservation = newReservation;
    }

    public Reservation getDeletedReservation() {
        return deletedReservation;
    }

    public void setDeletedReservation(Reservation deletedReservation) {
        this.deletedReservation = deletedReservation;
    }
}
