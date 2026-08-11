package br.com.mypass.auth;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @ConfigurationProperties(prefix = "rsa")
    public record RsaKeyProperties(
            RSAPublicKey publicKey,
            RSAPrivateKey privateKey
    ) {}

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer(oauth ->
                        oauth.jwt(withDefaults())
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .build();
    }

    @Bean
    UserDetailsManager users() {

        UserDetails joao = User
                .withUsername("joao")
                .password("{noop}123456")
                .build();

        UserDetails maria = User
                .withUsername("maria")
                .password("{noop}123456")
                .build();

        return new InMemoryUserDetailsManager(joao, maria);
    }

    @Bean
    JwtDecoder jwtDecoder(RsaKeyProperties properties) {
        return NimbusJwtDecoder
                .withPublicKey(properties.publicKey())
                .build();
    }

    @Bean
    JwtEncoder jwtEncoder(RsaKeyProperties properties) {

        RSAKey rsaKey = new RSAKey.Builder(properties.publicKey())
                .privateKey(properties.privateKey())
                .build();

        var jwkSource = new ImmutableJWKSet<>(
                new JWKSet(rsaKey)
        );

        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService
    ) {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        return new ProviderManager(provider);
    }

}