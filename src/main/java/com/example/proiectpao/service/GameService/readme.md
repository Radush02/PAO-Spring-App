# Jocul

In cazul in care nu esti familiarizat cu MR12, recomand sa vezi rezultatul acestui meci unde s-au jucat 3 mape de MR12, una ajungand in Overtime(OT):

https://www.hltv.org/matches/2370727/faze-vs-natus-vincere-pgl-cs2-major-copenhagen-2024

## Alegerea random a cum se decide o runda
- In fiecare runda, se amesteca random membrii fiecarei echipe
- Se alege primul user din fiecare echipa si se joaca 1v1
```java
//GameService.java
@Async
public CompletableFuture<String> attackTeam(String attackerCaptain, String defenderCaptain) {
    //verificari (daca echipele sunt intregi, nimeni nu e banat, s.a.m.d.)

    while (no_rounds > 0) {
        Collections.shuffle(attackerCopy); //Lista de playeri din echipa ce a initiat meciul
        Collections.shuffle(defenderCopy); //Lista de playeri din cealalta echipa
        while (!attackerCopy.isEmpty() && !defenderCopy.isEmpty()) {
            User attackerPlayer = attackerCopy.getFirst(); //Se iau primii useri
            User defenderPlayer = defenderCopy.getFirst();
            Random r = new Random();
            int attacker_roll = r.nextInt(6) + 1; //Se genereaza random un numar intre 1 si 6
            int defender_roll = r.nextInt(6) + 1;
            if (attacker_roll > defender_roll) {
                attacker_score = //Se calculeaza statisticile in functie de numerele generate.
                        attackHelper(
                                attackerPlayer,
                                defenderPlayer,
                                attacker_score,
                                attacker_roll,
                                defender_roll);
                defenderCopy.remove(defenderPlayer);
            } else if (attacker_roll < defender_roll) {
                attackerCopy.remove(attackerPlayer);
                defender_score =
                        attackHelper(
                                defenderPlayer,
                                attackerPlayer,
                                defender_score,
                                defender_roll,
                                attacker_roll);
            }
            //In caz de egalitate, nu fac nimic, ci se reia generarea de nr
        }
    }
    ///...
}
```

### Alegerea statisticilor in functie de nr generate
<ul>
 <li>Diferenta de 4+: Castigatorul primeste 1 kill + 1 hs, pierzatorul 1 death </li>
<li>Diferenta de 3: Castigatorul primeste 1 kill + 3 hits, pierzatorul 1 death</li>
<li>Diferenta de 2: Castigatorul primeste 1 kill + 3 hits, pierzatorul 1 death + 1 hit</li>
<li>Diferenta de 1: Castigatorul primeste 1 kill + 4 hits, pierzatorul 1 death + 2 hits</li>
<li>Egalitate: Se reia runda</li>
</ul>

```java
/**
     * Functie care calculeaza rezultatul unui atac
     * @param attacker - Castigatorul
     * @param defender - Pierzatorul
     * @param attacker_score - Retine nr de runde
     * @param attacker_roll - Numarul random al castigatorului pt a determina statisticile finale
     * @param defender_roll - Numarul random al pierzatorului pt a determina statisticile finale
     * @return
     */
    private int attackHelper(
            User attacker,
            User defender,
            int attacker_score,
            int attacker_roll,
            int defender_roll) {
        attacker_score++;
        attacker.addKill();
        defender.addDeath();
        switch (attacker_roll - defender_roll) {
            case 1 -> {
                attacker.addHits(4);
                defender.addHits(2);
            }
            case 2 -> {
                attacker.addHits(3);
                defender.addHits(1);
            }
            case 3 -> attacker.addHits(3);
            default -> {
                attacker.addHits(1);
                attacker.addHeadshot();
            }
        }
        return attacker_score;
    }
```

### Alegerea castigatorului unei runde
- Se joaca 1v1 pana cand una din echipe nu mai are membri (lista e goala)
- In cazul in care e egalitate la sfarsitul celor 24 de runde (12-12), se joaca overtime (inca 6 runde).
- Se vor juca OT-uri pana cand nu va mai fi egalitate de runde si o echipe va castiga.
```java
///...
     if (attackerCopy.isEmpty()) defenderRounds++;
     else attackerRounds++;
     
     if (no_rounds == 0 && attackerRounds == defenderRounds) {
           no_rounds += 6;
           no_ot++;
     }
     if (attackerRounds > 12 + 3 * no_ot && defenderRounds < 12 + 3 * no_ot) break;
     if (defenderRounds > 12 + 3 * no_ot && attackerRounds < 12 + 3 * no_ot) break;
    }
///...
```