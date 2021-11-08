package model;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.sql.DriverManager;
import util.DBUtil;

public class Users {
    private ResultSet rs = null;
    private PreparedStatement pstmt = null;
    private HashMap<String, String> users;
    
    String connectionURL = "jdbc:mysql://localhost:3306/cs3160_coursework";
    
    public Users() {
        users = new HashMap<String, String>();
    }

    //checks login credentials to see if the username and password is valid
    public int isValid(String name, String pwd) {
       
        try {
        	
        	//Attempts to connect to the database
        	Connection connection = DBUtil.getConnection();
			
        		//if there was a successful connection
	            if (connection != null) {
	               //TODO: implement this method so that if the user does not exist, it returns -1.
	               // If the username and password are correct, it should return the 'clientID' value from the database.
	            	
	            	String query = "SELECT * FROM clients WHERE username = ? AND password = ?";
	            	//This statement prepares the query and prevents SQL injection
	            	PreparedStatement ps = connection.prepareStatement(query);
	            	
	            	//This will pass the arguments to the query highlighted in '?'
	            	//The number refers to the order the argument is passed.
	            	//1 means the username gets the name and 2 means the password gets the pwd.
	            	ps.setString(1, name);
	            	ps.setString(2, pwd);
	            	//Query is executed and result set is to be returned
	            	rs = ps.executeQuery();
	            	
	            	//Returns the data record by record
	            	//If there is a record which matches the query, this will be added in the HashMap
	            	if(rs.next()) {
	            		users.put(name, pwd);
	            	}
	            	
	    			ResultSet rs1 = null;
	    			//Returning the clientid that refers to the supplied username and password
	    			String query1 = "SELECT clientid FROM clients WHERE username LIKE ? AND password LIKE ?";
	    			ps = connection.prepareStatement(query1);
	    			ps.setString(1, name);
	            	ps.setString(2, pwd);;
	    			rs1 = ps.executeQuery();
	    			int id = 0;
	    			
	    			if(rs1.next()) {
	    				//Gets the clientid and stores it in a separate variable called id
	    				int clientid = rs1.getInt("clientid");
	    				id = clientid;
	    			}
	    			
	    			//Checks if the name and password are in the hashmap and returns the id of that user if so
	    			//otherwise -1 is returned which is not an id in the database
	    			if(users.get(name) != null && users.get(name).equals(pwd)) {
	    				return id;
	    			
	    			} else {
	    				return -1;
	    			}
	            } 
	            
        } catch(Exception e) {    
            System.out.println("Exception is ;"+ e + ": message is " + e.getMessage());
            return -1;
        }
		return -1;   
    }
    
    // TODO  add a user with specified username and password
    public void addUser(String name, String pwd) {
       
        //TODO: implement this method so that the specified username and password are inserted into the database.

         try {
        	//Attempts to connect to the database
            Connection connection = DBUtil.getConnection();
    
            if (connection != null) {
            	//This query will insert the new username and password
                String query = "INSERT INTO clients (username, password) VALUES (?, ?)";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setString(1, name);
                ps.setString(2, pwd);
                
                //Executes the query and different to executeQuery because we are manipulating data in executeUpdate
                //executeQuery retrieves data
                int result1 = ps.executeUpdate();
                
                //Placed in the users hashmap
                if(rs.next()) {
                	users.put(name, pwd);
                }
            }
         }
            catch(Exception e) {
                System.out.println("Exception is ;"+e + ": message is " + e.getMessage());  
         }
    }
    
    //Function to check if the username is already in the database
    public boolean checkUsernameExists(String name) {
    	
    	try {
    		//Attempts to connect to the database
    		Connection connection = DBUtil.getConnection();
    		
    		try {
    			if(connection != null) {
    				
    				//Query to retrieve username that is equal to the supplied username
    				String query = "SELECT username FROM clients WHERE username = ?";
    				PreparedStatement ps = connection.prepareStatement(query);
    				ps.setString(1, name);
    				rs = ps.executeQuery();
    			
    				if(rs.next()) {
    					//If the username is found
    					return true;
    				} else {
    					//If the username is not found
    					return false;
    				}
    			}
    			connection.close();
    			
    		} catch(SQLException e) {
                System.out.println("Exception is ;"+e + ": message is " + e.getMessage());
            }
    		
    	} catch (Exception e) {
    		System.out.println("Exception is ;"+e + ": message is " + e.getMessage());
    	}
    	return false;
    }
}
