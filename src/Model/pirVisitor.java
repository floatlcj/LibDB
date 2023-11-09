package Model;

import Controller.CreateStmt;
import Controller.Visitor;

public interface pirVisitor<T> {
    T visitNote(Note note);
}
