# Arkkitehtuurikuvaus

## Rakenne

Sovellus muodostuu kolmesta kerroksesta, joita myös sen pakkausrakenne noudattaa:

- recipebook.ui (käyttöliittymä)
- recipebook.domain (sovelluslogiikka)

  - recipebook.domain.ingredient (ainesosiin liittyvä logiikka)
  - recipebook.domain.recipe (resepteihin liittyvä logiikka)
  - recipebook.domain.user (käyttäjiin liittyvä logiikka)

- recipebook.dao (tietojen tallennus ja lukeminen)

  - recipebook.dao.ingredientdao (ainesosien tallennus)
  - recipebook.dao.recipedao (reseptien tallennus)
  - recipebook.dao.userdao (käyttäjien tallennus)

### Käyttöliittymä

Sovelluksella on graafinen JavaFx:n avulla toteutettu käyttöliittymä.

Käyttöliittymän _stageen_ on sijoitettu _mainContainer_-niminen _Scene_-olio, joka sisältää sovelluksen nimen sekä tiedon kirjautuneesta käyttäjästä. _mainContainerin_ sisään asetetaan joko _BorderPane_-oliona toteutettu kirjautumisnäkymä tai _TabPane_-oliona toteutettu sisäänkirjautuneen käyttäjän näkymä (_recipesTabPane_).

Sisäänkirjautuneen käyttäjän näkymä koostuu neljästä välilehdestä (_Tab_):

- _All recipes_ (lista kaikista sovellukseen tallennetuista resepteistä)
- _Add recipe_ (näkymä uuden reseptin lisäämiseksi)
- _Search recipe_ (reseptien hakeminen)
- _Favorite recipes_ (käyttäjän suosikkireseptit)

Käyttöliittymää luotaessa (_init()_-metodi) luetaan myös _config.properties_-tiedostossa määritellyt konfiguraatiot ja luodaan niiden perusteella yhteys tietokantaan tai tiedostoon soveliaan _DataStoreConnector_-toteutuksen avulla. Samalla luodaan käyttöliittymän tarvitsemat service-luokkien toteutukset, joille injektoidaan niiden tarvitsemat DAO-toteutukset (asianmukaiset DAO-toteutukset saadaan _DataStoreConnectorilta_).

### Sovelluslogiikka

Sovelluksen käsittelemää dataa mallintavat kolme luokkaa: Recipe, Ingredient ja User, jotka kuvaavat sovellukseen tallennettavia reseptejä ja niiden ainesosia sekä sovelluksen käyttäjiä:

