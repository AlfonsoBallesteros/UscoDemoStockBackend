package com.usco.demo.stock.config;

import com.usco.demo.stock.security.AuthoritiesConstants;
import com.usco.demo.stock.security.JWTFilter;
import com.usco.demo.stock.security.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authorization.HttpStatusServerAccessDeniedHandler;
import org.springframework.security.web.server.header.ReferrerPolicyServerHttpHeadersWriter;
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter.Mode;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.OrServerWebExchangeMatcher;

import static org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.pathMatchers;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration {

    private final ApplicationProperties applicationProperties;

    private final TokenProvider tokenProvider;


    public SecurityConfiguration(ApplicationProperties applicationProperties, TokenProvider tokenProvider) {
        this.applicationProperties = applicationProperties;
        this.tokenProvider = tokenProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService) {
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(
                userDetailsService
        );
        authenticationManager.setPasswordEncoder(passwordEncoder());
        return authenticationManager;
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .securityMatcher(new NegatedServerWebExchangeMatcher(new OrServerWebExchangeMatcher(
                        pathMatchers("/app/**", "/i18n/**", "/content/**", "/swagger-ui/**", "/swagger-resources/**", "/v2/api-docs", "/v3/api-docs", "/test/**"),
                        pathMatchers(HttpMethod.OPTIONS, "/**")
                )))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .addFilterBefore(new JWTFilter(tokenProvider), SecurityWebFiltersOrder.HTTP_BASIC)
                .exceptionHandling(exceptionHandling -> {
                    exceptionHandling
                            .accessDeniedHandler(new HttpStatusServerAccessDeniedHandler(HttpStatus.FORBIDDEN))
                            .authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED));
                })
                .headers(headers ->
                        headers
                                .contentSecurityPolicy(csp -> csp.policyDirectives(applicationProperties.getSecurity().getContentSecurityPolicy()))
                                .frameOptions(frameOptions -> frameOptions.mode(Mode.DENY))
                                .referrerPolicy(referrer ->
                                        referrer.policy(ReferrerPolicyServerHttpHeadersWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                                )
                                .permissionsPolicy(permissions ->
                                        permissions.policy(
                                                "camera=(), fullscreen=(self), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), midi=(), payment=(), sync-xhr=()"
                                        )
                                )
                )
                .requestCache(cache -> cache.requestCache(NoOpServerRequestCache.getInstance()))
                .authorizeExchange(exchanges ->
                        exchanges
                                .pathMatchers("/api/authenticate").permitAll()
                                .pathMatchers("/api/admin/**").hasAuthority(AuthoritiesConstants.ADMIN)
                                .pathMatchers("/api/v1/**").permitAll()
                                .pathMatchers("/api/**").permitAll()
                                .pathMatchers("/swagger-doc/**").permitAll()
                                .pathMatchers("/v3/api-docs/**").permitAll()
                                .pathMatchers("/management/health").permitAll()
                                .pathMatchers("/management/health/**").permitAll()
                                .pathMatchers("/management/info").permitAll()
                                .pathMatchers("/management/prometheus").permitAll()
                                .pathMatchers("/management/**").hasAuthority(AuthoritiesConstants.ADMIN)
                )
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable);
            //.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
