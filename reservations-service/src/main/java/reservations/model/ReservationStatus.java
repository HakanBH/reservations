package reservations.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "reservation_status")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ReservationStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "status", unique = true)
    @Enumerated(EnumType.STRING)
    private StatusType statusType;

    @OneToMany(mappedBy = "reservationStatus", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<PendingAndRejectedReservation> pendingAndRejectedReservations;

    public ReservationStatus() {
    }

    public ReservationStatus(Integer id, StatusType statusType) {
        this.id = id;
        this.statusType = statusType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public StatusType getStatusType() {
        return statusType;
    }

    public void setStatusType(StatusType statusType) {
        this.statusType = statusType;
    }

    public List<PendingAndRejectedReservation> getPendingAndRejectedReservations() {
        return pendingAndRejectedReservations;
    }

    public void setPendingAndRejectedReservations(List<PendingAndRejectedReservation> pendingAndRejectedReservations) {
        this.pendingAndRejectedReservations = pendingAndRejectedReservations;
    }
}
