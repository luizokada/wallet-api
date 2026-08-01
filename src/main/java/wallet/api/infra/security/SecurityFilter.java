package wallet.api.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import wallet.api.domain.auth.AuthRepository;
import wallet.api.errors.auth.InvalidTokenError;
import wallet.api.errors.auth.NoTokenError;
import wallet.api.infra.jwt.JWTService;
import wallet.api.infra.security.annotations.PublicRoute;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    @Autowired
    private RequestMappingHandlerMapping reqMap;

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
            if(user == null){
                throw new InvalidTokenError();
            }
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
        response.getWriter().write(getErrorResponse("", path, "500", "Internal Server Error"));

    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        HandlerMethod method = null;
        var isSwagger = request.getServletPath().startsWith("/swagger-ui") || request.getServletPath().startsWith("/v3/api-docs");

        if(isSwagger){
            return true;
        }
        try {
            method = (HandlerMethod) Objects.requireNonNull(reqMap.getHandler(request)).getHandler();
        } catch (Exception e) {
            return true;
        }
        return method.getMethod().isAnnotationPresent(PublicRoute.class);
    }

}
