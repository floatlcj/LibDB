import java.io.*;
import java.io.IOException;
import java.sql.*;
import oracle.jdbc.driver.*;

public class LibraryDB
{
    public static void main(String args[]) throws SQLException, IOException, InterruptedException
    {
        
        String username, pwd;

        username = "\"21103213d\"";
        pwd = "hfnmexfb";

        System.out.println("Loading...");
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
                clearScreen();
                Reader reader = new Reader();
                if(reader.RLogin(conn)){
                    System.out.println(" ");
                }else continue;
                int knum = 0;
                while(knum != -1){
			reader.ReceiveNotifications(conn);
                    System.out.println("Choose the function you want to use.");
                    System.out.println("1. search books by Title");
                    System.out.println("2. search books by Author");
                    System.out.println("3. search books by Category");
                    System.out.println("4. find friend by book");
                    System.out.println("5. recommended books");
		    System.out.println("6. check the status of the book and reserve book");
                    System.out.println("-1 to exit");
                    knum = Integer.valueOf(readEntry("Input your choice: ")).intValue();
                    if (knum == 1 || knum == 2 || knum == 3) {
                        clearScreen();
                        reader.SearchingBooks(conn, knum);
                    }
                    if (knum == 4){
                        clearScreen();
                        reader.ReaderFindFriend(conn);
                    }
                    if (knum == 5){
                        clearScreen();
                        reader.ReaderRecommendBook(conn);
                    }
		    if (knum == 5){
                        clearScreen();
                        reader.Reserve(conn);
                    }
                    if (knum == -1) {clearScreen(); break;}
                }
            }
            if(k == 2){
                Admin admin = new Admin();
                if (admin.ALogin(conn)){
                    int input = 0;
                    while (input != -1) {
			admin.SendNotifications(conn);
                        System.out.println("1. Add reader");
                        System.out.println("2. Delete reader");
                        System.out.println("3. Deactivate account");
			System.out.println("4. Analysis category");
			System.out.println("5. Analysis admin management");
			System.out.println("6. Analysis banned accounts");
			System.out.println("7. Update book status");
                        System.out.println("-1 to exit");
                        input = Integer.valueOf(readEntry("Input your choice: "));
                        if (input == 1) {
                            clearScreen();
                            admin.AdminAddUser(conn);
                        }
                        else if(input == 2) {
                            clearScreen();
                            admin.AdminDeleteUser(conn);
                        }
                        else if(input == 3){
			    clearScreen();
                            admin.Deactivation(conn);
                        }
			else if(input == 4){
			    clearScreen();
                            admin.AnalysisReport_Category(conn);
                        }
			else if(input == 5){
				clearScreen();
				admin.AnalysisReport_NotEnoughAdmin(conn);
			}
			else if(input == 6){
				clearScreen();
				admin.AnalysisReport_BannedAccount(conn);
			}
			else if(input == 7){
				clearScreen();
				admin.AdminUpdateStatus(conn);
			}
                        else if (input == -1) {clearScreen(); break;}
                    }
                }else continue;
            }
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

class Reader{
    String accountID;
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
            {this.accountID = rst.getString("accountid"); System.out.println("You log in as " + rst.getString("accountid"));return true;}
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
                pst = conn.prepareStatement("SELECT * FROM BOOK WHERE TITLE like ?");
                System.out.print("Enter a book's name to search books: ");
            }else if (knum == 2) {
                pst = conn.prepareStatement("SELECT * FROM BOOK WHERE AUTHOR like ?");
                System.out.print("Enter a book's author to search books: ");
            }else {
                pst = conn.prepareStatement("SELECT * FROM BOOK WHERE CATEGORY like ?");
                System.out.print("Enter a book's category to search books: ");
            }
            String keyword;
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            keyword = br.readLine();
            StringBuilder stringBuilder = new StringBuilder(keyword);
            stringBuilder.append('%');
            stringBuilder.insert(0,'%');
            keyword = stringBuilder.toString();
            pst.setString(1, keyword);
            rst = pst.executeQuery();
            if (rst == null)
                System.out.println("No result");
            while (rst.next())
            {
                System.out.flush();
                System.out.println();
                System.out.println("-------------------------------------------------------------------");
                System.out.println(rst.getInt(1) + " " +
                        rst.getString(2) + " " +
                        rst.getString(3) + " " +
                        rst.getString(4) + " " +
                        rst.getString(5) + " " +
                        rst.getInt(6));
                System.out.println("-------------------------------------------------------------------");
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }finally {
            System.out.println();
            System.out.println();
        }
    }

    void ReaderFindFriend(OracleConnection conn) throws IOException {
        PreparedStatement findFriend;
        ResultSet rst =null;
        try {
            //st = conn.createStatement();
            findFriend = conn.prepareStatement ("select READER.ACCOUNTID,READER.EMAIL from READER where READERID in (select OPERATION.READERID from OPERATION where ISBN = ?)");
            int isbn;
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("ISBN: ");
            isbn = Integer.valueOf(br.readLine());
            findFriend.setInt(1,isbn);
            rst = findFriend.executeQuery();
            if(rst == null) {
                System.out.println("Find nobody");
            }
            System.out.println("-------------------------------------------------------------------");
            while (rst.next())
            {
                System.out.println("Friend name: " + rst.getString("ACCOUNTID") + "   " +"Friend email: " + rst.getString("EMAIL"));
                System.out.println("-------------------------------------------------------------------");
            }


        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    void ReaderRecommendBook(OracleConnection conn) throws IOException {
        PreparedStatement findbook;
        ResultSet rst =null;
        try {

            findbook = conn.prepareStatement ("SELECT book.ISBN, book.TITLE FROM BOOK WHERE book.CATEGORY IN (SELECT OPERATION.CATEGORY FROM OPERATION WHERE operation.ACCOUNTID = ?)");
            findbook.setString(1,accountID);
            rst = findbook.executeQuery();
            if(rst == null)
                System.out.println("Find nothing, please read more books first, thank you.");
            System.out.println("We recommend you to read: ");
            System.out.println("-------------------------------------------------------------------");
            while (rst.next()) {
                System.out.println("BOOK ISBN: " + rst.getString("ISBN") + "   " + "BOOK TITLE: " + rst.getString("TITLE"));
                System.out.println("-------------------------------------------------------------------");
            }

        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    Boolean Reserve(OracleConnection conn) throws IOException {
        PreparedStatement pst;
        PreparedStatement changestatus;
        PreparedStatement addoperation;
        PreparedStatement getreaderid;
        ResultSet add ;
        ResultSet rst;
        ResultSet change;
        int isbn, status;
        String title;
        try {
            pst = conn.prepareStatement("select status,title from book where isbn = ?");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("The ISBN of the book is: ");
            isbn = Integer.valueOf(br.readLine()).intValue();
            pst.setInt(1, isbn);
            rst = pst.executeQuery();
            if (rst.next()) {
                status = rst.getInt(1);
                title = rst.getString(2);
                System.out.println("Status: " + status);
                System.out.println("Title: " + title);
                if (status == 1) {
                    System.out.println("This book is available and can be reserved. " + " " + title);
                    System.out.println("If you want to reserve this book,please enter yes.");
                    BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
                    String reserve = buffer.readLine();
                    while(reserve.compareTo("yes") == 0) {
                        changestatus = conn.prepareStatement("update book set status=2 where isbn = ?");
                        changestatus.setInt(1,isbn);
                        change = changestatus.executeQuery();
                        System.out.println("Reserving becomes successfully.");
                        int readerid,STATUS;
                        STATUS = 1;
                        getreaderid = conn.prepareStatement("select readerid from reader where accountid = ?");
                        this.accountid = String.valueOf(accountID);
                        System.out.println("Your account id is: " + accountid);
                        accountid = String.valueOf(accountID);
                        getreaderid.setString(1,accountid);
                        add = getreaderid.executeQuery();
                        if (add.next()) {
                            readerid = add.getInt(1);

                        }
                        addoperation = conn.prepareStatement("INSERT INTO OPERATION(READERID,ISBN,BEGIN,END,STATUS) VALUES(?,?,?,?,?)");
                        int readerID, ISBN, Status;
                        Status = 1;
                        Date began  = new Date();
                        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
                        String begin = dateFormat.format(began);
                        Date reserveend = new Date(System.currentTimeMillis() +10*24*60*60*1000);
                        String end = dateFormat.format(reserveend);
                        addoperation.setInt(1,add.getInt(1));
                        addoperation.setInt(2,isbn);
                        addoperation.setDate(3, java.sql.Date.valueOf(begin));
                        addoperation.setDate(4, java.sql.Date.valueOf(end));
                        addoperation.setInt(5,Status);
                        boolean test = addoperation.execute();
                        if(test = true) System.out.println("Your booking time will be from " + begin +" to " + reserveend);
                        else System.out.println("There are some problems, please contact staffs for helping.");
                            break;
                    }
                }
                if (status == 2) {
                    System.out.println("This book is not available. " + " " + title);

                }
                if (status == 3) {
                    System.out.println("This book is not available. " + " " + title);
                }
            }
            else{
                System.out.println("No result");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return  true;
    }
	
	
	void ReceiveNotifications(OracleConnection conn) throws IOException {
        PreparedStatement pst;
        PreparedStatement pst1;
        PreparedStatement pst2;
        ResultSet rst;
        ResultSet rst1;
        ResultSet rst2;
        try{
            // Suppose the reader will be notified if the book is not returned three days before the deadline.
//            String today = "21-NOV-22";
            long millis = System.currentTimeMillis();
            Date date = new Date(millis);
            String sDate = date.toString();
            boolean flag1 = true;
            boolean flag2 = true;
            pst = conn.prepareStatement("select readerid from reader where accountid = ?");
            pst.setString(1, accountID);
            rst = pst.executeQuery();
            pst1 = conn.prepareStatement("select book.title from operation, book " +
                    "where book.isbn = operation.isbn " +
                    "and (operation.status = 2 or operation.status = 3)" +
                    "and round(to_number(end - to_date(?,'yyyy-mm-dd'))) <= 3" +
                    "and operation.readerid = ?");
            while (rst.next()) {
                pst1.setString(1,sDate);
                pst1.setInt(2, rst.getInt(1));
            }
            rst1 = pst1.executeQuery();
            while (rst1.next()){
                System.out.println("You should return the book " + rst1.getString(1) + " as soon as possible.");
                flag1 = false;
            }
            pst2 = conn.prepareStatement("select wishlist.title from wishlist, book " +
                    "where wishlist.isbn = book.isbn " +
                    "and book.status = 1" +
                    "and wishlist.accountid = ?");
            pst2.setString(1,accountID);
            rst2 = pst2.executeQuery();
            while (rst2.next()){
                System.out.println("The book " + rst2.getString(1) + " in your wishlist has been available now.");
                flag2 = false;
            }
            if (flag1 && flag2)
                System.out.println("No notifications.");
        } catch (SQLException e){
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
    void AdminAddUser(OracleConnection conn) throws IOException {
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

    void AdminDeleteUser(OracleConnection conn) throws IOException {
        PreparedStatement 	deleteReader;
        ResultSet rst =null;
        try {
            deleteReader = conn.prepareStatement ("delete from READER where READER.ACCOUNTID = ? ");
            String accountId;
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("accountId: ");
            accountId = br.readLine();
            deleteReader.setString(1,accountId);
            boolean result = deleteReader.execute();
            if(result = true) System.out.println("delete success");
            else System.out.println("delete fail");
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void Deactivation(OracleConnection conn) throws IOException {
        Statement pst;
        ResultSet rst;
        try {
            // Suppose today is 11-DEC-22
            String today = "11-DEC-22";
            String sql = "select distinct reader.readerid from operation, reader " +
                    "where reader.readerid = operation.readerid " +
                    "and operation.status = 2 " +
                    "and operation.end < '11-DEC-22'";
            pst = conn.createStatement();
            rst = pst.executeQuery(sql);
            if (rst ==null){
                System.out.println("No reader's account will be deactivated.");
            }
            while (rst.next()){
                conn.setAutoCommit(false);
                PreparedStatement updateStatus = conn.prepareStatement("Update reader set status = ? where readerid = ?");
                updateStatus.setInt(1,0);
                updateStatus.setString(2,rst.getString(1));
                updateStatus.executeUpdate();
                conn.commit();
                System.out.println("The account of the reader whose reader ID is " + rst.getString(1) + " has been deactivated.");
            }

        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void AnalysisReport_Category(OracleConnection conn){
        String sql = "select category, count(category) from book where status = 2 group by category order by count(category) desc";
        ResultSet result;
        Statement stmt;
        try {
            stmt = conn.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try{
            result = stmt.executeQuery(sql);
			System.out.println("-------------------------------------------------------------------");
			int No = 0;	
				while (result.next()) {
					No++;
					System.out.flush();
                	System.out.println("|No."+ No + "|Category:" + result.getString(1)+"|Borrowed:" + result.getInt(2) + "|\n");
					if(No == 3){
						System.out.println("The popular categories are the top 3 categories.");
						System.out.println("Storing more books in these categories should be a good choice.\n");

						}
			    }
			System.out.println("-------------------------------------------------------------------");
            result.close();
            stmt.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
	
	
	
   
    void AnalysisReport_NotEnoughAdmin(OracleConnection conn){
        String sql1 = "select count(adminid) from admin";
        String sql2 = "select count(readerid) from reader";
        ResultSet result1,result2;
        Statement stmt1,stmt2;
        try {
            stmt1 = conn.createStatement();
            stmt2 = conn.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try{
            result1 = stmt1.executeQuery(sql1);
            result2 = stmt2.executeQuery(sql2);
			System.out.println("-------------------------------------------------------------------");
				
				if (result1.next() & result2.next()) {
				 	System.out.flush();
				    System.out.println("Number of admin is "+ result1.getInt("count(adminid)") +".");
				  	System.out.println("Number of reader is " + result2.getInt("count(readerid)") +".\n");
					int NoOfAdmin = result1.getInt("count(adminid)");
					int NoOfReader = result2.getInt("count(readerid)");
					System.out.println("Each admin is supposed to manage 50 reader users.");
                    if(50*NoOfAdmin < NoOfReader){
						System.out.println("Result: Too many reader users may cause large workload that admins can not handle.");
						System.out.println("        Please arrange more admins to void potential administative mistakes.");
			    	}else{
                        
						System.out.println("Result: The system has adequate admins to manage the reader users.");
					}
				
				}
			System.out.println("-------------------------------------------------------------------");
            result1.close();result2.close();
            stmt1.close();stmt2.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
	
	
	
    void AnalysisReport_BannedAccount(OracleConnection conn){
        String sql1 = "select count(*) from reader where status = 0";
		String sql2 = "select count(*) from reader";
        ResultSet result1, result2;
        Statement stmt1, stmt2;
        try {
            stmt1 = conn.createStatement();
			stmt2 = conn.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try{
            result1 = stmt1.executeQuery(sql1);
			result2 = stmt2.executeQuery(sql2);
			System.out.println("-------------------------------------------------------------------");
				
				if (result1.next() & result2.next()) {
				 	System.out.flush();
				    System.out.println("The total number of readers is "+ result2.getInt(1) +".");
				  	System.out.println("The total number of banned accounts is " + result1.getInt(1) +".\n");
					double esti_proportion = 0.05;
					double real_proportion = result1.getDouble(1)/result2.getDouble(1);
					double rounded_prop = Math.round(real_proportion*100);
					System.out.println(rounded_prop+"% of readers' accounts are banned.");
					if(real_proportion < esti_proportion){
						System.out.println("Result: Our system of borrowing time and returning time for books is very reasonable.");
			    	}else{
						System.out.println("Result: Too many reader's account are banned.");
						System.out.println("        The main reason may be that the book borrowing time and returning time for readers are not enough.");
						System.out.println("        Extending the time for borrowing books is considerable.");
					}
				
				}
			System.out.println("-------------------------------------------------------------------");
            result1.close();result2.close();
            stmt1.close();stmt2.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
	
	
    void AdminUpdateStatus(OracleConnection conn) throws IOException {
        PreparedStatement 	updateStatus;
        ResultSet rst =null;
        try {
           
        	updateStatus = conn.prepareStatement ("update BOOK set STATUS = ? where ISBN = ?");
            int status, isbn;
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("status: ");
            status = Integer.valueOf(br.readLine()).intValue();
            System.out.print("ISBN: ");
            isbn = Integer.valueOf(br.readLine()).intValue();
            
            updateStatus.setInt(1,status);
            updateStatus.setInt(2,isbn);
            
            boolean result = updateStatus.execute();
            if(result = true) System.out.println("update success");
            else System.out.println("update fail");
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
	
	void SendNotifications(OracleConnection conn) throws IOException{
        PreparedStatement pst;
        Statement st;
        ResultSet rst1;
        ResultSet rst2;
        try{
            // Suppose the reader will be notified if the book is not returned three days before the deadline.
//            String today = "21-NOV-22";
            long millis = System.currentTimeMillis();
            Date date = new Date(millis);
            String sDate = date.toString();
            boolean flag1 = true;
            boolean flag2 = true;
            pst = conn.prepareStatement("select distinct reader.accountid from operation, reader " +
                    "where reader.readerid = operation.readerid " +
                    "and operation.status = 2 " +
                    "and round(to_number(end - to_date(?,'yyyy-mm-dd'))) <= 3");
            pst.setString(1,sDate);
            rst1 = pst.executeQuery();
            while (rst1.next()){
                System.out.println("NEWS: A notification urging the return of the book has been sent to account " + rst1.getString(1));
                flag1 = false;
            }
            st = conn.createStatement();
            rst2 = st.executeQuery("select distinct wishlist.accountid from wishlist, book " +
                    "where wishlist.isbn = book.isbn " +
                    "and book.status = 1");
            while (rst2.next()){
                System.out.println("NEWS: A notification which reminds the reader's desired books are available has been sent to account " + rst2.getString(1));
                flag2 = false;
            }
            if (flag1 && flag2)
                System.out.println("No notifications need to be sent.");
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
