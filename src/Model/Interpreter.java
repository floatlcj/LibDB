package Model;

import Controller.*;
import View.Printer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class Interpreter implements Visitor {
    private HashMap<String, PIR> PIRs = new HashMap<>();
    public Interpreter(){
    }
    public void interpret(Stmt stmt){
        stmt.accept(this);
    }
    @Override
    public Void visitCreateStmt(CreateStmt stmt) {
        String name = stmt.getName();
        Token dataType = stmt.getDataType();
        switch (dataType.type){
            case NOTE:
                try {
                    createNote(name);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + dataType.type);
        }
        return null;
    }

    @Override
    public Void visitPrintStmt(PrintStmt stmt) {
        String identifier = stmt.identifier.lexeme;
        Printer printer = new Printer(PIRs);
        printer.print(identifier);
        return null;
    }

    @Override
    public Void visitExitStmt(ExitStmt stmt) {
        System.exit(64);
        return null;
    }

    private void createNote(String name)throws IOException{
        String text = readLine("Input your text:");
        Note note = new Note(name, text);
        PIRs.put(name, note);
    }

    private String readLine(String message)throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        System.out.println(message);
        String res = reader.readLine();
        return res;
    }
}
