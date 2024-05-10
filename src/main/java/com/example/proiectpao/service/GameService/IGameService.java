package com.example.proiectpao.service.GameService;

import com.example.proiectpao.collection.MultiplayerGame;
import com.example.proiectpao.enums.Results;
import com.example.proiectpao.utils.Pair;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.core.io.Resource;
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
     * <a href="https://www.hltv.org/matches/2370726/natus-vincere-vs-g2-pgl-cs2-major-copenhagen-2024">NAVI vs G2 in semifinalele majorului de CS2 de la Copenhaga 2024</a> <br>
     * <br>
     * Logica jocului este sa se atace random: <br>
     * <ul>
     *     <li>Generez un nr random intre 1-6 pt a decide cine castiga o runda.</li>
     *     <li>In functie de diferenta dintre nr extrase, se atribuie stats. </li>
     *     <ul>
     *         <li>Diferenta de 4+: Castigatorul primeste 1 kill + 1 hs, pierzatorul 1 death </li>
     *         <li>Diferenta de 3: Castigatorul primeste 1 kill + 3 hits, pierzatorul 1 death</li>
     *         <li>Diferenta de 2: Castigatorul primeste 1 kill + 3 hits, pierzatorul 1 death + 1 hit</li>
     *         <li>Diferenta de 1: Castigatorul primeste 1 kill + 4 hits, pierzatorul 1 death + 2 hits</li>
     *         <li>Egalitate: Se reia runda</li>
     *     </ul>
     * </ul>
     * <br>
     * @param Player1 Numele jucatorului din echipa A
     * @param Player2 Numele jucatorului din echipa B
     * @return Rezultatul meciului (un enum ce specifica daca Player1 a castigat sau a pierdut)
     * @deprecated Inlocuit de {@link #attackTeam(String, String)}
     * @see <a href="https://cs.money/blog/esports/what-is-mr12-in-cs2-mr15-vs-mr12-compared">MR12 explicat</a>
     */
    @Async
    @Deprecated
    CompletableFuture<Results> attack(String Player1, String Player2);

    /**
     * Returneaza rezultatul unui meci.<br>
     * Game logic: <br>
     * MR12 cu OT de 6 runde <br>
     * Se joaca 24 de runde si primul la 13 castiga. <br>
     * In caz de egalitate se joaca inca 6 runde pana cand se alege castigtor. <br>
     * Exemplu de 3 meciuri MR12 cu OT: <br>
     * <a href="https://www.hltv.org/matches/2370726/natus-vincere-vs-g2-pgl-cs2-major-copenhagen-2024">NAVI vs G2 in semifinalele majorului de CS2 de la Copenhaga 2024</a> <br>
     * <br>
     * Logica jocului este sa se atace random:<br>
     * Se da shuffle la jucatorii echipei, apoi se ia primul cu primul, castigatorul ramanand in picioare,
     * iar pierzatorul este scos din lista. Se repeta pana cand o echipa ramane fara jucatori.<br>
     * Pentru a determina cine este scos din lista:
     * <ul>
     *     <li>Generez un nr random intre 1-6 pt a decide cine castiga o runda.</li>
     *     <li>In functie de diferenta dintre nr extrase, se atribuie stats. </li>
     *     <ul>
     *         <li>Diferenta de 4+: Castigatorul primeste 1 kill + 1 hs, pierzatorul 1 death </li>
     *         <li>Diferenta de 3: Castigatorul primeste 1 kill + 3 hits, pierzatorul 1 death</li>
     *         <li>Diferenta de 2: Castigatorul primeste 1 kill + 3 hits, pierzatorul 1 death + 1 hit</li>
     *         <li>Diferenta de 1: Castigatorul primeste 1 kill + 4 hits, pierzatorul 1 death + 2 hits</li>
     *         <li>Egalitate: Se reia runda</li>
     *     </ul>
     * </ul>
     * <br>
     * @param attackerCaptain Numele capitanului din echipa A
     * @param defenderCaptain Numele jucatorului din echipa B
     * @return ID-ul meciului

     * @see <a href="https://cs.money/blog/esports/what-is-mr12-in-cs2-mr15-vs-mr12-compared">MR12 explicat</a>
     */
    @Async
    CompletableFuture<String> attackTeam(String attackerCaptain, String defenderCaptain);
    /**
     * Exporta un joc multiplayer.<br>
     * Extensia fisierului este de tip .sb<br>
     * Format-ul fisierul este astfel:<br><br>
     * &lt;Id-ul jocului&gt;<br>
     * &lt;Capitan echipa 1&gt; vs &lt;Capitan echipa 2&gt;<br>
     * &lt;Nume echipa 1&gt; vs &lt;Nume echipa 2&gt;<br>
     * Scor: &lt;Scor echipa 1&gt; - &lt;Scor echipa 2&gt;<br>
     * Nume player | Kills | Deaths | Headshots | Score<br>
     * &lt;nume jucator 1&gt; | &lt;kills jucator 1&gt; | &lt;deaths jucator 1&gt; | &lt;hs jucator 1&gt; | &lt;2*(kills jucator 1)+1.5*(hs jucator 1)&gt;<br>
     * ...<br>
     * &lt;nume jucator 10&gt; | &lt;kills jucator 10&gt; | &lt;deaths jucator 10&gt; | &lt;hs jucator 10&gt; | &lt;2*(kills jucator 10)+1.5*(hs jucator 10)&gt;<br>
     * [EOF]<br>
     *
     * @param gameId ID-ul jocului
     * @return Pair-ul ce contine fisierul si numele fisierului
     */
    @Async
    CompletableFuture<Pair<Resource, String>> exportMultiplayerGame(String gameId);

    /**
     * Returneaza o lista cu toate jocurile jucate de un user.
     * @param username Numele userului
     * @return O lista cu toate meciurile jucate de user
     */
    @Async
    CompletableFuture<List<MultiplayerGame>> displayMultiplayerGame(String username);

    /**
     * Returneaza un joc dupa ID
     *
     * @param gameId ID-ul meciului
     * @return Meciul jucat
     */
    @Async
    CompletableFuture<MultiplayerGame> getGame(String gameId);

    /**
     * Neimplementat
     */
    @Async
    CompletableFuture<?> importMultiplayerGame(String gameId, MultipartFile file)
            throws IOException;
}
