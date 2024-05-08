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
