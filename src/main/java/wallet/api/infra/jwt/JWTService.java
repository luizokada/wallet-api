package wallet.api.infra.jwt;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;

import wallet.api.domain.user.entity.User;
import wallet.api.errors.auth.InvalidTokenError;

@Service
public class JWTService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private String jwtExpiration;

    public String createToken(User user){
        try {
            var secret =jwtSecret;

            var alg = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("API")
                    .withSubject(user.getEmail())
                    .withClaim("id",user.getId())
                    .withExpiresAt(generateExpDate())
                    .sign(alg);
        } catch (JWTCreationException exception){
            throw new RuntimeException("error on creation token", exception);
        }
    }

    public String getDecodedToken(String token){
        try {
            var secret =jwtSecret;

            var alg = Algorithm.HMAC256(secret);
            return JWT.require(alg).withIssuer("API").build().verify(token).getSubject();
        }catch (JWTVerificationException e) {
            throw new InvalidTokenError();
        }

    }


    public Instant generateExpDate(){
        var secret =jwtExpiration;
        var exp =Integer.parseInt(secret);
        return LocalDateTime.now().plusDays(exp).toInstant(ZoneOffset.of("-03:00"));

    }
}
