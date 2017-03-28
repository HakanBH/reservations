package reservations.model;

import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.sql.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Toncho_Petrov on 7/13/2016.
 */

@Entity
@Table(name = "reservation")
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Reservation implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id")
    @RestResource(exported = false)
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
//    @NotNull
    private Facility facility;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @RestResource(exported = false)
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
//    @NotNull
    private User user;

    @Min(value = 0)
    @Max(value = 23)
    @Column(name = "from_hour")
    private Integer fromHour;

    @Min(value = 0)
    @Max(value = 23)
    @Column(name = "to_hour")
    private Integer toHour;

    @Column(name = "date")
    private Date date;

    @Column(name = "deposit")
    private Boolean deposit;

    private AtomicInteger atomicInt;

    public Reservation() {
    }

    public Reservation(AtomicInteger atomicInt) {
        this.atomicInt = atomicInt;
    }

    public Reservation(Facility facility, User user, Integer fromHour, Integer toHour, Date date, Boolean deposit) {
        this.facility = facility;
        this.user = user;
        this.fromHour = fromHour;
        this.toHour = toHour;
        this.date = date;
        this.deposit = deposit;
    }

    public AtomicInteger getAtomicInt() {
        return atomicInt;
    }

    public void setAtomicInt(AtomicInteger atomicInt) {
        this.atomicInt = atomicInt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getFromHour() {
        return fromHour;
    }

    public void setFromHour(Integer fromHour) {
        this.fromHour = fromHour;
    }

    public Integer getToHour() {
        return toHour;
    }

    public void setToHour(Integer toHour) {
        this.toHour = toHour;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Boolean getDeposit() {
        return deposit;
    }

    public void setDeposit(Boolean deposit) {
        this.deposit = deposit;
    }


}
