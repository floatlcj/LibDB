import Controller.Parser;
import Controller.Scanner;
import Controller.ScannerError;
import Controller.Token;
import Controller.Stmt;
import Model.Interpreter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;


public class PIM {
    static boolean haveError = false;
    private static final Interpreter interpreter = new Interpreter();
    public static void main(String[] args){
        try {
            runPrompt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void runPrompt()throws IOException{
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        for (;;) {
            System.out.print("(PIM) ");
            String line = reader.readLine();
            if (line == null) break;
            run(line);
            haveError = false;
        }
    }

    private static void run(String line){
        try {
            Scanner scanner = new Scanner(line);
            List<Token> tokens = scanner.scanTokens();
            Parser parser = new Parser(tokens);
            Stmt stmt = parser.parse();
            interpreter.interpret(stmt);

//            for (Token token: tokens)
//                System.out.println(token);
        }catch (ScannerError error){
            haveError = false;
        }

    }

    public static void error(String message){
        haveError = true;
        System.out.println(message);
    }

}
