#Quizzitch

This Alexa skill is a template for a quiz game skill called Quizzitch. Alexa will ask one or two users to translate german words or sentences into english words.

Necessities

- Amazon Alexa Account
- Tomcat (download Apache Tomcat at https://tomcat.apache.org/download-90.cgi and save it to a chosen directory)
- ngrok (https://ngrok.com/)
- Eclipse (download and install the IDE Eclipse or Anaconda at https://www.eclipse.org/downloads/ or https://docs.anaconda.com/anaconda/install/)
- SQLite or DBrowser for SQLite (download at https://sqlitebrowser.org/dl/)
- SQLite JDBC Driver: is already added as a dependency in the xml-file 'pom.xml' in the 'Praxisprojekt-master'.

First, you need to create a localhost using Apache Tomcat. On Mac Os X, this can be done as follows in the terminal:

- cd apche-tomcat-9.0.12 (may differ in case your Tomcat file is named differently)
- cd bin/
- chmod +x *.sh
- ./startup.sh

Check whether or not Tomcat has started properly by writing 'localhost:8080' into your browser. the Apache server is running if you can see an Apache Tomcat interface.

Now you need to build a secure introspectable tunnel to your localhost using ngrok. To do so, go to https://ngrok.com/ and follow the instructions. Remember your localhost is on port 8080, so you need to run the following text to start a tunnel on port 8080: ./ngrok http 8080

Skill Setup

1. Download the file 'Praxisprojekt-master'.
2. Save file 'Praxisprojekt-master' to a chosen directory.
3. Open Eclipse and 'Import' the existing Maven Project 'Praxisprojekt-master'.
4. Update Project: Right mouse click on the project in the left toolbar (de.unidue.ltl.ourAlexaExample) > Maven > Update Project > Ok.
5. Maven Install: Right mouse click on the project in the left toolbar (de.unidue.ltl.ourAlexaExample) > Run As > Maven install.
6. Open Folder 'target' in file 'Praxisprojekt-master' and copy war-file 'myskill.war' to the folder 'webapps' in your Tomcat file.
7. Open your Amazon Alexa Account and create a skill. Give it a name of your choice, the model should be 'Custom' and the method to host your skill's backend resources should be 'Provision your own'. Most importantly, you need to give your skill an invocation name and set a web service endpoint to handle skill requests. The invocation name is up to you, but the endpoint must be the same as the one that is forwarded to you by ngrok. To do so, you need to set the Service Endpoint Type in the Alexa Developer Console to 'HTTPS', copy and paste the URL from ngrok to the free space in 'Default Region' and set it to 'My development endpoint is a sub-domain of a domain that has a wildcard certificate from a certificate authority'.

8. Copy and paste the following code into the JSON Editor in the Amazon Developer Console:

{ "interactionModel": { "languageModel": { "invocationName": "your chosen invocation name", "intents": [ { "name": "AMAZON.CancelIntent", "samples": [] }, { "name": "AMAZON.HelpIntent", "samples": [] }, { "name": "AMAZON.StopIntent", "samples": [] }, { "name": "AMAZON.NavigateHomeIntent", "samples": [] }, { "name": "Fallback", "slots": [ { "name": "anything", "type": "anything" } ], "samples": [ "{anything}" ] } ], "types": [ { "name": "anything", "values": [ { "name": { "value": "two words" } }, { "name": { "value": "whatever" } } ] } ] } } }

9. You can now save, build and test your model in the Alexa Developer Console.
10. If you want to use your skill with your Amazon Echo or Amazon Echo Plus, you need to go to https://alexa.amazon.com/ and activate it. You should now be able to start the skill by calling its invocation name.

Edit Code (Change or add range of themes)

Currently there are three databases used, two databases for phrases and antonyms, and one database for vocabulary. The vocabulary database is subdivided into more than 30 themes of which 8 are specified/used in the given code, meaning they are defined as a possible User Intent.
The 8 themes plus phrases and antonyms can be selected specifically by the user during the use of the application.
You can add other themes of your choice that are included in the database by adding their name as a User Intent in the class 'public static enum UserIntent' and adding a switch case to the function 'private String selectQuestion()'.
If you want to do so, you should check on the database first and look up all the currently possible themes.
If you want to add a theme in the code, it should look like this:

		case 12: try {
			logger.info("Try-Block");
			String sql = "SELECT * FROM Vokabelliste WHERE Thema LIKE 'insert a theme' ORDER BY RANDOM() LIMIT 1";
			ps = con.prepareStatement(sql); 
			rs = ps.executeQuery();
	    
			while(rs.next()) {
				question = rs.getString("de"); 
				correctAnswer = rs.getString("en");
				return question+correctAnswer;
	       
	    }
	      	  }
	  	   catch(SQLException e) {
	    //System.out.println(e.toString());
	  }

