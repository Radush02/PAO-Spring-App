package com.example.proiectpao.service.FriendRequestsService;

import com.example.proiectpao.dtos.FriendRequestDTO;
import com.example.proiectpao.dtos.FriendRequestResponseDTO;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;

public interface IFriendRequestService {

    /**
     * Trimite o cerere de prietenie unui user
     * @param friend Un DTO ce contine cel ce trimite cererea si cel care o primeste
     * @return true
     */
    CompletableFuture<Boolean> addFriendRequest(FriendRequestDTO friend);

    /**
     * Anuleaza o cerere de prietenie
     * @param friend Un DTO ce contine cel ce vrea sa anuleze cererea si userul pentru care vrea sa o anuleze
     * @return true
     */
    @Async
    CompletableFuture<Boolean> deleteFriendRequest(FriendRequestDTO friend);

    /**
     * Preia toate cererile de prietenie trimise.
     * @param username username-ul utilizatorului
     * @return O lista de DTO-uri ce contine userii ce au trimis cereri si numele user-ului
     */
    @Async
    CompletableFuture<List<FriendRequestDTO>> sentRequests(String username);

    /**
     * Preia toate cererile de prietenie primite
     * @param username username-ul utilizatorului
     * @return O lista de DTO-uri ce contine userii ce au primit cereri si numele user-ului
     */
    @Async
    CompletableFuture<List<FriendRequestDTO>> getRequests(String username);

    /**
     * Raspunde unei cereri de prietenie.
     * @param friend un DTO ce contine cel ce a trimis cererea, userul ce a primit cererea si raspunsul user-ului ce a primit cererea.
     * @return true daca userul accepta cererea, false daca o respinge
     */
    @Async
    CompletableFuture<Boolean> friendRequestResponse(FriendRequestResponseDTO friend);
}
