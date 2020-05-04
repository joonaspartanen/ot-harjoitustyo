# Sovelluksen testaus

## Yksikkö- ja integraatiotestaus

### Domain-kerros

Sovelluksen sovelluslogiikasta vastaavia service-luokkia (RecipeService, IngredientService, UserService) testataan varsin kattavasti yksikkö- ja integraatiotesteillä, joissa DAO-riippuvuudet on toteutettu keskusmuistiin tallentavilla mock-olioilla (RecipeDaoMock, IngredientDaoMock, UserDaoMock).

### DAO-kerros

Sekä tiedostoon että tietokantaan tallentavia DAO-toteutuksia testataan yksikkö- ja integraatiotestein, jotka tallentavat dataa väliaikaiseen kansioon.

### Käyttöliittymä

Käyttöliittymä (GraphicUi) jää kokonaan automaattisen testauksen ulkopuolelle, mutta sen toimintaa on testattu manuaalisin testein.

### Integraatiotestit

Edellä mainittujen testien lisäksi sovelluksessa on joitakin laajempia integraatiotestejä (IntegrationTestsUsingDatabaseTest, IntegrationTestsUsingFileTest), jotka testaavat sekä service- että DAO-luokkien yhteistoimintaa eräissä sovelluksen tyypillisissä käyttötilanteissa.

### Testauskattavuus

Kun käyttöliittymä ja sovelluksen käynnistävä Main-luokka jätetään pois laskuista, on testauksen rivikattavuus 89 % ja haarautumakattavuus 94 %. Rivikattavuutta laskee lähinnä se, ettei virheiden käsittelyä kaikissa odottamattomissa poikkeustapauksissa (kuten SQLException, jos tietokantayhteys katkeaa) testata.

[!Testauskattavuusraportti](https://github.com/joonaspartanen/ot-harjoitustyo/blob/master/RecipeBook/dokumentointi/kuvat/testauskattavuus.png)

## Järjestelmätestaus

Sovellusta on myös testattu manuaalisesti eri skenaarioissa, esimerkiksi niin, että hakemisto, johon tietokantatiedosto halutaan tallentaa puuttuu.

Samoin sovellusta on testattu käyttöliittymästä käsin erilaisilla odottamattomilla syötteillä (esim. tekstin syöttäminen lukua odottavaan kenttään).

## Testaukseen liittyviä puutteita

DAO-luokkien testeissä käytetään paikoin riippuvuuksina muita tiedostoon tai tietokantaan tallentavia DAO-toteutuksia mock-olioiden sijasta. On toki hyvä testata myös aitojen DAO-luokkien yhteistoimintaa, mutta yksikkötestien näkökulmasta voisi olla parempi käyttää näissäkin tapauksissa mock-toteutuksia.

Testikoodin laatua voisi parantaa refaktoroimalla esimerkiksi toisteisuuden poistamiseksi. Suuri osa toistuvista operaatioista on tosin siirretty testien apumetodeja tarjoavaan TestHelper-luokkaan, joka tosin kaipaisi siistimistä jatkokehityksen helpottamiseksi (joitain apumetodeja on luotu nopeasti _ad hoc_ -periaatteella).

Lisäksi automaattiset testit eivät testaa kovin kattavasti mahdollisia virheellisiä syötteitä ja rajatapauksia. Tämä johtuu osin siitä, että valtaosa syötteistä validoidaan jo käyttöliittymässä, mutta koska käyttöliittymää ei testata, jäävät nämä validoinnit testien ulkopuolelle.
