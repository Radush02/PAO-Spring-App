package com.example.proiectpao.service.GameService;

import com.example.proiectpao.enums.Results;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

public interface IGameService {
    /**
     * Returneaza rezultatul unui meci.<br>
     * Game logic: <br>
     * MR12 cu OT de 6 runde <br>
     * Se joaca 24 de runde si primul la 13 castiga. <br>
     * In caz de egalitate se joaca inca 6 runde pana cand se alege castigtor. <br>
     * Exemplu de 3 meciuri MR12 cu OT: <br>
     * <a href="https://www.hltv.org/matches/2370726/natus-vincere-vs-g2-pgl-cs2-major-copenhagen-2024">...</a> <br>
     * <br>
     * Momentan logica jocului este sa se atace random: <br>
     * <ul>
     *     <li>Generez un nr random intre 1-6 pt a decide cine castiga o runda.</li>
     *     <li>In functie de nr extrase, se atribuie stats. </li>
     *     <ul>
     *         <li>todo</li>
     *     </ul>
     * </ul>
     * <br>
     * @param Player1 Numele jucatorului din echipa A
     * @param Player2 Numele jucatorului din echipa B
     * @return Rezultatul meciului
     */
    @Async
    CompletableFuture<Results> attack(String Player1, String Player2);

    @Async
    CompletableFuture<?> attackTeam(String attackerCaptain, String defenderCaptain);

    @Async
    CompletableFuture<?> exportMultiplayerGame(String gameId);

    @Async
    CompletableFuture<?> importMultiplayerGame(String gameId, MultipartFile file) throws IOException;
}
