package wallet.api.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.weaver.ast.Not;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import wallet.api.domain.auth.AuthRepository;
import wallet.api.errors.auth.NoTokenError;
import wallet.api.infra.jwt.JWTService;

import java.io.IOException;
import java.util.Date;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final JWTService tokenService;
    private final AuthRepository authRepository;

    public SecurityFilter(JWTService tokenService, AuthRepository authRepository) {
        this.tokenService = tokenService;
        this.authRepository = authRepository;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {


            var token = this.getHeaderToken(request);


            if(token.isEmpty()){
                throw new NoTokenError();
            }
            var decodedSubject = tokenService.getDecodedToken(token);
            var user = authRepository.findByEmail(decodedSubject);
            var authorise = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authorise);

            filterChain.doFilter(request,response);
        }catch (RuntimeException e){
            handleErorInFilter(request,response,e);


        }

    }

    private String getHeaderToken(HttpServletRequest request) {
        var token =  request.getHeader("Authorization");
        if (token != null) {
            return token.replace("Bearer ", "").trim();
        }

        return "";

    }

    private String getErrorResponse(String message, String path, String code, String error ){
        return String.format("{\"timestamp\":%s,\"status\":%s,\"error\":\"%s\",\"message\":\"%s\",\"path\":\"%s\"}", new Date().getTime(), code, error, message, path);
    }


    private void handleErorInFilter(HttpServletRequest request,HttpServletResponse response, RuntimeException e)  throws  IOException{

        var message = e.getMessage();

        var path = request.getRequestURI();

        if(message!=null){
            if(message.equals("Invalid Token")) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write(getErrorResponse(message, path, "401", "Unauthorized"));
                return;
            }
            if(message.equals("No token provided")) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.getWriter().write(getErrorResponse(message, path, "400", "Bad Request"));
                return;
            }
        }
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.getWriter().write(getErrorResponse("", path, "500", "Bad Request"));

    }
}
