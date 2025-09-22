package main.java.loteria;

import main.java.gracze.*;
import java.util.HashSet;

public class Kolektura {
    private static final long cenaKuponu = 3_00, podatekOdKuponu = 0_60;
    private final int numerKolektury;
    private final Centrala mojaCentrala;
    private HashSet<Kupon> doOdebrania;

    public Kolektura(int numerKolektury, Centrala mojaCentrala) {
        if(mojaCentrala == null) {
            throw new NullPointerException();
        }
        this.numerKolektury = numerKolektury;
        this.mojaCentrala = mojaCentrala;
        doOdebrania = new HashSet<>();
    }

    //pozwalamy na to, żeby gracz mógł kupić kupon bez poprawnego zakładu za darmo
    public Kupon własnyBlankiet(Gracz osoba, Blankiet własny) throws NiewystarczajaceSrodkiException {
        if(osoba == null || własny == null) {
            throw new NullPointerException();
        }
        //opłata
        int ileLosowań = własny.dajLiczbęLosowań();
        int ileZakładów = własny.dajLiczbęZakładów();
        long cena = cenaKuponu * ileZakładów * ileLosowań;
        long podatek = podatekOdKuponu * ileZakładów * ileLosowań;
        //jeśli gracz nie ma pieniędzy, rzucamy wyjątek, który przechwycimy w graczu
        osoba.zapłać(cena);
        mojaCentrala.wpłaćPieniądze(cena);
        mojaCentrala.zapłaćPodatek(podatek);

        //generowanie kuponu
        int id = mojaCentrala.dajIdNastępnegoKuponu();
        int[] numeryLosowań = new int[ileLosowań];
        int start = mojaCentrala.dajNumerNastępnegoLosowania();
        for(int i = 0; i < ileLosowań; i ++) {
            numeryLosowań[i] = start + i;
        }
        Kupon produkt = new Kupon(id, this, własny, numeryLosowań, cena, podatek);

        //teraz chcemy, żeby centrala przetworzyła wynik tego kuponu na potrzeby ustalania puli
        mojaCentrala.uwzględnijKupon(produkt);
        //oraz zapamiętać ten kupon w lokalnej bazie
        doOdebrania.add(produkt);
        return produkt;
    }

    //dubluje się dużo kodu, ale musi tak być, ponieważ obliczanie ceny, którą musi zapłacić gracz jest problematyczne
    public Kupon chybiłTrafił(Gracz osoba, int ileZakładów, int ileLosowań) throws NiewystarczajaceSrodkiException {
        if(osoba == null) {
            throw new NullPointerException();
        }
        //jeśli liczba zakładów lub losowań jest zła, wychwycimy to na poziomie tworzenia blankietu
        Blankiet losowy = new Blankiet(ileZakładów, ileLosowań);

        long cena = cenaKuponu * ileZakładów * ileLosowań;
        long podatek = podatekOdKuponu * ileZakładów * ileLosowań;
        osoba.zapłać(cena);
        mojaCentrala.wpłaćPieniądze(cena);
        mojaCentrala.zapłaćPodatek(podatek);

        int id = mojaCentrala.dajIdNastępnegoKuponu();
        int[] numeryLosowań= new int[ileLosowań];
        int start = mojaCentrala.dajNumerNastępnegoLosowania();
        for(int i = 0; i < ileLosowań; i ++) {
            numeryLosowań[i] = start + i;
        }
        Kupon produkt = new Kupon(id, this, losowy, numeryLosowań, cena, podatek);

        mojaCentrala.uwzględnijKupon(produkt);
        doOdebrania.add(produkt);
        return produkt;
    }

    public void wypłaćNagrodę(Gracz osoba, Kupon kuponGracza) {
        if(osoba == null || kuponGracza == null) {
            throw new NullPointerException();
        }
        if (doOdebrania.contains(kuponGracza)) {
            //tutaj otrzymujemy wartość już po odliczeniu podatku
            long wartość = mojaCentrala.wypłaćKupon(kuponGracza);
            osoba.otrzymajWygraną(wartość);
            doOdebrania.remove(kuponGracza);
        }
    }

    public int dajNumerKolektury() {
        return numerKolektury;
    }


    public boolean sprawdźKupon(Kupon kuponGracza) {
        if(kuponGracza == null) {
            throw new NullPointerException();
        }
        int[] numeryLosowań = kuponGracza.dajNumeryLosowań();
        for(int i : numeryLosowań) {
            if(i >= mojaCentrala.dajNumerNastępnegoLosowania()) {
                return false;
            }
        }
        return true;
    }

}
