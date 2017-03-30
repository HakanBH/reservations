package polling.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Toncho_Petrov on 1/4/2017.
 */

@Entity
@Table(name = "facility_owner")
@PrimaryKeyJoinColumn(name = "user_id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class FacilityOwner extends User implements Serializable {

    @Column(name = "website")
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String website;

    @Column(name = "rating")
    @Min(value = 0)
    @Max(value = 5)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private Double rating;

    @OneToMany(mappedBy = "facilityOwner", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Facility> facilities;

    public FacilityOwner() {
    }

    public FacilityOwner(String email, String firstName, String lastName, String password, String phone) {
        super(email, firstName, lastName, password, phone);
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public List<Facility> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<Facility> facilities) {
        this.facilities = facilities;
    }
}
