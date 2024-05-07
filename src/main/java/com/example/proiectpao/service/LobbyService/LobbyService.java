package com.example.proiectpao.service.LobbyService;

import com.example.proiectpao.collection.Lobby;
import com.example.proiectpao.collection.User;
import com.example.proiectpao.dtos.lobbyDTOs.CreateLobbyDTO;
import com.example.proiectpao.dtos.lobbyDTOs.JoinLobbyDTO;
import com.example.proiectpao.dtos.lobbyDTOs.KickLobbyDTO;
import com.example.proiectpao.dtos.lobbyDTOs.LobbyDTO;
import com.example.proiectpao.enums.Penalties;
import com.example.proiectpao.exceptions.AlreadyExistsException;
import com.example.proiectpao.exceptions.NonExistentException;
import com.example.proiectpao.exceptions.UnauthorizedActionException;
import com.example.proiectpao.repository.LobbyRepository;
import com.example.proiectpao.repository.PunishRepository;
import com.example.proiectpao.repository.UserRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class LobbyService implements ILobbyService {
    private final LobbyRepository lobbyRepository;
    private final UserRepository userRepository;
    private final PunishRepository punishRepository;

    public LobbyService(
            LobbyRepository lobbyRepository,
            UserRepository userRepository,
            PunishRepository punishRepository) {
        this.lobbyRepository = lobbyRepository;
        this.userRepository = userRepository;
        this.punishRepository = punishRepository;
    }

    @Override
    @Async
    public CompletableFuture<List<LobbyDTO>> getLobbies() {
        List<Lobby> lobbies = lobbyRepository.findAll();
        List<LobbyDTO> result = new ArrayList<>();
        for (Lobby l : lobbies) {
            LobbyDTO dto = new LobbyDTO(l.getLobbyLeader(), l.getLobbyName(), l.getPlayers());
            result.add(dto);
        }
        return CompletableFuture.completedFuture(result);
    }

    @Override
    @Async
    public CompletableFuture<String> inLobby(String username) {
        Lobby lobby = lobbyRepository.findLobbyByPlayer(username);
        if(lobby==null){
            return CompletableFuture.completedFuture("");
        }
        return CompletableFuture.completedFuture("{\"name\": \""+lobby.getLobbyName()+"\"}");
    }
    @Override
    @Async
    public CompletableFuture<Boolean> inLobby(String lobby, String username) {
        Lobby l = lobbyRepository.findByLobbyName(lobby);
        if (l == null) {
            return CompletableFuture.completedFuture(false);
        }
        for (String u : l.getPlayers()) {
            if (u.equalsIgnoreCase(username)) {
                return CompletableFuture.completedFuture(true);
            }
        }
        return CompletableFuture.completedFuture(false);
    }
    @Override
    @Async
    public CompletableFuture<Lobby> createLobby(CreateLobbyDTO lobbyDTO) {
        User u = userRepository.findByUsernameIgnoreCase(lobbyDTO.getUsername());
        if (u == null) {
            throw new NonExistentException("Userul nu exista.");
        }
        if (!punishRepository
                .findAllByUserIDAndSanctionAndExpiryDateIsAfter(
                        u.getUserId(), Penalties.Ban, new Date())
                .isEmpty()) {
            throw new UnauthorizedActionException("Ai ban, nu poti crea lobby.");
        }
        if (lobbyRepository.findByLobbyLeader(u.getUsername()) != null) {
            throw new AlreadyExistsException("Userul are deja un lobby creat.");
        }
        Lobby lobby = lobbyRepository.findLobbyByPlayer(u.getUsername());
        if (lobby != null) {
            throw new AlreadyExistsException("Userul este deja in alt lobby.");
        }
        List<String> users = new ArrayList<>();
        users.add(u.getUsername());
        Lobby l =
                new Lobby(
                        String.valueOf(lobbyRepository.count()),
                        u.getUsername(),
                        lobbyDTO.getName(),
                        users);
        lobbyRepository.save(l);
        return CompletableFuture.completedFuture(l);
    }

    @Override
    @Async
    public CompletableFuture<Lobby> joinLobby(JoinLobbyDTO lobbyDTO) {
        if (!lobbyDTO.isAcceptedInvite()) {
            throw new UnauthorizedActionException("Invitatie refuzata");
        }
        User leader = userRepository.findByUsernameIgnoreCase(lobbyDTO.getLobbyLeader());
        User invited = userRepository.findByUsernameIgnoreCase(lobbyDTO.getUsername());
        if (leader == null || invited == null) {
            throw new NonExistentException("Userul nu exista.");
        }
        if (!punishRepository
                .findAllByUserIDAndSanctionAndExpiryDateIsAfter(
                        leader.getUserId(), Penalties.Ban, new Date())
                .isEmpty()) throw new UnauthorizedActionException("Liderul lobby-ului are ban.");
        if (!punishRepository
                .findAllByUserIDAndSanctionAndExpiryDateIsAfter(
                        invited.getUserId(), Penalties.Ban, new Date())
                .isEmpty()) throw new UnauthorizedActionException("Userul invitat are ban.");
        Lobby l = lobbyRepository.findByLobbyLeader(leader.getUsername());
        if (l == null) {
            throw new NonExistentException("Userul nu are lobby creat.");
        }

        Lobby lobby = lobbyRepository.findLobbyByPlayer(invited.getUsername());
        if (lobby != null) {
            throw new AlreadyExistsException("Userul este deja in alt lobby.");
        }
        List<String> users = l.getPlayers();
        if (users.size() >= 5) {
            throw new UnauthorizedActionException("Lobby-ul este plin.");
        }
        users.add(invited.getUsername());
        l.setPlayers(users);
        lobbyRepository.save(l);
        return CompletableFuture.completedFuture(l);
    }
    @Override
    @Async
    public CompletableFuture<List<String>> getLobbyLeaders() {
        List<Lobby> l = lobbyRepository.getLobbyLeaders();
        List<String> result = new ArrayList<>();
        for (Lobby lobby : l) {
            result.add(lobby.getLobbyLeader());
        }
        return CompletableFuture.completedFuture(result);
    }
    @Override
    @Async
    public CompletableFuture<Lobby> kickFromLobby(KickLobbyDTO lobbyDTO) {
        User leader = userRepository.findByUsernameIgnoreCase(lobbyDTO.getLobbyLeader());
        User invited = userRepository.findByUsernameIgnoreCase(lobbyDTO.getUsername());
        if (leader == null || invited == null) {
            throw new NonExistentException("Userul nu exista.");
        }
        Lobby l = lobbyRepository.findByLobbyLeader(leader.getUsername());
        if (l == null) {
            throw new NonExistentException("Userul nu are lobby creat.");
        }

        boolean inLobby = false;
        for (String u : l.getPlayers()) {
            if (u.equals(invited.getUsername())) {
                inLobby = true;
                break;
            }
        }
        if (!inLobby) {
            throw new NonExistentException("Userul nu este in lobby.");
        }
        boolean passLast = false;
        List<String> users = l.getPlayers();
        if (Objects.equals(leader.getUsername(), invited.getUsername()) && users.size() > 1) {
            l.setLobbyLeader(users.get(1));
            passLast = true;
        }
        if (users.size() == 1 && !passLast) {
            lobbyRepository.delete(l);
            return CompletableFuture.completedFuture(null);
        }
        users.remove(invited.getUsername());
        l.setPlayers(users);
        lobbyRepository.save(l);
        return CompletableFuture.completedFuture(l);
    }
}
