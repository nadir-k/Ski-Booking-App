package model;

import java.util.HashMap;
import java.util.Map;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import javax.sql.DataSource;

import util.DBUtil;

public class LessonTimetable {
  private ResultSet rs = null;
  private Statement st = null;
  private Lesson lesson = null;
  private Map<String, Lesson> lessons = null;
  private DataSource ds = null;
    
    public LessonTimetable() {
        try {
            // Connect to the database - you can use this connection to 
            Connection connection = DBUtil.getConnection();
        
             try {

                if (connection != null) {

                    // TODO instantiate and populate the 'lessons' HashMap by selecting the relevant infromation from the database
                	
                	//Query to get all the lessons
                	String query = "SELECT * FROM lessons";
                	st = connection.createStatement();
                	rs = st.executeQuery(query);
                	lessons = new HashMap<String, Lesson>();
                    
                    // TODO add code here to retrieve the information and create the new Lesson objects
                	
                	//Repeatedly iterates through each record getting the lesson properties and adding them into lessons hashmap
                	//The lessons hashmap shows all the lessons that can be chosen
                        while(rs.next()) {
                        	
                        	//Gets description from the lesson
	                    	String description = rs.getString("description");
	                    	//Gets the level from the lesson
	                    	int level = rs.getInt("level");
	                    	//Gets the start time from the lesson
	                    	Timestamp startDateTime = rs.getTimestamp("startDateTime");
	                    	//Gets the end time from the lesson
	                    	Timestamp endDateTime = rs.getTimestamp("endDateTime");
	                    	//Gets the id from the lesson
	                    	String id = rs.getString("lessonid");
	                    	
	                    	//Creates a lesson and places it inside the HashMap
	                    	lessons.put(id, new Lesson(description, startDateTime, endDateTime, level, id));
                    	}
                    
                    connection.close();
                }

            }catch(SQLException e) {
                System.out.println("Exception is ;"+e + ": message is " + e.getMessage());
            }
        
          }catch(Exception e) {
             System.out.println("Exception is ;"+e + ": message is " + e.getMessage());
          }
    }
  
    /**
     * @return the items
     */
    public Lesson getLesson(String itemID) {
        return (Lesson)this.lessons.get(itemID);
    }

    public Map<String, Lesson> getLessons() {
        return this.lessons;
    } 
}
