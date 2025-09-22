package main;

/*
Dla wygody implementacji będziemy intensywnie korzystać z
Wektor.długość i .czyTransponowany oraz ich widoczności protected.
Pozwoli nam to swobodnie rozpoznawać orientacje wektora
i jego "kompatybilność" z danymi operacjami.
 */

public class Wektor extends Tablica {
    public Wektor(int długość) {
        super(1, 1, długość);
    }

    @Override
    public int[] kształt() {
        int[] tab = new int[1];
        tab[0] = długość;
        return tab;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Wektor:\n");
        if(!czyTransponowany) {
            sb.append("( ");
            for (int i = 0; i < długość; i++) {
                sb.append(pola[0][i] + " ");
            }
            sb.append(")");
        }
        else {
            for(int i = 0; i < długość; i ++) {
                sb.append("( " + pola[0][i] + " )\n");
            }
        }
        return sb.toString();
    }

    protected double bezpieczneDaj(int idKolumny) {
        return pola[0][idKolumny].bezpieczneDaj();
    }

    protected void bezpieczneUstaw(double wartość, int idKolumny) {
        pola[0][idKolumny].bezpieczneUstaw(wartość);
    }

    public double daj(int idKolumny) throws ZłyIndeks {
        if(idKolumny < 0 || idKolumny >= długość) {
            throw new ZłyIndeks();
        }
        return bezpieczneDaj(idKolumny);
    }

    public void ustaw(double wartość, int idKolumny) throws ZłyIndeks {
        if(idKolumny < 0 || idKolumny >= długość) {
            throw new ZłyIndeks();
        }
        bezpieczneUstaw(wartość, idKolumny);
    }

    @Override
    public Wektor kopia() {
        Wektor nowy = new Wektor(długość);
        if(czyTransponowany) {
            nowy.transponuj();
        }
        for(int i = 0; i < długość; i ++) {
            nowy.bezpieczneUstaw(bezpieczneDaj(i), i);
        }
        return nowy;
    }

    public void przypisz(Wektor inny) throws NiezgodnośćRozmiarów {
        if(inny == null) {
            throw new NullPointerException();
        }
        if (długość != inny.długość) {
            throw new NiezgodnośćRozmiarów();
        }
        for (int i = 0; i < długość; i++) {
            bezpieczneUstaw(inny.bezpieczneDaj(i), i);
        }
        if(czyTransponowany != inny.czyTransponowany) {
            transponuj();
        }
    }

    //wycinek zawsze powstaje poziomy
    public Wektor wycinek(int l, int p) throws ZłyIndeks {
        if(l < 0 || p >= długość) {
            throw new ZłyIndeks();
        }
        Wektor nowy = new Wektor(p - l + 1);
        for(int i = l; i <= p; i ++) {
            nowy.bezpieczneUstaw(bezpieczneDaj(i), i - l);
        }
        return nowy;
    }

    @Override
    public Wektor negacja() {
        Wektor nowy = kopia();
        nowy.zaneguj();
        return nowy;
    }

    //OPERACJE Z SKALARAMI

    @Override
    public Wektor suma(Skalar inny) {
        if(inny == null) {
            throw new NullPointerException();
        }
        Wektor nowy = kopia();
        nowy.dodaj(inny);
        return nowy;
    }

    @Override
    public Wektor iloczyn(Skalar inny) {
        if(inny == null) {
            throw new NullPointerException();
        }
        Wektor nowy = kopia();
        nowy.przemnóż(inny);
        return nowy;
    }

    // OPERACJE Z WEKTORAMI

    public void dodaj(Wektor inny) throws NiezgodnośćRozmiarów {
        if(inny == null) {
            throw new NullPointerException();
        }
        if(długość != inny.długość || czyTransponowany != inny.czyTransponowany) {
            throw new NiezgodnośćRozmiarów();
        }
        for(int i = 0; i < długość; i ++) {
            bezpieczneUstaw(bezpieczneDaj(i) + inny.bezpieczneDaj(i), i);
        }
    }

