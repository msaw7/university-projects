package main;

/*
Wykorzystamy funkcję kształt, aby wygodnie rozpoznawać
rozmiar macierzy i uodpornić się na jej transpozycję.
kształt[0] = wysokość/liczba wierszy
kształt[1] = długość/liczba kolumn
 */

public class Macierz extends Tablica {
    public Macierz(int wysokość, int długość) {
        super(2, wysokość, długość);
    }

    @Override
    public int[] kształt() {
        int[] tab = new int[2];
        if(!czyTransponowany) {
            tab[0] = wysokość;
            tab[1] = długość;
        }
        else {
            tab[0] = długość;
            tab[1] = wysokość;
        }
        return tab;
    }

    protected double bezpieczneDaj(int idWiersza, int idKolumny) {
        if(czyTransponowany) {
            int temp = idWiersza;
            idWiersza = idKolumny;
            idKolumny = temp;
        }
        return pola[idWiersza][idKolumny].bezpieczneDaj();
    }

    protected void bezpieczneUstaw(double wartość, int idWiersza, int idKolumny) {
        if(czyTransponowany) {
            int temp = idWiersza;
            idWiersza = idKolumny;
            idKolumny = temp;
        }
        pola[idWiersza][idKolumny].bezpieczneUstaw(wartość);
    }

    public double daj(int idWiersza, int idKolumny) throws ZłyIndeks {
        int[] kształt = kształt();
        if(idWiersza < 0 || idWiersza >= kształt[0] || idKolumny < 0 || idKolumny >= kształt[1]) {
            throw new ZłyIndeks();
        }
        return bezpieczneDaj(idWiersza, idKolumny);
    }

    public void ustaw(double wartość, int idWiersza, int idKolumny) throws ZłyIndeks {
        int[] kształt = kształt();
        if(idWiersza < 0 || idWiersza >= kształt[0] || idKolumny < 0 || idKolumny >= kształt[1]) {
            throw new ZłyIndeks();
        }
        bezpieczneUstaw(wartość, idWiersza, idKolumny);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Macierz:\n");
        int[] kształt = kształt();
        for(int i = 0; i < kształt[0]; i ++) {
            sb.append("[ ");
            for(int j = 0; j < kształt[1]; j ++) {
                sb.append(bezpieczneDaj(i, j) + " ");
            }
            sb.append("]\n");
        }
        return sb.toString();
    }

    @Override
    public Macierz kopia() {
        Macierz nowa = new Macierz(wysokość, długość);
        if(czyTransponowany) {
            nowa.transponuj();
        }
        int[] kształt = kształt();
        for(int i = 0; i < kształt[0]; i ++) {
            for(int j = 0; j < kształt[1]; j ++) {
                nowa.bezpieczneUstaw(bezpieczneDaj(i, j), i, j);
            }
        }
        return nowa;
    }

    public void przypisz(Macierz inna) throws NiezgodnośćRozmiarów {
        if(inna == null) {
            throw new NullPointerException();
        }
        int[] kształtInnej = inna.kształt();
        int[] kształtMój = kształt();
        if(kształtMój[0] != kształtInnej[0] || kształtMój[1] != kształtInnej[1]) {
            throw new NiezgodnośćRozmiarów();
        }
        for(int i = 0; i < kształtMój[0]; i ++) {
            for(int j = 0; j < kształtMój[1]; j ++) {
                bezpieczneUstaw(inna.bezpieczneDaj(i, j), i, j);
            }
        }
    }

    public void przypisz(Wektor inny) throws NiezgodnośćRozmiarów {
        if(inny == null) {
            throw new NullPointerException();
        }
        int[] kształtMój = kształt();
        if(!inny.czyTransponowany) { //poziomy wektor
            if(inny.długość != kształtMój[1]) { //długość wektora != długość wiersza
                throw new NiezgodnośćRozmiarów();
            }
            for(int i = 0; i < kształtMój[0]; i ++) {
                for(int j = 0; j < kształtMój[1]; j ++) {
                    bezpieczneUstaw(inny.bezpieczneDaj(j), i, j);
                }
            }
        }
        else { //pionowy wektor
            if(inny.długość != kształtMój[0]) { //długość wektora != długość kolumny
                throw new NiezgodnośćRozmiarów();
            }
            for(int i = 0; i < kształtMój[0]; i ++) {
                for(int j = 0; j < kształtMój[1]; j ++) {
                    bezpieczneUstaw(inny.bezpieczneDaj(i), i, j);
                }
            }
        }
    }

    public Macierz wycinek(int dół, int góra, int lewo, int prawo) throws ZłyIndeks {
        int[] kształt = kształt();
        if((dół < 0) || (góra > kształt[0]) || (lewo < 0) || (prawo > kształt[1])) {
            throw new ZłyIndeks();
        }
        Macierz nowa = new Macierz(góra - dół + 1, prawo - lewo + 1);
        for(int i = dół; i <= góra; i ++) {
            for(int j = lewo; j <= prawo; j ++) {
                //musimy "żywcem" przepisać wskaźnik na pole
                if(!czyTransponowany) {
                    nowa.pola[i - dół][j - lewo] = pola[i][j];
                }
                else {
                    nowa.pola[i - dół][j - lewo] = pola[j][i];
                }
            }
        }
        return nowa;
    }

    @Override
    public Macierz negacja() {
        Macierz nowa = kopia();
        nowa.zaneguj();
        return nowa;
    }

