package main.java.loteria;

public class KonwerterKwoty {
    private KonwerterKwoty() {
        throw new UnsupportedOperationException();
    }
    public static String groszeDoStringa(long kwota) {
        StringBuilder sb = new StringBuilder();
        sb.append(kwota / 100 + " zł ");
        if(kwota % 100 == 0) {
            sb.append("0");
        }
        else if(kwota % 100 < 10) {
            sb.append("0");
        }
        sb.append(kwota % 100 + " gr\n");
        return sb.toString();
    }
}
