package main.java.gracze;

import main.java.loteria.*;

public class Staloliczbowy extends Gracz {
    private static final int ILE_ZAZNACZYĆ = Blankiet.ILE_ZAZNACZYĆ, ILE_LOSOWAŃ = 10;

    private int[] liczby;
    private Kolektura[] listaKolektur;
    private int licznik;

    public Staloliczbowy(String imię, String nazwisko, String pesel, long fundusze, int[] liczby, Kolektura[] listaKolektur) {
        super(imię, nazwisko, pesel, fundusze);
        if(liczby == null || listaKolektur == null) {
            throw new NullPointerException();
        }
        if(liczby.length != ILE_ZAZNACZYĆ) {
            throw new IllegalArgumentException();
        }
        this.liczby = liczby;
        this.listaKolektur = listaKolektur;
        licznik = 0;
    }

    @Override
    public void kupKupon() {
        //ten gracz sprawdza, czy poprzedni kupon został wypłacony (według wytycznych z treści)
        if(!kupony.isEmpty()) {
            return;
        }
        Blankiet nowy = new Blankiet();
        for(int i = 0; i < ILE_ZAZNACZYĆ; i ++) {
            nowy.zaznacz(1, liczby[i]);
        }
        nowy.wybierzLiczbęLosowań(ILE_LOSOWAŃ);
        //cyklujemy po kolekturach
        Kolektura mojaKolektura = listaKolektur[licznik % listaKolektur.length];
        try {
            Kupon potencjalny = mojaKolektura.własnyBlankiet(this, nowy);
            licznik ++;
            kupony.add(potencjalny);
        } catch (NiewystarczajaceSrodkiException e) {
            //jeśli brak środków, to nie kupuje
        }
    }
}
