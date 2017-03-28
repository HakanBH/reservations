package search.model.dto;

import search.model.Facility;
import search.model.User;

import java.util.List;

/**
 * Created by Trayan_Muchev on 9/14/2016.
 */
public class UserAndFacilities {

    private User user;

    private List<Facility> facilities;

    public UserAndFacilities() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Facility> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<Facility> facilities) {
        this.facilities = facilities;
    }
}
