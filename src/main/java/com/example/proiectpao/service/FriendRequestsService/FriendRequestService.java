package com.example.proiectpao.service.FriendRequestsService;

import com.example.proiectpao.collection.FriendRequest;
import com.example.proiectpao.collection.User;
import com.example.proiectpao.dtos.FriendRequestDTO;
import com.example.proiectpao.dtos.FriendRequestResponseDTO;
import com.example.proiectpao.exceptions.NonExistentException;
import com.example.proiectpao.exceptions.UnauthorizedActionException;
import com.example.proiectpao.repository.FriendRequestRepository;
import com.example.proiectpao.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class FriendRequestService implements IFriendRequestService {
    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;

    public FriendRequestService(
            FriendRequestRepository friendRequestRepository, UserRepository userRepository) {
        this.friendRequestRepository = friendRequestRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Async
    public CompletableFuture<Boolean> addFriendRequest(FriendRequestDTO friend) {
        User receiver = userRepository.findByUsernameIgnoreCase(friend.getReceiver());
        User sender = userRepository.findByUsernameIgnoreCase(friend.getSender());
        if (sender == null || receiver == null) {
            throw new NonExistentException("Nu exista acel user");
        }
        if (Objects.equals(friend.getReceiver(), friend.getSender())) {
            throw new UnauthorizedActionException(
                    "Nu poti sa iti trimiti cerere de prietenie singur.");
        }
        FriendRequest f =
                friendRequestRepository.findBySenderAndReceiver(
                        friend.getSender(), friend.getReceiver());
        if (f != null) {
            throw new UnauthorizedActionException("Ai trimis deja o cerere acestui user.");
        }
        List<String> friends = sender.getFriends();
        if (friends.contains(receiver.getUsername())) {
            throw new UnauthorizedActionException("Sunteti deja prieteni.");
        }
        friendRequestRepository.save(
                new FriendRequest(
                        String.valueOf(friendRequestRepository.count()),
                        friend.getSender(),
                        friend.getReceiver()));
        return CompletableFuture.completedFuture(true);
    }

    @Override
    @Async
    public CompletableFuture<Boolean> deleteFriendRequest(FriendRequestDTO friend) {
        if (userRepository.findByUsernameIgnoreCase(friend.getSender()) == null
                || userRepository.findByUsernameIgnoreCase(friend.getReceiver()) == null) {
            throw new NonExistentException("Nu exista acel user");
        }
        FriendRequest f =
                friendRequestRepository.findBySenderAndReceiver(
                        friend.getSender(), friend.getReceiver());
        if (f == null) {
            throw new UnauthorizedActionException("Nu ai trimis nici o cerere acestui user.");
        }
        friendRequestRepository.delete(f);
        return CompletableFuture.completedFuture(true);
    }

    @Override
    @Async
    public CompletableFuture<List<FriendRequestDTO>> sentRequests(String username) {
        List<FriendRequest> requests = friendRequestRepository.findAllBySender(username);
        if (requests == null || requests.isEmpty()) {
            return CompletableFuture.completedFuture(new ArrayList<>());
        }
        List<FriendRequestDTO> res = new ArrayList<>();
        for (FriendRequest f : requests) {
            res.add(new FriendRequestDTO(f.getSender(), f.getReceiver()));
        }
        return CompletableFuture.completedFuture(res);
    }

    @Override
    @Async
    public CompletableFuture<List<FriendRequestDTO>> getRequests(String username) {
        List<FriendRequest> requests = friendRequestRepository.findAllByReceiver(username);
        if (requests == null || requests.isEmpty()) {
            return CompletableFuture.completedFuture(new ArrayList<>());
        }
        List<FriendRequestDTO> res = new ArrayList<>();
        for (FriendRequest f : requests) {
            res.add(new FriendRequestDTO(f.getSender(), f.getReceiver()));
        }
        return CompletableFuture.completedFuture(res);
    }

    @Override
    @Async
    public CompletableFuture<Boolean> friendRequestResponse(FriendRequestResponseDTO friend) {
        User sender = userRepository.findByUsernameIgnoreCase(friend.getSender());
        User receiver = userRepository.findByUsernameIgnoreCase(friend.getReceiver());
        if (sender == null || receiver == null) {
            return CompletableFuture.completedFuture(false);
        }
        FriendRequest f =
                friendRequestRepository.findBySenderAndReceiver(
                        friend.getSender(), friend.getReceiver());
        if (f == null) {
            throw new UnauthorizedActionException("Nu ai primit nici o cerere de la acest user.");
        }
        if (friend.isAccepted()) {
            sender.addFriend(receiver.getUsername());
            receiver.addFriend(sender.getUsername());
            userRepository.save(sender);
            userRepository.save(receiver);
            friendRequestRepository.delete(
                    friendRequestRepository.findBySenderAndReceiver(
                            friend.getSender(), friend.getReceiver()));
            return CompletableFuture.completedFuture(true);
        }
        friendRequestRepository.delete(
                friendRequestRepository.findBySenderAndReceiver(
                        friend.getSender(), friend.getReceiver()));
        return CompletableFuture.completedFuture(false);
    }
}
