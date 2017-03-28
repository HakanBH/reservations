package search.model;

import com.fasterxml.jackson.annotation.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.validator.constraints.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Toncho_Petrov on 7/13/2016.
 */
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class User implements UserDetails {
    public static final int PHONE_NUMBER_WITH_BULGARIAN_CODE = 13;
    public static final String DEFAULT_PROFILE_PIC = "/opt/reservations/backend/images/default.png";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;

    @Column(name = "email", unique = true)
    @Email
    private String email;

    @Column(name = "first_name")
    @NotNull
    private String firstName;

    @Column(name = "last_name")
    @NotNull
    private String lastName;

    @Column(name = "password")
    private String password;

    @Column(name = "times_skiped")
    @Min(value = 0)
    @Max(value = 3)
    private Integer timesSkiped;

    @Column(name = "phone", unique = true)
    @Pattern(regexp = "08[7-9][0-9]{7}")
    private String phoneNumber;

    @JsonProperty
    @Column(name = "profile_pic")
    private String profilePic = DEFAULT_PROFILE_PIC;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    @RestResource(exported = false)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Address address;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Rating> allRatings;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @RestResource(rel = "reservations")
    @JsonIgnore
    private List<Reservation> reservations;

    @JsonBackReference
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @RestResource(rel = "comments")
    private List<Comment> comments;

    @Column(name = "google_email")
    @Email
    private String googleEmail;

    @Column(name = "googleid")
    private String googleId;

    @Column(name = "facebook_email")
    @Email
    private String facebookEmail;

    @Column(name = "facebookid")
    private String facebookId;

    @Column(name = "facebook_pic")
    private String facebookPic;

    @Value("false")
    @Column(name = "is_blocked")
    @ColumnDefault("FALSE")
    private boolean isBlocked;

    @Column(name = "is_deleted")
    @ColumnDefault("FALSE")
    private boolean isDeleted;

    @JsonIgnore
    @Column(name = "is_verified")
    @ColumnDefault("FALSE")
    private Boolean isVerified;

    /* Spring Security related fields*/
    @Transient
    private List<Role> authorities;
    @Transient
    private boolean accountNonExpired = true;
    @Transient
    private boolean accountNonLocked = true;
    @Transient
    private boolean credentialsNonExpired = true;
    @Transient
    private boolean enabled = true;

    public User() {
    }

    public User(String email, String firstName, String lastName, String password, String phoneNumber) {
        setEmail(email);
        setFirstName(firstName);
        setLastName(lastName);
        setPassword(password);
        setPhoneNumber(phoneNumber);
    }

    public User(String email, String lastName, String phoneNumber, Role role) {
        this.email = email;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            if (phoneNumber.length() == PHONE_NUMBER_WITH_BULGARIAN_CODE) {
                phoneNumber = "0" + phoneNumber.substring(4);
            }
            this.phoneNumber = phoneNumber;
        }
    }

    @Transactional
    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(Boolean isBlocked) {
        this.isBlocked = isBlocked;
    }

    public Integer getTimesSkiped() {
        return timesSkiped;
    }

    public void setTimesSkiped(Integer timesSkiped) {
        this.timesSkiped = timesSkiped;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public List<Rating> getAllRatings() {
        return allRatings;
    }

    public void setAllRatings(List<Rating> allRatings) {
        this.allRatings = allRatings;
    }

    @JsonIgnore
    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getFacebookEmail() {
        return facebookEmail;
    }

    public void setFacebookEmail(String facebookEmail) {
        this.facebookEmail = facebookEmail;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getGoogleEmail() {
        return googleEmail;
    }

    public void setGoogleEmail(String googleEmail) {
        this.googleEmail = googleEmail;
    }

    public String getFacebookPic() {
        return facebookPic;
    }

    public void setFacebookPic(String facebookPic) {
        this.facebookPic = facebookPic;
    }


    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<Role> roles = new ArrayList<>();
        roles.add(this.getRole());
        return roles;
    }

    @Override
    public String getUsername() {
        return getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public void setAuthorities(List<Role> authorities) {
        this.authorities = authorities;
    }

    public Boolean getVerified() {
        return isVerified;
    }

    public void setVerified(Boolean verified) {
        isVerified = verified;
    }
}
