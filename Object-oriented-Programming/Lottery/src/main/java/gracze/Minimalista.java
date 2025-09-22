package main.java.gracze;

import main.java.loteria.*;

public class Minimalista extends Gracz {
    private final Kolektura mojaKolektura;

    public Minimalista(String imię, String nazwisko, String pesel, long fundusze, Kolektura mojaKolektura) {
        super(imię, nazwisko, pesel, fundusze);
        if(mojaKolektura == null) {
            throw new NullPointerException();
        }
        this.mojaKolektura = mojaKolektura;
    }

    @Override
    public void kupKupon() {
        try {
            Kupon potencjalny = mojaKolektura.chybiłTrafił(this, 1, 1);
            kupony.add(potencjalny);
        } catch (NiewystarczajaceSrodkiException e) {
            //jeśli brak środków, to nie kupuje
        }
    }
}
