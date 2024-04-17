package com.example.proiectpao.service.UserService;

import com.example.proiectpao.collection.User;
import com.example.proiectpao.dtos.AssignRoleDTO;
import com.example.proiectpao.dtos.UserDTO;
import com.example.proiectpao.dtos.UserLoginDTO;
import com.example.proiectpao.dtos.UserRegisterDTO;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IUserService {
    /**
     * Metoda pentru inregistrarea unui utilizator.
     * @param - userRegisterDTO (DTO-ul ce contine datele necesare pentru inregistrare)
     * @return - Utilizatorul inregistrat.
     */
    CompletableFuture<User> register(UserRegisterDTO userRegisterDTO);
    /**
     * Metoda login verifica daca un utilizator exista in baza de date si daca parola este corecta.
     * @param userLoginDTO (DTO-ul ce contine datele necesare pentru logare)
     * @return Utilizatorul logat.
     */
    CompletableFuture<UserDTO> login(UserLoginDTO userLoginDTO) throws IOException;
    /**
     * Metoda displayUser afiseaza un utilizator.
     * @param username numele utilizatorului
     * @return Utilizatorul afisat.
     */
    CompletableFuture<UserDTO> displayUser(String username);
    /**
     * Metoda assignRole atribuie un rol unui utilizator.
     * @param userRoleDTO (DTO-ul ce contine username-ul si rolul atribuit)
     * @return Utilizatorul cu rolul atribuit.
     */
    CompletableFuture<UserDTO> assignRole(AssignRoleDTO userRoleDTO);
    /**
     Metoda downloadUser descarca un fisier JSON cu informatiile despre un utilizator.
     @param username numele utilizatorului
     @return  Fisierul JSON.
     @see <a href=" https://medium.com/@mertcakmak2/object-storage-with-spring-boot-and-aws-s3-64448c91018f"></a>
     */
    CompletableFuture<Resource> downloadUser(String username) throws IOException;
    /**
     * Metoda uploadStats incarca statisticile unui utilizator.
     * @param user numele utilizatorului
     * @param file fisierul JSON cu statisticile
     * @return true daca s-a incarcat cu succes, false altfel.
     */
    public CompletableFuture<Boolean> uploadStats(String user, MultipartFile file);
}
