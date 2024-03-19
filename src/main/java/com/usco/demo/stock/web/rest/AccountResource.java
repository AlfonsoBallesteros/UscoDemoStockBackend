package com.usco.demo.stock.web.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.usco.demo.stock.service.UserService;
import com.usco.demo.stock.service.dto.LoginDTO;
import com.usco.demo.stock.service.dto.RegisterUserDTO;
import com.usco.demo.stock.service.dto.UserDTO;
import com.usco.demo.stock.web.errors.BadRequestException;
import com.usco.demo.stock.web.errors.ErrorConstants;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

import static com.usco.demo.stock.security.SecurityUtils.AUTHORITIES_KEY;
import static com.usco.demo.stock.security.SecurityUtils.JWT_ALGORITHM;

@RestController
@RequestMapping("/api")
public class AccountResource {

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    private final UserService userService;

    private final JwtEncoder jwtEncoder;

    @Value("${application.security.authentication.jwt.token-validity-in-seconds:0}")
    private long tokenValidityInSeconds;

    @Value("${application.security.authentication.jwt.token-validity-in-seconds-for-remember-me:0}")
    private long tokenValidityInSecondsForRememberMe;

    private final ReactiveAuthenticationManager authenticationManager;

    public AccountResource(UserService userService, JwtEncoder jwtEncoder, ReactiveAuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtEncoder = jwtEncoder;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<UserDTO>> registerAccount(@Valid @RequestBody RegisterUserDTO registerUserDTO) {
        if (isPasswordLengthInvalid(registerUserDTO.getPassword())) {
            throw new BadRequestException(HttpStatus.BAD_REQUEST, "Incorrect password", ErrorConstants.INVALID_PASSWORD_TYPE.toString() ,HttpStatus.BAD_REQUEST.toString());
        }
        return userService.registerUser(registerUserDTO, registerUserDTO.getPassword())
                .map(u -> ResponseEntity.status(HttpStatus.CREATED).body(u));
    }

    private static boolean isPasswordLengthInvalid(String password) {
        return (
                StringUtils.isEmpty(password) ||
                        password.length() < RegisterUserDTO.PASSWORD_MIN_LENGTH ||
                        password.length() > RegisterUserDTO.PASSWORD_MAX_LENGTH
        );
    }

    @PostMapping("/authenticate")
    public Mono<ResponseEntity<JWTToken>> authorize(@Valid @RequestBody Mono<LoginDTO> login) {
        return login
                .flatMap(l ->
                        authenticationManager
                                .authenticate(new UsernamePasswordAuthenticationToken(l.getUsername(), l.getPassword()))
                                .flatMap(auth -> Mono.fromCallable(() -> this.createToken(auth, l.isRememberMe())))
                )
                .map(jwt -> {
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.setBearerAuth(jwt);
                    return new ResponseEntity<>(new JWTToken(jwt), httpHeaders, HttpStatus.OK);
                });
    }

    @GetMapping("/authenticate")
    public Mono<String> isAuthenticated(ServerWebExchange request) {
        log.debug("REST request to check if the current user is authenticated ");
        return request.getPrincipal().map(Principal::getName);
    }

    @GetMapping("/account")
    public Mono<ResponseEntity<UserDTO>> getAccount() {
        return userService
                .getUserWithAuthorities()
                .map(UserDTO::new)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(new BadRequestException(HttpStatus.BAD_REQUEST, "User could not be found", "ACCOUNT", HttpStatus.BAD_REQUEST.toString())));
    }

    public String createToken(Authentication authentication, boolean rememberMe) {
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

        Instant now = Instant.now();
        Instant validity;
        if (rememberMe) {
            validity = now.plus(this.tokenValidityInSecondsForRememberMe, ChronoUnit.SECONDS);
        } else {
            validity = now.plus(this.tokenValidityInSeconds, ChronoUnit.SECONDS);
        }

        // @formatter:off
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .build();

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    /**
     * Object to return as body in JWT Authentication.
     */
    static class JWTToken {

        private String idToken;

        JWTToken(String idToken) {
            this.idToken = idToken;
        }

        @JsonProperty("id_token")
        String getIdToken() {
            return idToken;
        }

        void setIdToken(String idToken) {
            this.idToken = idToken;
        }
    }
}
