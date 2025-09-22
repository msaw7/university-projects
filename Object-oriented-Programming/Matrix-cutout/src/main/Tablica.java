package main;

/*
Każdą podklasę reprezentujemy dwuwymiarową tablicą odpowiedniej wielkości.

Używamy klasy Pole, która jest boxem na typ double (jak Double),
ale pozwala modyfikować zawartość.
Takie rozwiązanie pozwala nam obsłużyć wycinki bez osobnej klasy.
Tworząc wycinek przepisujemy obiekty Pole,
a tworząc kopie/przypisując zmieniamy jedynie wartość.

Zwracamy dwa wyjątki nadzorowane: ZłyIndeks i NiezgodnośćRozmiarów.
Zrezygnowałem z ZłyZakres, ponieważ można go wyrazić przez ZłyIndeks.
Gdy argumenty są nullami rzucam nienadzorowane NullPointerException.

Każda klasa ma zaimplementowany swój odpowiednik daj i ustaw,
ale do użytku tylko przez dewelopera klasy Tablica.
Nie musimy wtedy przechwytywać wyjątków, które wiemy,
że się nie wydarzą (ponieważ poprawnie korzystamy z daj i ustaw).
Będziemy intensywnie wykorzystywać te dwie metody w implementacji
innych metod.

W tej implementacji kopia() jest odpięta od wycinków tablicy
z której powstawała.
Tak samo wszystkie obiekty zwracane przez iloczyn, suma, negacja.

przypisz() nie nadpisuje relacji wycinków, tzn zmiany które
wprowadza na tablicy będą widoczne na jej wycinkach i vice
versa.

Transpozycja wycinka nie zmienia w żaden sposób tablicy z której powstał.

Przemienne operacje na tablicach są napisane w jednej z dwóch klas.
(zazwyczaj w tej z wyższym wymiarem, z wyjątkiem iloczyn wektor macierz)

Zdecydowałem aby atrybuty Tablicy były protected (nie private).
Zamiast tego atrybuty na których zależy mi aby nie były
modyfikowane przez inne obiekty zostały zadeklarowane jako "final".

Przez decyzje implementacyjne to rozwiązanie nie jest modularne ze względu
na dodawanie nowych tablic większych wymiarów.
Możemy natomiast dodawać nowe metody i interakcje między podklasami.

 */

public abstract class Tablica {
    protected final static double precyzja = 0.00001;
    protected final int wymiar;
    protected final int liczba_elementów;
    protected boolean czyTransponowany;
    protected Pole[][] pola;
    //To jest FAKTYCZNY rozmiar tablicy pola.
    //tzn mamy niezmiennik wysokość = pola.length, długość=pola[0].length
    protected final int długość, wysokość;

    protected Tablica(int wymiar, int wysokość, int długość) {
        this.wymiar = wymiar;
        liczba_elementów = wysokość * długość;
        czyTransponowany = false;
        this.wysokość = wysokość;
        this.długość = długość;
        pola = new Pole[wysokość][długość];
        for(int i = 0; i < wysokość; i ++) {
            for(int j = 0; j < długość; j ++) pola[i][j] = new Pole(0);
        }
    }

    public int wymiar() {
        return wymiar;
    }

    public int liczba_elementów() {
        return liczba_elementów;
    }

    /*
    Wykorzystujemy to, że w podklasach mamy implementacje bardziej
    "szczególnego" przypadku dla określonej liczby argumentów.
    Oznacza to, że daj dla złej liczby argumentów zawsze rzuci
    ZłyIndeks, a dla pasującej do wymiaru zostanie overrideowana
    przez metodę w podklasie.
     */

    public double daj(int ...arg) throws ZłyIndeks {
        throw new ZłyIndeks();
    }

    public double ustaw(double wartość, int ...arg) throws ZłyIndeks {
        throw new ZłyIndeks();
    }

    abstract public int[] kształt();

    abstract public String toString();

    abstract public Tablica kopia();

    public void transponuj() {
        czyTransponowany = !czyTransponowany;
    }

    public void przypisz(Skalar inny) {
        if(inny == null) {
            throw new NullPointerException();
        }
        for(int i = 0; i < wysokość; i ++) {
            for(int j = 0; j < długość; j ++) {
                pola[i][j].bezpieczneUstaw(inny.bezpieczneDaj());
            }
        }
    }

    /*
    NASTĘPUJĄCE OPERACJE POWINNY BYĆ ZAGWARANTOWANE
    WE WSZYSTKICH PODKLASACH
    (skalar zawsze można dodać/mnożyć, negować również)
    */

    public void zaneguj() {
        for(int i = 0; i < wysokość; i ++) {
            for(int j = 0; j < długość; j ++) {
                pola[i][j].bezpieczneUstaw(-pola[i][j].bezpieczneDaj());
            }
        }
    }

    abstract public Tablica negacja();

    public void dodaj(Skalar inny) {
        if(inny == null) {
            throw new NullPointerException();
        }
        for(int i = 0; i < wysokość; i ++) {
            for(int j = 0; j < długość; j ++) {
                pola[i][j].bezpieczneUstaw(pola[i][j].bezpieczneDaj() + inny.bezpieczneDaj());
            }
        }
    }

    abstract public Tablica suma(Skalar inny);

    public void przemnóż(Skalar inny) {
        if(inny == null) {
            throw new NullPointerException();
        }
        for(int i = 0; i < wysokość; i ++) {
            for(int j = 0; j < długość; j ++) {
                pola[i][j].bezpieczneUstaw(pola[i][j].bezpieczneDaj() * inny.bezpieczneDaj());
            }
        }
    }

    abstract public Tablica iloczyn(Skalar inny);


}