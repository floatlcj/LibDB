package Controller;

public class ScannerError extends RuntimeException{
    public ScannerError(String message){
        System.out.println(message);
    }
}
