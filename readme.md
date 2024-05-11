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
spring.data.mongodb.port=port_mongodb
aws.access.key=${AWS_ACCESS_KEY:cheia_ta_aws}
aws.secret.key=${AWS_SECRET_KEY:secret_key_aws}
aws.s3.bucket=${AWS_S3_BUCKET:numele_bucketului}
```
Pas 1: Instaleaza dependentele de Maven:
```sh
mvn clean install
```
Pas 2: Instaleaza dependentele de Angular:
```sh
cd frontend
npm install
```

Pas 3: Porneste serverul:
```sh
cd ..
mvn spring-boot:run
```
Pas 4: Porneste aplicatia Angular:
```sh
cd frontend
ng serve
```

Aplicatia porneste la adresa: ``http://localhost:4200/``

