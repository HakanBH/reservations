package admin.model;

import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.sql.Date;

/**
 * Created by Toncho_Petrov on 7/13/2016.
 */

@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id")
    @RestResource(exported = false)
    private Facility facility;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @RestResource(exported = false)
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

    public Reservation() {
    }

    public Reservation(Facility facility, User user, Integer fromHour, Integer toHour, Date date, Boolean deposit) {
        this.facility = facility;
        this.user = user;
        this.fromHour = fromHour;
        this.toHour = toHour;
        this.date = date;
        this.deposit = deposit;
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
