package polling.model;

/**
 * Created by Toncho_Petrov on 1/4/2017.
 */
public class UserAndReservation {

    private User user;

    private Reservation reservation;

    public UserAndReservation() {
    }

    public UserAndReservation(User user, Reservation reservation) {
        this.user = user;
        this.reservation = reservation;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }
}
