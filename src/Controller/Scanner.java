package Controller;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
    private final String source;
    private int start = 0;
    private int current = 0;
    private boolean haveError = false;
    private final List<Token> tokens = new ArrayList<>();
    private static final Map<String, TokenType> keywords = new HashMap<>();
    static {
        keywords.put("create", TokenType.CREATE);
        keywords.put("modify", TokenType.MODIFY);
        keywords.put("search", TokenType.SEARCH);
        keywords.put("save", TokenType.SAVE);
        keywords.put("load", TokenType.LOAD);
        keywords.put("delete", TokenType.DELETE);
        keywords.put("help", TokenType.HELP);
        keywords.put("exit", TokenType.EXIT);
        keywords.put("print", TokenType.PRINT);
        keywords.put("Note", TokenType.NOTE);
        keywords.put("Task", TokenType.TASK);
        keywords.put("Schedule", TokenType.SCHEDULE);
        keywords.put("Contact", TokenType.CONTACT);
    }
    public Scanner(String source){
        this.source = source;
    }

    public List<Token> scanTokens(){
        while(!isAtEnd()){
            start = current;
            scanToken();
        }
        tokens.add(new Token(TokenType.EOF, "", null));
        return tokens;
    }

    boolean isAtEnd(){
        return current >= source.length();
    }

    private char advance(){
        return source.charAt(current++);
    }

    private void addToken(TokenType type, Object literal){
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal));
    }

    private void addToken(TokenType type){
        addToken(type, null);
    }

    private boolean match(char expected){
        if (isAtEnd()) return false;
        if (source.charAt(current) == expected){
            current++;
            return true;
        }
        return false;
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();
        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = TokenType.IDENTIFIER;
        addToken(type);
    }

    private void scanToken(){
        char c = advance();
        switch (c){
            case '=':
                addToken(TokenType.EQUAL);
                break;
            case '<':
                addToken(TokenType.BEFORE);
                break;
            case '>':
                addToken(TokenType.AFTER);
                break;
            case '!':
                addToken(TokenType.NEG);
            case '&':
                if (match('&')) {
                    addToken(TokenType.AND);
                    break;
                }
            case '|':
                if (match('|')) {
                    addToken(TokenType.OR);
                    break;
                }
            case ' ':
            case '\r':
            case '\t':
                break;
            default:
                if (isAlpha(c))
                    identifier();
                else{
                    haveError = true;
                    throw new ScannerError("Unexpected Character");
                }
        }
    }

    public boolean isHaveError(){
        return haveError;
    }

}
