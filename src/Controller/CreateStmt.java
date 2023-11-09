package Controller;

public class CreateStmt extends Stmt{
    private String name;
    private Token dataType;
    public CreateStmt(Token dataType, Token name){
        this.name = name.lexeme;
        this.dataType = dataType;
    }
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitCreateStmt(this);
    }

    public String getName() {
        return name;
    }

    public Token getDataType() {
        return dataType;
    }
}
