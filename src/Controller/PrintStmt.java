package Controller;

public class PrintStmt extends Stmt{
    public Token identifier;
    public PrintStmt(Token identifier){
        this.identifier = identifier;
    }
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitPrintStmt(this);
    }
}
