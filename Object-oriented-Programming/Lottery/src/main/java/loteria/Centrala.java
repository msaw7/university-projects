package main.java.loteria;

import java.util.ArrayList;

public class Centrala {
    private static final int BUFOR_LOSOWAŃ = 10;
    private static final long MAKSYMALNA_NIEOPODATKOWANA = 2280_00;

    private long fundusze;
    private long kumulacja;
    private int numerNastępnegoLosowania;
    private int licznikKuponów;
    private Panstwo mojePaństwo;
    /*
    Losowania i ich numery wygrywające będą generowane zawczasu.
    W rzeczywistości jest to bardzo słaby pomysł, ponieważ ta informacja może wyciec.
    U nas nie jest to możliwe, ponieważ tylko Centrala ma dostęp do swoich losowań przed publikacją.
     */
    private ArrayList<Losowanie> losowania;

    public Centrala(long fundusze, Panstwo mojePaństwo) {
        if(mojePaństwo == null) {
            throw new NullPointerException();
        }
        this.fundusze = fundusze;
        this.mojePaństwo = mojePaństwo;
        kumulacja = 0;
        numerNastępnegoLosowania = 1;
        licznikKuponów = 0;
        losowania = new ArrayList<>();
        //musimy mieć wygenerowane 10 losowań do przodu, żeby kupony dobrze działały
        //jedno losowanie (0) jest dodane żeby zrównać indeksy
        for(int i = 0; i <= BUFOR_LOSOWAŃ; i ++) {
            losowania.add(new Losowanie(i));
        }
    }

    public int dajNumerNastępnegoLosowania() {
        return numerNastępnegoLosowania;
    }

    public int dajIdNastępnegoKuponu() {
        licznikKuponów ++;
        return licznikKuponów;
    }

    public void zapłaćPodatek(long kwota) {
        mojePaństwo.wpłać(kwota);
        wypłaćPieniądze(kwota);
    }

    public void wypłaćPieniądze(long kwota) {
        if(kwota > fundusze) {
            mojePaństwo.pożycz(kwota - fundusze);
            fundusze = 0;
        }
        else {
            fundusze -= kwota;
        }
    }

    public void wpłaćPieniądze(long kwota) {
        fundusze += kwota;
    }

    public void uwzględnijKupon(Kupon kupon) {
        if(kupon == null) {
            throw new NullPointerException();
        }
        //przechodzimy się po wszystkich losowaniach, których dotyczy kupon i je aktualizujemy
        int[] numeryLosowań = kupon.dajNumeryLosowań();
        for(int i : numeryLosowań) {
            losowania.get(i).uwzględnijKupon(kupon);
        }
    }

    public String wypiszLosowanie(int idLosowania) {
        if(idLosowania < 1 || idLosowania >= numerNastępnegoLosowania) {
            throw new IllegalArgumentException("To głosowanie jeszcze się nie odbyło!");
        }
        return losowania.get(idLosowania).pełneInfo();
    }

    public long dostępneFundusze() {
        return fundusze;
    }

    //płaci należyty podatek i wypłaca pieniądze (zmniejsza fundusze centrali)
    public long wypłaćKupon(Kupon kupon) {
        if(kupon == null) {
            throw new NullPointerException();
        }
        long wartość = 0;
        int[] numeryLosowań = kupon.dajNumeryLosowań();
        for(int i : numeryLosowań) {
            if(i >= numerNastępnegoLosowania) {
                //nie wypłacamy pieniędzy za losowanie które jeszcze się nie odbyło
                continue;
            }
            long[] wygrane = losowania.get(i).ewaluujKupon(kupon);
            for(long j : wygrane) {
                if(j <= MAKSYMALNA_NIEOPODATKOWANA) {
                    wartość += j;
                }
                else {
                    wartość += (j * 9) / 10;
                    mojePaństwo.wpłać(j / 10); //10% kwoty
                }
            }
        }
        wypłaćPieniądze(wartość);
        return wartość;
    }

    public void ogłośWyniki() {
        //generujemy nowe losowanie do "bufora"
        losowania.add(new Losowanie(BUFOR_LOSOWAŃ + numerNastępnegoLosowania));
        Losowanie aktualneLosowanie = losowania.get(numerNastępnegoLosowania);
        kumulacja = aktualneLosowanie.przeliczWygrane(kumulacja);
        numerNastępnegoLosowania ++;
        //od teraz można uzyskać dostęp do następnego losowania
    }

    //stworzone tylko na potrzeby testowania
    public void przypiszLosowania(Losowanie l) {
        ArrayList<Losowanie> noweLosowania = new ArrayList<>();
        //zrównanie indeksu
        noweLosowania.add(l);
        noweLosowania.add(l);
        losowania = noweLosowania;
    }

    //wypełnia przekazane tablice wielkości 4
    public void przepiszWyniki(int numerLosowania, int[] liczbyWygranych, long[] pule, long[] nagrody) {
        losowania.get(numerLosowania).przepiszWyniki(liczbyWygranych, pule, nagrody);
    }
}
