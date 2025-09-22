package main.java.loteria;

import java.util.ArrayList;
import java.util.Random;

public class Kupon {
    private static final int MAX_ZNACZNIK = 999999999;

    private int id;
    private int nrKolektury;
    private int znacznik;
    private int suma;
    private long cena;
    private long podatek;
    private Blankiet zakład;
    private int[] numeryLosowań;
    private Kolektura gdzieZakupione;

    //wyznacza sumę kontrolną
    private void liczSumę() {
        suma = 0;
        int temp = id;
        while(temp > 0) {
            suma += temp % 10;
            temp = temp / 10;
        }
        temp = nrKolektury;
        while(temp > 0) {
            suma += temp % 10;
            temp = temp / 10;
        }
        temp = znacznik;
        while(temp > 0) {
            suma += temp % 10;
            temp = temp / 10;
        }
        suma = suma % 100;
    }

    public Kupon(int id, Kolektura kolektura, Blankiet zakład, int[] numeryLosowań, long cena, long podatek) {
        if(kolektura == null || zakład == null) {
            throw new NullPointerException();
        }
        this.id = id;
        this.gdzieZakupione = kolektura;
        this.nrKolektury = kolektura.dajNumerKolektury();
        Random rnd = new Random();
        znacznik = rnd.nextInt(MAX_ZNACZNIK + 1);
        liczSumę();
        this.zakład = zakład;
        this.numeryLosowań = numeryLosowań;
        this.cena = cena;
        this.podatek = podatek;
    }

    public int dajId() {
        return id;
    }

    public long dajCenę() {
        return cena;
    }

    public long dajPodatek() {
        return podatek;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("KUPON NR ");
        sb.append(id + "-" + nrKolektury + "-" + znacznik + "-" + suma + "\n");
        sb.append(zakład.toString());
        sb.append("NUMERY LOSOWAŃ: ");
        for (int j : numeryLosowań) {
            sb.append(" " + j);
        }
        sb.append("\nCENA: ");
        sb.append(KonwerterKwoty.groszeDoStringa(cena));
        return sb.toString();
    }

    public ArrayList<Integer> ileLiczbSięPokrywa(Losowanie l) {
        if(l == null) {
            throw new NullPointerException();
        }
        for (int i : numeryLosowań) {
            if (i == l.dajNumerLosowania()) {
                return zakład.ileLiczbSięPokrywa(l);
            }
        }
        return new ArrayList<Integer>();
    }

    public int ileLosowań() {
        return zakład.dajLiczbęLosowań();
    }

    public int[] dajNumeryLosowań() {
        return numeryLosowań;
    }

    public Kolektura dajGdzieZakupione() {
        return gdzieZakupione;
    }

    public int dajZnacznik() {
        return znacznik;
    }

    /*
    To tak naprawdę nie jest potrzebne, moglibyśmy użyć Object'owego equals. (bo kolektura zawsze tworzy swoje kupony)
     */
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Kupon inny = (Kupon) o;
        if(id != inny.dajId()) return false;
        if(znacznik != inny.dajZnacznik()) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return znacznik;
    }

}
