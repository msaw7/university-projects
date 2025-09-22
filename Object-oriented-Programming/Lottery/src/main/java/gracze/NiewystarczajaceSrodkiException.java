package main.java.gracze;

public class NiewystarczajaceSrodkiException extends RuntimeException {
  public NiewystarczajaceSrodkiException() {
    super("Za mało środków!");
  }
}
