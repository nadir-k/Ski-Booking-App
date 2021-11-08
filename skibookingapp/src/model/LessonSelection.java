package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import util.DBUtil;

public class LessonSelection  {
    private HashMap<String, Lesson> chosenLessons;
    private int ownerID;
    private ResultSet rs = null;
    private Statement st = null;

    public LessonSelection(int owner) {
        
    	//Structure to hold the users selected lessons
        chosenLessons = new HashMap<String, Lesson>();
        this.ownerID = owner;

        try {
            Connection connection = DBUtil.getConnection();

             try {
                if (connection != null) {
                    // TODO get the details of any lessons currently selected by this user
                    // One way to do this: create a join query which:
                       // 1. finds rows in the 'lessons_booked' table which relate to this clientid
                       // 2. links 'lessons' to 'lessons_booked' by 'lessonid
                       // 3. selects all fields from lessons for these rows
                	
                	//This query will join the client and the lesson table together
                	//It allows the user to see the lessons they have selected in the session
                	String query = "SELECT c.clientid AS clientid, lb.lessonid, l.description, "
                			+ "l.level, l.startDateTime, l.endDateTime "
                			+ "FROM clients c "
                			+ "JOIN lessons_booked lb ON (c.clientid = lb.clientid) "
                			+ "JOIN lessons l ON (lb.lessonid = l.lessonid) "
                			+ "WHERE c.clientid = ?";
                
                	PreparedStatement ps = connection.prepareStatement(query);
                	//shows the lessons selected by specific user
                	ps.setInt(1, owner);
                	rs = ps.executeQuery();
                	
                    // If you need to test your SQL syntax you can do this in any of your MySql clients. 
                    
                    // For each one, instantiate a new Lesson object, 
                    // and add it to this collection (use 'LessonSelection.addLesson()' )
                	
                	//Goes through each lesson and returns properties of each selected lesso
                	//This will be added in the chosenLessons hashmap
                	while(rs.next()) {
                	
                		String description = rs.getString("description");
                		int level = rs.getInt("level");
                		Timestamp startDateTime = rs.getTimestamp("startDateTime");
                		Timestamp endDateTime = rs.getTimestamp("endDateTime");
                		String id = rs.getString("lessonid");
                		
                		//Gets all selected lesson properties and adds it into the chosenLesson hashmap
                		addLesson(new Lesson(description, startDateTime, endDateTime, level, id));            		
                	}
                	connection.close();
                }

            }catch(SQLException e) {
                System.out.println("Exception is ;"+ e + ": message is " + e.getMessage());
            }
        }catch(Exception e) {
            System.out.println("Exception is ;"+ e + ": message is " + e.getMessage());
        }
        
    }

    /**
     * @return the items
     */
    public Set <Entry <String, Lesson>> getItems() {
        return chosenLessons.entrySet();
    }

    public void addLesson(Lesson l) {
        Lesson i = new Lesson(l);
        this.chosenLessons.put(l.getId(), i);
    }
    
    //This function gets a lesson already selected
    //and removes it from the hashmap if the user wants to
    //cancel a lesson
    public void removeLesson(Lesson l) {
    	this.chosenLessons.remove(l.getId());
    }
    
    //This function is used to make sure the lesson is added in the hashmap
    //Doing this ensures the user can only add one instance of a specific lesson
    public boolean checkLessonExists(String lessonId) {
    	if(getLesson(lessonId) == null) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    public Lesson getLesson(String id){
        return this.chosenLessons.get(id);
    }
    
    public int getNumChosen(){
        return this.chosenLessons.size();
    }

    public int getOwner() {
        return this.ownerID;
    }
    
    //returns the hashmap of lessons already selected
    public HashMap<String, Lesson> getChosenLessons(){
    	return this.chosenLessons;
    }
    
    public void updateBooking() {
        
        // A tip: here is how you can get the ids of any lessons that are currently selected
        Object[] lessonKeys = chosenLessons.keySet().toArray();
        for (int i=0; i<lessonKeys.length; i++) {        
              // Temporary check to see what the current lesson ID is....
              System.out.println("Lesson ID is : " + (String)lessonKeys[i]);
        }
        
        // TODO get a connection to the database 
        // TODO In the database, delete any existing lessons booked for this user in the table 'lessons_booked'
        // REMEMBER to use executeUpdate, not executeQuery
        // TODO - write and execute a query which, for each selected lesson, will insert into the correct table:
                    // the owner id into the clientid field
                    // the lesson ID into the lessonid field
        
        try {
        	Connection connection = DBUtil.getConnection();
        	
        	try {
        		if(connection != null) {
        			
        			//This query deletes the current booked lessons.
        			//This query is needed to overwrite and insert the new booking of lessons a user has made
        			String query = "DELETE FROM lessons_booked WHERE clientid = ?";
        			PreparedStatement ps = connection.prepareStatement(query);
        			//The clientid will be passed in to delete the lessons they've already booked
        			ps.setInt(1, ownerID);
        			
        			//Data being manipulated not retrieved so the an executeUpdate is necessary
                	int result = ps.executeUpdate();
        			
                	//Iterates through the chosen lessons by their lessonid
                	//One by one, it will insert a lesson into the lesson_booked
                	//based on number of lessons user has chosen
                	for(int index = 0; index < lessonKeys.length; index++) {
                		//Query to insert new lesson booking
                		String query1 = "INSERT INTO lessons_booked(clientid, lessonid) VALUES (?, ?)";
                		ps = connection.prepareStatement(query1);
                		//sets the clientid for the first parameter in lessons_booked
                		ps.setInt(1, ownerID);
                		//sets the current index of the selected lesson by its ID
                		ps.setString(2, (String)lessonKeys[index]);
                		
                		//manipulates the database by inserting new entry of lessons a client has booked
                		int result1 = ps.executeUpdate();
                	}
                	connection.close();
        		}
        		
        	} catch(SQLException e){
        		System.out.println("Exception is ;"+ e + ": message is " + e.getMessage());
        	}
        } catch (Exception e){
        	System.out.println("Exception is ;"+ e + ": message is " + e.getMessage());
        }
    }
}
