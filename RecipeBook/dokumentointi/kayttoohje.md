# Käyttöohje

## Alkutoimet

Kloonaa repositorio komennolla `git clone https://github.com/joonaspartanen/ot-harjoitustyo.git` tai lataa projektin [jar-tiedosto](https://github.com/joonaspartanen/ot-harjoitustyo/releases/tag/week6).

## Konfiguraatio

Ohjelman käynnistyshakemistosta tulee löytyä tiedosto _config.properties_, jonka sisältö voi olla esimerkiksi seuraavanlainen:

```

dataStoreType=database

dataStoreLocation=./resources/db/

```

Ominaisuus `dataStoreType=database` määrittelee, että ohjelma tallentaa käsittelemänsä tiedot tietokantaan. Mikäli haluat tallentaa tiedot tekstitiedostoon, voit asettaa ominaisuuden arvoksi `dataStoreType=file` (**HUOM: Viikon 6 releasen kohdalla ei suositella tiedostotallennuksen käyttöä, sillä esimerkiksi käyttäjätietojen tiedostotallennusta ei ole vielä toteutettu!**).

Ominaisuus `dataStoreLocation` puolestaan määrittelee tietokannan tai tallennustiedostojen sijainnin. Oletuksena käytetään käynnistyshakemiston sisällä olevaa hakemistoa _/resources/db/_. Mikäli hakemistoa ei ole olemassa, se luodaan automaattisesti.

## Käynnistäminen

Ohjelma voidaan käynnistää jar-paketista komennolla `java -jar RecipeBook-week6.jar`. Mikäli kloonaat koko repositorion, voit käynnistää ohjelman komennolla `mvn compile exec:java -Dexec.mainClass=recipebook.Main`.

## Käyttäjän luominen ja kirjautuminen

Ohjelma käynnistyessä näytetään ensin kirjautumisnäkymä, josta käsin voit myös luoda uuden käyttäjätunnuksen.

[Kirjautumisnäkymä](https://github.com/joonaspartanen/ot-harjoitustyo/blob/master/RecipeBook/dokumentointi/login.png)

Mikäli sinulla on jo käyttäjätunnus, voit kirjautua sovellukseen kirjoittamalla nimesi _Login_-kenttään ja painamalla _Login_-painiketta. Ohjelma ilmoittaa, mikäli yrität kirjautua käyttäjätunnuksella, jota ei ole olemassa.

Voit luoda uuden käyttäjätunnuksen kirjoittamalla sen _New user_ -kenttään. Ohjelma ilmoittaa, jos haluamasi käyttäjätunnus on jo varattu tai jos se ei täytä vaatimuksia (5–20 merkkiä pitkä).

Käyttäjätunnuksen luomisen jälkeen voit kirjautua sisään.

## Reseptien tarkastelu

Kirjautumisen jälkeen ohjelma avautuu kaikki ohjelmaan tallennetut reseptit näyttävään _All recipes_ -näkymään.

[Reseptinäkymä](https://github.com/joonaspartanen/ot-harjoitustyo/blob/master/RecipeBook/dokumentointi/all_recipes.png)

Näkymässä voit tarkastella reseptejä lähemmin valitsemalla haluamasi reseptin listasta ja painamalla _Show recipe_ -painiketta.

Voit myös poistaa reseptin _Delete recipe_ -painikkeella, mutta huomaa, että ohjelma sallii sinun poistaa vain itse lataamiasi reseptejä.

## Reseptin lisääminen

Voit lisätä reseptin valitsemalla _Add recipe_ -näkymän.

[Lisää resepti -näkymä](https://github.com/joonaspartanen/ot-harjoitustyo/blob/master/RecipeBook/dokumentointi/add_recipe.png)

Anna reseptille nimi, valmistusaika minuutteina, ainesosat määrineen ja valmistusohjeet. Voit lisätä enemmän ainesosia painamalla +-painiketta.

Voit tallentaa reseptin painamalla _Save recipe_ -painiketta. Ohjelma ilmoittaa, jos reseptin tiedoissa on oleellisia puutteita (esim. reseptin nimi puuttuu).

## Reseptien hakeminen

Voit hakea reseptejä _Search recipes_ -näkymästä.

[Hae reseptejä -näkymä](https://github.com/joonaspartanen/ot-harjoitustyo/blob/master/RecipeBook/dokumentointi/search_recipe.png)

Tässä vaiheessa voit hakea reseptejä ainesosien perusteella. Kirjoita _Search by ingredient_ -kenttään haluamasi ainesosa ja paina _Search_-painiketta. Ohjelma näyttää listassa kaikki reseptit, joihin kuuluu hakemasi ainesosa.

Voit tarkastella reseptiä lähemmin painamalla _Show recipe_ -painiketta.

## Oma reseptikirja (_toteuttamaton ominaisuus_)

Kaikki itse luomasi reseptit lisätään automaattisesti omaan reseptikirjaasi, jonka löydät _My recipebook_ -näkymästä. Voit lisätä myös muiden luomia reseptejä talteen omaan reseptikirjaasi.

## Uloskirjautuminen

Voit kirjautua ulos ohjelmasta painamalla oikeassa yläkulmassa olevaa _Logout_-painiketta. Ohjelma palaa tällöin takaisin kirjautumisnäkymään.