    //OPERACJE Z SKALARAMI

    @Override
    public Macierz suma(Skalar inny) {
        if(inny == null) {
            throw new NullPointerException();
        }
        Macierz nowa = kopia();
        nowa.dodaj(inny);
        return nowa;
    }

    @Override
    public Macierz iloczyn(Skalar inny) {
        if(inny == null) {
            throw new NullPointerException();
        }
        Macierz nowa = kopia();
        nowa.przemnóż(inny);
        return nowa;
    }

    //OPERACJE Z WEKTORAMI

    public void dodaj(Wektor inny) throws NiezgodnośćRozmiarów {
        if(inny == null) {
            throw new NullPointerException();
        }
        int[] kształtMój = kształt();
        if(!inny.czyTransponowany) { //wektor poziomy
            if(inny.długość != kształtMój[1]) {//długość wektora != długość wiersza
                throw new NiezgodnośćRozmiarów();
            }
            for(int i = 0; i < kształtMój[0]; i ++) {
                for (int j = 0; j < kształtMój[1]; j++) {
                    bezpieczneUstaw(bezpieczneDaj(i, j) + inny.bezpieczneDaj(j), i, j);
                }
            }
        }
        else {
            if(inny.długość != kształtMój[0]) { //długość wektora != długość kolumny
                throw new NiezgodnośćRozmiarów();
            }
            for(int i = 0; i < kształtMój[0]; i ++) {
                for(int j = 0; j < kształtMój[1]; j ++) {
                    bezpieczneUstaw(bezpieczneDaj(i, j) + inny.bezpieczneDaj(i), i, j);
                }
            }
        }
    }

    public Macierz suma(Wektor inny) throws NiezgodnośćRozmiarów {
        if(inny == null) {
            throw new NullPointerException();
        }
        Macierz nowa = kopia();
        nowa.dodaj(inny);
        return nowa;
    }

    public Wektor iloczyn(Wektor inny) throws NiezgodnośćRozmiarów {
        if(inny == null) {
            throw new NullPointerException();
        }
        return inny.iloczyn(this);
    }

    //OPERACJE Z MACIERZAMI

    public void dodaj(Macierz inna) throws NiezgodnośćRozmiarów {
        if(inna == null) {
            throw new NullPointerException();
        }
        int[] kształtInnej = inna.kształt();
        int[] kształtMój = kształt();
        if(kształtMój[0] != kształtInnej[0] || kształtMój[1] != kształtInnej[1]) {
            throw new NiezgodnośćRozmiarów();
        }
        for(int i = 0; i < kształtMój[0]; i ++) {
            for(int j = 0; j < kształtMój[1]; j ++) {
                bezpieczneUstaw(bezpieczneDaj(i, j) + inna.bezpieczneDaj(i, j), i, j);
            }
        }
    }

    public Macierz suma(Macierz inna) throws NiezgodnośćRozmiarów {
        if(inna == null) {
            throw new NullPointerException();
        }
        Macierz nowa = kopia();
        nowa.dodaj(inna);
        return nowa;
    }

    public Macierz iloczyn(Macierz inna) throws NiezgodnośćRozmiarów {
        if(inna == null) {
            throw new NullPointerException();
        }
        int[] kształtInnej = inna.kształt();
        int[] kształtMój = kształt();
        if(kształtMój[1] != kształtInnej[0]) { //jeśli długość wiersza this nie zgadza się z długością kolumny innej
            throw new NiezgodnośćRozmiarów();
        }
        Macierz nowa = new Macierz(kształtMój[0], kształtInnej[1]);
        for(int i = 0; i < kształtMój[0]; i ++) { //nr wiersza pierwsza this
            for(int j = 0; j < kształtInnej[1]; j ++) { //nr kolumny inna
                double suma = 0;
                for(int k = 0; k < kształtMój[1]; k ++) { //indeks do wymnożenia
                    suma += bezpieczneDaj(i, k) * inna.bezpieczneDaj(k, j);
                }
                nowa.bezpieczneUstaw(suma, i, j);
            }
        }
        return nowa;
    }

    public void przemnóż(Macierz inna) throws NiezgodnośćRozmiarów {
        if(inna == null) {
            throw new NullPointerException();
        }
        int[] kształtInnej = inna.kształt();
        int[] kształtMój = kształt();
        int d = kształtMój[0];
        //możemy wykonać tę operację tylko wtedy gdy obie macierze są kwadratowe i tego samego rozmiaru
        if(d != kształtInnej[0] || d != kształtInnej[1] || d != kształtMój[1]) {
            throw new NiezgodnośćRozmiarów();
        }
        Macierz nowa = iloczyn(inna);
        przypisz(nowa); //przepisujemy wartości do this nie modyfikując wskaźników na Pola
    }

    // EQUALS
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Macierz inna = (Macierz) obj;
        int[] kształtMój = kształt();
        int[] kształtInnej = inna.kształt();
        if(kształtMój[0] != kształtInnej[0] || kształtMój[1] != kształtInnej[1]) {
            return false;
        }
        for(int i = 0; i < kształtMój[0]; i ++) {
            for(int j = 0; j < kształtMój[1]; j ++) {
                if(Math.abs(bezpieczneDaj(i, j) - inna.bezpieczneDaj(i, j)) > precyzja) {
                    return false;
                }
            }
        }
        return true;
    }

}

