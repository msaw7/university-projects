package test.java;

import main.java.loteria.*;
import main.java.gracze.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TotolotekTest {
    @Test
    public void anulowanieZakładu() {
        //mając
        Blankiet b = new Blankiet();
        b.zaznacz(1, 1);
        b.zaznacz(1, 2);
        b.zaznacz(1, 3);
        b.zaznacz(1, 4);
        b.zaznacz(1, 5);
        b.zaznacz(1, 6);
        assertEquals(1, b.dajLiczbęZakładów());
        //gdy
        b.anuluj(1);
        //wtedy
        assertEquals(0, b.dajLiczbęZakładów());
    }

    @Test
    public void zaznaczanieLiczbyLosowań() {
        //mając
        Blankiet b1 = new Blankiet();
        b1.wybierzLiczbęLosowań(5);

        Blankiet b2 = new Blankiet();
        b2.wybierzLiczbęLosowań(5);
        b2.wybierzLiczbęLosowań(9);

        Blankiet b3 = new Blankiet();

        //wtedy
        assertEquals(5, b1.dajLiczbęLosowań());
        assertEquals(9, b2.dajLiczbęLosowań());
        assertEquals(1, b3.dajLiczbęLosowań());
    }

    @Test
    public void chybiłTrafił() {
        //mając
        Blankiet b1 = new Blankiet(3, 7);

        //wtedy
        assertEquals(3, b1.dajLiczbęZakładów());
        assertEquals(7, b1.dajLiczbęLosowań());
        assertEquals(6, b1.dajZakład(1).length);
        assertEquals(6, b1.dajZakład(2).length);
        assertEquals(6, b1.dajZakład(3).length);
        assertEquals(0, b1.dajZakład(4).length);
        assertEquals(0, b1.dajZakład(5).length);
        assertEquals(0, b1.dajZakład(6).length);
        assertEquals(0, b1.dajZakład(7).length);
        assertEquals(0, b1.dajZakład(8).length);
    }

    @Test
    public void liczbaTrafionychLiczb() {
        //mając
        int[] wygraneLiczby = new int[] {10, 11, 12, 13, 14, 15};
        Losowanie l = new Losowanie(wygraneLiczby);
        //tworzymy blankiet z odpowiednią liczbą trafionych liczb
        Blankiet[] blankiety = new Blankiet[7];
        for(int i = 0; i < 7; i ++) blankiety[i] = new Blankiet();

        for(int i = 0; i <= 6; i ++) { //ile poprawnych strzałów w danym blankiecie
            for(int j = 0; j < i; j ++) {
                blankiety[i].zaznacz(1, wygraneLiczby[j]);
            }
            for(int j = i; j < 6; j ++) {
                blankiety[i].zaznacz(1, j + 1);
            }
        }

        //wtedy
        for(int i = 0; i <= 6; i ++) {
            assertEquals(i, blankiety[i].ileLiczbSięPokrywa(l).get(0));
        }
    }

    @Test
    public void kuponIGracz() {
        //mając
        Panstwo mojePaństwo = new Panstwo();
        Centrala mojaCentrala = new Centrala(0, mojePaństwo);
        Kolektura mojaKolektura = new Kolektura(1, mojaCentrala);
        Blankiet mójBlankiet = new Blankiet(2, 3);
        Gracz biedny = new Minimalista("", "", "", 0, mojaKolektura);
        Gracz bogaty = new Minimalista("", "", "", 100000, mojaKolektura);

        //wtedy
        assertThrows(NiewystarczajaceSrodkiException.class, () -> {
            biedny.zapłać(100);
        });
        bogaty.zapłać(100); //tu nie powinno wyrzucić

        //gdy
        bogaty.kupKupon();
        Kupon kupon = bogaty.dajKupon(1);

        //wtedy
        assertEquals(1, kupon.ileLosowań());
        assertEquals(mojaKolektura, kupon.dajGdzieZakupione());
        assertThrows(IllegalArgumentException.class, () -> {
            bogaty.dajKupon(2);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            bogaty.dajKupon(0);
        });

        //gdy
        bogaty.odbierzNagrodę(1);

        //wtedy poniższe ma nie rzucić wyjątku, ponieważ gracz nie odebrał nagrody, bo losowanie jeszcze się nie odbyło
        kupon = bogaty.dajKupon(1);

        //gdy
        mojaCentrala.ogłośWyniki();
        bogaty.odbierzNagrodę(1);

        //wtedy już kupon został oddany
        assertThrows(IllegalArgumentException.class, () -> {
            bogaty.dajKupon(1);
        });
    }

    @Test
    public void obszernyTestLosowania() {
        //tworzymy 7 blankietów, aby móc dokładnie zasymulować liczbę trafionych zakładów

        int[] wygraneLiczby = new int[] {10, 11, 12, 13, 14, 15};
        Losowanie l = new Losowanie(wygraneLiczby);
        //tworzymy blankiet z odpowiednią liczbą trafionych liczb
        Blankiet[] blankiety = new Blankiet[7];
        for(int i = 0; i < 7; i ++) blankiety[i] = new Blankiet();

        for(int i = 0; i <= 6; i ++) { //ile poprawnych strzałów w danym blankiecie
            for(int j = 0; j < i; j ++) {
                blankiety[i].zaznacz(1, wygraneLiczby[j]);
            }
            for(int j = i; j < 6; j ++) {
                blankiety[i].zaznacz(1, j + 1);
            }
        }

        Panstwo mojePaństwo = new Panstwo();
        Centrala mojaCentrala = new Centrala(0, mojePaństwo);
        Kolektura mojaKolektura = new Kolektura(1, mojaCentrala);

        mojaCentrala.przypiszLosowania(l);
        //tworzymy 7 stałoblankietowych graczy, dla których możemy kontrolować, kiedy stawiają zakłady
        Staloblankietowy[] gracze = new Staloblankietowy[7];
        for(int i = 0; i < 7; i ++) {
            gracze[i] = new Staloblankietowy("", "", "", 999999999,
                    blankiety[i], new Kolektura[] {mojaKolektura});
        }
        int[] ileTrafionych = new int[] {631915, 39814, 12876, 4434, 303, 17, 2};

        //sprawdzanie transakcji zakupu kuponów
        int ileKupionych = 0;
        for(int i = 0; i < 7; i ++) {
            ileKupionych += ileTrafionych[i];
        }
        for(int i = 0; i < 7; i ++) {
            for(int j = 0; j < ileTrafionych[i]; j ++) {
                long początkowy = gracze[i].dajFundusze();
                gracze[i].kupKupon();
                long końcowy = gracze[i].dajFundusze();
                //ci gracze są bogaci i mają pieniądze na swoje kupony
                assertEquals((long) 300, początkowy - końcowy);

            }
        }
        assertEquals((long) 240 * ileKupionych, mojaCentrala.dostępneFundusze());
        assertEquals((long) 60 * ileKupionych, mojePaństwo.ileZysków());

        //testowanie informacji z ogłoszenia wyników
        mojaCentrala.ogłośWyniki();
        int[] liczbyWygranych = new int[4];
        //decyduje się nie sprawdzać wielkości pul wygranych i nagród
        //musiałbym napisać dokładnie te samą logikę co w Losowaniu, ale drugi raz
        //uważam, że mija się to z celem
        long[] pule = new long[4];
        long[] nagrody = new long[4];
        mojaCentrala.przepiszWyniki(1, liczbyWygranych, pule, nagrody);
        for(int i = 0; i < 4; i ++) {
            assertEquals(ileTrafionych[6 - i], liczbyWygranych[i]);
        }

        //będziemy odbierać POJEDYNCZY kupon od każdego z graczy
        for(int i = 0; i < 7; i ++) {
            long początkowy = gracze[i].dajFundusze(), początkowyPaństwa = mojePaństwo.ileZysków();
            gracze[i].odbierzNagrodę(1); //to jest "numer kuponu" tzn numer jaki gracz przypisał w swojej kolekcji
            long końcowy = gracze[i].dajFundusze(), końcowyPaństwa = mojePaństwo.ileZysków();
            long delta = końcowy - początkowy, deltaPaństwa = końcowyPaństwa - początkowyPaństwa;
            if(i <= 2) {
                assertEquals(0, delta);
                assertEquals(0, deltaPaństwa);
            }
            else if(i == 3) {
                assertEquals(nagrody[3], delta);
                assertEquals(0, deltaPaństwa);
            }
            else if(i == 4) {
                assertEquals(nagrody[2], delta);
                assertEquals(0, deltaPaństwa);
            }
            //tutaj z podatkiem
            else if(i == 5) {
                assertEquals((nagrody[1] * 9) / 10, delta);
                assertEquals(nagrody[1] / 10, deltaPaństwa);
            }
            else {
                assertEquals((nagrody[0] * 9) / 10, delta);
                assertEquals(nagrody[0] / 10, deltaPaństwa);
            }

        }
    }
}
