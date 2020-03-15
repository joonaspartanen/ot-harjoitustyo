package com.mycompany.unicafe;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class KassapaateTest {

    Kassapaate kassa;
    Maksukortti korttiJollaPaljonRahaa;
    Maksukortti korttiJollaVahanRahaa;

    @Before
    public void setUp() {
        kassa = new Kassapaate();
        korttiJollaPaljonRahaa = new Maksukortti(1000);
        korttiJollaVahanRahaa = new Maksukortti(200);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void uudenKassapaatteenRahamaaraOnOikein() {
        assertEquals(kassa.kassassaRahaa(), 100000);
    }

    @Test
    public void uudessaKassapaatteessaEdullistenLounaidenMaaraOikein() {
        assertEquals(kassa.edullisiaLounaitaMyyty(), 0);
    }

    @Test
    public void uudessaKassapaatteessaMaukkaidenLounaidenMaaraOikein() {
        assertEquals(kassa.maukkaitaLounaitaMyyty(), 0);
    }

    @Test
    public void rahaaKasitellaanOikeinKunLounasEdullinenJaMaksuRiittava() {
        int vaihtoraha = kassa.syoEdullisesti(300);

        assertEquals(kassa.kassassaRahaa(), 100240);
        assertEquals(vaihtoraha, 60);
    }

    @Test
    public void lounaidenMaaraKasvaaOikeinKunLounasEdullinenJaMaksuRiittava() {
        kassa.syoEdullisesti(300);

        assertEquals(kassa.edullisiaLounaitaMyyty(), 1);
    }

    @Test
    public void rahaaKasitellaanOikeinKunLounasEdullinenJaMaksuEiRiittava() {
        int vaihtoraha = kassa.syoEdullisesti(200);

        assertEquals(kassa.kassassaRahaa(), 100000);
        assertEquals(vaihtoraha, 200);
    }

    @Test
    public void lounaidenMaaraEiKasvaKunLounasEdullinenJaMaksuEiRiittava() {
        kassa.syoEdullisesti(200);

        assertEquals(kassa.edullisiaLounaitaMyyty(), 0);
    }

    @Test
    public void rahaaKasitellaanOikeinKunLounasMaukasJaMaksuRiittava() {
        int vaihtoraha = kassa.syoMaukkaasti(500);

        assertEquals(kassa.kassassaRahaa(), 100400);
        assertEquals(vaihtoraha, 100);
    }

    @Test
    public void lounaidenMaaraKasvaaOikeinKunLounasMaukasJaMaksuRiittava() {
        kassa.syoMaukkaasti(400);

        assertEquals(kassa.maukkaitaLounaitaMyyty(), 1);
    }

    @Test
    public void rahaaKasitellaanOikeinKunLounasMaukasJaMaksuEiRiittava() {
        int vaihtoraha = kassa.syoMaukkaasti(300);

        assertEquals(kassa.kassassaRahaa(), 100000);
        assertEquals(vaihtoraha, 300);
    }

    @Test
    public void lounaidenMaaraEiKasvaKunLounasMaukasJaMaksuEiRiittava() {
        kassa.syoMaukkaasti(300);

        assertEquals(kassa.maukkaitaLounaitaMyyty(), 0);
    }

    @Test
    public void rahaaKasitellaanOikeinKunLounasEdullinenJaKortillaRahaa() {
        boolean riittiko = kassa.syoEdullisesti(korttiJollaPaljonRahaa);
        assertTrue(riittiko);
        assertEquals(korttiJollaPaljonRahaa.saldo(), 1000 - 240);
    }

    @Test
    public void lounaidenMaaraKasvaaOikeinKunLounasEdullinenJaKortillaRahaa() {
        kassa.syoEdullisesti(korttiJollaPaljonRahaa);
        assertEquals(kassa.edullisiaLounaitaMyyty(), 1);
    }

    @Test
    public void rahaaKasitellaanOikeinKunLounasEdullinenJaKortillaVahanRahaa() {
        boolean riittiko = kassa.syoEdullisesti(korttiJollaVahanRahaa);
        assertFalse(riittiko);
        assertEquals(korttiJollaVahanRahaa.saldo(), 200);
    }

    @Test
    public void lounaidenMaaraKasvaaOikeinKunLounasEdullinenJaKortillaVahanRahaa() {
        kassa.syoEdullisesti(korttiJollaVahanRahaa);
        assertEquals(kassa.edullisiaLounaitaMyyty(), 0);
    }

    @Test
    public void rahaaKasitellaanOikeinKunLounasMaukasJaKortillaRahaa() {
        boolean riittiko = kassa.syoMaukkaasti(korttiJollaPaljonRahaa);
        assertTrue(riittiko);
        assertEquals(korttiJollaPaljonRahaa.saldo(), 1000 - 400);
    }

    @Test
    public void lounaidenMaaraKasvaaOikeinKunLounasMaukasJaKortillaRahaa() {
        kassa.syoMaukkaasti(korttiJollaPaljonRahaa);
        assertEquals(kassa.maukkaitaLounaitaMyyty(), 1);
    }

    @Test
    public void rahaaKasitellaanOikeinKunLounasMaukasJaKortillaVahanRahaa() {
        boolean riittiko = kassa.syoMaukkaasti(korttiJollaVahanRahaa);
        assertFalse(riittiko);
        assertEquals(korttiJollaVahanRahaa.saldo(), 200);
    }

    @Test
    public void lounaidenMaaraKasvaaOikeinKunLounasMaukasJaKortillaVahanRahaa() {
        kassa.syoMaukkaasti(korttiJollaVahanRahaa);
        assertEquals(kassa.maukkaitaLounaitaMyyty(), 0);
    }

    @Test
    public void kassanSaldoEiMuutuKunLounasEdullinenJaMaksetaanKortilla() {
        kassa.syoEdullisesti(korttiJollaPaljonRahaa);
        assertEquals(kassa.kassassaRahaa(), 100000);
    }

    @Test
    public void kassanSaldoEiMuutuKunLounasMaukasJaMaksetaanKortilla() {
        kassa.syoMaukkaasti(korttiJollaPaljonRahaa);
        assertEquals(kassa.kassassaRahaa(), 100000);
    }

    @Test
    public void rahanLataaminenOnnistuuKunSummaPositiivinen() {
        kassa.lataaRahaaKortille(korttiJollaVahanRahaa, 1000);
        assertEquals(korttiJollaVahanRahaa.saldo(), 200 + 1000);
    }

    @Test
    public void rahaaEiLadataKunSummaPositiivinen() {
        kassa.lataaRahaaKortille(korttiJollaVahanRahaa, -1000);
        assertEquals(korttiJollaVahanRahaa.saldo(), 200);
    }

    @Test
    public void kassanSaldoKasvaaOikeinKunRahaaLadataanKortille() {
        kassa.lataaRahaaKortille(korttiJollaVahanRahaa, 1000);
        assertEquals(kassa.kassassaRahaa(), 100000 + 1000);
    }

    @Test
    public void kassanSaldoEiKasvaJosKortilleLadattaSummaNegatiivinen() {
        kassa.lataaRahaaKortille(korttiJollaVahanRahaa, -1000);
        assertEquals(kassa.kassassaRahaa(), 100000);
    }
}
