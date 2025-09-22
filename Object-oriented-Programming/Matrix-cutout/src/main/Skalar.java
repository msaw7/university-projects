package main;
public class Skalar extends Tablica {
    public Skalar() {
        super(0, 1, 1);
    }

    @Override
    public int[] kształt() {
        return new int[0];
    }

    @Override
    public String toString() {
        return ("Skalar: {" + pola[0][0].bezpieczneDaj() + "}");
    }

    protected double bezpieczneDaj() {
        return pola[0][0].bezpieczneDaj();
    }

    protected void bezpieczneUstaw(double wartość) {
        pola[0][0].bezpieczneUstaw(wartość);
    }

    public double daj() {
        return bezpieczneDaj();
    }

    public void ustaw(double wartość) {
        bezpieczneUstaw(wartość);
    }

    @Override
    public Skalar kopia() {
        Skalar nowy = new Skalar();
        nowy.bezpieczneUstaw(bezpieczneDaj()); //kopiujemy samą wartość
        return nowy;
    }

    public Skalar wycinek() {
        Skalar nowy = new Skalar();
        nowy.pola[0][0] = pola[0][0]; //kopiujemy obiekt, nie samą wartość
        return nowy;
    }

    @Override
    public Skalar negacja() {
        Skalar nowy = kopia();
        nowy.zaneguj();
        return nowy;
    }

    //OPERACJE Z SKALARAMI

    @Override
    public Skalar suma(Skalar inny) {
        if(inny == null) {
            throw new NullPointerException();
        }
        Skalar nowy = kopia();
        nowy.dodaj(inny);
        return nowy;
    }

    @Override
    public Skalar iloczyn(Skalar inny) {
        if(inny == null) {
            throw new NullPointerException();
        }
        Skalar nowy = kopia();
        nowy.przemnóż(inny);
        return nowy;
    }

    //OPERACJE Z WEKTORAMI

    public Wektor suma(Wektor inny) {
        if(inny == null) {
            throw new NullPointerException();
        }
        return inny.suma(this);
    }

    public Wektor iloczyn(Wektor inny) {
        if(inny == null) {
            throw new NullPointerException();
        }
        return inny.iloczyn(this);
    }

    //OPERACJE Z MACIERZAMI

    public Macierz suma(Macierz inna) {
        if(inna == null) {
            throw new NullPointerException();
        }
        return inna.suma(this);
    }

    public Macierz iloczyn(Macierz inna) {
        if(inna == null) {
            throw new NullPointerException();
        }
        return inna.iloczyn(this);
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
        Skalar inny = (Skalar) obj;
        if(Math.abs(inny.bezpieczneDaj() - bezpieczneDaj()) > precyzja) {
            return false;
        }
        return true;
    }
}
