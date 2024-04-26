package com.example.proiectpao.service.PunishService;

import com.example.proiectpao.collection.Punish;
import com.example.proiectpao.collection.User;
import com.example.proiectpao.dtos.PunishDTO;
import com.example.proiectpao.enums.Penalties;
import com.example.proiectpao.enums.Role;
import com.example.proiectpao.exceptions.NonExistentException;
import com.example.proiectpao.exceptions.UnauthorizedActionException;
import com.example.proiectpao.repository.PunishRepository;
import com.example.proiectpao.repository.UserRepository;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class PunishService implements IPunishService {

    private final UserRepository userRepository;
    private final PunishRepository punishRepository;

    public PunishService(UserRepository userRepository, PunishRepository punishRepository) {
        this.userRepository = userRepository;
        this.punishRepository = punishRepository;
    }

    @Override
    @Async
    public CompletableFuture<?> mute(PunishDTO mute) {
        User u = userRepository.findByUsernameIgnoreCase(mute.getUsername());
        User a = userRepository.findByUsernameIgnoreCase(mute.getAdmin());
        if (u == null || a == null) throw new NonExistentException("Userul sau adminul nu exista.");
        else if (a.getRole() == Role.User || u.getRole() == Role.Admin) {
            throw new UnauthorizedActionException("Nu ai suficiente permisiuni.");
        }

        Punish p =
                Punish.builder()
                        .userID(u.getUserId())
                        .adminID(a.getUserId())
                        .expiryDate(mute.getExpiryDate())
                        .reason(mute.getReason())
                        .sanction(Penalties.Mute)
                        .expiryDate(mute.getExpiryDate())
                        .build();
        trackAction("i-a dat mute lui ", a, u, mute.getExpiryDate());
        punishRepository.save(p);
        return CompletableFuture.completedFuture(true);
    }

    @Override
    @Async
    public CompletableFuture<?> warn(PunishDTO warn) {
        User u = userRepository.findByUsernameIgnoreCase(warn.getUsername());
        User a = userRepository.findByUsernameIgnoreCase(warn.getAdmin());
        if (u == null || a == null) throw new NonExistentException("Userul sau adminul nu exista.");
        else if (a.getRole() == Role.User || u.getRole() == Role.Admin) {
            throw new UnauthorizedActionException("Nu ai suficiente permisiuni.");
        }
        Punish p =
                Punish.builder()
                        .userID(u.getUserId())
                        .adminID(a.getUserId())
                        .reason(warn.getReason())
                        .sanction(Penalties.Warn)
                        .build();
        trackAction("a acordat warn lui ", a, u, null);
        if (punishRepository.findByUserIDAndSanction(u.getUserId(), Penalties.Warn) != null) {
            LocalDate now = LocalDate.now();
            LocalDate expiry = now.plusDays(3);
            ban(
                    new PunishDTO(
                            warn.getUsername(),
                            warn.getAdmin(),
                            "2/2 warn",
                            Date.from(expiry.atStartOfDay(ZoneId.systemDefault()).toInstant())));
            punishRepository.delete(
                    punishRepository.findByUserIDAndSanction(u.getUserId(), Penalties.Warn));
        } else punishRepository.save(p);
        return CompletableFuture.completedFuture(true);
    }

    @Override
    @Async
    public CompletableFuture<?> ban(PunishDTO ban) {
        User u = userRepository.findByUsernameIgnoreCase(ban.getUsername());
        User a = userRepository.findByUsernameIgnoreCase(ban.getAdmin());
        if (u == null || a == null) throw new NonExistentException("Userul sau adminul nu exista.");
        else if (a.getRole() != Role.Admin || u.getRole() == Role.Admin) {
            throw new UnauthorizedActionException("Nu ai suficiente permisiuni.");
        }
        Punish p =
                Punish.builder()
                        .userID(u.getUserId())
                        .adminID(a.getUserId())
                        .expiryDate(ban.getExpiryDate())
                        .reason(ban.getReason())
                        .sanction(Penalties.Ban)
                        .build();
        trackAction("a banat pe", a, u, ban.getExpiryDate());
        punishRepository.save(p);
        return CompletableFuture.completedFuture(true);
    }

    private void trackAction(String Action, User staff, User sanctioned, Date expiryDate) {
        try (FileWriter fw =
                new FileWriter("src/main/java/com/example/proiectpao/logs/adminlog.csv", true)) {
            String w =
                    "\n\""
                            + staff.getUsername()
                            + "\", "
                            + staff.getRole()
                            + "\", "
                            + Action
                            + ", \""
                            + sanctioned.getUsername()
                            + "\", de la "
                            + java.time.LocalDateTime.now();
            if (expiryDate != null) w += " pana la " + expiryDate;
            w += "\"";
            fw.write(w);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    @Async
    public CompletableFuture<?> unban(String user, String admin) {
        User u = userRepository.findByUsernameIgnoreCase(user);
        User a = userRepository.findByUsernameIgnoreCase(admin);
        if (u == null || a == null) throw new NonExistentException("Userul sau adminul nu exista.");
        else if (a.getRole() != Role.Admin) {
            throw new UnauthorizedActionException("Nu ai suficiente permisiuni.");
        }
        List<Punish> pnsh =
                punishRepository.findAllByUserIDAndSanctionAndExpiryDateIsAfter(
                        u.getUserId(), Penalties.Ban, new Date());
        for (Punish p : pnsh) {
            punishRepository.delete(p);
            trackAction("a debanat pe ", a, u, null);
        }
        if (!pnsh.isEmpty()) {
            return CompletableFuture.completedFuture("Debanat");
        }
        trackAction("a incercat sa debaneze pe ", a, u, null);
        return CompletableFuture.completedFuture("Nu e banat");
    }

    @Override
    @Async
    public CompletableFuture<?> unmute(String user, String admin) {
        User u = userRepository.findByUsernameIgnoreCase(user);
        User a = userRepository.findByUsernameIgnoreCase(admin);
        if (u == null || a == null) throw new NonExistentException("Userul sau adminul nu exista.");
        else if (a.getRole() != Role.Admin) {
            throw new UnauthorizedActionException("Nu ai suficiente permisiuni.");
        }
        List<Punish> pnsh =
                punishRepository.findAllByUserIDAndSanctionAndExpiryDateIsAfter(
                        u.getUserId(), Penalties.Mute, new Date());
        for (Punish p : pnsh) {
            punishRepository.delete(p);
            trackAction("a dat unmute lui ", a, u, null);
        }
        if (!pnsh.isEmpty()) {
            return CompletableFuture.completedFuture("A primit unmute");
        }
        trackAction("a incercat sa dea unmute ", a, u, null);
        return CompletableFuture.completedFuture("Nu e muted");
    }
}
