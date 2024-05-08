package com.example.proiectpao.service.UserService;

import com.example.proiectpao.collection.User;
import com.example.proiectpao.dtos.userDTOs.UserDTO;
import com.example.proiectpao.dtos.userDTOs.UserLoginDTO;
import com.example.proiectpao.dtos.userDTOs.UserRegisterDTO;
import com.example.proiectpao.utils.Pair;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

public interface IUserService {
    /**
     * Metoda pentru inregistrarea unui utilizator.
     * @param - userRegisterDTO (DTO-ul ce contine datele necesare pentru inregistrare)
     * @return - Utilizatorul inregistrat.
     */
    @Async
    CompletableFuture<User> register(UserRegisterDTO userRegisterDTO);

    /**
     * Metoda login verifica daca un utilizator exista in baza de date si daca parola este corecta.
     * @param userLoginDTO (DTO-ul ce contine datele necesare pentru logare)
     * @return Utilizatorul logat.
     */
    @Async
    CompletableFuture<UserDTO> login(UserLoginDTO userLoginDTO) throws IOException;

    /**
     * Metoda displayUser afiseaza un utilizator.
     * @param username numele utilizatorului
     * @return Utilizatorul afisat.
     */
    @Async
    CompletableFuture<UserDTO> displayUser(String username);

    /**
     * Metoda downloadUser descarca un fisier JSON cu informatiile despre un utilizator.
     * @param username numele utilizatorului
     * @return  Un pair ce contine fisierul efectiv si numele acestuia.
     * @see <a href=" https://medium.com/@mertcakmak2/object-storage-with-spring-boot-and-aws-s3-64448c91018f"></a>
     */
    @Async
    CompletableFuture<Pair<Resource, String>> downloadUser(String username) throws IOException;

    /**
     * Metoda uploadStats incarca statisticile unui utilizator.
     * @param user numele utilizatorului
     * @param file fisierul JSON cu statisticile
     * @return true daca s-a incarcat cu succes, false altfel.
     */
    @Async
    CompletableFuture<Boolean> uploadStats(String user, MultipartFile file);
}
