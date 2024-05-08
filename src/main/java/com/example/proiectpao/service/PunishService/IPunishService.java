package com.example.proiectpao.service.PunishService;

import com.example.proiectpao.dtos.PunishDTO;
import com.example.proiectpao.dtos.userDTOs.AssignRoleDTO;
import java.util.concurrent.CompletableFuture;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;

public interface IPunishService {
    @Async
    CompletableFuture<?> mute(PunishDTO mute);

    @Async
    CompletableFuture<?> warn(PunishDTO warn);

    /**
     * Metoda assignRole atribuie un rol unui utilizator.
     * @param userRoleDTO (DTO-ul ce contine username-ul si rolul atribuit)
     * @return true
     */
    @Async
    CompletableFuture<Boolean> assignRole(AssignRoleDTO userRoleDTO);

    @Async
    CompletableFuture<?> ban(PunishDTO ban);

    @Async
    CompletableFuture<?> unban(String user, String admin);

    @Async
    CompletableFuture<Resource> getLogs(String admin);

    @Async
    CompletableFuture<?> unmute(String user, String admin);
}
