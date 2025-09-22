package test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import java.lang.reflect.Method;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

public class MojaMaszynkaTestująca {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Test {
    }

    public static void wykonajTesty(Class<?> testClass) {
        // W terminologii JUnit ta klasa to Runner (wykonywacz testów)
        String ZIELONY = '\u001B' + "[32m"; // Kolor zielony
        String CZERWONY = '\u001B' + "[31m"; // Kolor czerwony
        String DOMYŚLNY = '\u001B' + "[0m"; // Kolor domyślny

        int zadziałały = 0;   // Ile metoda z testami zadziałało (tzn. wszystkie testy w tych metodach skończyły się sukcesem)
        int padły = 0;        // Ile metoda z testami padło (tzn. przynajmniej jeden test w tych metodach skończył się niepowodzeniem)

        testyDziałające = 0;  // Inicjalizacja zmiennej klasowej

        StringBuilder błędy = new StringBuilder(); // Połączony tekst opisujący kolejne błędy, wypisywany po wszystkich testach.

        Method[] metody = testClass.getDeclaredMethods();
        // Sortowanie metod po nazwach, żeby były w tej samej kolejności przy każdym uruchomieniu.
        // Metoda getDeclareMethods() daje metody w dowolnej kolejności (nie jest to kolejność deklaracji).
        Arrays.sort(metody, (m1, m2) -> m1.getName().compareTo(m2.getName()));

        for (Method metoda : metody) {
            if (metoda.isAnnotationPresent(MojaMaszynkaTestująca.Test.class)) {
                System.out.print("Testuję: " + metoda.getName() + " ... ");
                try {
                    metoda.setAccessible(true);
                    metoda.invoke(testClass.getDeclaredConstructor().newInstance());
                    System.out.println(ZIELONY + "OK" + DOMYŚLNY); // Zielone "OK"
                    zadziałały++;
                } catch (Exception e) {
                    System.out.println(CZERWONY + "padło" + DOMYŚLNY); // Czerwone "padło"
                    padły++;
                    błędy.append("\nOpis błędu z metody: ").append(CZERWONY).append(metoda.getName()).
                          append(DOMYŚLNY).append("\n");

                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw); // Pobranie stosu z momentu zgłoszenia wyjątku
                    błędy.append(sw.toString()).append("\n");

                }
            }
        }

        System.out.println("\nPodsumowanie metod testujących:");
        System.out.println("Zadziałały : " + zadziałały);
        System.out.println("Padły      : " + padły);

        System.out.println("Liczba działających testów (jedna metoda może mieć wiele testów): " + testyDziałające);

        if (padły > 0) {
            System.out.println(CZERWONY + "\nNie wszystkie testy przeszły pomyślnie!" + DOMYŚLNY);
            System.out.println("\nSzczegóły błędów:");
            System.out.println(błędy);
        } else {
            System.out.println(ZIELONY + "Wszystkie testy przeszły pomyślnie!" + DOMYŚLNY);
        }
    }

    public static int testyDziałające = 0;
    // public static int testyNiedziałające = 0;

        public static void assertEquals(Object oczekiwane, Object faktyczne) {
            if ((oczekiwane == null && faktyczne != null) || (oczekiwane != null && !oczekiwane.equals(faktyczne))) {
                throw new AssertionError("Oczekiwano: " + oczekiwane + ", ale było: " + faktyczne);
            }
            testyDziałające++;
        }

        public static void assertArrayEquals(int[] oczekiwane, int[] faktyczne) {
            if (oczekiwane == null || faktyczne == null) {
                if (oczekiwane != faktyczne) {
                    throw new AssertionError("Oczekiwano: " + Arrays.toString(oczekiwane) +
                                              ", ale było: " + Arrays.toString(faktyczne));
                }
                return;
            }
            if (oczekiwane.length != faktyczne.length) {
                throw new AssertionError("Różne długości tablic. Oczekiwano: " + oczekiwane.length +
                                          ", ale było: " + faktyczne.length);
            }
            for (int i = 0; i < oczekiwane.length; i++) {
                if (oczekiwane[i] != faktyczne[i]) {
                    throw new AssertionError("Elementy tablic różnią się pod indeksem " + i +
                                              ". Oczekiwano: " + oczekiwane[i] + ", ale było: " + faktyczne[i]);
                }
            }
            testyDziałające++;
        }

        public static void assertArrayEquals(double[] oczekiwane, double[] faktyczne, double delta) {
            if (oczekiwane == null || faktyczne == null) {
                if (oczekiwane != faktyczne) {
                    throw new AssertionError("Oczekiwano: " + Arrays.toString(oczekiwane) +
                                              ", ale było: " + Arrays.toString(faktyczne));
                }
                return;
            }
            if (oczekiwane.length != faktyczne.length) {
                throw new AssertionError("Różne długości tablic. Oczekiwano: " + oczekiwane.length +
                                         ", ale było: " + faktyczne.length);
            }
            for (int i = 0; i < oczekiwane.length; i++) {
                if (Math.abs(oczekiwane[i] - faktyczne[i]) > delta) {
                    throw new AssertionError("Elementy tablic różnią się pod indeksem " + i +
                                              ". Oczekiwano: " + oczekiwane[i] + ", ale było: " + faktyczne[i]);
                }
            }
            testyDziałające++;
        }

        public static void fail(String message) {
            throw new AssertionError(message);
        }
}
