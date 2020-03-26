# Recipe Book (Ohjelmistotekniikka kevät 2020, harjoitustyö)

## Tietoja

Recipe Book on Java-työpöytäsovellus, johon käyttäjä voi tallentaa ruokareseptejä. Sovelluksella voi olla useampi rekisteröitynyt käyttäjä. Käyttäjät voivat tarkastella myös muiden käyttäjien sovellukseen lisäämiä reseptejä.

Kyseessä on harjoitustyö Helsingin yliopiston kurssille [Ohjelmistotekniikka, kevät 2020](https://github.com/mluukkai/ohjelmistotekniikka-kevat-2020/).

## Dokumentaatio

- [Vaatimusmäärittely](https://github.com/joonaspartanen/ot-harjoitustyo/blob/master/RecipeBook/dokumentointi/vaatimusmaarittely.md)

- [Tuntikirjanpito](https://github.com/joonaspartanen/ot-harjoitustyo/blob/master/RecipeBook/dokumentointi/tuntikirjanpito.md)

## Vaatimukset

Java ja Maven.

## Komentorivitoiminnot

### Ohjelman suorittaminen

Voit suorittaa ohjelman komentoriviltä komennolla:

`mvn compile exec:java -Dexec.mainClass=recipebook.Main`

### Testien suorittaminen

Voit suorittaa testit komennolla

`mvn test`

ja generoida testikattavuusraportin komennolla

`mvn jacoco:report`
