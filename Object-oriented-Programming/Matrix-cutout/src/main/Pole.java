package main;
public class Pole {
    private double wartość;
    public Pole(double wartość) {
        this.wartość = wartość;
    }
    public double bezpieczneDaj() {
        return wartość;
    }
    public void bezpieczneUstaw(double wartość) {
        this.wartość = wartość;
    }
    @Override
    public String toString() {
        return Double.toString(wartość);
    }

}
