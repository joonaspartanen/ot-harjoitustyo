package com.mycompany.unicafe;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class MaksukorttiTest {

    Maksukortti kortti;

    @Before
    public void setUp() {
        kortti = new Maksukortti(1000);
    }

    @Test
    public void luotuKorttiOlemassa() {
        assertTrue(kortti != null);
    }

    @Test
    public void kortinSaldoAlussaOikein() {
        assertEquals(kortti.toString(), "saldo: 10.0");
    }

    @Test
    public void rahanLataaminenKasvattaaSaldoaOikein() {
        kortti.lataaRahaa(500);
        assertEquals(kortti.toString(), "saldo: 15.0");
    }

    @Test
    public void saldoVaheneeOikeinJosRahaaTarpeeksi() {
        kortti.otaRahaa(500);
        assertEquals(kortti.toString(), "saldo: 5.0");
    }

    @Test
    public void saldoEiMuutuJosRahaaEiOleTarpeeksi() {
        kortti.otaRahaa(2000);
        assertEquals(kortti.toString(), "saldo: 10.0");
    }

    @Test
    public void otaRahaaPalauttaaTrueJosRahatRiittivat() {
        boolean riittiko = kortti.otaRahaa(240);
        assertTrue(riittiko);
    }
    
    @Test
    public void otaRahaaPalauttaaFalseJosRahatEivatRiita() {
        boolean riittiko = kortti.otaRahaa(2000);
        assertFalse(riittiko);
    }
    
    @Test
    public void saldoPalauttaaOikeanSaldon() {
        assertEquals(kortti.saldo(), 1000);
    }
}
