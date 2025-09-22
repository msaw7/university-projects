package main.java.gracze;

import main.java.loteria.*;

public class Staloblankietowy extends Gracz {
    private Blankiet mójBlankiet;
    private Kolektura[] listaKolektur;
    private int licznik;

    public Staloblankietowy(String imię, String nazwisko, String pesel, long fundusze,
                            Blankiet mójBlankiet, Kolektura[] listaKolektur) {
        super(imię, nazwisko, pesel, fundusze);
        if(mójBlankiet == null || listaKolektur == null) {
            throw new NullPointerException();
        }
        this.mójBlankiet = mójBlankiet;
        this.listaKolektur = listaKolektur;
        licznik = 0;
    }

    @Override
    public void kupKupon() {
        //cyklujemy po kolekturach
        Kolektura mojaKolektura = listaKolektur[licznik % listaKolektur.length];
        try {
            Kupon potencjalny = mojaKolektura.własnyBlankiet(this, mójBlankiet);
            licznik ++;
            kupony.add(potencjalny);
        } catch (NiewystarczajaceSrodkiException e) {
            //jeśli brak środków, to nie kupuje
        }
    }
}
