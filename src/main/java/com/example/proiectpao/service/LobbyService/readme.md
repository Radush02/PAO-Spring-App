# Sistem de lobby

## Creare lobby
- Userul poate crea un lobby in care sa joace cu alti useri.
- Dupa ce userul a creat lobby-ul, el devine liderul lobby-ului.
- In lobby pot fi maxim 5 useri (incluzand liderul)



```java
//LobbyService.java
@Async
public CompletableFuture<Lobby> createLobby(CreateLobbyDTO lobbyDTO) {
    //Verificari pentru user (ban, in alt lobby, s.a.m.d.)
    List<String> users = new ArrayList<>(); // Lista cu userii din lobby, initial goala
    users.add(u.getUsername()); // Adaugare lider in lista
    Lobby l =
            new Lobby(
                    String.valueOf(lobbyRepository.count()),
                    u.getUsername(),
                    lobbyDTO.getName(),
                    users);  // Creare lobby
    lobbyRepository.save(l);//Inserare lobby in baza de date
    return CompletableFuture.completedFuture(l); // Returnare lobby
}
```

## Alaturarea unui user in lobby
- Userul poate alatura unui lobby daca acesta nu este plin
- Daca userul se alatura unui lobby, acesta va fi adaugat in lista de useri din lobby
- Daca userul este banat, nu poate intra in lobby
```java
    @Override
    @Async
    public CompletableFuture<Lobby> joinLobby(JoinLobbyDTO lobbyDTO) {
        //verificari user si lider (daca userul e banat, userul incearca sa intre in propriul lobby, s.a.m.d.)
        users.add(invited.getUsername()); //Adaugare user in lista
        l.setPlayers(users);
        lobbyRepository.save(l); //Salvare lobby in BD
        return CompletableFuture.completedFuture(l);
    }
```

## Parasirea unui lobby
- Userul poate parasi un lobby
- In cazul in care liderul paraseste lobby-ul, urmatorul user din lobby va deveni liderul
- In cazul in care liderul paraseste lobby-ul, iar el e singurul user, lobby-ul va fi sters

```java
@Override
    @Async
    public CompletableFuture<Lobby> kickFromLobby(KickLobbyDTO lobbyDTO) {
        //verificari
        boolean passLast = false; //Verific daca liderul incearca sa iasa dintr-un lobby ce are alti useri in el
        List<String> users = l.getPlayers(); 
        if (Objects.equals(leader.getUsername(), invited.getUsername()) && users.size() > 1) {
            l.setLobbyLeader(users.get(1)); //Iau urmatorul user din lista si-l pun lider.
            passLast = true; //Liderul a iesit cu alti useri in lobby
        }
        if (users.size() == 1 && !passLast) { //Liderul a iesit dintr-un lobby in care era doar el
            lobbyRepository.delete(l); //Sterg lobby-ul
            return CompletableFuture.completedFuture(null);
        }
        users.remove(invited.getUsername()); //Elimin userul din lista
        l.setPlayers(users);
        lobbyRepository.save(l); //Salvez lobby-ul
        return CompletableFuture.completedFuture(l);
    }
```