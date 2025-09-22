package main.java.gracze;

import main.java.loteria.*;
import java.util.ArrayList;

abstract public class Gracz {
    private final String imię;
    private final String nazwisko;
    private final String pesel;
    private long fundusze;
    protected ArrayList<Kupon> kupony;

    public Gracz(String imię, String nazwisko, String pesel, long fundusze) {
        this.imię = imię;
        this.nazwisko = nazwisko;
        this.pesel = pesel;
        //pozwalamy na utworzenie zadłużonego gracza, który nie może zakupić kuponu
        this.fundusze = fundusze;
        kupony = new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("GRACZ:\n");
        sb.append(imię + "\n");
        sb.append(nazwisko + "\n");
        sb.append(pesel + "\n");
        if(kupony.isEmpty()) {
            sb.append("Brak kuponów\n");
        }
        else {
            for (Kupon kupon : kupony) {
                sb.append(kupon.dajId() + "\n");
            }
        }
        return sb.toString();
    }

    //używamy nadzorowanego wyjątku, aby skutecznie rozpoznawać sytuacje, w których graczowi brakuje pieniędzy na kupon
    public void zapłać(long kwota) throws NiewystarczajaceSrodkiException {
        if(kwota <= fundusze) {
            fundusze -= kwota;
        }
        else {
            throw new NiewystarczajaceSrodkiException();
        }
    }

    public void otrzymajWygraną(long kwota) {
        fundusze += kwota;
    }

    abstract public void kupKupon();

    //oddelegowujemy sprawdzenie do kolektury, aby odizolować gracza od centrali
    public boolean sprawdźKupon(int nrKuponu) {
        if(nrKuponu <= 0 || nrKuponu > kupony.size()) {
            throw new IllegalArgumentException("Niepoprawny numer kuponu");
        }
        Kupon mójKupon = kupony.get(nrKuponu - 1);
        Kolektura mojaKolektura = mójKupon.dajGdzieZakupione();
        return mojaKolektura.sprawdźKupon(mójKupon);
    }

    //jeśli kupon jest gotowy do odbioru, to go wypłacamy
    public void odbierzNagrodę(int nrKuponu) {
        if(nrKuponu <= 0 || nrKuponu > kupony.size()) {
            throw new IllegalArgumentException("Niepoprawny numer kuponu");
        }
        if(sprawdźKupon(nrKuponu)) {
            Kupon mójKupon = kupony.get(nrKuponu - 1);
            Kolektura mojaKolektura = mójKupon.dajGdzieZakupione();
            mojaKolektura.wypłaćNagrodę(this, mójKupon);
            //"oddajemy" nasz kupon
            kupony.remove(mójKupon);
        }
    }

    public void odbierzWszystkie() {
        int n = kupony.size();
        for(int i = n; i >= 1; i --) {
            odbierzNagrodę(i);
        }
    }

    public long dajFundusze() {
        return fundusze;
    }

    public Kupon dajKupon(int nrKuponu) {
        if(nrKuponu <= 0 || nrKuponu > kupony.size()) {
            throw new IllegalArgumentException("Niepoprawny numer kuponu");
        }
        return kupony.get(nrKuponu - 1);
    }
}

