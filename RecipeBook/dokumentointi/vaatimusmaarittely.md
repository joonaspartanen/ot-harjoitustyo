# Vaatimusmäärittely

## Sovelluksen tarkoitus

Sovelluksen avulla käyttäjät voivat tallentaa ruokareseptejä. Sovelluksella voi olla useampi rekisteröitynyt käyttäjä. Käyttäjät voivat tarkastella myös muiden käyttäjien sovellukseen lisäämiä reseptejä.

## Käyttäjät

Aluksi sovelluksen ainoa käyttäjärooli on _peruskäyttäjä_. Myöhemmin on mahdollista, että sovellukseen lisätään _pääkäyttäjän_ rooli. Pääkäyttäjällä olisi mahdollisuus muokata myös muiden (perus)käyttäjien reseptejä ja käyttäjätietoja.

## Perusversion tarjoama toiminnallisuus

### Ennen kirjautumista

- käyttäjä voi luoda sovellukseen käyttäjätunnuksen
  - käyttäjätunnuksen on oltava uniikki ja vähintään 5 merkkiä pitkä
  - käyttäjätunnukseen liittyy salasana, jonka on oltava vähintään 8 merkkiä pitkä ja sisältää isoja ja pieniä kirjaimia sekä ainakin yksi numero
  - sovellus ilmoittaa, mikäli käyttäjätunnus tai salasana ei täytä yllä olevia vaatimuksia
- käyttäjä voi kirjautua järjestelmään
  - kirjautuminen onnistuu, kun käyttäjä syöttää olemassaolevan käyttäjätunnuksen ja sitä vastaavan salasanan kirjautumislomakkeella
  - sovellus ilmoittaa, mikäli käyttäjää ei ole olemassa tai salasana on virheellinen

### Kirjautumisen jälkeen

- käyttäjä voi tarkastella sovellukseen tallentamiaan reseptejä
- käyttäjä voi tarkastella muiden käyttäjien sovellukseen tallentamia reseptejä
- käyttäjä voi hakea reseptejä ainakin raaka-aineiden perusteella
- käyttäjä voi luoda uuden reseptin
  - reseptillä on oltava nimi, raaka-aineet, valmistusaika ja valmistusohjeet
- käyttäjä voi kirjautua ulos järjestelmästä

## Jatkokehitysideoita

Sovelluksen kehittäminen aloitetaan yllä kuvaillusta, toiminnoiltaan suppeammasta perusversiosta. Mikäli mahdollista, sovellukseen voidaan lisätä esimerkiksi seuraavia toiminnallisuuksia:

- reseptien luokittelu esimerkiksi keittiön (italialainen, meksikolainen, thaimaalainen...) perusteella
- reseptien hakeminen useilla eri kriteereillä
- käyttäjä voi lisätä reseptin omaan reseptikirjaansa eli eräänlaiseen suosikkilistaan
- sovellus suosittelee käyttäjälle uusia reseptejä omaan reseptikirjaan tallennettujen reseptien perusteella
- reseptien arviointi tähtiluokituksella ja arvioiden keskiarvon laskeminen/näyttäminen
- raaka-aineiden kilo-/litrahinnan lisääminen järjestelmään ja annoskohtaisen hinnan laskeminen näiden perusteella
- raaka-aineiden kalorimäärien lisääminen järjestelmään ja annoskohtaisen kalorimäärän laskeminen näiden perusteella

## Toimintaympäristön rajoitteet

Ohjelmiston tulee toimia ainakin Helsingin yliopiston tietojenkäsittelytieteen osaston Linux-koneilla, joissa on Java asennettuna.
