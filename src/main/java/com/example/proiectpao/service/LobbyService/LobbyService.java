package com.example.proiectpao.service.LobbyService;

import com.example.proiectpao.collection.Lobby;
import com.example.proiectpao.collection.User;
import com.example.proiectpao.dtos.CreateLobbyDTO;
import com.example.proiectpao.dtos.JoinLobbyDTO;
import com.example.proiectpao.dtos.KickLobbyDTO;
import com.example.proiectpao.exceptions.AlreadyExistsException;
import com.example.proiectpao.exceptions.NonExistentException;
import com.example.proiectpao.exceptions.UnauthorizedActionException;
import com.example.proiectpao.repository.LobbyRepository;
import com.example.proiectpao.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class LobbyService implements ILobbyService {
    @Autowired private final LobbyRepository lobbyRepository;
    private final UserRepository userRepository;

    public LobbyService(LobbyRepository lobbyRepository, UserRepository userRepository) {
        this.lobbyRepository = lobbyRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Async
    public CompletableFuture<Lobby> createLobby(CreateLobbyDTO lobbyDTO) {
        User u = userRepository.findByUsernameIgnoreCase(lobbyDTO.getUsername());
        if (u == null) {
            throw new NonExistentException("Userul nu exista.");
        }
        if (lobbyRepository.findByLobbyLeader(u) != null) {
            throw new AlreadyExistsException("Userul are deja un lobby creat.");
        }
        List<Lobby> lobbies = lobbyRepository.findAll();
        for(Lobby lobby : lobbies) {
            if (lobby.getPlayers().contains(u)) {
                throw new AlreadyExistsException("Userul este deja in alt lobby.");
            }
        }
        List<User> users = new ArrayList<>();
        users.add(u);
        Lobby l = new Lobby(u, lobbyDTO.getName(), users);
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
        Lobby l = lobbyRepository.findByLobbyLeader(leader);
        if (l == null) {
            throw new NonExistentException("Userul nu are lobby creat.");
        }
        List<Lobby> lobbies = lobbyRepository.findAll();
        for(Lobby lobby : lobbies) {
            if (lobby.getPlayers().contains(invited)) {
                throw new AlreadyExistsException("Userul este deja in alt lobby.");
            }
        }
        List<User> users = l.getPlayers();
        if (users.size() >= 5) {
            throw new UnauthorizedActionException("Lobby-ul este plin.");
        }
        users.add(invited);
        l.setPlayers(users);
        lobbyRepository.save(l);
        return CompletableFuture.completedFuture(l);
    }

    @Override
    @Async
    public CompletableFuture<Lobby> kickFromLobby(KickLobbyDTO lobbyDTO) {
        User leader = userRepository.findByUsernameIgnoreCase(lobbyDTO.getLobbyLeader());
        User invited = userRepository.findByUsernameIgnoreCase(lobbyDTO.getUsername());
        if (leader == null || invited == null) {
            throw new NonExistentException("Userul nu exista.");
        }
        Lobby l = lobbyRepository.findByLobbyLeader(leader);
        if (l == null) {
            throw new NonExistentException("Userul nu are lobby creat.");
        }
        boolean inLobby = false;
        for(User u : l.getPlayers()) {
            if (u.equals(invited)) {
                inLobby = true;
                break;
            }
        }
        if (!inLobby) {
            throw new NonExistentException("Userul nu este in lobby.");
        }
        boolean passLast = false;
        List<User> users = l.getPlayers();
        if (leader == invited && users.size() > 1) {
            l.setLobbyLeader(users.get(1));
            passLast = true;
        }
        if (users.size() == 1 && !passLast) {
            lobbyRepository.delete(l);
            return CompletableFuture.completedFuture(null);
        }
        users.remove(invited);
        l.setPlayers(users);
        lobbyRepository.save(l);
        return CompletableFuture.completedFuture(l);
    }
}
