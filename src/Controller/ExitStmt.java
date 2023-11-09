package Controller;

public class ExitStmt extends Stmt{
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitExitStmt(this);
    }
}
