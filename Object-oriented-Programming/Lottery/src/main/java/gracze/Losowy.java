package main.java.gracze;

import main.java.loteria.*;
import java.util.Random;

public class Losowy extends Gracz {
    private static final long MAX_FUNDUSZE = 1_000_000;
    private static final int MAX_LICZBA_KUPONÓW = 100, MAX_ZAKŁADY = Blankiet.MAX_ZAKŁADY, MAX_LOSOWANIA = Blankiet.MAX_LOSOWANIA;

    private Kolektura[] listaKolektur;
    private static final Random random = new Random();

    public Losowy(String imię, String nazwisko, String pesel, Kolektura[] listaKolektur) {
        super(imię, nazwisko, pesel, random.nextLong(MAX_FUNDUSZE));
        if(listaKolektur == null) {
            throw new NullPointerException();
        }
        if(listaKolektur.length == 0) {
            throw new IllegalArgumentException();
        }
        this.listaKolektur = listaKolektur;
    }

    @Override
    public void kupKupon() {
        int liczbaKuponów = random.nextInt(MAX_LICZBA_KUPONÓW) + 1;
        for(int i = 0; i < liczbaKuponów; i ++) {
            //wybieramy losową kolekturę z listy
            Kolektura temp = listaKolektur[random.nextInt(listaKolektur.length)];
            try {
                Kupon potencjalny = temp.chybiłTrafił(this, random.nextInt(MAX_ZAKŁADY) + 1, random.nextInt(MAX_LOSOWANIA) + 1);
                kupony.add(potencjalny);
            } catch (NiewystarczajaceSrodkiException e) {
                //jeśli brak środków, to nie kupuje
            }
        }
    }

}
