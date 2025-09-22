package main.java.prezentacja;

import main.java.gracze.*;
import main.java.loteria.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Prezentacja {
    public static void main(String[] args) {
        //państwo
        Random random = new Random(0); //to kontroluje losowość wewnątrz prezentacji (nie w graczach i centrali)
        Panstwo wielkaLechia = new Panstwo();

        //centrala
        long funduszeStartowe = 23_189_00L; //wszystkie kwoty są podawane w groszach
        Centrala naszaCentrala = new Centrala(funduszeStartowe, wielkaLechia);

        //kolektury
        final int liczbaKolektur = 10;
        Kolektura[] naszeKolektury = new Kolektura[liczbaKolektur];
        for(int i = 0; i < liczbaKolektur; i ++) {
            naszeKolektury[i] = new Kolektura(i + 1, naszaCentrala);
        }
        final int liczbaGraczy = 800;
        Gracz[] wszyscyGracze = new Gracz[liczbaGraczy];

        //minimaliści
        for(int i = 0; i < liczbaGraczy / 4; i ++) {
            wszyscyGracze[i] = new Minimalista("Jan", "Minimalny", "3928173981", i * 10_00,
                    naszeKolektury[i % liczbaKolektur]);
        }

        //losowi
        for(int i = liczbaGraczy / 4; i < liczbaGraczy / 2; i ++) {
            wszyscyGracze[i] = new Losowy("Julia", "Losowa", "321896512", naszeKolektury);
        }

        //stałoliczbowi
        ArrayList<Integer> indeksyKolektur = new ArrayList<>();
        for(int i = 0; i < liczbaKolektur; i ++) {
            indeksyKolektur.add(i);
        }

        ArrayList<Integer> liczbyDoZaznaczenia = new ArrayList<>();
        for(int i = 1; i <= Blankiet.MAX_NUMER; i ++) {
            liczbyDoZaznaczenia.add(i);
        }

        for(int i = liczbaGraczy / 2; i < (liczbaGraczy / 4) * 3; i ++) {
            Kolektura[] ulubioneKolektury = new Kolektura[random.nextInt(5) + 1];
            Collections.shuffle(indeksyKolektur);
            for(int j = 0; j < ulubioneKolektury.length; j ++) {
                ulubioneKolektury[j] = naszeKolektury[indeksyKolektur.get(j)];
            }
            int[] ulubioneLiczby = new int[6];
            Collections.shuffle(liczbyDoZaznaczenia);
            for(int j = 0; j < 6; j ++) {
                ulubioneLiczby[j] = liczbyDoZaznaczenia.get(j);
            }
            wszyscyGracze[i] = new Staloliczbowy("Paweł", "Ustalony", "396102196", i*30_00,
                    ulubioneLiczby, ulubioneKolektury);
        }

        //stałoblankietowi
        for(int i = (liczbaGraczy / 4) * 3; i < liczbaGraczy; i ++) {
            Kolektura[] ulubioneKolektury = new Kolektura[random.nextInt(5) + 1];
            Collections.shuffle(indeksyKolektur);
            for(int j = 0; j < ulubioneKolektury.length; j ++) {
                ulubioneKolektury[j] = naszeKolektury[indeksyKolektur.get(j)];
            }
            Blankiet blankietGracza = new Blankiet(random.nextInt(8) + 1, random.nextInt(10) + 1);
            wszyscyGracze[i] = new Staloblankietowy("Piotr", "Blankietowy", "987518", i*20_00,
                    blankietGracza, ulubioneKolektury);
        }

        /*
        wypisujemy graczy (opcjonalnie)
        for(int i = 0; i < liczbaGraczy; i ++) {
            System.out.println(wszyscyGracze[i]);
        }
        */

        //symulujemy losowania
        for(int i = 1; i <= 20; i ++) {
            for(int j = 0; j < liczbaGraczy; j ++) {
                wszyscyGracze[j].kupKupon();
            }
            naszaCentrala.ogłośWyniki();
            for(int j = 0; j < liczbaGraczy; j ++) {
                wszyscyGracze[j].odbierzWszystkie();
            }
        }
        //wypisujemy losowania
        for(int i = 1; i <= 20; i ++) {
            System.out.println(naszaCentrala.wypiszLosowanie(i));
        }
        //wypisujemy budżet państwa
        System.out.println(wielkaLechia);
    }
}