![Luokkakaavio](https://github.com/joonaspartanen/ot-harjoitustyo/blob/master/RecipeBook/dokumentointi/kuvat/luokkakaavio.png)

Varsinaisesta sovelluslogiikasta vastaavat kolme service-luokkaa, joiden suhteita seuraava kaavio kuvaa:

![Sovelluslogiikka](https://github.com/joonaspartanen/ot-harjoitustyo/blob/master/RecipeBook/dokumentointi/kuvat/sovelluslogiikka.png)

RecipeService vastaa reseptien käsittelystä, IngredientService ainesosien käsittelystä ja UserService käyttäjähallintaan liittyvästä logiikasta.

Koska resepteihin liityy tieto ainesosista ja ne luoneesta käyttäjästä, täytyy RecipeServicen käyttää myös IngredientServicen ja UserServicen tarjoamia metodeja: esimerkiksi uutta reseptiä lisättäessä tulee tarkistaa UserServicen avulla, kuka käyttäjistä on kirjautuneena sisään. Tarvittava UserService-toteutus injektoidaan RecipeServicelle konstruktorissa.

### Tietojen tallennus ja lukeminen

Edellisestä kaaviosta näkyy myös, että tietojen tallennuksesta ja lukemisesta vastaavat rajapintojen RecipeDao, IngredientDao ja UserDao toteutukset.

Kustakin DAO-rajapinnasta on olemassa sekä tiedostoon että tietokantaan tallentava toteutus. Käyttäjä voi valita tallennustavan konfiguraatiotiedoston avulla (ks. [käyttöohje](https://github.com/joonaspartanen/ot-harjoitustyo/blob/master/RecipeBook/dokumentointi/kayttoohje.md)).

Tietokantaoperaatioiden apuna käytetään luokkia QueryBuilder (tarjoaa SQL-kyselyitä) ja ResultSetMapper (käsittelee tietokantakyselyjen tulokset ja muuttaa ne Java-olioiksi). Lisäksi IdExtractor-luokka tarjoaa DAO-toteutuksille apumetodit, joilla ne saavat selville tietokantaan/tiedostoon juuri lisätyn tietueen (resepti/ainesosa/käyttäjä) yksilöivän id-numeron.

Dao-paketissa on myös abstrakti DataStoreConnector-luokka, jonka aliluokat DatabaseConnector ja FileConnector vastaavat tietokantayhteyden ja tallennustiedostojen alustamisesta.

### Eräitä toiminnallisuuksia sekvenssikaavioina

Eräs sovelluksen keskeisimmistä toiminnallisuuksista on uuden reseptin luominen ja tallentaminen. Käyttäjä syöttää reseptin nimen, keittoajan, ainesosat yksikköineen ja määrineen sekä valmistusohjeet graafisen käyttöliittymän kenttiin. Käyttöliittymä validoi aluksi syötteet eli tarkistaa esimerkiksi, ettei mikään kentistä ole tyhjä ja että keittoaika on annettu kokonaislukuna. Tämän jälkeen reseptin ainesosat käydään yksitellen lävitse ja tarkistetaan, löytyvätkö ne jo tietokannasta/ainesosatiedostosta. Jos ainesosa on uusi, se lisätään kantaan. Tätä voidaan kuvata seuraavalla sekvenssikaaviolla:

![Sekvenssikaavio ainesosan lisäämisestä](https://github.com/joonaspartanen/ot-harjoitustyo/blob/master/RecipeBook/dokumentointi/kuvat/ingredient_sekvenssikaavio.png)

Käyttöliittymä kutsuu siis ingredientService-olion metodia _addIngredient_, minkä jälkeen tietokantayhteyksistä vastaavan ingredientDao-olion avulla tarkistetaan, löytyykö vastaava ainesosa jo kannasta. Mikäli ei löydy, palautetaan _null_, luodaan uusi ainesosaolio ja annetaan se parametrina ingredientDao:n metodille _create_, joka tallentaa ainesosan tietokantaan.

Huomionarvoinen yksityiskohta on, että ingredientDao hakee tietokantaoperaatioihin liittyviä yleisiä aputoimintoja tarjoavasta IdExtractor-luokasta tallennetun ainesosan id-numeron.

Uusi ainesosaolio palautetaan aina käyttöliittymälle asti, missä se lisätään reseptin ainemäärät sisältävään hajautustauluun. Kun kaikki ainesosat on käsitelty, on kaikki valmista itse reseptin lisäämistä varten. Tätä havainnollistaa seuraava sekvenssikaavio:

![Sekvenssikaavio reseptin lisäämisestä](https://github.com/joonaspartanen/ot-harjoitustyo/blob/master/RecipeBook/dokumentointi/kuvat/recipe_sekvenssikaavio.png)

Reseptin lisääminen etenee pääpiirteissään hyvin samalla tavalla kuin yksittäisen ainesosankin tapauksessa. Huomionarvoista on, että itse resepti (nimi, keittoaika, ohjeet) tallennetaan yhteen tietokantatauluun (tai tiedostoon) ja tieto siihen liittyvistä ainesosista erilliseen liitostauluun (tai tiedostoon) metodin _saveRecipeIngredients_ avulla.

Kun resepti on tallennettu, tyhjentää käyttöliittymä reseptin lisäämiseen liittyvät kentät ja päivittää kaikki reseptit sisältävän näkymän.

### Virheidenkäsittely

Koska sovellus tallettaa tietoja tietokantaan tai tiedostoon, on DAO-luokissa lukuisia metodeja, jotka voivat aiheuttaa SQLException- tai IOException-tyyppisen poikkeuksen. Nämä tietokannan tai tiedoston käsittelyyn liittyvät virheet kääritään asiaankuuluvan virheviestin kanssa erilliseen DataStoreException-poikkeukseen, joka heitetään sovellusten kerrosten läpi aina käyttöliittymätasolle asti. Käyttöliittymässä nämä virheet napataan ja käyttäjälle näytetään selväkielinen virheilmoitus JavaFx:n Alert-komponentin avulla.

Sovelluksessa on myös eräitä muita erityisiä poikkeuksia, jotka käsitellään käyttöliittymässä:

- BadUsernameException: heitetään, jos käyttäjänimi ei kelpaa
- UserNotFoundException: heitetään, jos käyttäjää ei löydy
