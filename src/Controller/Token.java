package Controller;

public class Token {
    public final TokenType type;
    public final String lexeme;
    public final Object literal;

    public Token(TokenType type, String lexeme, Object literal){
        this.type = type;
        this. lexeme = lexeme;
        this. literal = literal;
    }

    public String toString(){
        return type + " " + lexeme;
    }

}
