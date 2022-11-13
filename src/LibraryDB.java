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
        BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter your username: ");
        username = buffer.readLine();
        System.out.print("Enter your password: ");
        pwd = buffer.readLine();


        // Connection
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        OracleConnection conn =
                (OracleConnection)DriverManager.getConnection(
                        "jdbc:oracle:thin:@studora.comp.polyu.edu.hk:1521:dbms",username,pwd);
        clearScreen();

        // Prepare employee list
        Statement stmt;
        ResultSet rset;
        String snum, namer;
        int enumber=0;

        // Prepare SQL for request
        PreparedStatement prepareQuery = conn.prepareStatement(
                "select * from book where isbn = ?");

        while (enumber != -1)
        {
            stmt = conn.createStatement();
            rset = stmt.executeQuery("select isbn, title from book");
//            System.out.println("ENO" + " " + "ENAME");
            while (rset.next())
            {
                namer = rset.getString(2);
                if (!rset.wasNull())
                {
                    System.out.println(rset.getInt(1) + " " + namer);
                }
            }

            // Get request
            System.out.println();
            snum = readEntry("ISBN: ");
            enumber = Integer.valueOf(snum).intValue();

            // Get result
            prepareQuery.setInt(1, enumber);
            rset = prepareQuery.executeQuery();

            // Display result
            while (rset.next())
            {
                System.out.flush();
//                System.out.println("ENO ENAME ZIO HDATE");
                System.out.println(rset.getInt(1) + " " +
                        rset.getString(2) + " " +
                        rset.getString(3) + " " +
                        rset.getString(4) + " " +
                        rset.getString(5) + " " +
                        rset.getInt(6));
            }

            // Continue?
            System.out.println();
            snum = readEntry(" Enter a number to continue or -1 to exit. ");
            enumber = Integer.valueOf(snum).intValue();
            clearScreen();
        }

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