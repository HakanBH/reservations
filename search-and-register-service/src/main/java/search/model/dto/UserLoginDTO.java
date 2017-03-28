package search.model.dto;

import javax.validation.constraints.NotNull;

/**
 * Created by Trayan_Muchev on 8/4/2016.
 */
public class UserLoginDTO {

    @NotNull
    private String email;

    @NotNull
    private String password;

    public UserLoginDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UserLoginDTO() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
