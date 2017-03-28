package common.jsonwebtoken;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

/**
 * Created by Toncho_Petrov on 10/5/2016.
 */
public class AuthTokenDetailsDTO {

    public String userId;
    public String email;
    public List<String> roleNames;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    public Date expirationDate;

    @Override
    public String toString() {
        return "AuthTokenDetailsDTO{" +
                "userId='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", roleNames=" + roleNames +
                ", expirationDate=" + expirationDate +
                '}';
    }


    @Override
    public boolean equals(Object o) {

        AuthTokenDetailsDTO dto = (AuthTokenDetailsDTO) o;
        boolean isExpire = true;
        if (this.expirationDate.getYear() == dto.expirationDate.getYear()
                && this.expirationDate.getMonth() == dto.expirationDate.getMonth()
                && this.expirationDate.getDay() == dto.expirationDate.getDay()
                && this.expirationDate.getHours() == dto.expirationDate.getHours()
                && this.expirationDate.getMinutes() == dto.expirationDate.getMinutes()) {
            isExpire = false;
        } else {
            isExpire = true;
        }

        if (this.email.equals(dto.email)
                && this.userId.equals(dto.userId)
                && this.roleNames.get(0).equals(dto.roleNames.get(0))
                && !isExpire) {

            return true;
        } else {
            return false;
        }

    }
}

