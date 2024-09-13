package wallet.api.infra.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;
import wallet.api.domain.user.entity.User;
import wallet.api.errors.auth.InvalidTokenError;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class JWTService {

    public String createToken(User user){
        try {
            Dotenv dotenv = Dotenv.load();
            var secret =dotenv.get("JWT_SECRET");

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
            Dotenv dotenv = Dotenv.load();

            var secret =dotenv.get("JWT_SECRET");

            var alg = Algorithm.HMAC256(secret);
            return JWT.require(alg).withIssuer("API").build().verify(token).getSubject();
        }catch (JWTVerificationException e) {
            throw new InvalidTokenError();
        }



    }


    public Instant generateExpDate(){
        Dotenv dotenv = Dotenv.load();
        var secret =dotenv.get("JWT_EXPIRATION");
        var exp =Integer.parseInt(secret);
        return LocalDateTime.now().plusDays(exp).toInstant(ZoneOffset.of("-03:00"));

    }
}
