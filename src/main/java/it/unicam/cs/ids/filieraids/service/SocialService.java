package it.unicam.cs.ids.filieraids.service;

import it.unicam.cs.ids.filieraids.event.ContenutoApprovatoEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class SocialService {

    @EventListener
    public void gestisciPubblicazioneSocial(ContenutoApprovatoEvent event) {
        var contenuto = event.getContenuto();

        if (contenuto.isCondivisioneSocial()) {
            System.out.println("\n**************************************************");
            System.out.println("[SISTEMA SOCIAL - OBSERVER]");
            System.out.println(">>> NUOVO POST PUBBLICATO AUTOMATICAMENTE <<<");
            System.out.println("Contenuto: " + contenuto.getDescrizione());
            System.out.println("ID Riferimento: " + contenuto.getId());
            System.out.println("Link: https://filiera-agricola.local/share/" + contenuto.getId());
            System.out.println("**************************************************\n");
        }
    }
}
