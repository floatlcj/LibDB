package View;

import Model.Note;
import Model.PIR;
import Model.pirVisitor;

import java.util.HashMap;

public class Printer implements pirVisitor {
    private HashMap<String, PIR>  PIRs;
    public Printer(HashMap<String, PIR> PIRs){
        this.PIRs = PIRs;
    }

    public void print(String name){
        PIR pir = PIRs.get(name);
        pir.accept(this);
    }

    @Override
    public Void visitNote(Note note) {
        System.out.println("Note:" + note.getName());
        System.out.println(note.getText());
        return null;
    }
}
