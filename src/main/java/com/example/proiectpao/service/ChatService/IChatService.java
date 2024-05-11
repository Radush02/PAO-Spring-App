package com.example.proiectpao.service.ChatService;

import com.example.proiectpao.dtos.ChatDTO;
import com.example.proiectpao.dtos.ImportMessageDTO;
import com.example.proiectpao.dtos.MessageDTO;
import com.example.proiectpao.dtos.MessageExportDTO;
import com.example.proiectpao.utils.Pair;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;

public interface IChatService {

    @Async
    CompletableFuture<Boolean> importChat(ImportMessageDTO dto) throws IOException;

    /**
     * Exporta mesajele dintre 2 useri.<br>
     * <p>
     * Un mesaj poate fi exportat fie de userii ce vorbesc, fie de un administrator (chiar daca acesta nu este unul dintre cei doi useri din discutie).
     * @param dto Un DTO ce contine userul care vrea sa exporteze mesajele si cei doi useri ce vorbesc.
     * @return Un pair ce contine fisierul efectiv si numele acestuia.
     * @throws IOException Daca fisierul nu se poate crea.
     */
    @Async
    CompletableFuture<Pair<Resource, String>> exportChat(MessageExportDTO dto) throws IOException;

    /**
     * Trimite un mesaj.
     * @param chat un DTO ce contine numele user-ului ce trimite mesajul si mesajul in sine.
     * @param receiver numele user-ului ce primeste mesajul
     * @return true
     */
    @Async
    CompletableFuture<Boolean> send(ChatDTO chat, String receiver);

    /**
     * Primeste un mesaj.
     * @param senderName numele celui cu care user-ul vorbeste
     * @param username numele user-ului
     * @return O lista de DTO-uri ce contine mesajele userilor si data la care au fost trimise acestea.
     */
    @Async
    CompletableFuture<List<MessageDTO>> receive(String senderName, String username);
}
