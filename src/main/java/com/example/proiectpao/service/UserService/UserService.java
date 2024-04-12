package com.example.proiectpao.service.UserService;

import static com.example.proiectpao.enums.Role.Admin;
import static com.example.proiectpao.enums.Role.Moderator;

import com.example.proiectpao.collection.Stats;
import com.example.proiectpao.collection.User;
import com.example.proiectpao.dtos.*;
import com.example.proiectpao.repository.UserRepository;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/*
 * Clasa UserService implementeaza interfata IUserService si
 * contine metodele necesare pentru inregistrarea si logarea unui utilizator.
 */
@Service
public class UserService implements IUserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDTO configureDTO(User k) {
        UserDTO u = new UserDTO();
        u.setUserId(k.getUserId());
        u.setUsername(k.getUsername());
        u.setRole(k.getRole());
        u.setName(k.getName());
        u.setEmail(k.getEmail());
        Stats w = k.getStats();
        double wr = 0, kdr = 0, hsp = 0;
        if (w.getWins() + w.getLosses() != 0)
            wr = (double) w.getWins() / (w.getWins() + w.getLosses());
        if (k.getStats().getDeaths() != 0) kdr = (double) w.getKills() / w.getDeaths();
        if (k.getStats().getHits() != 0) hsp = (double) w.getHeadshots() / w.getHits();

        StatsDTO stats =
                StatsDTO.builder()
                        .wins(w.getWins())
                        .losses(w.getLosses())
                        .WR(wr)
                        .HSp(hsp)
                        .KDR(kdr)
                        .kills(w.getKills())
                        .deaths(w.getDeaths())
                        .hits(w.getHits())
                        .headshots(w.getHeadshots())
                        .build();
        u.setStats(stats);
        return u;
    }

    @Override
    @Async
    public CompletableFuture<User> register(UserRegisterDTO userRegisterDTO) {
        if (userRepository.findByUsernameIgnoreCase(userRegisterDTO.getUsername()) != null) {
            return CompletableFuture.completedFuture(null);
        }
        User u = new User();
        u.setUserId(UUID.randomUUID().toString().split("-")[0]);
        u.setUsername(userRegisterDTO.getUsername());
        u.setEmail(userRegisterDTO.getEmail());
        u.setName(userRegisterDTO.getName());
        u.setStats(Stats.builder().build());
        String password = userRegisterDTO.getPassword();
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        String encodedSalt = Base64.getEncoder().encodeToString(salt);
        u.setSeed(encodedSalt);
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (md != null) {
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
            String encodedHash = Base64.getEncoder().encodeToString(hashedPassword);
            u.setHash(encodedHash);
        }
        return CompletableFuture.completedFuture(userRepository.save(u));
    }

    @Override
    @Async
    public CompletableFuture<UserDTO> login(UserLoginDTO userLoginDTO) throws IOException {
        User k = userRepository.findByUsernameIgnoreCase(userLoginDTO.getUsername());
        if (k == null) {
            return CompletableFuture.completedFuture(null);
        }
        UserDTO u = configureDTO(k);
        String password = userLoginDTO.getPassword();
        byte[] salt = Base64.getDecoder().decode(k.getSeed());
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (md != null) {
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
            String encodedHash = Base64.getEncoder().encodeToString(hashedPassword);
            if (encodedHash.equals(k.getHash())) {
                if (k.getRole() == Admin || k.getRole() == Moderator) {
                    try (FileWriter fw =
                            new FileWriter(
                                    "src/main/java/com/example/proiectpao/logs/adminlog.csv",
                                    true)) {
                        String w =
                                "\n\""
                                        + k.getUsername()
                                        + "\", "
                                        + k.getRole()
                                        + "\", Logged in @ "
                                        + java.time.LocalDateTime.now()
                                        + "\"";
                        fw.write(w);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return CompletableFuture.completedFuture(u);
            }
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    @Async
    public CompletableFuture<UserDTO> displayUser(String username) {
        User k = userRepository.findByUsernameIgnoreCase(username);
        if (k == null) {
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.completedFuture(configureDTO(k));
    }

    @Override
    @Async
    public CompletableFuture<UserDTO> assignRole(AssignRoleDTO userRoleDTO) {
        User k = userRepository.findByUsernameIgnoreCase(userRoleDTO.getUsername());
        User adm = userRepository.findById(userRoleDTO.getPossibleAdminID()).orElse(null);
        if (k == null || adm==null) {
            return CompletableFuture.completedFuture(null);
        }
        try {
            k.setRole(userRoleDTO.getRole());
        } catch (IllegalArgumentException e) {
            return CompletableFuture.completedFuture(null);
        }
        userRepository.save(k);
        return CompletableFuture.completedFuture(configureDTO(k));
    }



}
