package main.java.loteria;

import java.util.*;

public class Blankiet {
    public static final int MAX_ZAKŁADY = 8, MAX_NUMER = 49, ILE_ZAZNACZYĆ = 6, MAX_LOSOWANIA = 10;

    private boolean[][] pola;
    private boolean[] czyAnulowane;
    private boolean[] liczbaLosowań;

    //dla klienta
    public Blankiet() {
        pola = new boolean[MAX_ZAKŁADY + 1][MAX_NUMER + 1];
        czyAnulowane = new boolean[MAX_ZAKŁADY + 1];
        liczbaLosowań = new boolean[MAX_LOSOWANIA + 1];
        for(int i = 1; i <= MAX_ZAKŁADY; i ++) {
            czyAnulowane[i] = false;
            liczbaLosowań[i] = false;
            for(int j = 0; j <= MAX_NUMER; j ++) {
                pola[i][j] = false;
            }
        }
        for(int i = 0; i <= MAX_LOSOWANIA; i ++) {
            liczbaLosowań[i] = false;
        }
    }

    //chybił-trafił
    public Blankiet(int ileZakładów, int ileLosowań) {
        this();
        if(ileZakładów <= 0 || ileZakładów > MAX_ZAKŁADY) {
            throw new IllegalArgumentException("Niepoprawna liczba zakładów");
        }
        if(ileLosowań<= 0 || ileLosowań > MAX_LOSOWANIA) {
            throw new IllegalArgumentException("Niepoprawna liczba losowań");
        }
        ArrayList<Integer> numery = new ArrayList<>();
        for(int i = 1; i <= MAX_NUMER; i ++) {
            numery.add(i);
        }
        for(int i = 1; i <= ileZakładów; i ++) {
            Collections.shuffle(numery, new Random());
            for(int j = 0; j < ILE_ZAZNACZYĆ; j ++) {
                pola[i][numery.get(j)] = true;
            }
        }
        liczbaLosowań[ileLosowań] = true;
    }

    public void zaznacz(int nrZakładu, int liczba) {
        if(nrZakładu <= 0 || nrZakładu > MAX_ZAKŁADY) {
            throw new IllegalArgumentException("Niepoprawny numer zakładu");
        }
        if(liczba <= 0 || liczba > MAX_NUMER) {
            throw new IllegalArgumentException("Niepoprawny numer do zaznaczenia");
        }
        pola[nrZakładu][liczba] = true;
    }

    public void anuluj(int nrZakładu) {
        if(nrZakładu <= 0 || nrZakładu > MAX_ZAKŁADY) {
            throw new IllegalArgumentException("Niepoprawny numer zakładu");
        }
        czyAnulowane[nrZakładu] = true;
    }

    public void wybierzLiczbęLosowań(int liczba) {
        if(liczba <= 0 || liczba > MAX_LOSOWANIA) {
            throw new IllegalArgumentException("Niepoprawna liczba losowań");
        }
        liczbaLosowań[liczba] = true;
    }

    //zwraca liczby zaznaczone
    public int[] dajZakład(int nrZakładu) {
        if(nrZakładu <= 0 || nrZakładu > MAX_ZAKŁADY) {
            throw new IllegalArgumentException("Niepoprawny numer zakładu");
        }
        int[] wynik;
        if(czyAnulowane[nrZakładu]) {
            wynik = new int[0];
            return wynik;
        }
        int ileZaznaczonych = 0;
        for(int i = 1; i <= MAX_NUMER; i ++) {
            if(pola[nrZakładu][i]) ileZaznaczonych ++;
        }
        wynik = new int[ileZaznaczonych];
        int licznik = 0;
        for(int i = 1; i <= MAX_NUMER; i ++) {
            if(pola[nrZakładu][i]) {
                wynik[licznik] = i;
                licznik ++;
            }
        }
        return wynik;
    }

    public int dajLiczbęZakładów() {
        int wynik = 0;
        for(int i = 1; i <= MAX_ZAKŁADY; i ++) {
            int[] temp = dajZakład(i);
            if(temp.length == ILE_ZAZNACZYĆ) {
                wynik ++;
            }
        }
        return wynik;
    }

    public int dajLiczbęLosowań() {
        for(int i = MAX_LOSOWANIA; i >= 1; i --) {
            if(liczbaLosowań[i]) {
                return i;
            }
        }
        //Przypadek, gdy nic nie zaznaczone
        return 1;
    }

    //zwraca liczbę liczb, które zgadzają się z losowaniem
    public ArrayList<Integer> ileLiczbSięPokrywa(Losowanie l) {
        if(l == null) {
            throw new NullPointerException();
        }
        ArrayList<Integer> wynik = new ArrayList<>();
        for(int i = 1; i <= MAX_ZAKŁADY; i ++) {
            int[] zakład = dajZakład(i);
            int[] wygrane = l.dajWygraneLiczby();
            if(zakład.length == ILE_ZAZNACZYĆ) {
                Set<Integer> set = new HashSet<>();
                for (int num : wygrane) {
                    set.add(num);
                }
                int zlicz = 0;
                for (int num : zakład) {
                    if (set.contains(num)) {
                        zlicz++;
                    }
                }
                wynik.add(zlicz);
            }
        }
        return wynik;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int licznik = 1;
        for(int i = 1; i <= MAX_ZAKŁADY; i ++) {
            int[] temp = dajZakład(i);
            if(temp.length == ILE_ZAZNACZYĆ) {
                sb.append(licznik + ": ");
                for(int j = 0; j < ILE_ZAZNACZYĆ; j ++) {
                    if(temp[j] < 10) sb.append(" ");
                    sb.append(temp[j] + " ");
                }
                sb.append("\n");
                licznik ++;
            }
        }
        sb.append("LICZBA LOSOWAŃ: " + dajLiczbęLosowań() + "\n");
        return sb.toString();
    }

}
