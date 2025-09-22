package main.java.loteria;

public class Panstwo {
    private long pożyczki, zyski;
    public Panstwo() {
        pożyczki = 0;
        zyski = 0;
    }
    public void wpłać(long kwota) {
        zyski += kwota;
    }
    public void pożycz(long kwota) {
        pożyczki += kwota;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Pożyczki: " + KonwerterKwoty.groszeDoStringa(pożyczki));
        sb.append("Zyski: " + KonwerterKwoty.groszeDoStringa(zyski));
        return sb.toString();
    }

    public long ileZysków() {
        return zyski;
    }
}
