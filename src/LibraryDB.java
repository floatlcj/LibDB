/************************************/
/* Employee JDBC					*/
/************************************/

import java.io.*;
import java.io.IOException;
import java.sql.*;
import oracle.jdbc.driver.*;

public class LibraryDB
{
    public static void main(String args[]) throws SQLException, IOException, InterruptedException
    {
        // Login
//        clearScreen();
//        Console console = System.console();
//        System.out.print("Enter your username: ");    // Your Oracle ID with double quote
//        String username = console.readLine();         // e.g. "98765432d"
//        System.out.print("Enter your password: ");    // Password of your Oracle Account
//        char[] password = console.readPassword();
//        String pwd = String.valueOf(password);
        String username, pwd;
//        BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
//        System.out.print("Enter your username: ");
//        username = buffer.readLine();
//        System.out.print("Enter your password: ");
//        pwd = buffer.readLine();
        username = "\"21100052d\"";
        pwd = "Lcj200268";


        // Connection
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        OracleConnection conn =
                (OracleConnection)DriverManager.getConnection(
                        "jdbc:oracle:thin:@studora.comp.polyu.edu.hk:1521:dbms",username,pwd);
        clearScreen();

        int k = 0;
        while(k != -1){
            System.out.println("Choose the type of account to login.");
            System.out.println("1. Reader");
            System.out.println("2. Admin");
            System.out.println("-1 to exit");
            k = Integer.valueOf(readEntry("Input your choice: ")).intValue();
            if (k == 1){
                Reader reader = new Reader();
                if(reader.RLogin(conn)){
                    System.out.println("Reader");
                }else continue;
                int knum = 0;
                while(knum != -1){
                    System.out.println("Choose the type of keyword to search books.");
                    System.out.println("1. Title");
                    System.out.println("2. Author");
                    System.out.println("3. Category");
                    System.out.println("-1 to exit");
                    knum = Integer.valueOf(readEntry("Input your choice: ")).intValue();
                    reader.SearchingBooks(conn,knum);
                }
            }
            if(k == 2){
                Admin admin = new Admin();
                if (admin.ALogin(conn)){
                    System.out.println("Admin");
                }else continue;
            }
        }

//        // Prepare employee list
//        Statement stmt;
//        ResultSet rset;
//        String snum, namer;
//        int enumber=0;
//
//        // Prepare SQL for request
//        PreparedStatement prepareQuery = conn.prepareStatement(
//                "select * from book where isbn = ?");
//
//        while (enumber != -1)
//        {
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery("select isbn, title from book");
////            System.out.println("ENO" + " " + "ENAME");
//            while (rset.next())
//            {
//                namer = rset.getString(2);
//                if (!rset.wasNull())
//                {
//                    System.out.println(rset.getInt(1) + " " + namer);
//                }
//            }
//
//            // Get request
//            System.out.println();
//            snum = readEntry("ISBN: ");
//            enumber = Integer.valueOf(snum).intValue();
//
//            // Get result
//            prepareQuery.setInt(1, enumber);
//            rset = prepareQuery.executeQuery();
//
//            // Display result
//            while (rset.next())
//            {
//                System.out.flush();
////                System.out.println("ENO ENAME ZIO HDATE");
//                System.out.println(rset.getInt(1) + " " +
//                        rset.getString(2) + " " +
//                        rset.getString(3) + " " +
//                        rset.getString(4) + " " +
//                        rset.getString(5) + " " +
//                        rset.getInt(6));
//            }
//
//            // Continue?
//            System.out.println();
//            snum = readEntry(" Enter a number to continue or -1 to exit. ");
//            enumber = Integer.valueOf(snum).intValue();
//            clearScreen();
//        }

        // Exit the program
        conn.close();
        System.out.println("Bye! \n Press \'Enter\' to exit.");
        System.in.read();
        clearScreen();
    }

    // readEntry function -- Read input string
    static String readEntry(String prompt)
    {
        try
        {
            StringBuffer buffer = new StringBuffer();
            System.out.print(prompt);
            System.out.flush();
            int c = System.in.read();
            while (c != '\n' && c != -1)
            {
                buffer.append((char)c);
                c = System.in.read();
            }
            return buffer.toString().trim();
        }
        catch (IOException e)
        {
            return "";
        }
    }

