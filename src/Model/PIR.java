package Model;

public abstract class PIR {
    public abstract <T> T accept(pirVisitor<T> visitor);
}
