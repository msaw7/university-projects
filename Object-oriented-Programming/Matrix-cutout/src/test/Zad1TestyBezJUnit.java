package test;

//import org.junit.jupiter.api.Test;
import main.*;


import test.MojaMaszynkaTestująca.Test;

import static test.MojaMaszynkaTestująca.*;
//import static test.MojaMaszynkaTestująca.assertArrayEquals;
//import static test.MojaMaszynkaTestująca.assertEquals;

public class Zad1TestyBezJUnit {
    // Testy z treści zadania.
    // Wydanie kompletne i uzupełnione o własne metody assert*.

    /*
    Trzy funkcje do inicjalizacji naszych obiektów.
    Obsługę inicjalizacji normalnie pozostawialibyśmy użytkownikowi.
     */

    public static Skalar stwórzSkalar(double data) {
        Skalar s = new Skalar();
        s.ustaw(data);
        return s;
    }

    public static Wektor stwórzWektor(double[] data, boolean b) {
        Wektor w = new Wektor(data.length);
        for(int i = 0; i < data.length; i ++) {
            try {
                w.ustaw(data[i], i);
            }
            catch (ZłyIndeks e) {
                throw new RuntimeException(e);
            }
        }
        if(!b) {
            w.transponuj();
        }
        return w;
    }

