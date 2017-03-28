package admin.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Toncho_Petrov on 7/13/2016.
 */
@Entity
@Table(name = "facility_type")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class FacilityType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;


    @Column(name = "type", unique = true)
    private String type;

    @OneToMany(mappedBy = "facilityType", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Facility> facilities;

    public FacilityType() {
    }

    public FacilityType(String type) {
        setType(type);
    }

    public List<Facility> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<Facility> facilities) {
        this.facilities = facilities;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
