package common.jsonwebtoken;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Toncho_Petrov on 10/5/2016.
 */

@Component
public class JsonWebTokenUtility {

    private final String ENCODED_KEY = "L7A/6zARSkK1j7Vd5SDD9pSSqZlqF7mAhiOgRbgv9Smce6tf4cJnvKOjtKPxNNnWQj+2lQEScm3XIUjhW+YVZg==";

    private SignatureAlgorithm signatureAlgorithm;
    private Key secretKey;

//    @Value("${security.encodedKey}")
//    private String encodedKey;

    public JsonWebTokenUtility() {
        signatureAlgorithm = SignatureAlgorithm.HS512;
        secretKey = deserializeKey(ENCODED_KEY);
    }

    public String createJsonWebToken(AuthTokenDetailsDTO authTokenDetailsDTO) {
        authTokenDetailsDTO.expirationDate = buildExpirationDate();
        String token = Jwts.builder().setSubject(authTokenDetailsDTO.userId).claim("email", authTokenDetailsDTO.email)
                .claim("roles", authTokenDetailsDTO.roleNames).setExpiration(authTokenDetailsDTO.expirationDate)
                .signWith(getSignatureAlgorithm(), getSecretKey()).compact();
        return token;
    }

    private Key deserializeKey(String encodedKey) {
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        Key key = new SecretKeySpec(decodedKey, getSignatureAlgorithm().getJcaName());
        return key;
    }

    private Key getSecretKey() {
        return secretKey;
    }

    public SignatureAlgorithm getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    public AuthTokenDetailsDTO parseAndValidate(String token) {
        AuthTokenDetailsDTO authTokenDetailsDTO = null;

        Claims claims = null;
        claims = Jwts.parser().setSigningKey(getSecretKey()).parseClaimsJws(token).getBody();

        String userId = claims.getSubject();
        String email = (String) claims.get("email");
        List<String> roleNames = (List<String>) claims.get("roles");
        Date expirationDate = claims.getExpiration();

        authTokenDetailsDTO = new AuthTokenDetailsDTO();
        authTokenDetailsDTO.userId = userId;
        authTokenDetailsDTO.email = email;
        authTokenDetailsDTO.roleNames = roleNames;
        authTokenDetailsDTO.expirationDate = expirationDate;

        if (!isExpiration(authTokenDetailsDTO.expirationDate)) {
            return authTokenDetailsDTO;
        } else {
            return null;
        }
    }


    public boolean isExpiration(Date date) {
        Date currentDate = new Date();
        if (date.getTime() < currentDate.getTime()) {
            return true;
        } else {
            return false;
        }
    }

    private String serializeKey(Key key) {
        String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
        return encodedKey;
    }

    public Date buildExpirationDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 1);
        Date expirationDate = calendar.getTime();
        return expirationDate;
    }

    public Date buildVerificationExpirationDate(int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, hours);
        Date expirationDate = calendar.getTime();
        return expirationDate;
    }

}
