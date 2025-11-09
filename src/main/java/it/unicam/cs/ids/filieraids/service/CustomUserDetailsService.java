package it.unicam.cs.ids.filieraids.service;

import it.unicam.cs.ids.filieraids.model.*;
import it.unicam.cs.ids.filieraids.repository.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.*;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AttoreRepository attoreRepository;

    public CustomUserDetailsService(AttoreRepository attoreRepository) {
        this.attoreRepository = attoreRepository;
    }

    //metodo chiamato da springboot quando un utente prova a fare login
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        //cerca l'utente nell'db tramite la mail
        Attore attore = attoreRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Attore non trovato con email: " + email));

        //conversione dei ruoli nella classe enum Ruolo in permessi (GrantedAuthority)
        Set<GrantedAuthority> authorities = attore.getRuoli().stream()
                .map(ruolo -> new SimpleGrantedAuthority("ROLE_" + ruolo.name()))
                .collect(Collectors.toSet());

        //ritorna un oggetto user di spring security
        //spring confronterà la password fornita dall'utente con la password nel DB
        return new User(
                attore.getEmail(),
                attore.getPassword(),
                attore.isEnabled(),
                true,
                true,
                true,
                authorities
        );
    }
}
