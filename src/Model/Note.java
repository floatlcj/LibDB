package Model;

public class Note extends PIR{
    protected String name;
    protected String text;

    Note(String name, String text){
        this.name = name;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public <T> T accept(pirVisitor<T> visitor) {
        return visitor.visitNote(this);
    }
}
