package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.Lesson;
import model.LessonSelection;
import model.LessonTimetable;
import model.Users;
import util.DBUtil;


@WebServlet(value = "/do/*")
public class Controller extends HttpServlet {

   private Users users;
   private LessonTimetable availableLessons;

    public void init() {
         users = new Users();
         availableLessons = new LessonTimetable();
         // TODO Attach the lesson timetable to an appropriate scope
        
    }
    
    public void destroy() {
        
    }

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
    	//Returns the path passed to the servlet
    	//like the action the user has chosen
        String action = request.getPathInfo();
        System.out.println("action is " + action);
        
        //If the user is logging in
        if(action.equals("/login")) {
        	
        	//Gets the username entered in the input field in log in
        	String username = request.getParameter("username");
        	//Gets the password entered in the input field in log in
        	String password = request.getParameter("password");
        	//Checks if the username and password is valid and
        	//returns the clientid of that user if so
        	int ownerId = users.isValid(username, password);
        	
        	//If the clientid is returned
        	if(ownerId > -1) {
        		
        		//Creates a session
        		HttpSession session = request.getSession(true);
        		//Creates selection of lessons for the user logged in
        		LessonSelection user = new LessonSelection(ownerId);
        		//Adds the name of the user to the session so the view can retrieve the username
        		//This was used to help identify the user who is logged in
        		session.setAttribute("name", username);
        		//Adds the user to the session
        		session.setAttribute("user", user);
        		
        		//Displays the list of lessons that can be chosen
        		RequestDispatcher dispatcher = request.getRequestDispatcher("/LessonTimetableView.jspx");
        		dispatcher.forward(request, response);
        		
        	} else {
        		//Invalid login resulting in going back to the login page
        		RequestDispatcher dispatcher = request.getRequestDispatcher("/login.jsp");
        		dispatcher.forward(request,response);
        	}
        	
        	//Checks if the name supplied in the input field for signing up
        	//is already taken or available
        } else if(action.equals("/checkName")){
        	//Gets the value of the username being inputted for the sign up
        	String newUsername = request.getParameter("newName");
        	//Checks if the supplied username for the registration is already in the database
        	boolean check = users.checkUsernameExists(newUsername);
        	//If the username already exists
        	if(check) {
        		//Makes the browser return html content
        		response.setContentType("text/html");
        		//The object that will return content
        		PrintWriter out = response.getWriter();
        		//What the PrintWriter will print
        		out.print("username already taken");
        		
        		//If the username is available
        	} else {
        		response.setContentType("text/html");
        		PrintWriter out = response.getWriter();
        		out.print("username available");
        	}
        	
        	//Checks if the user clicked sign up button
        } else if (action.equals("/addUser")){
        	String newUsername = request.getParameter("newUsername");
        	String newPassword = request.getParameter("newPassword");
        	//Gets the username and password supplied in the register form
        	//and sends it to the client table
        	users.addUser(newUsername, newPassword);
        	
        	//Goes to view to indicate a successful registration was made
        	RequestDispatcher dispatcher = request.getRequestDispatcher("/successRegister.jspx");
        	dispatcher.forward(request, response);
        	
        } else {
        	//No session will need to be created
        	HttpSession session = request.getSession(false);
        	if(session == null || (session.getAttribute("user") == null)) {
        		//User hasn't logged in
        		//The session doesn't contain a LessonSelection so return to login view
        		RequestDispatcher dispatcher = request.getRequestDispatcher("/login.jsp");
        		dispatcher.forward(request, response);
        		
        	} else {
        		//Checks if the user selected a lesson
        		if(action.equals("/addLesson")) {
        			//Retrieves the selection of lessons from the session
        			LessonSelection user = (LessonSelection) session.getAttribute("user");
        			//Retrieves the lesson ID to be selected
        			String lessonId = request.getParameter("lessonId");
        			//Retrieves any object reference associated to the chosen lesson
        			Lesson chosen = user.getLesson(lessonId);
        			//Retrieves the object reference associated to the chosen lesson
        			Lesson item = this.availableLessons.getLesson(lessonId);
        			
        			if(chosen == null) {
        				//Checks if the lesson was already added
        				user.addLesson(item);
        			}
        			
        			//Goes to the view with the users lesson selection
            		RequestDispatcher dispatcher = request.getRequestDispatcher("/LessonSelection.jspx");
            		dispatcher.forward(request,response);
        			
        		} else if(action.equals("/viewAllLessons")) {
        			//Goes to the view with all lessons
        			RequestDispatcher dispatcher = request.getRequestDispatcher("/LessonTimetableView.jspx");
        			dispatcher.forward(request, response);
        			
        		} else if(action.equals("/viewSelectedLessons")) {
        			//Goes to the view with the users lesson selection
        			RequestDispatcher dispatcher = request.getRequestDispatcher("/LessonSelection.jspx");
        			dispatcher.forward(request, response);
        			
        			
        			//Checks if the user has finalised a lesson booking
        		} else if (action.equals("/finaliseBooking")) {
        			//Retrieves the selection of lessons from the session
        			LessonSelection user = (LessonSelection) session.getAttribute("user");
        			//Calls this function to send the new selection of lessons to the database
        			user.updateBooking();
        			
        			//Goes to the booking confirmation view
        			RequestDispatcher dispatcher = request.getRequestDispatcher("/bookingFinalised.jspx");
        			dispatcher.forward(request, response);
        			
        			//Checks if the user has logged out
        		} else if(action.equals("/logout")){
        			//Erases the session
        			session.invalidate();
        			
        			//Goes back to the login page
        			RequestDispatcher dispatcher = request.getRequestDispatcher("/login.jsp");
        			dispatcher.forward(request, response);
        			
        			//Checks if a lesson is to be removed
        		} else if(action.equals("/removeLesson")) {
        			//Retrieves the selection of lessons from the session
        			LessonSelection user = (LessonSelection) session.getAttribute("user");
        			//Gets the ID of the lesson to be deleted when clicking the cancel button
        			String chosenlessonId = request.getParameter("chosenlessonId");
        			Lesson chosen = user.getLesson(chosenlessonId);
        			
        			//Accesses the chosenLesson hashmap to remove a selected lesson
        			user.removeLesson(chosen);
        			
        			//Goes to the lesson selection view to show the current selection of lessons
            		RequestDispatcher dispatcher = request.getRequestDispatcher("/LessonSelection.jspx");
            		dispatcher.forward(request,response);
        		} 
        	}
        }
        
        
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
    	processRequest(request, response);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    	processRequest(request, response);
    	
    }


    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
