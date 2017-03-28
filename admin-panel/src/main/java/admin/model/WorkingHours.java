package admin.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * Created by Hakan_Hyusein on 8/3/2016.
 */
@Entity
@Table(name = "working_hours")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class WorkingHours {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "start_hour")
    @Min(value = 0)
    @Max(value = 23)
    private short startHour;

    @Column(name = "end_hour")
    @Min(value = 0)
    @Max(value = 23)
    private short endHour;

    @OneToMany(mappedBy = "weekdayHours")
    @JsonIgnore
    private List<Facility> facilityWeekdayHours;

    @OneToMany(mappedBy = "weekendHours")
    @JsonIgnore
    private List<Facility> facilityWeekendHours;

    public WorkingHours() {
    }

    public WorkingHours(short startHour, short endHour) {
        this.startHour = startHour;
        this.endHour = endHour;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public short getStartHour() {
        return startHour;
    }

    public void setStartHour(short startHour) {
        this.startHour = startHour;
    }

    public short getEndHour() {
        return endHour;
    }

    public void setEndHour(short endHour) {
        this.endHour = endHour;
    }

    public List<Facility> getFacilityWeekdayHours() {
        return facilityWeekdayHours;
    }

    public void setFacilityWeekdayHours(List<Facility> facilityWeekdayHours) {
        this.facilityWeekdayHours = facilityWeekdayHours;
    }

    public List<Facility> getFacilityWeekendHours() {
        return facilityWeekendHours;
    }

    public void setFacilityWeekendHours(List<Facility> facilityWeekendHours) {
        this.facilityWeekendHours = facilityWeekendHours;
    }
}
