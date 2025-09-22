package main.java.loteria;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Losowanie {
    private static final int ILE_ZAZNACZYĆ = Blankiet.ILE_ZAZNACZYĆ;
    private static final int MAX_NUMER = Blankiet.MAX_NUMER;
    private static final long MIN_STOPNIA_1 = 2_000_000_00, STOPNIA_4 = 24_00, MIN_STOPNIA_3 = 36_00;
    private static final long ZYSK_Z_BILETU = 240;

    private int id;
    private int[] wygraneLiczby;

    //te dwie zmienne będziemy na bieżąco aktualizować podczas spływania kuponów
    private int[] ileTrafionych;
    private long pula;

    //te zmienne wyliczymy w trakcie ogłaszania wyników z pomocą wartości wyliczonych powyżej
    private long[] pulaStopnia;
    private long[] wielkośćWygranej;

    public Losowanie(int id) {
        this.id = id;
        wygraneLiczby = new int[ILE_ZAZNACZYĆ];
        ArrayList<Integer> numery = new ArrayList<>();
        for(int i = 0; i <= MAX_NUMER; i ++) {
            numery.add(i);
        }
        Collections.shuffle(numery, new Random());
        for(int i = 0; i < ILE_ZAZNACZYĆ; i ++) {
            wygraneLiczby[i] = numery.get(i);
        }
        Arrays.sort(wygraneLiczby);
        pulaStopnia = new long[5];
        wielkośćWygranej = new long[5];
        ileTrafionych = new int[5];
        for(int i = 1; i <= 4; i ++) {
            ileTrafionych[i] = 0;
        }
        pula = 0;
    }

    //Ustawione losowanie na potrzeby testowania
    public Losowanie(int[] ustawioneLiczby) {
        this(1);
        wygraneLiczby = ustawioneLiczby;
    }

    public void uwzględnijKupon(Kupon kupon) {
        if(kupon == null) {
            throw new NullPointerException();
        }
        ArrayList<Integer> pokrycie = kupon.ileLiczbSięPokrywa(this);
        pula += (ZYSK_Z_BILETU * 51) / 100 * pokrycie.size(); //musimy domnożyć liczbę zakładów na kuponie
        for(Integer i : pokrycie) {
            //(rozpisałem w taki sposób, aby było czytelniej, da się jednym forem)
            if (i == 6) {
                ileTrafionych[1]++;
            } else if (i == 5) {
                ileTrafionych[2]++;
            } else if (i == 4) {
                ileTrafionych[3]++;
            } else if (i == 3) {
                ileTrafionych[4]++;
            }
        }
    }

    //zwraca nową pulę kumulacji
    public long przeliczWygrane(long kumulacja) {
        //najpierw liczymy pule stopnia 1 i 2
        pulaStopnia[1] = (pula * 44) / 100;
        if(pulaStopnia[1] < MIN_STOPNIA_1) {
            pulaStopnia[1] = MIN_STOPNIA_1;
        }
        pulaStopnia[1] += kumulacja;
        pulaStopnia[2] = (pula * 8) / 100;

        //potem sprawdzamy wielkość puli stopnia 3
        pulaStopnia[3] = pula - (pula * 44) / 100 - (pula * 8) / 100 - STOPNIA_4 * ileTrafionych[4];

        if(ileTrafionych[3] > 0) {
            if(pulaStopnia[3] / ileTrafionych[3] < MIN_STOPNIA_3) {
                pulaStopnia[3] = MIN_STOPNIA_3 * ileTrafionych[3];
            }
        }
        else {
            if(pulaStopnia[3] < 0) {
                pulaStopnia[3] = 0;
            }
        }

        for(int i = 1; i <= 3; i ++) {
            if(ileTrafionych[i] == 0) {
                wielkośćWygranej[i] = 0;
            }
            else {
                wielkośćWygranej[i] = pulaStopnia[i] / ileTrafionych[i];
            }
        }

        //nawet jeśli nikt nie trafił wygranej stopnia 3, to i tak powinna mieć odpowiednią minimalną wartość
        if(wielkośćWygranej[3] < MIN_STOPNIA_3) {
            wielkośćWygranej[3] = MIN_STOPNIA_3;
        }
        wielkośćWygranej[4] = STOPNIA_4;

        //obsługujemy kumulacje na następne losowania
        //UWAGA: przyjąłem, że kumulacji nie obejmuje gwarantowane zrównanie do 2mln
        //oznacza to, że pula nagród stopnia 1 będzie rosła wolniej
        if(ileTrafionych[1] > 0) {
            return 0;
        }
        return kumulacja + (pula * 44) / 100;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Losowanie nr " + id + "\nWyniki: ");
        for(int i = 0; i < ILE_ZAZNACZYĆ; i ++) {
            if(wygraneLiczby[i] < 10) {
                sb.append(" ");
            }
            sb.append(wygraneLiczby[i] + " ");
        }
        return sb.toString();
    }

    //na potrzeby ogłaszania wyników przez centrale
    public String pełneInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.toString());
        sb.append("\n");
        pulaStopnia[4] = ileTrafionych[4] * wielkośćWygranej[4];
        for(int i = 1; i <= 4; i ++) {
            sb.append("Wygrana stopnia " + i + "\n");
            sb.append("Całkowita pula nagród: " + KonwerterKwoty.groszeDoStringa(pulaStopnia[i]));
            //jeśli nikt nie trafił, to wypisujemy nagrodę 0 zł 0 gr
            sb.append("Nagroda: " + KonwerterKwoty.groszeDoStringa(wielkośćWygranej[i]));
            //ważne: liczba zwycięzców, to liczba wygrywających kuponów, nie liczba osób
            //dlatego czasami "wygranych" jest podejrzanie dużo (nawet więcej niż osób biorących udział)
            sb.append("Liczba zwycięzców: " + ileTrafionych[i] + "\n\n");
        }
        return sb.toString();
    }

    public void przepiszWyniki(int[] liczbyWygranych, long[] pule, long[] nagrody) {
        for(int i = 0; i <= 3; i ++) {
            liczbyWygranych[i] = ileTrafionych[i + 1];
            pule[i] = pulaStopnia[i + 1];
            nagrody[i] = wielkośćWygranej[i + 1];
        }
    }

    public int dajNumerLosowania() {
        return id;
    }

    public int[] dajWygraneLiczby() {
        return wygraneLiczby;
    }

    //wycenia wartość zakładów na to (this) losowanie w danym kuponie
    public long[] ewaluujKupon(Kupon kupon) {
        if(kupon == null) {
            throw new NullPointerException();
        }

        ArrayList<Integer> pokrycie = kupon.ileLiczbSięPokrywa(this);
        long[] wygrane = new long[pokrycie.size()];
        int licznik = 0;
        for (Integer i : pokrycie) {
            if (i == 6) {
                wygrane[licznik] = wielkośćWygranej[1];
            } else if (i == 5) {
                wygrane[licznik] = wielkośćWygranej[2];
            } else if (i == 4) {
                wygrane[licznik] = wielkośćWygranej[3];
            } else if (i == 3) {
                wygrane[licznik] = wielkośćWygranej[4];
            }
            else {
                wygrane[licznik] = 0;
            }
            licznik ++;
        }
        return wygrane;
    }
}
