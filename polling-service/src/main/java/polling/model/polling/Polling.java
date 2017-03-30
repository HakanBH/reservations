package polling.model.polling;

import polling.model.Reservation;

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
