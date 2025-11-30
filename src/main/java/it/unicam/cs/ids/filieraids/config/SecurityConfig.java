package it.unicam.cs.ids.filieraids.config;

import it.unicam.cs.ids.filieraids.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth

                        //ENDPOINT PUBBLICI
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/prodotti/visibili/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/eventi/visibili/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/mappa").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/pacchetti/visibili/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()

                        //ENDPOINT PROTETTI (Curatore/Gestore)
                        .requestMatchers("/api/curatore/**").hasRole("CURATORE")
                        .requestMatchers("/api/gestore/**").hasRole("GESTORE")

                        //VENDITORI
                        .requestMatchers("/api/ordini/venditore").hasAnyRole("PRODUTTORE", "DISTRIBUTORE", "TRASFORMATORE")

                        .requestMatchers(HttpMethod.POST, "/api/prodotti").hasAnyRole("PRODUTTORE", "DISTRIBUTORE", "TRASFORMATORE")
                        .requestMatchers(HttpMethod.GET, "/api/prodotti/miei").hasAnyRole("PRODUTTORE", "DISTRIBUTORE", "TRASFORMATORE")
                        .requestMatchers(HttpMethod.GET, "/api/venditori/**").hasAnyRole("PRODUTTORE", "DISTRIBUTORE", "TRASFORMATORE")


                        //ACQUIRENTE
                        .requestMatchers("/api/carrello/**").hasRole("ACQUIRENTE")
                        .requestMatchers("/api/prenotazioni/**").hasRole("ACQUIRENTE")
                        .requestMatchers("/api/ordini/**").hasRole("ACQUIRENTE")


                        //ANIMATORE
                        .requestMatchers(HttpMethod.POST, "/api/eventi").hasRole("ANIMATORE")
                        .requestMatchers(HttpMethod.DELETE, "/api/eventi/**").hasRole("ANIMATORE")
                        .requestMatchers(HttpMethod.PUT, "/api/eventi/**").hasRole("ANIMATORE")
                        .requestMatchers(HttpMethod.GET, "/api/eventi/miei").hasRole("ANIMATORE")
                        .requestMatchers(HttpMethod.GET, "/api/eventi/{id}/prenotazioni").hasRole("ANIMATORE")
                        .requestMatchers(HttpMethod.POST, "/api/eventi/{eventoId}/invita/{venditoreId}").hasRole("ANIMATORE")
                        .requestMatchers(HttpMethod.GET, "/api/eventi/{eventoId}/invitati").hasRole("ANIMATORE")

                        .anyRequest().authenticated()
                )

                .httpBasic(withDefaults())
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        return http.build();
    }
}