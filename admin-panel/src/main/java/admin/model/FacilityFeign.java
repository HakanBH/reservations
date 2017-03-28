package admin.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

/**
 * Created by Trayan_Muchev on 10/10/2016.
 */
public class FacilityFeign {

    private Integer id;


    private String name;

    @Min(value = 0)
    @NotNull
    private Integer price;

    @Min(value = 0)
    private Integer weekendsPrice;

    @Min(value = 0)
    private Integer discountPrice;

    private String description;

    private Double rating;

    private List<Comment> comments;


    private List<Rating> allRatings;

    private List<Reservation> reservations;

    private List<Picture> pictures;


    private WorkingHours weekdayHours;


    private WorkingHours weekendHours;

    @NotNull
    private FacilityType facilityType;

    @NotNull
    private Address address;


    protected FacilityOwner facilityOwner;

    public FacilityFeign() {
    }

    public FacilityFeign(Address address, FacilityOwner facilityOwner, FacilityType facilityType, Integer price, String name) {
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

    public List<Picture> getPictures() {
        return Collections.unmodifiableList(pictures);
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }
}
