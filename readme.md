# Proiect PAO - Aplicatie de matchmaking

## Descriere

Inspirat dupa [Faceit](https://www.faceit.com/en), aplicatia mea doreste sa simuleze o aplicatie de matchmaking.<br>
Utilizatorii se pot inregistra, loga, crea lobby-uri, invita prieteni, socializa si juca impotriva altor lobby-uri formate din 5 jucatori.<br>
Utilizatorii pot fi sanctionati de catre administratori sau moderatori daca nu respecta regulamentul. Sanciunile prevazute sunt:
<ul>
<li> Mute - Userul nu poate trimite mesaje</li>
<li> Warn - Userul este avertizat. La 2 warn-uri va primi ban pentru 3 zile</li>
<li> Ban - Userul nu se poate loga si nu poate face nici o actiune.</li>
</ul>


Tipul de meci este de MR12, similar cu cel din jocurile populare precum CS2, Valorant, etc.
Aplicatia genereaza random rezultatul unui meci.


## Framework-uri folosite
<ul>
<li> Spring Boot - Backend</li>
<li> Angular - Frontend</li>
<li> MongoDB - Baza de date</li>
<li> AWS S3 - Stocare fisiere</li>
</ul>


## Development setup

Pentru a rula aplicatia este necesar sa ai instalat Java 21, Maven si Angular CLI.
Pentru functionalitate completa (descarcare de fisiere) ai nevoie de un bucket S3 de la AWS.

Pas 0: Creeaza fisierul application.properties in src/main/resources cu urmatorul continut:
```properties
spring.application.name=ProiectPAO
spring.data.mongodb.port=port_mongodb (default 27017)
spring.data.mongodb.host=host_mongodb (default localhost)
aws.access.key=${AWS_ACCESS_KEY:cheia_ta_aws}
aws.secret.key=${AWS_SECRET_KEY:secret_key_aws}
aws.s3.bucket=${AWS_S3_BUCKET:numele_bucketului}
```
Pas 1: Instaleaza dependentele de Maven & porneste server:
```sh
mvn clean install
mvn spring-boot:run
```
Pas 2: Instaleaza dependentele de Angular & porneste aplicatia:
```sh
cd frontend
npm i
ng serve
```

Aplicatia porneste la adresa: ``http://localhost:4200/``


# Functionalitati

## Autentificare
- Utilizatorii se pot inregistra si loga. 
- Parola este criptata folosind SHA-256 inainte de a fi stocata in baza de date.
- Pentru simplicitate, nu se creeaza semnaturi precum JWT, ci se salveaza direct datele userului ca cookies.
- In cazul in care userul nu este logat, nu poate accesa anumite pagini.
```java
//UserService.Java
@Async
public CompletableFuture<User> register(UserRegisterDTO userRegisterDTO) {
    ///...
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
}
```

![Form](https://cdn.discordapp.com/attachments/1062136144382398506/1240980038233620551/image.png?ex=66488839&is=664736b9&hm=3364cb1e709330ea2613bca2634a9d0bd1dcfd1c9966d85d08ce39dcdbf794c2&)
## Verificare sanctiuni

- Inainte de orice actiune (logare, creare lobby, trimitere mesaj, s.a.m.d. se verifica daca userul are o sanctiune activa)
Exemplu de verificare daca userul este banat inainte de a se loga.
```java
///UserService.Java
@Async
public CompletableFuture<UserDTO> login(UserLoginDTO userLoginDTO) {
    User k = userRepository.findByUsernameIgnoreCase(userLoginDTO.getUsername());
    if (k == null) {
        throw new NonExistentException("Userul nu exista sau parola incorecta.");
    }
    if (!punishRepository
            .findAllByUserIDAndSanctionAndExpiryDateIsAfter( // Verificare daca userul este banat
                    k.getUserId(), Penalties.Ban, new Date()) // Se extrag din baza de date toate banurile userului ce expira dupa data curenta.
            .isEmpty()) throw new UnauthorizedActionException("Userul este banat.");
    ///...
}
```

Exemplu de verificare daca userul are mute.
```java
//ChatService.jav
    @Async
    public CompletableFuture<Boolean> send(ChatDTO chat, String receiver) {
    User sender = userRepository.findByUsernameIgnoreCase(chat.getSenderName());
    User receiverUser = userRepository.findByUsernameIgnoreCase(receiver);
    if (sender == null) throw new NonExistentException("Nu ai cont.");
    if (receiverUser == null) throw new NonExistentException("Userul nu exista.");
    if (!punishRepository
            .findAllByUserIDAndSanctionAndExpiryDateIsAfter(
                    sender.getUserId(), Penalties.Mute, new Date())
            .isEmpty())
        throw new UnauthorizedActionException("Esti sanctionat, nu poti trimite mesaje.");
    if (Objects.equals(chat.getSenderName(), receiver))
        throw new UnauthorizedActionException("Nu iti poti da mesaje singur");
    //...
}
```
## Pagina principala

Pagina principala prezinta userului un istoric al tuturor meciurilor jucate.
![Pagina](https://cdn.discordapp.com/attachments/1062136144382398506/1240981792777830451/image.png?ex=664889db&is=6647385b&hm=2544072bde666f01be1b8395fa591a766cb87da7fdbb36b17fbdeef1e15e0603&)

## Sistem de lobby
Un user poate crea un lobby, el devenind lider. In lobby pot participa maxim 4 alte persoane.
<br>Lobby-urile sunt folosite pentru ca userii sa se atace intre ei in echipe.

Pentru mai multe detalii despre functionalitatile lobby-ului, [vezi readme-ul din LobbyService.](https://github.com/Radush02/ProiectPAO/tree/master/src/main/java/com/example/proiectpao/service/LobbyService)


## Jocul
- Jocul este unul de tip MR12, unde userii sunt impartiti in 2 echipe de cate 5.
- Echipele sunt formate din 2 lobby-uri create de useri.
- Rezultatul unui meci este generat random, de la cine incepe runda pana la cine face kill-urile si statisticile pentru fiecare user din acea runda.


Pentru mai multe detalii despre functionalitatile jocului, [vezi readme-ul din GameService.](https://github.com/Radush02/ProiectPAO/tree/master/src/main/java/com/example/proiectpao/service/GameService)

## Exportarea informatiilor

Userii pot exporta informatii precum:
- Detalii despre contul lor
- Istoricul mesajelor
- Istoricul meciurilor

In cazuri extreme, userii pot folosi fisierele exportate ca back-ul pentru a-si repara statisticile.
- Se aplica doar pentru propiile lor statistici
- Fisierele nu pot fi alterate, deoarece un back-up este salvat intr-un bucket S3 pentru a preveni fraude.

Administratorii pot exporta informatii despre mesajele si meciurile jucate.
- La randul lor, din admin panel, pot utiliza fisierele exportate pentru a reseta istoricul mesajelor sau al unui meci.
- Fisierele exportate de admini sunt salvate intr-un bucket S3 si verificate din nou pentru a devni alterarea continutului.

#### Adaugarea si citirea fisierelor din S3
```java
//S3Service.java
    public void uploadFile(String keyName, MultipartFile file) throws IOException {
        var putObjectResult = s3client.putObject(bucketName, keyName, file.getInputStream(), null);
        log.info(putObjectResult.getMetadata());
    }

    public S3Object getFile(String keyName) {
        return s3client.getObject(bucketName, keyName);
    }
```

#### Exemplu de validare de date
```java
///JsonFileParser.Java
 @Override
    public boolean read(Object user, MultipartFile file, S3Service s3, Object type) {
     try {

         String s3Json = getS3FileContent(file.getOriginalFilename(), s3);
         String json = getFileContent(file);
         if (!json.equals(s3Json))
             throw new NonExistentException("Continutul back-up-ului a fost modificat!");
         //...
     }
     //...
 }
```
```java
///FileParser.java

protected String getFileContent(MultipartFile file) throws IOException {
    try (InputStream is = file.getInputStream()) {
        return IOUtils.toString(is, StandardCharsets.UTF_8);
    }
}


protected String getS3FileContent(String fileName, S3Service s3) throws IOException {
    S3Object s3Object = s3.getFile(fileName);
    try (InputStream is = s3Object.getObjectContent()) {
        return IOUtils.toString(is, StandardCharsets.UTF_8);
    }
}
```
## Chat
- Userii pot trimite mesaje intre ei.
- Userii pot trimite mesaje doar daca nu sunt sanctionati.
- Un user nu-si poate trimite singur mesaje
- Nu s-a folosit un WebSocket in implementarea chatului

#### Trimitera unui mesaj
```java
    @Async
    public CompletableFuture<Boolean> send(ChatDTO chat, String receiver) {
       //verificari (sanctiuni useri, user inexistent, s.a.m.d.)
        Chat c = new Chat();
        c.setChatId(UUID.randomUUID().toString().split("-")[0]); //Atribui un ID nou chatului pentru a seta mesajul original inapoi in caz d backup.
        c.setMessage(chat.getMessage());
        c.setSenderName(chat.getSenderName());
        c.setReceiverName(userRepository.findByUsernameIgnoreCase(receiver).getUsername());
        c.setDate(new Date());
        chatRepository.save(c);
        return CompletableFuture.completedFuture(true);
    }
```

#### Returnarea mesajelor primite
```java
    @Async
    public CompletableFuture<List<MessageDTO>> receive(String senderName, String username) {
        List<MessageDTO> c = new ArrayList<>();
        List<Chat> chats = chatRepository.findAllBySenderNameAndReceiverName(senderName, username);
        chats.addAll(chatRepository.findAllBySenderNameAndReceiverName(username, senderName));
        chats.sort(Comparator.comparing(Chat::getDate));
        for (Chat chat : chats) {
            c.add(new MessageDTO(chat.getMessage(), chat.getSenderName(), chat.getDate()));
        }
        return CompletableFuture.completedFuture(c);
    }
```


## Administrare
- Administratorii pot sanctiona userii din Admin panel
- Orice actiune a unui admin este logata intr-un fisier .csv
- Administratorii pot extrage oricand acel fisier pentru a verifica actiunile atat a lor cat si a altor administratori.
- Din admin panel, administratorii pot folosi fisiere trimise de useri pentru a aplica back-up-uri la chat-uri sau meciuri.
- In cazul unei sanctiuni acordate gresit, administratorul/moderatorul poate da revert la actiune doar specificand username-ul.