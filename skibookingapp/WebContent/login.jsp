<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <title>Login / signup page</title>
    	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js" type="text/javascript"></script>
    	<script type="text/javascript">
    	$(document).ready(function(){
    		$("#newName").change(function(){
    			var newName = $("#newName").val();
    				$.ajax({
    					//Where the ajax call should occur
    					url:"http://localhost:8080/coursework/do/checkName",
    					//The request being made to the server
    					type:"POST",
    					//The username for the registration is the subject
    					data: {newName:newName},
    					dataType:"text",
    					//The callback function in which the server responds 
    					//to the client with the resultant data 
    					success:function(data){
    						//Checks if username taken and the length of the username is less than 8 characters
    						
    						if(data == "username already taken" && newName.length <= 7){
    							//Prevents the client from being able to submit the registration of the new user
    							document.getElementById("canSubmit").disabled = true;
    							//Returns to the client side the length of the username and its elligibility
    							document.getElementById("nameproperties").innerHTML = "Username size is: " + newName.length + ", Your username must contain a minimum of 8 characters!";
    							//Parses the result from checkName action in Controller servlet
    							$("#available").html(data);
    							
    							//Checks if username taken and the length of the username is greater than 7 characters
    						} else if(data == "username already taken" && newName.length > 7){
    							document.getElementById("canSubmit").disabled = true;
    							document.getElementById("nameproperties").innerHTML = "Username size is: " + newName.length;
    							$("#available").html(data);
    							
    							//Checks if username available and the length of the username is less than 7 characters
    						} else if(data == "username available" && newName.length <= 7){
    							document.getElementById("canSubmit").disabled = true;
    							document.getElementById("nameproperties").innerHTML = "Username size is: " + newName.length + ", Your username must contain a minimum of 8 characters!";
    							$("#available").html(data);
    						
    							//Checks if username available and the length of the username meets the requirement
    						} else if(data == "username available" && newName.length > 7){
    							//Allows the client  to submit the registration of the new user
    							document.getElementById("canSubmit").disabled = false;
    							document.getElementById("nameproperties").innerHTML = "Username size is: " + newName.length + ", Your username is elligible!";
    							$("#available").html(data);
    						}
    					}
    				});
    		});
    	});
    	</script>
    </head>
    <body>
        <h2>Please log in!</h2>
        <!-- Invokes the login action to allow a client to log onto the site -->
        <form method="POST" action="http://localhost:8080/coursework/do/login">
                Username:<input type="text" name="username" value="" />----
                Password:<input type="password" name="password" value="" />        
        <input type="submit" value="Click to log in" />
        </form>
        
        <!-- Invokes the addUser action to register a new user -->
        <form method="POST" action="http://localhost:8080/coursework/do/addUser">
            <h2> Don't yet have an account? </h2>
            Username:<input id="newName" type="text" name="newUsername" value="" />----
                Password:<input type="password" name="newPassword" value="" />      
            <input type="submit" id="canSubmit" value="Sign up as a new user"/>
            <!-- The availability of the username -->
            <p id="available"></p>
            <!-- The username length -->
       		<p id="nameproperties"></p>
        </form>
        
    </body>
</html>
