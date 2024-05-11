package com.example.proiectpao.service.UserService;

import com.example.proiectpao.collection.User;
import com.example.proiectpao.dtos.userDTOs.UserDTO;
import com.example.proiectpao.dtos.userDTOs.UserLoginDTO;
import com.example.proiectpao.dtos.userDTOs.UserRegisterDTO;
import com.example.proiectpao.utils.Pair;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

public interface IUserService {
    /**
     * Metoda pentru inregistrarea unui utilizator.<br>
     * Se introduc in baza de date urm. informatii: username-ul, parola, email-ul si numele acestuia.
     * Pentru a fi siguri ca parola nu poate fi extrasa, aceasta este criptata folosind SHA-256.
     * @param userRegisterDTO (DTO-ul ce contine datele necesare pentru inregistrare)
     * @return Utilizatorul inregistrat.
     */
    @Async
    CompletableFuture<User> register(UserRegisterDTO userRegisterDTO);

    /**
     * Metoda login conecteaza un user.<br>
     * Metoda verifica daca un utilizator exista in baza de date si daca parola este corecta.
     * Daca este admin sau moderator, se salveaza logarea acestuia in admin logs.
     * @param userLoginDTO (DTO-ul ce contine datele necesare pentru logare)
     * @return Utilizatorul logat.
     * @throws IOException daca nu se poate scrie in admin logs cand s-a logat adminul/moderatorul.
     */
    @Async
    CompletableFuture<UserDTO> login(UserLoginDTO userLoginDTO) throws IOException;

    /**
     * Metoda displayUser afiseaza informatiile un utilizator.<br>
     * Afiseaza informatii valabile doar pentru user, precum numele acestuia, email-ul, rolul(user/admin/mod) si statisticile.
     * @param username numele utilizatorului
     * @return Utilizatorul afisat.
     */
    @Async
    CompletableFuture<UserDTO> displayUser(String username);

    /**
     * Metoda downloadUser descarca un fisier JSON cu informatiile despre un utilizator.
     * @param username numele utilizatorului
     * @return  Un pair ce contine fisierul efectiv si numele acestuia.
     * @throws IOException daca nu se poate crea fisierul.
     */
    @Async
    CompletableFuture<Pair<Resource, String>> downloadUser(String username) throws IOException;

    /**
     * Metoda uploadStats incarca statisticile unui utilizator.
     * Un fisier trebuie sa fie <b>exact</b> acelasi cu un fisier deja creat ca back-up.
     * @param user numele utilizatorului
     * @param file fisierul JSON cu statisticile
     * @return true daca s-a incarcat cu succes, false altfel.
     * @see UserService#downloadUser(String)
     */
    @Async
    CompletableFuture<Boolean> uploadStats(String user, MultipartFile file);

    /**
     * Metoda getFriends returneaza lista de prieteni a unui utilizator.
     * @param username numele utilizatorului
     * @return lista de prieteni a utilizatorului.
     */
    @Async
    CompletableFuture<List<String>> getFriends(String username);
}
