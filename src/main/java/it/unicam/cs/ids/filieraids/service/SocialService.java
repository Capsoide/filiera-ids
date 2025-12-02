package it.unicam.cs.ids.filieraids.service;

import it.unicam.cs.ids.filieraids.model.Contenuto;
import org.springframework.stereotype.Service;

@Service
public class SocialService implements ContenutoObserver {

    @Override
    public void update(Contenuto contenuto) {

        if (contenuto.isCondivisioneSocial()) {
            System.out.println("\n**************************************************");
            System.out.println("[SISTEMA SOCIAL]");
            System.out.println(">>> NUOVO POST PUBBLICATO <<<");
            System.out.println("Contenuto: " + contenuto.getDescrizione());
            System.out.println("ID Riferimento: " + contenuto.getId());
            System.out.println("Link: https://filiera-agricola.local/share/" + contenuto.getId());
            System.out.println("**************************************************\n");
        } else {
            System.out.println("[SISTEMA SOCIAL] Contenuto approvato, ma niente social.");
        }
    }
}