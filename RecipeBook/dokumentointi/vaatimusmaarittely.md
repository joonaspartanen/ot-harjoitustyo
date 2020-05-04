# Vaatimusmäärittely

## Sovelluksen tarkoitus

Sovelluksen avulla käyttäjät voivat tallentaa ruokareseptejä. Sovelluksella voi olla useampi rekisteröitynyt käyttäjä. Käyttäjät voivat tarkastella myös muiden käyttäjien sovellukseen lisäämiä reseptejä.

## Perusversion tarjoama toiminnallisuus

### Ennen kirjautumista

- käyttäjä voi luoda sovellukseen käyttäjätunnuksen (tehty)
  - käyttäjätunnuksen on oltava uniikki ja 5–20 merkkiä pitkä (tehty)
  - sovellus ilmoittaa, mikäli käyttäjätunnus ei täytä yllä olevia vaatimuksia (tehty)
- käyttäjä voi kirjautua järjestelmään (tehty)
  - kirjautuminen onnistuu, kun käyttäjä syöttää sovellukseen lisätyn käyttäjätunnuksen kirjautumisnäkymässä (tehty)
  - sovellus ilmoittaa, mikäli käyttäjää ei ole olemassa (tehty)

### Kirjautumisen jälkeen

- käyttäjä voi tarkastella sovellukseen tallentamiaan reseptejä (tehty; tiedot voi tallentaa joko tietokantaan tai tiedostoon)
  - reseptejä voi tarkastella graafisella käyttöliittymällä (tehty)
- käyttäjä voi tarkastella muiden käyttäjien sovellukseen tallentamia reseptejä (tehty)
- käyttäjä voi hakea reseptejä ainakin raaka-aineiden perusteella (tehty)
- käyttäjä voi luoda uuden reseptin (tehty)
  - reseptillä on oltava nimi, raaka-aineet, valmistusaika ja valmistusohjeet (tehty)
- käyttäjä voi poistaa lisäämänsä reseptin (tehty)
- käyttäjä voi lisätä muiden luomia reseptejä omaan suosikkireseptilistaansa (tehty)
- käyttäjä voi kirjautua ulos järjestelmästä (tehty)

## Jatkokehitysideoita

Sovelluksen kehittäminen aloitetaan yllä kuvaillusta, toiminnoiltaan suppeammasta perusversiosta. Mikäli mahdollista, sovellukseen voidaan lisätä esimerkiksi seuraavia toiminnallisuuksia:

- reseptien luokittelu esimerkiksi keittiön (italialainen, meksikolainen, thaimaalainen...) perusteella
- reseptien hakeminen useilla eri kriteereillä
- sovellus suosittelee käyttäjälle uusia reseptejä omaan reseptikirjaan tallennettujen reseptien perusteella
- reseptien arviointi tähtiluokituksella ja arvioiden keskiarvon laskeminen/näyttäminen
- raaka-aineiden kilo-/litrahinnan lisääminen järjestelmään ja annoskohtaisen hinnan laskeminen näiden perusteella
- raaka-aineiden kalorimäärien lisääminen järjestelmään ja annoskohtaisen kalorimäärän laskeminen näiden perusteella
- käyttäjätunnuksen yhteyteen olisi syytä lisätä salasana, jonka olisi oltava vähintään 8 merkkiä pitkä ja sisältää isoja ja pieniä kirjaimia sekä ainakin yksi numero

## Toimintaympäristön rajoitteet

Sovelluksen tulee toimia ainakin Helsingin yliopiston tietojenkäsittelytieteen osaston Linux-koneilla, joissa on Java asennettuna.
