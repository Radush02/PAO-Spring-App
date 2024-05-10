package com.example.proiectpao.controller;

import com.example.proiectpao.dtos.PunishDTO;
import com.example.proiectpao.dtos.UnpunishDTO;
import com.example.proiectpao.dtos.userDTOs.AssignRoleDTO;
import com.example.proiectpao.exceptions.NonExistentException;
import com.example.proiectpao.service.PunishService.IPunishService;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/punish")
@CrossOrigin(origins = "http://localhost:4200")
public class PunishController {
    @Autowired private IPunishService punishService;

    /**
     * API POST pentru a da ban unui utilizator.
     * @param banDTO (DTO-ul ce contine numele utilizatorului ce primeste ban-ul, numele adminului, data la care expira si motivul)
     * @return true sau mesajul erorii.
     */
    @PostMapping("/ban")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> ban(@RequestBody PunishDTO banDTO) {
        try {
            CompletableFuture<?> user = punishService.ban(banDTO);
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        } catch (NonExistentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API POST pentru a da mute unui utilizator.
     * @param muteDTO (DTO-ul ce contine numele utilizatorului ce primeste mute-ul, numele adminului, data la care expira si motivul)
     * @return true sau mesajul erorii.
     */
    @PostMapping("/mute")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> mute(@RequestBody PunishDTO muteDTO) {
        try {
            CompletableFuture<?> user = punishService.mute(muteDTO);
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        } catch (NonExistentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API POST pentru a avertiza un utilizator.
     * @param warnDTO (DTO-ul ce contine numele utilizatorului ce primeste avertizarea, numele adminului si motivul)
     * @return
     */
    @PostMapping("/warn")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> warn(@RequestBody PunishDTO warnDTO) {
        try {
            warnDTO.setExpiryDate(null);
            CompletableFuture<?> user = punishService.warn(warnDTO);
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        } catch (NonExistentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API POST pentru a atribui un rol unui utilizator.
     * @param userRoleDTO (DTO-ul ce contine numele utilizatorului, adminul ce atribuie rolul si rolul atribuit)
     * @return true sau mesajul erorii.
     */
    @PostMapping("/assignRole")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> assignRole(@RequestBody AssignRoleDTO userRoleDTO) {
        try {
            CompletableFuture<Boolean> user = punishService.assignRole(userRoleDTO);
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        } catch (NonExistentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API GET pentru a prelua toate actiunile facute de administratori / moderatori.
     * @param admin numele adminului care cere fisierul
     * @return Fisierul cu toate actiunile sau mesajul erorii.
     */
    @GetMapping("/getLogs")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getLogs(@RequestParam String admin) {
        try {
            CompletableFuture<?> logs = punishService.getLogs(admin);
            return new ResponseEntity<>(logs.get(), HttpStatus.OK);
        } catch (NonExistentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API POST pentru a ridica sanctiunea de ban a unui utilizator.
     * @param u (DTO-ul ce contine numele utilizatorului si numele adminului care ridica sanctiunea)
     * @return Daca s-a ridicat banul (userul poate fi banat sau nu) sau mesajul erorii.
     */
    @PostMapping("/unban")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> unban(@RequestBody UnpunishDTO u) {
        try {
            CompletableFuture<?> logs = punishService.unban(u.getUser(), u.getAdmin());
            return new ResponseEntity<>(logs.get(), HttpStatus.OK);
        } catch (NonExistentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API POST pentru a ridica sanctiunea de mute a unui utilizator.
     * @param u (DTO-ul ce contine numele utilizatorului si numele adminului care ridica sanctiunea)
     * @return Daca s-a ridicat mute-ul (userul poate avea mute sau nu) sau mesajul erorii.
     */
    @PostMapping("/unmute")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> unmute(@RequestBody UnpunishDTO u) {
        try {
            CompletableFuture<?> logs = punishService.unmute(u.getUser(), u.getAdmin());
            return new ResponseEntity<>(logs.get(), HttpStatus.OK);
        } catch (NonExistentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
