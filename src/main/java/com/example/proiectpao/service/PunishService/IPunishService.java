package com.example.proiectpao.service.PunishService;

import com.example.proiectpao.dtos.PunishDTO;
import com.example.proiectpao.dtos.userDTOs.AssignRoleDTO;
import java.util.concurrent.CompletableFuture;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;

public interface IPunishService {
    /**
     * Metoda mute interzice unui utilizator sa trimita mesaje
     * @param mute (DTO-ul ce contine username-ul, adminul, motivul pentru care a fost mutat si data la care expira sanctiunea)
     * @return true
     */
    @Async
    CompletableFuture<?> mute(PunishDTO mute);

    /**
     * Metoda warn avertizeaza un utilizator
     * @param warn (DTO-ul ce contine username-ul, adminul si motivul pentru care a fost avertizat)
     * @return true
     */
    @Async
    CompletableFuture<?> warn(PunishDTO warn);

    /**
     * Metoda assignRole atribuie un rol unui utilizator.
     * @param userRoleDTO (DTO-ul ce contine username-ul si rolul atribuit)
     * @return true
     */
    @Async
    CompletableFuture<Boolean> assignRole(AssignRoleDTO userRoleDTO);

    /**
     * Metoda ban interzice unui utilizator sa faca orice actiune
     * @param ban (DTO-ul ce contine username-ul, adminul, motivul pentru care a fost banat si data la care expira sanctiunea)
     * @return true
     */
    @Async
    CompletableFuture<?> ban(PunishDTO ban);

    /**
     * Metoda unban ridica sanctiunea de ban a unui utilizator
     * @param user numele utilizatorului
     * @param admin numele adminului care ridica sanctiunea
     * @return true
     */
    @Async
    CompletableFuture<?> unban(String user, String admin);

    /**
     * Metoda getLogs returneaza un fisier cu toate actiunile facute de administratori / moderatori
     * @param admin numele adminului care cere fisierul
     * @return fisierul cu toate actiunile
     */
    @Async
    CompletableFuture<Resource> getLogs(String admin);

    /**
     * Metoda unmute ridica sanctiunea de mute a unui utilizator
     * @param user numele utilizatorului
     * @param admin numele adminului care ridica sanctiunea
     * @return true
     */
    @Async
    CompletableFuture<?> unmute(String user, String admin);
}