    public Wektor suma(Wektor inny) throws NiezgodnośćRozmiarów {
        if(inny == null) {
            throw new NullPointerException();
        }
        Wektor nowy = kopia();
        nowy.dodaj(inny);
        return nowy;
    }

    /*
    public void przemnóż(Wektor inny)

    Tego NIE implementujemy, ponieważ wynikiem iloczynu nigdy nie jest wektor.
    Ta operacja nigdy nie da poprawnego typu.
     */

    //Pozostawiamy użytkownikowi śledzenie jakiego typu będzie wynik tej operacji.
    //Można go castować na oczekiwaną klasę.
    public Tablica iloczyn(Wektor inny) throws NiezgodnośćRozmiarów {
        if(inny == null) {
            throw new NullPointerException();
        }
        if(długość != inny.długość) {
            throw new NiezgodnośćRozmiarów();
        }
        if(czyTransponowany == inny.czyTransponowany) {
            Skalar nowy = new Skalar();
            double suma = 0;
            for(int i = 0; i < długość; i ++) {
                suma += bezpieczneDaj(i) * inny.bezpieczneDaj(i);
            }
            nowy.bezpieczneUstaw(suma);
            return nowy;
        }
        else if(czyTransponowany) { //lewy pionowy prawy poziomy
            Macierz nowa = new Macierz(długość, długość);
            for(int i = 0; i < długość; i ++) {
                for(int j = 0; j < długość; j ++) {
                    nowa.bezpieczneUstaw(bezpieczneDaj(i) * inny.bezpieczneDaj(j), i, j);
                }
            }
            return nowa;
        }
        else { //lewy poziomy prawy pionowy
            Macierz nowa = new Macierz(1, 1);
            double suma = 0;
            for(int i = 0; i < długość; i ++) {
                suma += bezpieczneDaj(i) * inny.bezpieczneDaj(i);
            }
            nowa.bezpieczneUstaw(suma, 0, 0);
            return nowa;
        }
    }

    //OPERACJE Z MACIERZAMI

    public Macierz suma(Macierz inna) throws NiezgodnośćRozmiarów {
        if(inna == null) {
            throw new NullPointerException();
        }
        return inna.suma(this);
    }

    public Wektor iloczyn(Macierz inna) throws NiezgodnośćRozmiarów {
        if(inna == null) {
            throw new NullPointerException();
        }
        int[] kształtInnej = inna.kształt();
        if(!czyTransponowany) {
            if(długość != kształtInnej[0]) {
                throw new NiezgodnośćRozmiarów();
            }
            Wektor nowy = new Wektor(kształtInnej[1]);
            for(int i = 0; i < kształtInnej[1]; i ++) {
                double suma = 0;
                for(int j = 0; j < długość; j ++) {
                    suma += bezpieczneDaj(j) * inna.bezpieczneDaj(j, i);
                }
                nowy.bezpieczneUstaw(suma, i);
            }
            return nowy;
        }
        else {
            if(długość != kształtInnej[1]) {
                throw new NiezgodnośćRozmiarów();
            }
            Wektor nowy = new Wektor(kształtInnej[0]);
            for(int i = 0; i < kształtInnej[0]; i ++) {
                double suma = 0;
                for(int j = 0; j < długość; j ++) {
                    suma += bezpieczneDaj(j) * inna.bezpieczneDaj(i, j);
                }
                nowy.bezpieczneUstaw(suma, i);
            }
            nowy.transponuj();
            return nowy;
        }
    }

    //EQUALS
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Wektor inny = (Wektor) obj;
        if(czyTransponowany != inny.czyTransponowany) {
            return false;
        }
        if(długość != inny.długość) {
            return false;
        }
        for(int i = 0; i < długość; i ++) {
            if(Math.abs(bezpieczneDaj(i) - inny.bezpieczneDaj(i)) > precyzja) {
                return false;
            }
        }
        return true;
    }
}
