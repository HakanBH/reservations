package reservations.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "facility")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class Facility implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    //    @Min(value = 0)
    @Column(name = "name")
    private String name;

    @Min(value = 0)
    @NotNull
    @Column(name = "price")
    private Integer price;

    @Min(value = 0)
    @Column(name = "weekends_price")
    private Integer weekendsPrice;

    @Min(value = 0)
    @Column(name = "discount_price")
    private Integer discountPrice;

    @Column(name = "description")
    private String description;

    @Column(name = "rating")
    private Double rating;

    @OneToMany(mappedBy = "facility", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Comment> comments;

    @OneToMany(mappedBy = "facility")
    @JsonIgnore
    private List<Rating> allRatings;

    @JsonIgnore
    @OneToMany(mappedBy = "facility", fetch = FetchType.LAZY)
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "facility", fetch = FetchType.LAZY)
    @JsonProperty
    private List<Picture> pictures;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "weekday_hours_id")
    private WorkingHours weekdayHours;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "weekend_hours_id")
    private WorkingHours weekendHours;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_type_id")
    @NotNull
    private FacilityType facilityType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    @RestResource(exported = false)
    @NotNull
    private Address address;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
//    @NotNull
//    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    protected FacilityOwner facilityOwner;

    @Column(name = "is_deleted")
    boolean isDeleted;

    public Facility() {
    }

    public Facility(Address address, FacilityOwner facilityOwner, FacilityType facilityType, Integer price, String name) {
        this.address = address;
        this.facilityOwner = facilityOwner;
        this.facilityType = facilityType;
        this.price = price;
        this.name = name;
    }

    public void addPicture(Picture picture) {
        if (picture != null)
            pictures.add(picture);
    }

    public void removePicture(Picture picture) {
        if (picture != null) {
            pictures.remove(picture);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getWeekendsPrice() {
        return weekendsPrice;
    }

    public void setWeekendsPrice(Integer weekendsPrice) {
        this.weekendsPrice = weekendsPrice;
    }

    public Integer getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(Integer discountPrice) {
        this.discountPrice = discountPrice;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public FacilityOwner getFacilityOwner() {
        return facilityOwner;
    }

    public void setFacilityOwner(FacilityOwner facilityOwner) {
        this.facilityOwner = facilityOwner;
    }

    public FacilityType getFacilityType() {
        return facilityType;
    }

    public void setFacilityType(FacilityType facilityType) {
        this.facilityType = facilityType;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public WorkingHours getWeekdayHours() {
        return weekdayHours;
    }

    public void setWeekdayHours(WorkingHours weekdayHours) {
        this.weekdayHours = weekdayHours;
    }

    public WorkingHours getWeekendHours() {
        return weekendHours;
    }

    public void setWeekendHours(WorkingHours weekendHours) {
        this.weekendHours = weekendHours;
    }

    public List<Rating> getAllRatings() {
        return allRatings;
    }

    public void setAllRatings(List<Rating> allRatings) {
        this.allRatings = allRatings;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    //    @JsonIgnore
//    @Transactional
    public List<Picture> getPictures() {
        return Collections.unmodifiableList(pictures);
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
