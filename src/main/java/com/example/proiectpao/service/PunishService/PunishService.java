package com.example.proiectpao.service.PunishService;

import static com.example.proiectpao.enums.Role.Admin;

import com.example.proiectpao.collection.AdminLog;
import com.example.proiectpao.collection.Punish;
import com.example.proiectpao.collection.User;
import com.example.proiectpao.dtos.PunishDTO;
import com.example.proiectpao.dtos.userDTOs.AssignRoleDTO;
import com.example.proiectpao.enums.Actions;
import com.example.proiectpao.enums.Penalties;
import com.example.proiectpao.enums.Role;
import com.example.proiectpao.exceptions.NonExistentException;
import com.example.proiectpao.exceptions.UnauthorizedActionException;
import com.example.proiectpao.repository.AdminLogRepository;
import com.example.proiectpao.repository.PunishRepository;
import com.example.proiectpao.repository.UserRepository;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class PunishService implements IPunishService {

    private final UserRepository userRepository;
    private final PunishRepository punishRepository;
    private final AdminLogRepository adminLogRepository;

    public PunishService(
            UserRepository userRepository,
            PunishRepository punishRepository,
            AdminLogRepository adminLogRepository) {
        this.userRepository = userRepository;
        this.punishRepository = punishRepository;
        this.adminLogRepository = adminLogRepository;
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
                        .id(String.valueOf(punishRepository.count()))
                        .userID(u.getUserId())
                        .adminID(a.getUserId())
                        .expiryDate(mute.getExpiryDate())
                        .reason(mute.getReason())
                        .sanction(Penalties.Mute)
                        .expiryDate(mute.getExpiryDate())
                        .build();
        trackAction("i-a dat mute lui ", a, u, mute.getExpiryDate());
        this.adminLogRepository.save(
                new AdminLog(a.getUsername(), u.getUsername(), Actions.Mute, mute.getExpiryDate()));
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
                        .id(String.valueOf(punishRepository.count()))
                        .userID(u.getUserId())
                        .adminID(a.getUserId())
                        .reason(warn.getReason())
                        .sanction(Penalties.Warn)
                        .build();
        trackAction("a acordat warn lui ", a, u, null);
        this.adminLogRepository.save(new AdminLog(a.getUsername(), u.getUsername(), Actions.Warn));
        if (punishRepository.findByUserIDAndSanction(u.getUserId(), Penalties.Warn) != null) {
            punishRepository.delete(
                    punishRepository.findByUserIDAndSanction(u.getUserId(), Penalties.Warn));
            LocalDate now = LocalDate.now();
            LocalDate expiry = now.plusDays(3);
            ban(
                    new PunishDTO(
                            warn.getUsername(),
                            warn.getAdmin(),
                            "2/2 warn",
                            Date.from(expiry.atStartOfDay(ZoneId.systemDefault()).toInstant())));

        } else punishRepository.save(p);
        return CompletableFuture.completedFuture(true);
    }

    /**
     * Metoda assignRole atribuie un rol unui utilizator.
     * @param userRoleDTO (DTO-ul ce contine username-ul, rolul atribuit si adminul care a schimbat rolul)
     * @return true
     */
    @Override
    @Async
    public CompletableFuture<Boolean> assignRole(AssignRoleDTO userRoleDTO) {
        User k = userRepository.findByUsernameIgnoreCase(userRoleDTO.getUsername());
        User adm = userRepository.findByUsernameIgnoreCase(userRoleDTO.getAdmin());
        if (k == null || adm == null) {
            throw new NonExistentException("Userul nu exista.");
        }
        if (adm.getRole() != Admin) {
            throw new UnauthorizedActionException("Nu ai suficiente permisiuni.");
        }
        try {
            k.setRole(userRoleDTO.getRole());
        } catch (IllegalArgumentException e) {
            throw new NonExistentException("Rolul nu exista.");
        }
        System.out.println(userRoleDTO.getRole());
        trackAction("a atribuit rolul de " + userRoleDTO.getRole() + " lui ", adm, k, null);
        this.adminLogRepository.save(
                new AdminLog(adm.getUsername(), k.getUsername(), Actions.AssignedRole));
        userRepository.save(k);
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
                        .id(String.valueOf(punishRepository.count()))
                        .userID(u.getUserId())
                        .adminID(a.getUserId())
                        .expiryDate(ban.getExpiryDate())
                        .reason(ban.getReason())
                        .sanction(Penalties.Ban)
                        .build();
        trackAction("a banat pe", a, u, ban.getExpiryDate());
        this.adminLogRepository.save(
                new AdminLog(a.getUsername(), u.getUsername(), Actions.Ban, ban.getExpiryDate()));
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
        Map<String, String> response = new HashMap<>();
        List<Punish> pnsh =
                punishRepository.findAllByUserIDAndSanctionAndExpiryDateIsAfter(
                        u.getUserId(), Penalties.Ban, new Date());
        for (Punish p : pnsh) {
            punishRepository.delete(p);
            trackAction("a debanat pe ", a, u, null);
            this.adminLogRepository.save(
                    new AdminLog(a.getUsername(), u.getUsername(), Actions.Unban));
        }
        if (!pnsh.isEmpty()) {
            response.put("message", "A primit unban");
            return CompletableFuture.completedFuture(response);
        }
        trackAction("a incercat sa debaneze pe ", a, u, null);
        response.put("message", "Nu e banat");
        return CompletableFuture.completedFuture(response);
    }

    @Override
    @Async
    public CompletableFuture<Resource> getLogs(String admin) {
        User a = userRepository.findByUsernameIgnoreCase(admin);
        if (a == null) throw new NonExistentException("Adminul nu exista.");
        else if (a.getRole() == Role.User) {
            throw new UnauthorizedActionException("Nu ai suficiente permisiuni.");
        }
        try (FileWriter fw =
                new FileWriter("src/main/java/com/example/proiectpao/logs/adminlog.csv", true)) {
            String w =
                    "\n\""
                            + a.getUsername()
                            + "\", "
                            + a.getRole()
                            + "\", "
                            + "a extras logurile, "
                            + java.time.LocalDateTime.now()
                            + "\"";
            fw.write(w);
            this.adminLogRepository.save(new AdminLog(a.getUsername(), Actions.Export));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return CompletableFuture.completedFuture(
                    new InputStreamResource(
                            new FileInputStream(
                                    "src/main/java/com/example/proiectpao/logs/adminlog.csv")));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
        Map<String, String> response = new HashMap<>();
        List<Punish> pnsh =
                punishRepository.findAllByUserIDAndSanctionAndExpiryDateIsAfter(
                        u.getUserId(), Penalties.Mute, new Date());
        for (Punish p : pnsh) {
            punishRepository.delete(p);
            trackAction("a dat unmute lui ", a, u, null);
            this.adminLogRepository.save(
                    new AdminLog(a.getUsername(), u.getUsername(), Actions.Unmute));
        }
        if (!pnsh.isEmpty()) {
            response.put("message", "A primit unmute");
            return CompletableFuture.completedFuture(response);
        }
        trackAction("a incercat sa dea unmute ", a, u, null);
        response.put("message", "Nu e muted");
        return CompletableFuture.completedFuture(response);
    }

    @Override
    @Async
    public CompletableFuture<?> revertAction(String user, String admin) {
        return revertAction(user, admin, null);
    }

    @Override
    @Async
    public CompletableFuture<?> revertAction(String user, String admin, Date expiryDate) {
        User u = userRepository.findByUsernameIgnoreCase(user);
        User a = userRepository.findByUsernameIgnoreCase(admin);

        if (u == null || a == null) {
            throw new NonExistentException("Userul sau adminul nu exista.");
        } else if (a.getRole() != Role.Admin && a.getRole() != Role.Moderator) {
            throw new UnauthorizedActionException("Nu ai suficiente permisiuni.");
        }

        Map<String, String> response = new HashMap<>();
        List<AdminLog> actions =
                adminLogRepository.findAllByAdminAndUser(a.getUsername(), u.getUsername());

        actions.removeIf(adminLog -> !isValidAction(adminLog.getAction(), expiryDate));

        if (actions.isEmpty()) {
            response.put(
                    "message",
                    "Acest admin nu a interacitonat cu acel user. Asigura-te ca nu dai revert la"
                            + " revert.");
            return CompletableFuture.completedFuture(response);
        }

        AdminLog lastAction = actions.getLast();

        handleRevertAction(lastAction.getAction(), u, a, expiryDate, response);

        adminLogRepository.save(new AdminLog(a.getUsername(), u.getUsername(), Actions.Revert));
        trackAction("A anulat/restaurat sanctiunea lui", a, u, expiryDate);

        return CompletableFuture.completedFuture(response);
    }

    private boolean isValidAction(Actions action, Date expiryDate) {
        if (expiryDate == null) {
            return action == Actions.Mute || action == Actions.Ban || action == Actions.Warn;
        } else {
            return action == Actions.Unmute || action == Actions.Unban;
        }
    }

    private void handleRevertAction(
            Actions action, User u, User a, Date expiryDate, Map<String, String> response) {
        switch (action) {
            case Mute -> {
                unmute(u.getUsername(), a.getUsername());
                response.put("message", "A fost dat unmute");
            }
            case Ban -> {
                unban(u.getUsername(), a.getUsername());
                response.put("message", "A fost dat unban");
            }
            case Warn -> {
                punishRepository.delete(
                        punishRepository.findByUserIDAndSanction(u.getUserId(), Penalties.Warn));
                response.put("message", "A fost sters warn-ul");
            }
            case Unban -> {
                ban(new PunishDTO(u.getUsername(), a.getUsername(), "Revert", expiryDate));
                response.put("message", "A fost dat ban");
            }
            case Unmute -> {
                mute(new PunishDTO(u.getUsername(), a.getUsername(), "Revert", expiryDate));
                response.put("message", "A fost dat mute");
            }
        }
    }
}