    // clearScreen function -- clear screen
    static void clearScreen() throws IOException, InterruptedException
    {
        if (System.getProperty("os.name").contains("Windows"))
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        else
            System.out.print("\033[H\033[2J");
    }
}

class Reader{
    Boolean RLogin(OracleConnection conn) throws IOException{
        PreparedStatement pst;
        ResultSet rst;
        try {
            pst = conn.prepareStatement("select accountid, password from reader where accountid = ? and password = ?");
            String accountID, password;
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Reader name: ");
            accountID = br.readLine();
            System.out.print("Password: ");
            password = br.readLine();
            pst.setString(1, accountID);
            pst.setString(2, password);
            rst = pst.executeQuery();
            if (rst.next())
            {System.out.println("You login as " + rst.getString("accountid"));return true;}
            else {System.out.println("Wrong Reader name or password."); return false;}
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void SearchingBooks(OracleConnection conn, int knum) throws IOException{
        PreparedStatement pst;
        ResultSet rst;
        try{
            if (knum == 1) {
                pst = conn.prepareStatement("SELECT * FROM BOOK WHERE TITLE = ?");
                System.out.println("Enter a book's name to search books: ");
            }else if (knum == 2) {
                pst = conn.prepareStatement("SELECT * FROM BOOK WHERE AUTHOR = ?");
                System.out.println("Enter a book's author to search books: ");
            }else {
                pst = conn.prepareStatement("SELECT * FROM BOOK WHERE CATEGORY = ?");
                System.out.println("Enter a book's category to search books: ");
            }
            String keyword;
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            keyword = br.readLine();
            pst.setString(1, keyword);
            rst = pst.executeQuery();
            while (rst.next())
            {
                System.out.flush();
                System.out.println(rst.getInt(1) + " " +
                        rst.getString(2) + " " +
                        rst.getString(3) + " " +
                        rst.getString(4) + " " +
                        rst.getString(5) + " " +
                        rst.getInt(6));
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    
    Boolean ReaderFindFriend(OracleConnection conn) throws IOException {
        PreparedStatement findFriend;
        ResultSet rst =null;
        try {
        		//st = conn.createStatement();
        	    findFriend = conn.prepareStatement ("select READER.ACCOUNTID,READER.EMAIL from READER where READERID in (select OPERATION.READERID from OPERATION where ISBN = '?')");
        		int isbn;
        		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("ISBN: ");
                isbn = Integer.valueOf(br.readLine());
                findFriend.setLong(1,isbn);
                rst = findFriend.executeQuery();
                if(rst == null) {
                	System.out.println("Find nobody"); 
                	return false;}
                while (rst.next())
                {System.out.println("Friend name: " + rst.getString("READER.ACCOUNTID" + "   " +"Friend email: " + rst.getString("READER.EMAIL")));return true;}
                
                
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    
}
class Admin {
        Boolean ALogin(OracleConnection conn) throws IOException {
            PreparedStatement pst;
            ResultSet rst;
            try {
                pst = conn.prepareStatement("select admin_name, password from admin where admin_name = ? and password = ?");
                String adminName, password;
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("Admin name: ");
                adminName = br.readLine();
                System.out.print("Password: ");
                password = br.readLine();
                pst.setString(1, adminName);
                pst.setString(2, password);
                rst = pst.executeQuery();
                if (rst.next())
                {System.out.println("You login as " + rst.getString("admin_name"));return true;}
                else {System.out.println("Wrong Admin name or password."); return false;}
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
        Integer AdminAddUser(OracleConnection conn) throws IOException {
            PreparedStatement 	addReader;
            ResultSet rst =null;
            try {
            		//st = conn.createStatement();
            	    addReader = conn.prepareStatement ("INSERT INTO READER(ACCOUNTID,PASSWORD,EMAIL,STATUS,LEND) VALUES (?,?,?,?,?)");
            		String accountId, password, email;
            		int status = 1;
            		int lend = 0;
            		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                    System.out.print("accountId: ");
                    accountId = br.readLine();
                    System.out.print("password: ");
                    password = br.readLine();
                    System.out.print("email: ");
                    email = br.readLine();
                    addReader.setString(1,accountId);
                    addReader.setString(2,password);
                    addReader.setString(3,email);
                    addReader.setLong(4,status);
                    addReader.setLong(5,lend);
                    boolean result = addReader.execute();
                    if(result = true) System.out.println("add success");
                    else System.out.println("add fail");
            }catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
}
