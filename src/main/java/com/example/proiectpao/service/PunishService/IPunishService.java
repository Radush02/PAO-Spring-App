package com.example.proiectpao.service.PunishService;

import com.example.proiectpao.dtos.PunishDTO;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;

public interface IPunishService {
    @Async
    CompletableFuture<?> mute(PunishDTO mute);

    @Async
    CompletableFuture<?> warn(PunishDTO warn);

    @Async
    CompletableFuture<?> ban(PunishDTO ban);

    @Async
    CompletableFuture<?> unban(String user, String admin);

    @Async
    CompletableFuture<?> unmute(String user, String admin);
}
