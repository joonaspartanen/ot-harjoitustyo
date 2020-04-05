# Arkkitehtuurikuvaus

__Huom. Sovelluksen rakenne ei vielä tässä vaiheessa (vk4) kaikilta osin noudata tämän kuvauksen mukaista tavoitearkkitehtuuria.__

## Rakenne

Sovellus muodostuu kolmesta kerroksesta, joita myös sen pakkausrakenne noudattaa:

- recipebook.ui (käyttöliittymä)
- recipebook.domain (sovelluslogiikka)
- recipebook.dao (tietojen tallennus ja lukeminen)

### Käyttöliittymä

Aluksi sovellus sisältää tekstikäyttöliittymän (luokka TextUi), joka on kuitenkin tarkoitus korvata graafisella JavaFX-käyttöliittymällä (luokka GraphicUi). Sovelluksen jatkokehityksessä keskityttäneen graafiseen käyttöliittymään eli uusia ominaisuuksia ei välttämättä tuoda tekstikäyttöliittymään.

### Sovelluslogiikka

Sovelluksen käsittelemää dataa mallintavat kolme luokkaa: Recipe, Ingredient ja User, jotka kuvaavat sovellukseen tallennettavia reseptejä ja niiden ainesosia sekä sovelluksen käyttäjiä:

![Luokkakaavio](https://github.com/joonaspartanen/ot-harjoitustyo/blob/master/RecipeBook/dokumentointi/luokkakaavio.png)

Varsinaisesta sovelluslogiikasta vastaavat kolme service-luokkaa, joiden suhteita seuraava kaavio kuvaa:

![Sovelluslogiikka](https://github.com/joonaspartanen/ot-harjoitustyo/blob/master/RecipeBook/dokumentointi/sovelluslogiikka.png)

RecipeService vastaa reseptien käsittelystä, IngredientService ainesosien käsittelystä ja UserService käyttäjähallintaan liittyvästä logiikasta.

Koska resepteihin liityy tieto ainesosista ja ne luoneesta käyttäjästä, täytyy RecipeServicen käyttää myös IngredientServicen ja UserServicen tarjoamia metodeja: esimerkiksi uutta reseptiä lisättäessä tulee tarkistaa UserServicen avulla, kuka käyttäjistä on kirjautuneena sisään. Tarvittavat IngredientServicen ja UserServicen toteutukset injektoidaan RecipeServicelle.

### Tietojen tallennus ja lukeminen

Edellisestä kaaviosta näkyy myös, että tietojen tallennuksesta ja lukemisesta vastaavat rajapintojen RecipeDao, IngredientDao ja UserDao toteutukset.

Kehityksen alkuvaiheessa näistä on olemassa vain ArrayList-tietorakennetta hyödyntävät toteutukset, jotka tullaan korvaamaan myöhemmin ainakin tiedostoon ja mahdollisesti myös tietokantaan tallentavilla toteutuksilla. Tässä auttaa se, että service-luokat tuntevat vain dao-rajapinnat. ArrayList-toteutuksia voidaan kuitenkin jatkossakin hyödyntää esimerkiksi service-luokkien testauksessa mock-olioina.
