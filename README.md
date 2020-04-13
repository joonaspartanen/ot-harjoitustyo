# Recipe Book (Ohjelmistotekniikka kevät 2020, harjoitustyö)

## Tietoja

Recipe Book on Java-työpöytäsovellus, johon käyttäjä voi tallentaa ruokareseptejä. Sovelluksella voi olla useampi rekisteröitynyt käyttäjä. Käyttäjät voivat tarkastella myös muiden käyttäjien sovellukseen lisäämiä reseptejä.

Kyseessä on harjoitustyö Helsingin yliopiston kurssille [Ohjelmistotekniikka, kevät 2020](https://github.com/mluukkai/ohjelmistotekniikka-kevat-2020/).

## Dokumentaatio

- [Vaatimusmäärittely](https://github.com/joonaspartanen/ot-harjoitustyo/blob/master/RecipeBook/dokumentointi/vaatimusmaarittely.md)

- [Arkkitehtuurikuvaus](https://github.com/joonaspartanen/ot-harjoitustyo/blob/master/RecipeBook/dokumentointi/arkkitehtuuri.md)

- [Tuntikirjanpito](https://github.com/joonaspartanen/ot-harjoitustyo/blob/master/RecipeBook/dokumentointi/tuntikirjanpito.md)

## Vaatimukset

Java 11 ja Maven tulee olla asennettuna.

## Komentorivitoiminnot

Muista ensin siirtyä reposition juuresta sovelluksen kansioon komennolla `cd RecipeBook`.

### Ohjelman suorittaminen

Voit suorittaa ohjelman komentoriviltä komennolla:

`mvn compile exec:java -Dexec.mainClass=recipebook.Main`

### Testien suorittaminen

Voit suorittaa testit komennolla

`mvn test`

ja generoida testikattavuusraportin komennolla

`mvn test jacoco:report`

Raportti löytyy polusta `/target/site/jacoco/index.html`.

### Checkstyle

Voit luoda Checkstyle-raportin komennolla

`mvn jxr:jxr checkstyle:checkstyle`

Raportti löytyy polusta `/target/site/checkstyle.html`.

### Jarin generointi

Voit generoida projektista jar-paketin komennolla

`mvn package`.

Paketin voi suorittaa komennolla

`java -jar target/RecipeBook-1.0-SNAPSHOT.jar`.
