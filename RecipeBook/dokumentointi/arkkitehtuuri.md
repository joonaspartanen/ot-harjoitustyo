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

Sovelluksella on graafinen JavaFx:n avulla toteutettu käyttöliittymä. Kaikki käyttöliittymään liittyvä koodi on tällä hetkellä (vk6) yhdessä luokassa, mutta se olisi syytä jakaa pienempiin luokkiin.

Käyttöliittymän _stageen_ on sijoitettu _mainContainer_-niminen _Scene_-olio, joka sisältää sovelluksen nimen sekä tiedon kirjautuneesta käyttäjästä. _mainContainerin_ sisään asetetaan joko _BorderPane_-oliona toteutettu kirjautumisnäkymä tai _TabPane_-oliona toteutettu sisäänkirjautuneen käyttäjän näkymä (_recipesTabPane_).

Sisäänkirjautuneen käyttäjän näkymä koostuu neljästä välilehdestä (_Tab_):

- All Recipes (lista kaikista sovellukseen tallennetuista resepteistä)
- Add Recipe (näkymä uuden reseptin lisäämiseksi)
- Search Recipe (reseptien hakeminen)
- My Recipebook (käyttäjän omat suosikkireseptit; _ei vielä toteutettu viikolla 6_)

Käyttöliittymää luotaessa (_init()_-metodi) luetaan myös _config.properties_-tiedostossa määritellyt konfiguraatiot ja luodaan niiden perusteella yhteys tietokantaan tai tiedostoon soveliaan _DataStoreConnector_-toteutuksen avulla. Samalla luodaan käyttöliittymän tarvitsemat service-luokkien toteutukset, joille injektoidaan niiden tarvitsemat dao-toteutukset (asianmukaiset dao-toteutukset saadaan _DatasStoreConnectorilta_).

### Sovelluslogiikka

Sovelluksen käsittelemää dataa mallintavat kolme luokkaa: Recipe, Ingredient ja User, jotka kuvaavat sovellukseen tallennettavia reseptejä ja niiden ainesosia sekä sovelluksen käyttäjiä:

![Luokkakaavio](https://github.com/joonaspartanen/ot-harjoitustyo/blob/master/RecipeBook/dokumentointi/luokkakaavio.png)

Varsinaisesta sovelluslogiikasta vastaavat kolme service-luokkaa, joiden suhteita seuraava kaavio kuvaa:

![Sovelluslogiikka](https://github.com/joonaspartanen/ot-harjoitustyo/blob/master/RecipeBook/dokumentointi/sovelluslogiikka.png)

RecipeService vastaa reseptien käsittelystä, IngredientService ainesosien käsittelystä ja UserService käyttäjähallintaan liittyvästä logiikasta.

Koska resepteihin liityy tieto ainesosista ja ne luoneesta käyttäjästä, täytyy RecipeServicen käyttää myös IngredientServicen ja UserServicen tarjoamia metodeja: esimerkiksi uutta reseptiä lisättäessä tulee tarkistaa UserServicen avulla, kuka käyttäjistä on kirjautuneena sisään. Tarvittava UserService-toteutus injektoidaan RecipeServicelle konstruktorissa.

### Tietojen tallennus ja lukeminen

Edellisestä kaaviosta näkyy myös, että tietojen tallennuksesta ja lukemisesta vastaavat rajapintojen RecipeDao, IngredientDao ja UserDao toteutukset.

Kehityksen alkuvaiheessa näistä on olemassa vain ArrayList-tietorakennetta hyödyntävät toteutukset, jotka tullaan korvaamaan myöhemmin ainakin tiedostoon ja mahdollisesti myös tietokantaan tallentavilla toteutuksilla. Tässä auttaa se, että service-luokat tuntevat vain dao-rajapinnat. ArrayList-toteutuksia voidaan kuitenkin jatkossakin hyödyntää esimerkiksi service-luokkien testauksessa mock-olioina.

Tietokantaoperaatioiden apuna käytetään luokkia DaoHelper (tarjoaa eri daoille yhteisiä apumetodeja), QueryBuilder (tarjoaa SQL-kyselyitä), ResultSetMapper (käsittelee tietokantakyselyjen tulokset ja muuttaa ne Java-olioiksi) sekä DatabaseConnector (tarjoaa toiminnot tietokantayhteyden avaamiseen ja sulkemiseen).

### Eräitä toiminnallisuuksia sekvenssikaavioina

Eräs sovelluksen keskeisimmistä toiminnallisuuksista on uuden reseptin luominen ja tallentaminen. Käyttäjä syöttää reseptin nimen, keittoajan, ainesosat yksikköineen ja määrineen sekä valmistusohjeet graafisen käyttöliittymän kenttiin. Käyttöliittymä validoi aluksi syötteet eli tarkistaa esimerkiksi, ettei mikään kentistä ole tyhjä ja että keittoaika on annettu kokonaislukuna. Tämän jälkeen reseptin ainesosat käydään yksitellen lävitse ja tarkistetaan löytyvätkö ne jo tietokannasta/ainesosatiedostosta. Jos ainesosa on uusi, se lisätään kantaan. Tätä voidaan kuvata seuraavalla sekvenssikaaviolla:

![Sekvenssikaavio ainesosan lisäämisestä](https://github.com/joonaspartanen/ot-harjoitustyo/blob/master/RecipeBook/dokumentointi/ingredient_sekvenssikaavio.png)

Käyttöliittymä kutsuu siis ingredientService-olion metodia _addIngredient_, minkä jälkeen tietokantayhteyksistä vastaavan ingredientDao-olion avulla tarkistetaan, löytyykö vastaava ainesosa jo kannasta. Mikäli ei löydy, palautetaan _null_, luodaan uusi ainesosaolio ja annetaan se parametrina ingredientDao:n metodille _create_, joka tallentaa ainesosan tietokantaan.

Huomionarvoinen yksityiskohta on, että ingredientDao hakee tietokantaoperaatioihin liittyviä yleisiä aputoimintoja tarjoavasta DaoHelper-luokasta tallennetun ainesosan id-numeron.

Uusi ainesosaolio palautetaan aina käyttöliittymälle asti, missä se lisätään reseptin ainemäärät sisältävään hajautustauluun. Kun kaikki ainesosat on käsitelty, on kaikki valmista itse reseptin lisäämistä varten. Tätä havainnollistaa seuraava sekvenssikaavio:

![Sekvenssikaavio reseptin lisäämisestä](https://github.com/joonaspartanen/ot-harjoitustyo/blob/master/RecipeBook/dokumentointi/recipe_sekvenssikaavio.png)

Reseptin lisääminen etenee pääpiirteissään hyvin samalla tavalla kuin yksittäisen ainesosankin tapauksessa. Huomionarvoista on, että itse resepti (nimi, keittoaika, ohjeet) tallennetaan yhteen tietokantatauluun (tai tiedostoon) ja tieto siihen liittyvistä ainesosista erilliseen liitostauluun (tai tiedostoon) metodin _saveRecipeIngredients_ avulla.

Kun resepti on tallennettu, tyhjentää käyttöliittymä reseptin lisäämiseen liittyvät kentät ja päivittää kaikki reseptit sisältävän näkymän.