    public static Macierz stwórzMacierz(double[][] data) {
        Macierz m = new Macierz(data.length, data[0].length);
        for(int i = 0; i < data.length; i ++) {
            for(int j = 0; j < data[i].length; j ++) {
                try {
                    m.ustaw(data[i][j], i, j);
                }
                catch (ZłyIndeks e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return m;
    }

    @Test
    void testWłasnościSkalarów() {
        Skalar skalar = stwórzSkalar(1.0);
        assertEquals(0, skalar.wymiar());
        assertArrayEquals(new int[]{}, skalar.kształt());
        assertEquals(1, skalar.liczba_elementów());
    }

    @Test
    void testWłasnościWektorów() {
        Wektor wektor1 = stwórzWektor(new double[]{1.0, 2.0, 1.0}, true);
        Wektor wektor2 = stwórzWektor(new double[]{2.0, 2.0, 3.0}, false);
        assertEquals(1, wektor1.wymiar());
        assertArrayEquals(new int[]{3}, wektor1.kształt());
        assertEquals(3, wektor1.liczba_elementów());
        assertEquals(1, wektor2.wymiar());
        assertArrayEquals(new int[]{3}, wektor2.kształt());
        assertEquals(3, wektor2.liczba_elementów());
    }

    @Test
    void testWłasnościMacierzy() {
        Macierz matrix = stwórzMacierz(new double[][]{
                {1.0, 0.0, 2.0},
                {2.0, 1.0, 3.0},
                {1.0, 1.0, 1.0},
                {2.0, 3.0, 1.0}
        });
        assertEquals(2, matrix.wymiar());
        assertArrayEquals(new int[]{4, 3}, matrix.kształt());
        assertEquals(12, matrix.liczba_elementów());
    }

    @Test
    void testArytmetykiSkalarów() {
        Skalar skalar1 = stwórzSkalar(3.5);
        Skalar skalar2 = stwórzSkalar(11.5);
        assertEquals(stwórzSkalar(15.0), skalar1.suma(skalar2));

        Skalar skalar3 = stwórzSkalar(3.0);
        Skalar skalar4 = stwórzSkalar(12.0);
        assertEquals(stwórzSkalar(36.0), skalar3.iloczyn(skalar4));
    }

    @Test
    void testArytmetykiSkalarWektor() {
        for(boolean b: new boolean[]{true, false}) {
            // 3.0 + [1.0, 2.5] = [4.0, 5.5]
            Skalar skalar = stwórzSkalar(3.0);
            Wektor wektor1 = stwórzWektor(new double[]{1.0, 2.5}, b);
            assertEquals(stwórzWektor(new double[]{4.0, 5.5}, b), skalar.suma(wektor1));

            // 4.0 * [1.5, 2.25] = [6.0, 9.0]
            Wektor wektor2 = stwórzWektor(new double[]{1.5, 2.25}, b);
            assertEquals(stwórzWektor(new double[]{6.0, 9.0}, b),
                          stwórzSkalar(4.0).iloczyn(wektor2));
        }  // for b
    }

    @Test
    void testArytmetykiWektorSkalar() {
        for(boolean b: new boolean[]{true, false}) {
            // [1.0, 2.5] + 3.0 = [4.0, 5.5]
            Skalar skalar = stwórzSkalar(3.0);
            Wektor wektor1 = stwórzWektor(new double[]{1.0, 2.5}, b);
            assertEquals(stwórzWektor(new double[]{4.0, 5.5}, b),
                           wektor1.suma(skalar));

            // [1.5, 2.25] * 4.0 = [6.0, 9.0]
            Wektor wektor2 = stwórzWektor(new double[]{1.5, 2.25}, b);
            assertEquals(stwórzWektor(new double[]{6.0, 9.0}, b),
                         wektor2.iloczyn(stwórzSkalar(4.0)));
        }  // for b
    }

    @Test
    void testArytmetykiSkalarMacierz() {
        Skalar skalar = stwórzSkalar(3.0);
        Macierz macierz = stwórzMacierz(new double[][]{
                {1.25, 3.0, -12.0},
                {-51.0, 8.0, 3.5}
        });
        Macierz oczekiwanyWynikDodawania = stwórzMacierz(new double[][]{
                {4.25, 6.0, -9.0},
                {-48.0, 11.0, 6.5}
        });
        assertEquals(oczekiwanyWynikDodawania, skalar.suma(macierz));

        Skalar skalar2 = stwórzSkalar(-3.0);
        Macierz oczekiwanyWynikMnożenia = stwórzMacierz(new double[][]{
                {-3.75, -9.0, 36.0},
                {153.0, -24.0, -10.5}
        });
        assertEquals(oczekiwanyWynikMnożenia, skalar2.iloczyn(macierz));

        // Odwrotna Kolejność
        assertEquals(oczekiwanyWynikDodawania, macierz.suma(skalar));
        assertEquals(oczekiwanyWynikMnożenia, macierz.iloczyn(skalar2));
    }

    @Test
    void testDodawaniaIMnożeniaWektorWektor() throws NiezgodnośćRozmiarów {
        // Wektor + Wektor
        Wektor wektor1 = stwórzWektor(new double[]{1.0, 2.0, 3.0}, true);
        Wektor wektor2 = stwórzWektor(new double[]{1.0, 1.0, -2.0}, true);
        assertEquals(stwórzWektor(new double[]{2.0, 3.0, 1.0}, true),
                wektor1.suma(wektor2));

        Wektor wektor3 = stwórzWektor(new double[]{-2.0, 5.0}, false);
        Wektor wektor4 = stwórzWektor(new double[]{-5.0, 2.0}, false);
        assertEquals(stwórzWektor(new double[]{-7.0, 7.0}, false),
                wektor3.suma(wektor4));

        // Wektor * Wektor (Scalar result)
        Wektor wektor5 = stwórzWektor(new double[]{3.0, 2.0, -1.0}, true);
        Wektor wektor6 = stwórzWektor(new double[]{-2.0, 2.0, 1.0}, true);
        assertEquals(stwórzSkalar(-3.0), wektor5.iloczyn(wektor6));

        Wektor wektor7 = stwórzWektor(new double[]{-2.0, -5.0, 1.0, 3.0}, false);
        Wektor wektor8 = stwórzWektor(new double[]{-5.0, 1.0, 2.0, -3.0}, false);
        assertEquals(stwórzSkalar(-2.0), wektor7.iloczyn(wektor8));

        Wektor wektor9 = stwórzWektor(new double[]{1.0, 1.0, -2.0}, false);
        assertEquals(stwórzMacierz(new double[][]{{-3.0}}), wektor1.iloczyn(wektor9));

        Wektor wektor10 = stwórzWektor(new double[]{1.0, 2.0, 3.0}, false);
        Wektor wektor11 = stwórzWektor(new double[]{1.0, 1.0, -2.0}, true);
        assertEquals(stwórzMacierz(new double[][]{
                {1.0, 1.0, -2.0},
                {2.0, 2.0, -4.0},
                {3.0, 3.0, -6.0}
                }), wektor10.iloczyn(wektor11));
    }

    @Test
    void testDodawaniaWektorMacierz() throws NiezgodnośćRozmiarów {
        // Wektor + Macierz
        Wektor wektor1 = stwórzWektor(new double[]{3.0, 1.5, -2.0}, true);
        Macierz macierz1 = stwórzMacierz(new double[][]{
                {1.0, 3.5, -12.0},
                {-5.0, 8.0, 3.0}
        });
        assertEquals(stwórzMacierz(new double[][]{
                {4.0, 5.0, -14.0},
                {-2.0, 9.5, 1.0}
        }), wektor1.suma(macierz1));

        Wektor wektor2 = stwórzWektor(new double[]{7.5, -5.0}, false);
        assertEquals(stwórzMacierz(new double[][]{
                {8.5, 11.0, -4.5},
                {-10.0, 3.0, -2.0}
        }), wektor2.suma(macierz1));

        // Odwrotna Kolejność

        // Macierz + Wektor (odwrotna kolejność)
        assertEquals(stwórzMacierz(new double[][]{
                {4.0, 5.0, -14.0},
                {-2.0, 9.5, 1.0}
        }), macierz1.suma(wektor1));

        assertEquals(stwórzMacierz(new double[][]{
                {8.5, 11.0, -4.5},
                {-10.0, 3.0, -2.0}
        }), macierz1.suma(wektor2));

    }

    @Test
    void testMnożeniaWektorMacierz() throws NiezgodnośćRozmiarów {
        // Wektor * Macierz
        Wektor wektor1 = stwórzWektor(new double[]{1.0, 2.0, 3.0}, true);
        Wektor wektor2 = stwórzWektor(new double[]{1.0, 1.0, -2.0}, false);
        assertEquals(stwórzMacierz(new double[][]{{-3.0}}), wektor1.iloczyn(wektor2));

        Wektor wektor3 = stwórzWektor(new double[]{1.0, 2.0, 3.0}, false);
        Wektor wektor4 = stwórzWektor(new double[]{1.0, 1.0, -2.0}, true);
        Macierz macierz1 = stwórzMacierz(new double[][]{
                {1.0, 1.0, -2.0},
                {2.0, 2.0, -4.0},
                {3.0, 3.0, -6.0}
        });
        assertEquals(macierz1, wektor3.iloczyn(wektor4));
    }

    @Test
    void testMnożeniaMacierzWektor() throws NiezgodnośćRozmiarów {
        Macierz macierz1 = stwórzMacierz(new double[][]{
                {1.0, 2.0},
                {3.0, -2.0},
                {2.0, 1.0}
        });
        Wektor wektor1 = stwórzWektor(new double[]{-1.0, 3.0}, false);
        Wektor oczekiwany1 = stwórzWektor(new double[]{5.0, -9.0, 1.0}, false);
        assertEquals(oczekiwany1, macierz1.iloczyn(wektor1));

        // [1.0, -1.0, 2.0] * [[1.0, 2.0], [3.0, -2.0], [2.0, 1.0]] = [2.0, 6.0]
        Wektor wektor2 = stwórzWektor(new double[]{1.0, -1.0, 2.0}, true);
        Macierz macierz2 = stwórzMacierz(new double[][]{
                {1.0, 2.0},
                {3.0, -2.0},
                {2.0, 1.0}
        });
        Wektor oczekiwany2 = stwórzWektor(new double[]{2.0, 6.0}, true);
        assertEquals(oczekiwany2, wektor2.iloczyn(macierz2));
    }

    @Test
    void testDodawaniaMacierzMacierz() throws NiezgodnośćRozmiarów {
        // [[1.0, -2.0, 3.0], [2.0, 1.0, -1.0]] + [[3.0, -1.0, 2.0], [1.0, 1.0, -2.0]] = [[4.0, -3.0, 5.0], [3.0, 2.0, -3.0]]
        Macierz macierz1 = stwórzMacierz(new double[][]{
                {1.0, -2.0, 3.0},
                {2.0, 1.0, -1.0}
        });
        Macierz macierz2 = stwórzMacierz(new double[][]{
                {3.0, -1.0, 2.0},
                {1.0, 1.0, -2.0}
        });
        Macierz oczekiwany = stwórzMacierz(new double[][]{
                {4.0, -3.0, 5.0},
                {3.0, 2.0, -3.0}
        });
        assertEquals(oczekiwany, macierz1.suma(macierz2));
    }

    @Test
    void testMnożeniaMacierzMacierz() throws NiezgodnośćRozmiarów {
        // [[2.0, 0.5], [1.0, -2.0], [-1.0, 3.0]] * [[2.0, -1.0, 5.0], [-3.0, 2.0, -1.0]] = [[2.5, -1.0, 9.5], [8.0, -5.0, 7.0], [-11.0, 7.0, -8.0]]
        Macierz macierz1 = stwórzMacierz(new double[][]{
                {2.0, 0.5},
                {1.0, -2.0},
                {-1.0, 3.0}
        });
        Macierz macierz2 = stwórzMacierz(new double[][]{
                {2.0, -1.0, 5.0},
                {-3.0, 2.0, -1.0}
        });
        Macierz oczekiwany = stwórzMacierz(new double[][]{
                {2.5, -1.0, 9.5},
                {8.0, -5.0, 7.0},
                {-11.0, 7.0, -8.0}
        });
        assertEquals(oczekiwany, macierz1.iloczyn(macierz2));
    }

    @Test
    void testNegacji() {
        Skalar skalar = stwórzSkalar(17.0);
        assertEquals(stwórzSkalar(-17.0), skalar.negacja());

        Wektor wektor = stwórzWektor(new double[]{10.0, -45.0, 0.0, 29.0, -3.0}, true);
        assertEquals(stwórzWektor(new double[]{-10.0, 45.0, 0.0, -29.0, 3.0}, true),
                wektor.negacja());

        Macierz macierz = stwórzMacierz(new double[][]{
                {0.0, 0.5, -1.25},
                {11.0, -71.0, -33.5},
                {-2.0, -1.75, -99.0}
        });
        Macierz oczekiwany = stwórzMacierz(new double[][]{
                {0.0, -0.5, 1.25},
                {-11.0, 71.0, 33.5},
                {2.0, 1.75, 99.0}
        });
        assertEquals(oczekiwany, macierz.negacja());
    }

    @Test
    void testPrzypisaniaSkalarów() {
        // Przypisz skalar [0.5] do skalara [1.0]
        Skalar skalar1 = stwórzSkalar(1.0);
        skalar1.przypisz(stwórzSkalar(0.5));
        assertEquals(stwórzSkalar(0.5), skalar1);

        // Przypisz skalar [0.5] do wektora [1.0, 2.0, 3.0]
        Wektor wektor1 = stwórzWektor(new double[]{1.0, 2.0, 3.0}, true);
        wektor1.przypisz(stwórzSkalar(0.5));
        assertEquals(stwórzWektor(new double[]{0.5, 0.5, 0.5}, true), wektor1);

        // Przypisz skalar [0.5] do macierzy
        Macierz macierz1 = stwórzMacierz(new double[][]{
                {1.0, 2.0},
                {-3.0, -4.0},
                {5.0, -6.0}
        });
        macierz1.przypisz(stwórzSkalar(0.5));
        assertEquals(stwórzMacierz(new double[][]{
                {0.5, 0.5},
                {0.5, 0.5},
                {0.5, 0.5}
        }), macierz1);
    }

    @Test
    void testPrzypisaniaWektorów() throws NiezgodnośćRozmiarów {
        // Przypisz wektor [1.5, 2.5, 3.5] do wektora [-1.0, 0.0, 1.0]
        Wektor wektor1 = stwórzWektor(new double[]{1.5, 2.5, 3.5}, false);
        Wektor wektor2 = stwórzWektor(new double[]{-1.0, 0.0, 1.0}, false);
        wektor2.przypisz(wektor1);
        assertEquals(stwórzWektor(new double[]{1.5, 2.5, 3.5}, false), wektor2);

        // Przypisz wektor [1.5, 2.5, 3.5] do wektora [-1.0, 0.0, 1.0] (wektor wierszowy i kolumnowy)
        Wektor wektor3 = stwórzWektor(new double[]{-1.0, 0.0, 1.0}, true);
        wektor3.przypisz(wektor1);
        assertEquals(stwórzWektor(new double[]{1.5, 2.5, 3.5}, false), wektor3);

        // Przypisz wektor [1.5, 2.5, 3.5] do macierzy
        Macierz macierz1 = stwórzMacierz(new double[][]{
                {1.0, 2.0, -1.0, -2.0},
                {-3.0, -4.0, 3.0, 4.0},
                {5.0, -6.0, -5.0, 6.0}
        });
        macierz1.przypisz(wektor1);
        assertEquals(stwórzMacierz(new double[][]{
                {1.5, 1.5, 1.5, 1.5},
                {2.5, 2.5, 2.5, 2.5},
                {3.5, 3.5, 3.5, 3.5}
        }), macierz1);
    }

    @Test
    void testPrzypisaniaMacierzy() throws NiezgodnośćRozmiarów {
        // Przypisz macierz [10.5, 20.5, 30.5; -1.5, 0.0, 1.5] do macierzy [1.0, 2.0, 3.0; 3.0, 2.0, 1.0]
        Macierz macierz1 = stwórzMacierz(new double[][]{
                {1.0, 2.0, 3.0},
                {3.0, 2.0, 1.0}
        });
        Macierz macierz2 = stwórzMacierz(new double[][]{
                {10.5, 20.5, 30.5},
                {-1.5, 0.0, 1.5}
        });

        macierz1.przypisz(macierz2);
        assertEquals(macierz2, macierz1);
    }

    @Test
    void testWycinków() throws ZłyIndeks {
        Skalar skalar = stwórzSkalar(13.125);
        assertEquals(skalar, skalar.wycinek());

        Wektor wektor = stwórzWektor(new double[]{1.0, 21.0, 32.0, 43.0, 54.0}, true);
        Wektor oczekiwanyWycinekWektora = stwórzWektor(new double[]{32.0, 43.0}, true);
        assertEquals(oczekiwanyWycinekWektora, wektor.wycinek(2, 3));

        Macierz macierz = stwórzMacierz(new double[][]{
                {7.0, -21.0, 15.0, -31.0, 25.0},
                {-21.0, 15.0, -31.0, 25.0, 7.0},
                {15.0, -31.0, 25.0, -7.0, -21.0},
                {-31.0, 25.0, 7.0, -21.0, 15.0}
        });
        Macierz oczekiwanyWycinekMacierzy = stwórzMacierz(new double[][]{
                {15.0, -31.0},
                {-31.0, 25.0},
                {25.0, 7.0}
        });
        assertEquals(oczekiwanyWycinekMacierzy, macierz.wycinek(1, 3, 1, 2));
    }

    public static void main(String[] args) {

        MojaMaszynkaTestująca.wykonajTesty(Zad1TestyBezJUnit.class);

    }

}
