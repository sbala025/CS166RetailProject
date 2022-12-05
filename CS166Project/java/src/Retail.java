/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;
import java.time.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Retail {
   public static String loggeduserID;
   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));
	

   public static int stringCompare(String str1, String str2)
    {
  
        int l1 = str1.length();
        int l2 = str2.length();
        int lmin = Math.min(l1, l2);
  
        for (int i = 0; i < lmin; i++) {
            int str1_ch = (int)str1.charAt(i);
            int str2_ch = (int)str2.charAt(i);
  
            if (str1_ch != str2_ch) {
                return str1_ch - str2_ch;
            }
        }
	if (l1 != l2) {
            return l1 - l2;
        }
	else {
            return 0;
        }
    }
	
   /**
    * Creates a new instance of Retail shop
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Retail(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end Retail

   // Method to calculate euclidean distance between two latitude, longitude pairs. 
   public static double calculateDistance (double lat1, double long1, double lat2, double long2){
      double t1 = (lat1 - lat2) * (lat1 - lat2);
      double t2 = (long1 - long2) * (long1 - long2);
      return Math.sqrt(t1 + t2); 
   }
   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
		 if(outputHeader){
			for(int i = 1; i <= numCol; i++){
			System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out.println();
			outputHeader = false;
		 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();
      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            Retail.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      Retail esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Retail object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Retail (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. View Stores within 30 miles");
                System.out.println("2. View Product List");
                System.out.println("3. Place a Order");
		System.out.println("4. View 5 recent orders");

		//the following functionalities basically used by managers
		System.out.println("\nThe following functions require manager authorization.");
                System.out.println("5. View 5 recent orders of store");
                System.out.println("6. Update Product");
                System.out.println("7. View 5 recent Product Updates Info");
                System.out.println("8. View 5 Popular Items");
                System.out.println("9. View 5 Popular Customers");
                System.out.println("10. Place Product Supply Request to Warehouse");

		//the following functionalities basically used by admin
          	System.out.println("\nThe following functions require admin authorization.");
		System.out.println("11. Update Product in Any Store");
		System.out.println("12. Modify User information");
		System.out.println("13. View Products or Users");

	        System.out.println(".........................");
                System.out.println("20. Log out");
                switch (readChoice()){
                   case 1: viewStores(esql); break;
                   case 2: viewProducts(esql); break;
                   case 3: placeOrder(esql); break;
		   case 4: viewRecentOrders(esql); break;
                   case 5: viewRecentOrdersOfStore(esql); break;
                   case 6: updateProduct(esql); break;
                   case 7: viewRecentUpdates(esql); break;
                   case 8: viewPopularProducts(esql); break;
                   case 9: viewPopularCustomers(esql); break;
                   case 10: placeProductSupplyRequests(esql); break;
		   case 11: updateProductAllStores(esql); break;
		   case 12: modifyUser(esql); break;
		   case 13: viewProductsUsers(esql); break;

                   case 20: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user
    **/
   public static void CreateUser(Retail esql){
      try{
         System.out.print("\tEnter name: ");
         String name = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine();
         System.out.print("\tEnter latitude: ");   
         String latitude = in.readLine();       //enter lat value between [0.0, 100.0]
         System.out.print("\tEnter longitude: ");  //enter long value between [0.0, 100.0]
         String longitude = in.readLine();
         
         String type="Customer";

			String query = String.format("INSERT INTO USERS (name, password, latitude, longitude, type) VALUES ('%s','%s', %s, %s,'%s')", name, password, latitude, longitude, type);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Retail esql){
      try{
         System.out.print("\tEnter name: ");
         String name = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine();

        String query = String.format("SELECT * FROM USERS WHERE name = '%s' AND password = '%s'", name, password);
	    int userNum = esql.executeQuery(query);
	    query = String.format("SELECT userID FROM USERS WHERE name = '%s' AND password = '%s'", name, password);
        if (userNum > 0){
            List<List<String>> userResult = esql.executeQueryAndReturnResult(query);
            loggeduserID = userResult.get(0).get(0);
            System.out.print("Current user: " +loggeduserID + "\n");
            return name;
        }
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
	 System.out.println ("User not found. Please create user");
         return null;
      }
   }//end

// Rest of the functions definition go in here

   public static void viewStores(Retail esql) {
	try {
		String query = String.format("SELECT DISTINCT(s.storeID), s.name, calculate_distance(u.latitude, u.longitude, s.latitude, s.longitude) as dist FROM users u, store s WHERE calculate_distance(u.latitude, u.longitude, s.latitude, s.longitude) < 30 AND u.userID = '%s'", loggeduserID);
		esql.executeQueryAndPrintResult(query);
	} catch(Exception e){
		System.err.println (e.getMessage());
		System.out.println ("No stores in your area. Advocate for density in your area and try again.");
	}
   }
   public static void viewProducts(Retail esql) {
	try{
		String query = "SELECT *  FROM Product WHERE Product.storeID = ";
		System.out.print("\tEnter Store ID: ");
                String input = in.readLine();
		query +=input;
		esql.executeQueryAndPrintResult(query);
	} catch (Exception e) {
		System.err.println (e.getMessage());
	}
   }
   public static void placeOrder(Retail esql) {
	try {
		String distQuery = String.format("SELECT latitude, longitude  FROM USERS WHERE userID='%s'",loggeduserID);
        	List<List<String>> userDist = esql.executeQueryAndReturnResult(distQuery);
                double user_lat = Double.parseDouble(userDist.get(0).get(0));
                double user_lon = Double.parseDouble(userDist.get(0).get(1));

		System.out.print("\n\tEnter StoreID you wish to place an order from: ");
		String store_id = in.readLine();
	        String storeQuery = String.format("SELECT latitude, longitude FROM Store WHERE storeID = '%s'",store_id);
	        List<List<String>> stores_avail = esql.executeQueryAndReturnResult(storeQuery);    

		System.out.print("\n\tEnter name of product you want: ");
		String prod_name = in.readLine();
		String productQuery = String.format("SELECT p.productName,P.numberOfUnits FROM Product P WHERE storeID='%s' AND productName='%s'",store_id, prod_name);
	        List<List<String>> prod_result = esql.executeQueryAndReturnResult(productQuery);

		System.out.print("\n\tEnter number of units you want of this product: ");
		String num_of_units = in.readLine();

		double store_lat;
                double store_lon;
                double dist;
                boolean store_check = false;
		boolean product_check = false;
		boolean units_check = false;
		//check if store that user entered exists
		if(stores_avail.size()>0 && stores_avail.size()<=20){
			store_lat = Double.parseDouble(stores_avail.get(0).get(0));
                        store_lon = Double.parseDouble(stores_avail.get(0).get(1));
			dist = calculateDistance(user_lat,user_lon,store_lat,store_lon);
			if(dist<30){
				store_check = true;
                		store_lat = Double.parseDouble(stores_avail.get(0).get(0));
                		store_lon = Double.parseDouble(stores_avail.get(0).get(1));
				System.out.print("\n\tStore is within range");
			}else{
				System.out.print("\n\tStore is too far.");
				System.out.print("\n\tDistance: ");
                                System.out.print(dist);
			}
		}else{
			System.out.print("\n\tStore Does not exist");	
		}
		//check if product that user entered exists
		if(prod_result.size()>0){
			product_check = true;	
		}else{
			System.out.print("\n\tProduct not found. Request cannot go through");
		}

		if(prod_result.size()>0){
			if(Double.parseDouble(num_of_units) <= Double.parseDouble(prod_result.get(0).get(1))){
				System.out.print("\n\tYou have successfully ordered item. Thank you. ");
				String query = String.format("UPDATE Product SET numberOfUnits=numberOfUnits-'%s' WHERE storeID='%s' AND productName='%s'", num_of_units, store_id, prod_name);	
				esql.executeUpdate(query);
				
				query = String.format("INSERT INTO Orders(customerID,storeID,productName,unitsOrdered,orderTime) VALUES('%s', '%s', '%s', '%s', CURRENT_TIMESTAMP)",loggeduserID, store_id, prod_name,num_of_units);
			esql.executeUpdate(query);
			}else{
				System.out.print("\n\tNot enough units of desired product in stock. Request cannot go through.");
			}
		}else{
			//product not found so do nothing
		}
	System.out.print("\n");
		
	} catch(Exception e) {
		System.err.println (e.getMessage());
	}
    }
    public static void viewRecentOrders(Retail esql) {
    	try{
	   String query = String.format("SELECT O.productName,O.unitsOrdered,O.orderTime FROM Orders O WHERE customerID='%s' ORDER BY O.orderTime DESC LIMIT 5",loggeduserID);
	   List<List<String>> topOrders = esql.executeQueryAndReturnResult(query);
                                int result5 = topOrders.size();
                                if(topOrders.size()>0){
                                        for(int i=0; i<5 && result5>0; i++){
                                                System.out.print("\n\tProduct Name: " + topOrders.get(i).get(0));
                                                System.out.print("\n\tUnits Ordered: " + topOrders.get(i).get(1));
                                                System.out.print("\n\tOrder Time: " + topOrders.get(i).get(2) + "\n");
                                        }
                                }else{
                                        System.out.print("\n\tThere are no orders");
				}
    	} catch(Exception e) {
                System.err.println (e.getMessage());
        }
    }
    public static void viewRecentOrdersOfStore(Retail esql) {
   	try{
		String managerQuery = String.format("SELECT type FROM Users WHERE userID = '%s'", loggeduserID);
		List<List<String>> type = esql.executeQueryAndReturnResult(managerQuery);
		String user_type = type.get(0).get(0);
		String utype = user_type.trim();
		String managerstr = "manager";
		if (stringCompare(managerstr, utype) == 0) {
			System.out.print("Enter storeID:");
			String store_id = in.readLine();
			
			String query =  String.format("SELECT * FROM Store Where managerID = '%s' AND storeID = '%s'", loggeduserID, store_id);
                        int valid_storeid = esql.executeQuery(query);
			if (valid_storeid  > 0 ){
				String query2 = String.format("SELECT O.storeID,S.name,O.productName,O.unitsOrdered,O.orderTime FROM Orders O, Store S WHERE customerID='%s' AND O.storeID=S.storeID ORDER BY O.orderTime DESC LIMIT 5",loggeduserID);
	 			List<List<String>> topOrders = esql.executeQueryAndReturnResult(query2);
	 			int result5 = topOrders.size();
         			if(topOrders.size()>0){
                			for(int i=0; i<5 && result5>0; i++){
                				System.out.print("\n\tStore ID: " + topOrders.get(i).get(0));
                				System.out.print("\n\tStore Name: " + topOrders.get(i).get(1));
                				System.out.print("\n\tProduct Name: " + topOrders.get(i).get(2));
                        			System.out.print("\n\tUnits Ordered: " + topOrders.get(i).get(3));
                        			System.out.print("\n\tOrder Time: " + topOrders.get(i).get(4) + "\n");
               				}
				}else{
	 				System.out.print("\n\tThere are no orders");
			}
			} else {
				System.out.print("This function is reserved for the manager of this store only. \n");
			}
		} else {
			System.out.print("This function is reserved for the manager of this store only. \n");
		}
	} catch (Exception e) {
		System.err.println(e.getMessage());
	}

   }
   public static void updateProduct(Retail esql) {
	try{
		String managerQuery = String.format("SELECT type FROM Users WHERE userID = '%s'", loggeduserID);
                List<List<String>> type = esql.executeQueryAndReturnResult(managerQuery);
                String user_type = type.get(0).get(0);
                String utype = user_type.trim();
                String managerstr = "manager";
                if (stringCompare(managerstr, utype) == 0) {
                        System.out.print("Enter storeID:");
                        String store_id = in.readLine();

                        String query =  String.format("SELECT * FROM Store Where managerID = '%s' AND storeID = '%s'", loggeduserID, store_id);
                        int valid_storeid = esql.executeQuery(query);
                        if (valid_storeid  > 0 ){
         			String query2 =  String.format("SELECT * FROM Product Where storeID = '%s'",store_id);
				esql.executeQueryAndPrintResult(query2);
         			System.out.println("\tChoose product to update");
				String prod = in.readLine();
				System.out.println(".........................");
				System.out.println("\tChoose what to update");
				System.out.println("1. Number of Units");
                                System.out.println("2. Price Per Units");
				System.out.println(".........................");
        			int c = readChoice();
         			switch(c){
                			case 1:
						System.out.println("Enter New Number of Units: ");
						int newUnits = readChoice();
						String updateQuery = String.format("UPDATE Product SET numberOfUnits='%s' WHERE storeID='%s' AND productName='%s'", newUnits, store_id, prod);
						esql.executeUpdate(updateQuery);
						String updateQuery2 = String.format("INSERT INTO ProductUpdates(managerID,storeID,productName,updatedOn) VALUES('%s', '%s', '%s', CURRENT_TIMESTAMP)",loggeduserID, store_id, prod);
                                                esql.executeUpdate(updateQuery2);
						break;
					case 2:
						System.out.println("Enter New Price Per Unit: ");
                                                int newPrice = readChoice();
						String updateQuery3 = String.format("UPDATE Product SET pricePerUnit='%s' WHERE storeID='%s' AND productName='%s'", newPrice, store_id, prod);
                                                esql.executeUpdate(updateQuery3);
                                                String updateQuery4 = String.format("INSERT INTO ProductUpdates(managerID,storeID,productName,updatedOn) VALUES('%s', '%s', '%s', CURRENT_TIMESTAMP)",loggeduserID, store_id, prod);
						esql.executeUpdate(updateQuery4);
						break;
				}	
			} else {
                                System.out.print("This function is reserved for the manager of this store only. \n");
                        }
                } else {
                        System.out.print("This function is reserved for managers only. \n");
                }
        } catch (Exception e) {
                System.err.println(e.getMessage());
        }

}
			

   public static void viewRecentUpdates(Retail esql) {
      try{
        String managerQuery = String.format("SELECT type FROM Users WHERE userID = '%s'", loggeduserID);
        List<List<String>> type = esql.executeQueryAndReturnResult(managerQuery);
		String user_type = type.get(0).get(0);
		String utype = user_type.trim();
		String managerstr = "manager";
        // Ensure manager is accessing function
        if (stringCompare(managerstr, utype) == 0) {
            // Get store ID
			System.out.print("Enter storeID:");
			String store_id = in.readLine();
			// Compare store ID against all stores that are run by manager
			String query =  String.format("SELECT * FROM Store Where managerID = '%s' AND storeID = '%s'", loggeduserID, store_id);
                        int valid_storeid = esql.executeQuery(query);
            		// Check if store is run by manager
			if (valid_storeid  > 0 ){
				String viewRecentUpdatesQuery = String.format("SELECT PU.productName, PU.updatedOn FROM ProductUpdates PU WHERE PU.storeID = '%s' AND PU.managerID = '%s' ORDER BY updatedOn DESC", store_id, loggeduserID);
	 			List<List<String>> topUpdates = esql.executeQueryAndReturnResult(viewRecentUpdatesQuery);
	 			int result5 = topUpdates.size();
                    //Output the top 5 recent (less if there are less than 5 orders) updates
         			if(topUpdates.size()>0){
                			for(int i=0; i<5 && result5>0; i++){
                				System.out.print("\n\tProduct Name: " + topUpdates.get(i).get(0));
						System.out.print("\n\tUpdated On: " + topUpdates.get(i).get(1) + "\n");
               				}
				}else{
	 				System.out.print("\n\tThere are no recent updates.");
			}
			} else {
				System.out.print("This function is reserved for the manager of this store only. \n");
			}
		} else {
			System.out.print("This function is reserved for managers only. \n");
		}
   	} catch(Exception e) {
   		System.err.println (e.getMessage());
   	}
   }
   // viewPopularProducts
   public static void viewPopularProducts(Retail esql) {
    try{
        String managerQuery = String.format("SELECT type FROM Users WHERE userID = '%s'", loggeduserID);
        List<List<String>> type = esql.executeQueryAndReturnResult(managerQuery);
		String user_type = type.get(0).get(0);
		String utype = user_type.trim();
		String managerstr = "manager";
        // Ensure manager is accessing function
        if (stringCompare(managerstr, utype) == 0) {
            // Get store ID
			System.out.print("Enter storeID:");
			String store_id = in.readLine();
			// Compare store ID against all stores that are run by manager
			String query =  String.format("SELECT * FROM Store Where managerID = '%s' AND storeID = '%s'", loggeduserID, store_id);
                        int valid_storeid = esql.executeQuery(query);
            // Check if store is run by manager
			if (valid_storeid  > 0 ){
                String viewPopularProductsQuery = String.format("SELECT O.productName, sum(O.unitsOrdered) as TotProductOrdered FROM Orders O, Store S WHERE S.managerID='%s' AND O.storeID='%s' GROUP BY O.productName ORDER BY TotProductOrdered DESC", loggeduserID, store_id);
                List<List<String>> topProducts = esql.executeQueryAndReturnResult(viewPopularProductsQuery);
	 			int result5 = topProducts.size();
                    //Output the top 5 recent (less if there are less than 5 orders) updates
         			if(topProducts.size()>0){
                			for(int i=0; i<5 && result5>0; i++){
                				System.out.print("\n\tProduct Name: " + topProducts.get(i).get(0));
               					System.out.print("\n\tTotal Ordered: " + topProducts.get(i).get(1) + "\n");
					}
				}else{
	 				System.out.print("\n\tThere are no products.");
			}
			} else {
				System.out.print("This function is reserved for the manager of this store only. \n");
			}
		} else {
			System.out.print("This function is reserved for managers only. \n");
		}
   	} catch(Exception e) {
   		System.err.println (e.getMessage());
   	}
   }
   public static void viewPopularCustomers(Retail esql) {
    try{
        String managerQuery = String.format("SELECT type FROM Users WHERE userID = '%s'", loggeduserID);
        List<List<String>> type = esql.executeQueryAndReturnResult(managerQuery);
		String user_type = type.get(0).get(0);
		String utype = user_type.trim();
		String managerstr = "manager";
        // Ensure manager is accessing function
        if (stringCompare(managerstr, utype) == 0) {
            // Get store ID
			System.out.print("Enter storeID:");
			String store_id = in.readLine();
			// Compare store ID against all stores that are run by manager
			String query =  String.format("SELECT * FROM Store Where managerID = '%s' AND storeID = '%s'", loggeduserID, store_id);
                        int valid_storeid = esql.executeQuery(query);
            // Check if store is run by manager
			if (valid_storeid  > 0 ){
                String viewPopularCustomersQuery = String.format("SELECT O.customerID, COUNT(O.customerID) AS totalCustomers FROM Orders O, Store S WHERE S.managerID='%s' AND O.storeID=S.storeID GROUP BY O.customerID ORDER BY totalCustomers DESC", loggeduserID);
                List<List<String>> topCustomers = esql.executeQueryAndReturnResult(viewPopularCustomersQuery);
	 			int result5 = topCustomers.size();
                    //Output the top 5 recent (less if there are less than 5 orders) updates
         			if(topCustomers.size()>0){
                			for(int i=0; i<5 && result5>0; i++){
                				System.out.print("\n\tCustomer Name: " + topCustomers.get(i).get(0));
               					System.out.print("\n\tTotal Orders: " + topCustomers.get(i).get(1) + "\n");
					}
				}else{
	 				System.out.print("\n\tThere are no customers for this store.");
			}
			} else {
				System.out.print("This function is reserved for the manager of this store only. \n");
			}
		} else {
			System.out.print("This function is reserved for managers only. \n");
		}
   	} catch(Exception e) {
   		System.err.println (e.getMessage());
   	}
   }
   public static void placeProductSupplyRequests(Retail esql) {
	try {
         String managerQuery = String.format("SELECT type FROM Users WHERE userID = '%s'", loggeduserID);
         List<List<String>> type = esql.executeQueryAndReturnResult(managerQuery);
		   String user_type = type.get(0).get(0);
		   String utype = user_type.trim();
		   String managerstr = "manager";
         if (stringCompare(managerstr, utype) == 0) {
            System.out.print("\tEnter storeID:");
            String store_id = in.readLine();
            String query =  String.format("SELECT * FROM Store Where managerID = '%s' AND storeID = '%s'", loggeduserID, store_id);
                  int valid_storeid = esql.executeQuery(query);
            if (valid_storeid  > 0 ){
               System.out.print("\n\tEnter name of product you want: ");
               String prod_name = in.readLine();
               String productQuery = String.format("SELECT p.productName FROM Product P WHERE storeID='%s' AND productName='%s'",store_id, prod_name);
                    List<List<String>> prod_result = esql.executeQueryAndReturnResult(productQuery);
       
	       if(prod_result.size() > 0){
		String productStatus = String.format("SELECT p.productName, p.numberOfUnits FROM Product P WHERE storeID='%s' AND productName='%s'", store_id, prod_name);
                    List<List<String>> productBeforeResult = esql.executeQueryAndReturnResult(productStatus);
		System.out.print("\n\tProduct " + productBeforeResult.get(0).get(0) + " currently has " + productBeforeResult.get(0).get(1) + " units before order.\n");		     
               
		System.out.print("\n\tEnter number of units you want of this product: ");
               String num_of_units = in.readLine();

               System.out.print("\n\tEnter warehouseID:");
               String warehouse_id = in.readLine();
               String warehouse_query =  String.format("SELECT warehouseID  FROM Warehouse Where warehouseID = '%s'", warehouse_id);
                  int valid_warehouseid = esql.executeQuery(warehouse_query);
               	  //System.out.print("\n Warehouse " + valid_warehouseid + " is valid.");
	       		if(valid_warehouseid > 0){ 
                     		System.out.print("\n\tYou have successfully ordered item. Thank you. ");
                     		String w_query = String.format("UPDATE Product SET numberOfUnits=numberOfUnits+'%s' WHERE storeID='%s' AND productName='%s'", num_of_units, store_id, prod_name);	
                     		esql.executeUpdate(w_query);
                        
                     		query = String.format("INSERT INTO ProductSupplyRequests(managerID,warehouseID,storeID,productName, unitsRequested) VALUES('%s', '%s', '%s', '%s', '%s')",loggeduserID, warehouse_id, store_id, prod_name,num_of_units);
                     		esql.executeUpdate(query);
                 	
		     		//String productAfter = String.format("SELECT p.productName, p.numberOfUnits FROM Product P WHERE storeID='%s' AND productName='%s'", store_id, prod_name);
                     		List<List<String>> productAfterResult = esql.executeQueryAndReturnResult(productStatus);
                     		System.out.print("\n\tProduct " + productAfterResult.get(0).get(0) + " currently has " + productAfterResult.get(0).get(1) + " units after order.\n"); 
               		} else {
                  	System.out.print("This warehouse does not exist. \n");
	       		} 
		} else {
		  System.out.print("This product does not exist. \n");
	        }
             } else {
               System.out.print("This function is reserved for the manager of this store only. \n");
               }  
	} else {
            System.out.print("This function is reserved for managers only. \n");
         }
	} catch(Exception e) {
         System.err.println (e.getMessage());
      }  
   }          
   
   public static void updateProductAllStores(Retail esql) {
        try{
                String adminQuery = String.format("SELECT type FROM Users WHERE userID = '%s'", loggeduserID);
                List<List<String>> type = esql.executeQueryAndReturnResult(adminQuery);
                String user_type = type.get(0).get(0);
                String utype = user_type.trim();
                String managerstr = "admin";
                if (stringCompare(managerstr, utype) == 0) {
                        System.out.print("Enter storeID:");
                        String store_id = in.readLine();

                        String query =  String.format("SELECT * FROM Store Where storeID = '%s'", store_id);
                        int valid_storeid = esql.executeQuery(query);
                        if (valid_storeid  > 0 ){
                                String query2 =  String.format("SELECT * FROM Product Where storeID = '%s'",store_id);
                                esql.executeQueryAndPrintResult(query2);
                                System.out.println("Choose product to update");
                                String prod = in.readLine();
                                System.out.println(".........................");
                                System.out.println("Choose what to update");
                                System.out.println("1. Number of Units");
                                System.out.println("2. Price Per Units");
                                System.out.println(".........................");
                                int c = readChoice();
                                switch(c){
                                        case 1:
                                                System.out.println("Enter New Number of Units: ");
                                                int newUnits = readChoice();
                                                System.out.println("Enter warehouse ID to place order from:");
						int inputWID = readChoice();
						String warehouse_query =  String.format("SELECT warehouseID  FROM Warehouse Where warehouseID = '%s'",  inputWID);
                  				int valid_warehouseid = esql.executeQuery(warehouse_query);
						if ( valid_warehouseid > 0){
							String updateQuery = String.format("UPDATE Product SET numberOfUnits='%s' WHERE storeID='%s' AND productName='%s'", newUnits, store_id, prod);
                                                	esql.executeUpdate(updateQuery);
                                                	String updateQuery2 = String.format("INSERT INTO ProductUpdates(managerID,storeID,productName,updatedOn) VALUES('%s', '%s', '%s', CURRENT_TIMESTAMP)",loggeduserID, store_id, prod);
                                                	esql.executeUpdate(updateQuery2);
                                                } else {
							System.out.print("This warehouse does not exist. \n");	
						} break;
                                        case 2:
                                                System.out.println("Enter New Price Per Unit: ");
                                                int newPrice = readChoice();
                                                String updateQuery3 = String.format("UPDATE Product SET pricePerUnit='%s' WHERE storeID='%s' AND productName='%s'", newPrice, store_id, prod);
                                                esql.executeUpdate(updateQuery3);
                                                String updateQuery4 = String.format("INSERT INTO ProductUpdates(managerID,storeID,productName,updatedOn) VALUES('%s', '%s', '%s', CURRENT_TIMESTAMP)",loggeduserID, store_id, prod);
                                                esql.executeUpdate(updateQuery4);
                                                break;
                                }
                        } else {
                                System.out.print("This store does not exist. \n");
                        }
                } else {
                        System.out.print("This function is reserved for admin only. \n");
                }
        } catch (Exception e) {
                System.err.println(e.getMessage());
        }

   }

   public static void modifyUser(Retail esql){
	try{
	   	String adminQuery = String.format("SELECT type FROM Users WHERE userID = '%s'", loggeduserID);
                List<List<String>> type = esql.executeQueryAndReturnResult(adminQuery);
                String user_type = type.get(0).get(0);
                String utype = user_type.trim();
                String managerstr = "admin";
                if (stringCompare(managerstr, utype) == 0) {
			System.out.print("Enter userID:");
                        String user_id = in.readLine();

                        String query =  String.format("SELECT * FROM Users Where userID = '%s'", user_id);
                        int valid_userid = esql.executeQuery(query);
                        if (valid_userid  > 0 ){
				System.out.println(".........................");
                                System.out.println("Choose what to modify");
                                System.out.println("1. Name");
                                System.out.println("2. Password");
				System.out.println("3. Latitude and Longitude");
                                System.out.println("4. Type");
				System.out.println(".........................");
                                int c = readChoice();
				switch(c){
                                        case 1:
						System.out.println("Enter New Name: ");
                                                String input = in.readLine();	
						String updateQuery1 = String.format("UPDATE Users SET name='%s' WHERE userID='%s'", input, user_id);										   esql.executeUpdate(updateQuery1);break;
					case 2:
						System.out.println("Enter New Password: ");
                                                String input0 = in.readLine();   
						String updateQuery2 = String.format("UPDATE Users SET password='%s' WHERE userID='%s'", input0, user_id);                                                                            esql.executeUpdate(updateQuery2);break;
					case 3:
						System.out.println("Enter New Latitude: ");
                                                String input1 = in.readLine();   
						System.out.println("Enter New Longitude: ");
                                                String input2 = in.readLine();
						String updateQuery3 = String.format("UPDATE Users SET latitude='%s' AND longitude = '%s' WHERE userID='%s'", input1, input2, user_id);                                              esql.executeUpdate(updateQuery3);break;
					case 4:
						System.out.println("Enter New User Type: ");
                                                String input4 = in.readLine();   
						String updateQuery4 = String.format("UPDATE Users SET type='%s' WHERE userID='%s'", input4, user_id);                                                                                esql.executeUpdate(updateQuery4);break;
				}
			} else {
				System.out.print("This user does not exist.\n");
			}			
		} else {
                        System.out.print("This function is reserved for admin only. \n");
                }
	} catch (Exception e) {
                System.err.println(e.getMessage());
        }

   }

   public static void viewProductsUsers(Retail esql){
	try{
		String adminQuery = String.format("SELECT type FROM Users WHERE userID = '%s'", loggeduserID);
                List<List<String>> type = esql.executeQueryAndReturnResult(adminQuery);
                String user_type = type.get(0).get(0);
                String utype = user_type.trim();
                String managerstr = "admin";
                if (stringCompare(managerstr, utype) == 0) {
			System.out.println(".........................");
                        System.out.println("Choose what to view");
			System.out.println("1. Find a specific user.");
			System.out.println("2. Find a specific user and their 3 recent orders.");
			System.out.println("3. Find all stores with x product.");
			System.out.println("4. Find all users that ordered x product.");
			System.out.println(".........................");
			int c = readChoice();
                        switch(c){
				case 1:
					System.out.println("Enter userID:");
					String input1 = in.readLine();
					String viewQuery1 = String.format("SELECT * FROM Users WHERE userID = '%s'", input1);
					List<List<String>> result1 = esql.executeQueryAndReturnResult(viewQuery1);
					if(result1.size() > 0){
						System.out.println("User Information: \n\t" + result1.get(0).get(1) + result1.get(0).get(2) + result1.get(0).get(3) + result1.get(0).get(4) + result1.get(0).get(5) + "\n");
					} else {
						System.out.print("\n\tThere are no users with that ID.");
					}
					break; 	
				case 2:
					System.out.println("Enter userID:");
                                        String input2 = in.readLine();
					String viewQuery2 = String.format("SELECT O.productName,O.unitsOrdered,O.orderTime FROM Orders O WHERE customerID='%s' ORDER BY O.orderTime DESC LIMIT 5",input2);
					List<List<String>> topOrders = esql.executeQueryAndReturnResult(viewQuery2);
	                                int result3 = topOrders.size();
        	                        if(topOrders.size()>0){
                                        	for(int i=0; i<3 && result3>0; i++){
                                                	System.out.print("\n\tProduct Name: " + topOrders.get(i).get(0));
                                        	        System.out.print("\n\tUnits Ordered: " + topOrders.get(i).get(1));
                                                	System.out.print("\n\tOrder Time: " + topOrders.get(i).get(2) + "\n");
                                        	}
                	                }else{
                        	                System.out.print("\n\tThere are no orders placed by this user");
                                	}
					break;
				case 3:
					System.out.println("Enter product name: ");
					String input3 = in.readLine();
					String viewQuery3 = String.format("SELECT S.storeID, S.name FROM Store S, Product P WHERE P.productName = '%s' AND S.storeID = P.storeID", input3);
					List<List<String>> storesL = esql.executeQueryAndReturnResult(viewQuery3);
					if(storesL.size() > 0){
						System.out.print("\n\tStores with this product are:\n");
						for(int i=0; i < storesL.size(); i++){
							System.out.print("\n\t Store ID: " + storesL.get(i).get(0));
							System.out.print("\n\t Store Name: " + storesL.get(i).get(1) + "\n");
						}
					} else { System.out.println("This product does not exist in any store."); }			 
					break;
				case 4:
					System.out.println("Enter product name: ");
                                        String input4 = in.readLine();
                                        String viewQuery4 = String.format(" SELECT U.userID, U.name FROM Users U, Orders O WHERE U.userID = O.customerID AND O.productName = '%s'", input4);
					List<List<String>> customers = esql.executeQueryAndReturnResult(viewQuery4);
					if(customers.size() > 0){
						System.out.print("\n\tSCustomers that ordered this product are:\n");
                                                for(int i=0; i < customers.size(); i++){
                                                        System.out.print("\n\t User ID: " + customers.get(i).get(0));
                                                        System.out.print("\n\t Customer Name: " + customers.get(i).get(1) + "\n");
                                                }
					} else { System.out.println("No one has ordered this product.\n"); }
					break;

			}
		} else {
                        System.out.print("This function is reserved for admin only. \n");
                }
	} catch (Exception e) {
                System.err.println(e.getMessage());
        }	
   }

}//end Retail

