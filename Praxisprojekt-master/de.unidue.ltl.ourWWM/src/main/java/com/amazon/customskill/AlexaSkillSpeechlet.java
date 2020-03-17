/**
    Copyright 2014-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.

    Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance with the License. A copy of the License is located at

        http://aws.amazon.com/apache2.0/

    or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.amazon.customskill;

import java.sql.Connection;



import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;





import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.customskill.AlexaSkillSpeechlet.RecognitionState;
import com.amazon.customskill.AlexaSkillSpeechlet.UserIntent;
import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.speechlet.SpeechletV2;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SsmlOutputSpeech;

/*import nlp.dkpro.backend.LinguisticPreprocessor;
import nlp.dkpro.backend.NlpSingleton;*/




/*
 * This class is the actual skill. Here you receive the input and have to produce the speech output. 
 */
public class AlexaSkillSpeechlet
implements SpeechletV2
{
	//Connector zur Verbindung des Java Projektes mit der SQLite Datenbank
	public static Connection connect() {
	    Connection con = null; 
	    try {
	      Class.forName("org.sqlite.JDBC");
	      con = DriverManager.getConnection("jdbc:sqlite:/Users/raphaelstedler/Desktop/Praxisprojekt-master/de.unidue.ltl.ourWWM/Vokabeln.db"); // die Location der Datenbank einfügen
	      logger.info("Connected!");
	    } catch (ClassNotFoundException | SQLException e ) {
	      logger.info(e+"");
	    }
	    return con; 
	  }
	
	static Logger logger = LoggerFactory.getLogger(AlexaSkillSpeechlet.class);

	public static String userRequest;

	public static int questions = 1;
	private static int sum;
	private static int sum2;
	private static int sum3;
	private static int sum4;
	private static int sum5;
	private static int sum6;
	private static int sum7;
	private static int sum8;
	private static int sum9;
	private static int sum777;
	private static String question = "";
	//private static String question2 = "";
	//private static String antonym = "";
	private static String Answer = "i don't know";
	private static String correctAnswer = "";
	//private static String correctAnswer2 = "";
	public static enum RecognitionState {SingleThemes, MultiThemes,YesNoQuizLevelEnd, YesNoQuizLevelOne, YesNoQuizLevelTwo, YesNoQuizLevelThree, YesNoVokabelnEasy, YesNoVokabelnBasics, YesNoVokabelnHard, AnswerQuizLevelOne, AnswerQuizLevelTwo, AnswerQuizLevelThree, AnswerVokabelnEasy, Answer, AnswerTwo, AnswerThree, AnswerFour, AnswerFive, AnswerSix, AnswerSeven, YesNo, YesNoTwo, YesNoLevel, YesNoLevelTwo, OneTwo, VokabelQuiz, Vokabel, WhichPlayer, WhichPlayerThree, WhichPlayerFour, AgainOrMenu, resumequizzen, SingleQuiz, YesNoQuiz, YesNoVokabeln, AnswerVokabeln, AnswerQuiz};
	private RecognitionState recState;
	public static enum UserIntent {rules, phrases, adjectives, time, colours, verbs, antonyms, food, animals, answer, again, vocab, levelone, leveltwo, themes, onne, twwo, menu, playerone, playertwo, vocabulary, quiz, resume, no, quit, any, basics, expressions, nextlevel, Error, Quiz};
	UserIntent ourUserIntent;

	static String welcomeMsg = "Hello and welcome at Quizzitch. How many players want to play,";
	static String singleMsg = "You're in single mode. Do you want to train vocabulary first or start a quiz?";	
	static String ThemeMsg = "You can choose between different themes. For example phrases, adjectives, verbs or antonyms. If you want to know all possible areas, say themes, if you want to train all vocabulary, say any word. If you don´t know an answer, say I don't know.";
	static String singleQuizMsg = "Welcome to the single quiz mode. In case you don´t know the answer, say I don't know and the answer will be given without loosing points. Let's begin!";
	static String multiMsg = "You're in two player mode. Level one is up, collecting points! You can get the rules or start directly.";
	static String multiRulesMsg = "Please clarify who wants to be player one and who wants to be player two. If you think you know the correct answer, say you're player number. You will get points if your answer is correct. If nobody knows the answer, say I don't know. You can choose from the different themes or get questions from all areas.";
	static String antonymMsg = "What's the antonym of ";
	static String wrongMsg = "That's wrong. The correct answer would be";
	static String wrongVocMsg = "That's wrong. The correct answer would be";
	static String dontknowMsg = "What a pity. The correct answer would be";
	static String correctAnswerMsg = "The correct answer would be ";
	static String continueMsg = "Continue?";
	static String correctMsg = "Correct!";
	static String congratsMsg = "Congratulations! You've won one {replacement} points.";
	static String goodbyeMsg = "I hope to hear from you soon, good bye!";
	static String sumMsg = "You've got {replacement} points. ";
	static String sumTwoMsg = "The score is {replacement3} ";
	static String sumThreeMsg = "to {replacement5}.";
	static String sumLifesTwoMsg = "Player one has {replacement3} lifes ";
	static String sumLifesThreeMsg = "and Player two {replacement5} lifes.";
	static String errorYesNoMsg = "Sorry, I did not understand that. Please say resume or quit.";
	static String errorAgainOrMenuMsg = "Sorry I did not understand that. Please say menu, again or quit.";
	static String errorAnswerMsg = "Sorry I did not understand that. Please mention your answer again.";
	static String errorOneTwoMsg = "Unfortunately I did not understand that. Please say one or two.";
	static String errorVokabelQuizMsg = "Unfortunately I did not understand that. Say vocabulary or quiz.";
	static String errorVokabelMsg = "Do you want to train your vocabulary in a certain area? If so, choose one or continue with all areas.";
	static String errorSpielereinszweiMsg = "Which player knows the correct answer?";
	static String SpielerEinsMsg = "Player one was faster. What's your answer?";
	static String SpielerEinsKurzMsg = "Player one?";
	static String SpielerZweiMsg = "Player two was faster. What's your answer?";
	static String SpielerZweiKurzMsg = "Player two?";
	static String continueLevelMsg = "Level two is up. Do you want to continue?";
	static String continueLevelTwoMsg = "Level three is up. Do you want to continue?";
	static String continueEinzelQuizLevelTwoMsg = "Congratulations! You've accomplished Level one. Do you want to jump to the second level?";
	static String continueEinzelQuizLevelThreeMsg = "Congratulations! You've accomplished Level two. Do you want to jump to the third level?";
	static String continueEinzelQuizEndMsg = "Congratulations! You made it all the way up! You've accomplished all levels! Do you want to resume the quiz, change level or go back to the menu?";
	static String playerOneWins = "Player one has won this round.";
	static String playerTwoWins = "Player two has won this round.";
	static String playerOneWinsGame = "Player one has won the game.";
	static String playerTwoWinsGame = "Player two has won the game.";
	static String againOrMenuMsg = "Do you want to play again, go back to the menu or quit?";
	static String resumequizzenMsg = "Do you want to play a quiz instead or quit the app??";
	static String errorresumequizzen = "Sorry I did not understand that. Please say quiz or quit the app.";
	static String resumeVokabelnMsg = "Do you want to the some vocabulary instead or quit the app?";
	static String ThemesMsg = "The following themes are possible: basics, expressions, food, animals, verbs, colours, time, adjectives, antonyms and phrases. If you wanna learn vocabulary from a certain area, say the corresponding word.";
	
	//Veränderte Punktzahlen
	private String buildString(String msg, String replacement1, String replacement2) {
		return msg.replace("{replacement}", replacement1).replace("{replacement2}", replacement2);
	}
	
	private String buildString2(String msg, String replacement3, String replacement4) {
		return msg.replace("{replacement3}", replacement3).replace("{replacement4}", replacement4);
	}
	
	private String buildString3(String msg, String replacement5, String replacement6) {
		return msg.replace("{replacement5}", replacement5).replace("{replacement6}", replacement6);
	}


	@Override
	public void onSessionStarted(SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope)
	{
		//Alle Summen zu Beginn auf Null gesetzt
		logger.info("Alexa session begins");
		sum = 0;
		sum2 = 0;
		sum3 = 0;
		sum4 = 0;
		sum5 = 0;
		sum6 = 5;
		sum7 = 5;
		sum8 = 0;
		sum9 = 0;
		sum777 = 0;
		recState = RecognitionState.OneTwo;
	}

	@Override
	public SpeechletResponse onLaunch(SpeechletRequestEnvelope<LaunchRequest> requestEnvelope)
	{
		// Willkommensnachricht
		return responseWithFlavour(welcomeMsg, 5);
		
	}
	
	
	// Funktion für den Aufruf von Fragen
	private String selectQuestion() {
		
		Answer = "i don't know";
		
		Connection con = AlexaSkillSpeechlet.connect(); 
		  PreparedStatement ps = null; 
		  ResultSet rs = null; 

		switch(questions){  
		case 1: try {
			 logger.info("Try-Block");
			 
		    String sql = "SELECT * FROM Vokabelliste ORDER BY RANDOM() LIMIT 1";
		    ps = con.prepareStatement(sql); 
		    rs = ps.executeQuery();
		    
		    while(rs.next()) {
		      /*int number = rs.getInt("number");*/
		   //  responseWithFlavour3(question);
		     question = rs.getString("de"); 
		     correctAnswer = rs.getString("en");
		     String Thema = rs.getString("Thema");
		     return question+correctAnswer;
		       
		    }
		      	  }
		  	   catch(SQLException e) {
		    //System.out.println(e.toString());
		  } 
		
		case 2: try {
			logger.info("Try-Block");
		 
			String sql = "SELECT * FROM Vokabelliste WHERE Thema LIKE 'Grundlagen' ORDER BY RANDOM() LIMIT 1";
			ps = con.prepareStatement(sql); 
			rs = ps.executeQuery();
	    
			while(rs.next()) {
				/*int number = rs.getInt("number");*/
				question = rs.getString("de"); 
				correctAnswer = rs.getString("en");
				String Thema = rs.getString("Thema");
				return question+correctAnswer;
	       
	    }
	      	  }
	  	   catch(SQLException e) {
	    //System.out.println(e.toString());
	  } 
		case 3: try {
			logger.info("Try-Block");
		 
			String sql = "SELECT * FROM Vokabelliste WHERE Thema LIKE 'Ausdrücke' ORDER BY RANDOM() LIMIT 1";
			ps = con.prepareStatement(sql); 
			rs = ps.executeQuery();
	    
			while(rs.next()) {
				/*int number = rs.getInt("number");*/
				question = rs.getString("de"); 
				correctAnswer = rs.getString("en");
				String Thema = rs.getString("Thema");
				return question+correctAnswer;
	       
	    }
	      	  }
	  	   catch(SQLException e) {
	    //System.out.println(e.toString());
	  }
		case 4: try {
			logger.info("Try-Block");
		 
			String sql = "SELECT * FROM Vokabelliste WHERE Thema LIKE 'Essen' ORDER BY RANDOM() LIMIT 1";
			ps = con.prepareStatement(sql); 
			rs = ps.executeQuery();
	    
			while(rs.next()) {
				/*int number = rs.getInt("number");*/
				question = rs.getString("de");   
				correctAnswer = rs.getString("en");
				String Thema = rs.getString("Thema");
				return question+correctAnswer;
	       
	    }
	      	  }
	  	   catch(SQLException e) {
	    //System.out.println(e.toString());
	  }
		case 5: try {
			logger.info("Try-Block");
		 
			String sql = "SELECT * FROM Vokabelliste WHERE Thema LIKE 'Tiere' ORDER BY RANDOM() LIMIT 1";
			ps = con.prepareStatement(sql); 
			rs = ps.executeQuery();
	    
			while(rs.next()) {
				/*int number = rs.getInt("number");*/
				question = rs.getString("de"); 
				correctAnswer = rs.getString("en");
				String Thema = rs.getString("Thema");
				return question+correctAnswer;
	       
	    }
	      	  }
	  	   catch(SQLException e) {
	    //System.out.println(e.toString());
	  }
		case 6: try {
			logger.info("Try-Block");
		 
			String sql = "SELECT * FROM gegenteile ORDER BY RANDOM() LIMIT 1";
			ps = con.prepareStatement(sql); 
			rs = ps.executeQuery();
	    
			while(rs.next()) {
				/*int number = rs.getInt("number");*/
				question = rs.getString("word"); 
				correctAnswer = rs.getString("antonym");
				// String Thema = rs.getString("Thema");
				return question+correctAnswer;
	       
	    }
	      	  }
	  	   catch(SQLException e) {
	    //System.out.println(e.toString());
	  }
		case 7: try {
			logger.info("Try-Block");
		 
			String sql = "SELECT * FROM Vokabelliste WHERE Thema LIKE 'Verben' ORDER BY RANDOM() LIMIT 1";
			ps = con.prepareStatement(sql); 
			rs = ps.executeQuery();
	    
			while(rs.next()) {
				/*int number = rs.getInt("number");*/
				question = rs.getString("de"); 
				correctAnswer = rs.getString("en");
				String Thema = rs.getString("Thema");
				return question+correctAnswer;
	       
	    }
	      	  }
	  	   catch(SQLException e) {
	    //System.out.println(e.toString());
	  }
		case 8: try {
			logger.info("Try-Block");
		 
			String sql = "SELECT * FROM Vokabelliste WHERE Thema LIKE 'Farben' ORDER BY RANDOM() LIMIT 1";
			ps = con.prepareStatement(sql); 
			rs = ps.executeQuery();
	    
			while(rs.next()) {
				/*int number = rs.getInt("number");*/
				question = rs.getString("de"); 
				correctAnswer = rs.getString("en");
				String Thema = rs.getString("Thema");
				return question+correctAnswer;
	       
	    }
	      	  }
	  	   catch(SQLException e) {
	    //System.out.println(e.toString());
	  }
		case 9: try {
			logger.info("Try-Block");
		 
			String sql = "SELECT * FROM Vokabelliste WHERE Thema LIKE 'Zeit' ORDER BY RANDOM() LIMIT 1";
			ps = con.prepareStatement(sql); 
			rs = ps.executeQuery();
	    
			while(rs.next()) {
				/*int number = rs.getInt("number");*/
				question = rs.getString("de"); 
				correctAnswer = rs.getString("en");
				String Thema = rs.getString("Thema");
				return question+correctAnswer;
	       
	    }
	      	  }
	  	   catch(SQLException e) {
	    //System.out.println(e.toString());
	  }
		case 10: try {
			logger.info("Try-Block");
		 
			String sql = "SELECT * FROM Vokabelliste WHERE Thema LIKE 'Adjektive' ORDER BY RANDOM() LIMIT 1";
			ps = con.prepareStatement(sql); 
			rs = ps.executeQuery();
	    
			while(rs.next()) {
				/*int number = rs.getInt("number");*/
				question = rs.getString("de"); 
				correctAnswer = rs.getString("en");
				String Thema = rs.getString("Thema");
				return question+correctAnswer;
	       
	    }
			}
		catch(SQLException e) {
		    //System.out.println(e.toString());
		} 	
		case 11: try {
			logger.info("Try-Block");
		 
			String sql = "SELECT * FROM phrasen10 ORDER BY RANDOM() LIMIT 1";
			ps = con.prepareStatement(sql); 
			rs = ps.executeQuery();
	    
			while(rs.next()) {
				/*int number = rs.getInt("number");*/
				question = rs.getString("field2"); 
				correctAnswer = rs.getString("field3");
				String Thema = rs.getString("Thema");
				return question+correctAnswer;
	       
	    }
	      	  }
	  	   catch(SQLException e) {
	    //System.out.println(e.toString());
	  }
	}
		  return null;
		  }


	@Override
	public SpeechletResponse onIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope)
	{
		IntentRequest request = requestEnvelope.getRequest();
		Intent intent = request.getIntent();
		userRequest = intent.getSlot("anything").getValue();
		logger.info("Received following text: [" + userRequest + "]");
		logger.info("recState is [" + recState + "]");
		SpeechletResponse resp = null;
		switch (recState) {
		case OneTwo: resp = evaluateOneTwo(userRequest); break;
		case VokabelQuiz: resp = evaluateVokabelQuiz(userRequest); break;
		case YesNo: resp = evaluateYesNo(userRequest); break;
		case WhichPlayer: resp = evaluateWhichPlayer(userRequest); break;
		case WhichPlayerThree: resp = evaluateWhichPlayerThree(userRequest); break;
		case WhichPlayerFour: resp = evaluateWhichPlayerFour(userRequest); break;
		case Vokabel: resp = evaluateVokabel(userRequest); break;
		case AnswerTwo: resp = evaluateAnswerTwo(userRequest); break;
		case AnswerThree: resp = evaluateAnswerThree(userRequest); break;
		case AnswerFour: resp = evaluateAnswerFour(userRequest); break;
		case AnswerFive: resp = evaluateAnswerFive(userRequest); break;
		case AnswerSix: resp = evaluateAnswerSix(userRequest); break;
		case AnswerSeven: resp = evaluateAnswerSeven(userRequest); break;
		case YesNoTwo: resp = evaluateYesNoTwo(userRequest); break;
		case YesNoLevel: resp = evaluateYesNoLevel(userRequest); break;
		case YesNoLevelTwo: resp = evaluateYesNoLevelTwo(userRequest); break;
		case AgainOrMenu: resp = evaluateAgainOrMenu(userRequest); break;
		case SingleQuiz: resp = evaluateSingleQuiz(userRequest); break;
		case SingleThemes: resp = evaluateSingleThemes(userRequest); break;
		case MultiThemes: resp = evaluateMultiThemes(userRequest); break;
		case YesNoQuizLevelOne: resp = evaluateYesNoQuizLevelOne(userRequest); break;
		case YesNoQuizLevelTwo: resp = evaluateYesNoQuizLevelTwo(userRequest); break;
		case YesNoQuizLevelThree: resp = evaluateYesNoQuizLevelThree(userRequest); break;
		case YesNoQuizLevelEnd: resp = evaluateYesNoQuizLevelEnd(userRequest); break;
		case YesNoVokabelnEasy: resp = evaluateYesNoVokabelnEasy(userRequest); break;
		case AnswerVokabelnEasy: resp = evaluateAnswerVokabelnEasy(userRequest); break;
		case AnswerQuizLevelOne: resp = evaluateAnswerQuizLevelOne(userRequest); break;
		case AnswerQuizLevelTwo: resp = evaluateAnswerQuizLevelTwo(userRequest); break;
		case AnswerQuizLevelThree: resp = evaluateAnswerQuizLevelThree(userRequest); break;
		default: resp = response("Erkannter Text: " + userRequest);
		}   
		return resp;
	}

	// Vokabelmodus: Nach Beantwortung einer Frage weitermachen?
	private SpeechletResponse evaluateYesNoVokabelnEasy(String userRequest) {	
		SpeechletResponse res = null;
		
		recognizeUserIntent(userRequest);
		switch (ourUserIntent) {
		case resume: {
			
			if (sum777 >= 1) {
				questions = 6;
				increaseQuestions();
				selectQuestion();
				res = responseWithFlavour(question, 11);
				recState = RecognitionState.AnswerVokabelnEasy; break;
			}
			
			else {
				increaseQuestions();
				selectQuestion();
				res = responseWithFlavour3(question);
				recState = RecognitionState.AnswerVokabelnEasy; break;
			}
				
		} case quit: {
			res = responseWithFlavour2(goodbyeMsg, 0); break;
		} case no: {
			res = responseWithFlavour2(goodbyeMsg, 0); break;
		} case menu: {
			res = responseWithFlavour(welcomeMsg, 5);
			recState = RecognitionState.OneTwo; break;
		} case themes: {
			questions = 1;
			res = askUserResponse(ThemesMsg);
			recState = RecognitionState.Vokabel; break;
		} case vocabulary: {
			questions = 1;
			res = askUserResponse(ThemeMsg);
			recState = RecognitionState.Vokabel; break;
			
		} default: {
			res = askUserResponse(errorYesNoMsg);
		}
		}
		return res;
	}
	
	// Vokabelmodus: Soll ein bestimmtes Thema abgefragt werden?
	private SpeechletResponse evaluateVokabel(String userRequest) {
		SpeechletResponse res = null;
		recognizeUserIntent(userRequest);
		switch (ourUserIntent) {
		case any: {
			questions = 1;
			increaseQuestions();
			selectQuestion();
			res = responseWithFlavour3(question);
			recState = RecognitionState.AnswerVokabelnEasy; break;
		} case basics: {
			questions = 2;
			increaseQuestions();
			selectQuestion();
			res = responseWithFlavour3(question);
			recState = RecognitionState.AnswerVokabelnEasy; break;
		} case expressions: {
			questions = 3;
			increaseQuestions();
			selectQuestion();
			res = responseWithFlavour3(question);
			recState = RecognitionState.AnswerVokabelnEasy; break;
		} case food: {
			questions = 4;
			increaseQuestions();
			selectQuestion();
			res = responseWithFlavour3(question);
			recState = RecognitionState.AnswerVokabelnEasy; break;
		} case animals: {
			questions = 5;
			increaseQuestions();
			selectQuestion();
			res = responseWithFlavour3(question);
			recState = RecognitionState.AnswerVokabelnEasy; break;
		} case antonyms: {
			questions = 6;
			antonymsselected();
			increaseQuestions();
			selectQuestion();
			res = responseWithFlavour(question, 11);
			recState = RecognitionState.AnswerVokabelnEasy; break;
		} case verbs: {
			questions = 7;
			increaseQuestions();
			selectQuestion();
			res = responseWithFlavour3(question);
			recState = RecognitionState.AnswerVokabelnEasy; break;
		} case colours: {
			questions = 8;
			increaseQuestions();
			selectQuestion();
			res = responseWithFlavour3(question);
			recState = RecognitionState.AnswerVokabelnEasy; break;
		} case time: {
			questions = 9;
			increaseQuestions();
			selectQuestion();
			res = responseWithFlavour3(question);
			recState = RecognitionState.AnswerVokabelnEasy; break;
		} case adjectives: {
			questions = 10;
			increaseQuestions();
			selectQuestion();
			res = responseWithFlavour3(question);
			recState = RecognitionState.AnswerVokabelnEasy; break;
		} case phrases: {
			questions = 11;
			increaseQuestions();
			selectQuestion();
			res = responseWithFlavour3(question);
			recState = RecognitionState.AnswerVokabelnEasy; break;
		} case themes: {
			res = askUserResponse(ThemesMsg);
			recState = RecognitionState.Vokabel; break;
		} case menu: {
			res = responseWithFlavour(welcomeMsg, 5);
			recState = RecognitionState.OneTwo; break;
		} default: {
			res = askUserResponse(errorVokabelMsg);
		}
		}
		return res;
	}

	
	// Einzelquiz Level 1: Nach Beantwortung einer Frage weitermachen?
	private SpeechletResponse evaluateYesNoQuizLevelOne(String userRequest) {	
		SpeechletResponse res = null;
		recognizeUserIntent(userRequest);
		switch (ourUserIntent) {
		
		 case resume: { 
			if (sum777 >= 1) {
				questions = 6;
				increaseQuestions();
				selectQuestion();
				res = responseWithFlavour(question, 11);
				recState = RecognitionState.AnswerQuizLevelOne; break;
			}
			
			else {
				increaseQuestions();
				selectQuestion();
				res = responseWithFlavour3(question);
				recState = RecognitionState.AnswerQuizLevelOne; break;
			}
				
		} case quit: {
			res = responseWithFlavour2(goodbyeMsg, 0); break;
		} case no: {
			res = responseWithFlavour2(goodbyeMsg, 0); break;
		} case menu: {
			res = responseWithFlavour(welcomeMsg, 5);
			recState = RecognitionState.OneTwo; break;
			
		} default: {
			res = askUserResponse(errorYesNoMsg);
		}
		}
		return res;
	}
	
	// Einzelquiz Level 2: Nach Beantwortung einer Frage weitermachen?
	private SpeechletResponse evaluateYesNoQuizLevelTwo(String userRequest) {	
		SpeechletResponse res = null;
		recognizeUserIntent(userRequest);
		switch (ourUserIntent) {
		case resume: {
			if (sum777 >= 1) {
				questions = 6;
				increaseQuestions();
				selectQuestion();
				res = responseWithFlavour(question, 11);
				recState = RecognitionState.AnswerQuizLevelTwo; break;
			}
			
			else {
				increaseQuestions();
				selectQuestion();
				res = responseWithFlavour3(question);
				recState = RecognitionState.AnswerQuizLevelTwo; break;
			}
			
		} case quit: {
			res = responseWithFlavour2(goodbyeMsg, 0); break;
		} case no: {
			res = responseWithFlavour2(goodbyeMsg, 0); break;
			
		} case menu: {
			res = responseWithFlavour(welcomeMsg, 5);
			recState = RecognitionState.OneTwo; break;
			
		} default: {
			res = askUserResponse(errorYesNoMsg);
		}
		}
		return res;
	}
	
	// Einzelquiz Level 3: Nach Beantwortung einer Frage weitermachen?
	private SpeechletResponse evaluateYesNoQuizLevelThree(String userRequest) {	
		SpeechletResponse res = null;
		recognizeUserIntent(userRequest);
		switch (ourUserIntent) {
		case resume: {
			if (sum777 >= 1) {
				questions = 6;
				increaseQuestions();
				selectQuestion();
				res = responseWithFlavour(question, 11);
				recState = RecognitionState.AnswerQuizLevelThree; break;
			}
			
			else {
				increaseQuestions();
				selectQuestion();
				res = responseWithFlavour3(question);
				recState = RecognitionState.AnswerQuizLevelThree; break;
			}
		
		} case quit: {
			res = responseWithFlavour2(goodbyeMsg, 0); break;
		} case no: {
			res = responseWithFlavour2(goodbyeMsg, 0); break;
			
		} case menu: {
			res = responseWithFlavour(welcomeMsg, 5);
			recState = RecognitionState.OneTwo; break;
			
		} default: {
			res = askUserResponse(errorYesNoMsg);
		}
		}
		return res;
	}
	
	// Einzelquiz Level 3: Spiel angeschlossen. Weitermachen, aufhören, in andere Level wechseln?
	private SpeechletResponse evaluateYesNoQuizLevelEnd(String userRequest) {	
		SpeechletResponse res = null;
		recognizeUserIntent(userRequest);
		switch (ourUserIntent) {
		case resume: {
			if (sum777 >= 1) {
				questions = 6;
				increaseQuestions();
				selectQuestion();
				res = responseWithFlavour(question, 11);
				recState = RecognitionState.AnswerQuizLevelThree; break;
			}
			
			else {
				increaseQuestions();
				selectQuestion();
				res = responseWithFlavour3(question);
				recState = RecognitionState.AnswerQuizLevelThree; break;
			}
			
		} case quit: {
			res = responseWithFlavour2(goodbyeMsg, 0); break;
		} case no: {
			res = responseWithFlavour2(goodbyeMsg, 0); break;
		} case levelone: {
			increaseQuestions();
			selectQuestion();
			res = responseWithFlavour3("Here we go:"+" "+question);
			recState = RecognitionState.AnswerQuizLevelOne; break;
		} case leveltwo: {
			increaseQuestions();
			selectQuestion();
			res = responseWithFlavour3("Here we go:"+" "+question);
			recState = RecognitionState.AnswerQuizLevelTwo; break;
			
		} case menu: {
			res = responseWithFlavour(welcomeMsg, 5);
			recState = RecognitionState.OneTwo; break;
			
		} default: {
			res = askUserResponse(errorYesNoMsg);
		}
		}
		return res;
	}
	
	
	//Einzelspieler: Weitermachen oder aufhören?
	private SpeechletResponse evaluateYesNo(String userRequest) {
		SpeechletResponse res = null;
		recognizeUserIntent(userRequest);
		switch (ourUserIntent) {
		case resume: {
			if (sum777 >= 1) {
				questions = 6;
				increaseQuestions();
				selectQuestion();
				res = responseWithFlavour(question, 11);
				recState = RecognitionState.Answer; break;
			}
			
			else {
				increaseQuestions();
				selectQuestion();
				res = responseWithFlavour3(question);
				recState = RecognitionState.Answer; break;
			}
			
		} case quit: {
			res = responseWithFlavour2(goodbyeMsg, 0); break;
		} case no: {
			res = responseWithFlavour2(goodbyeMsg, 0); break;
		} case menu: {
			res = askUserResponse(welcomeMsg);
			recState = RecognitionState.OneTwo; break;
		} default: {
			res = askUserResponse(errorYesNoMsg);
		}
		}
		return res;
	}
	
	// Mehrspieler Level 1: Nach Beantwortung einer Frage weitermachen?
	private SpeechletResponse evaluateYesNoTwo(String userRequest) {
		SpeechletResponse res = null;
		recognizeUserIntent(userRequest);
		switch (ourUserIntent) {
		case resume: {
			if (sum777 >= 1) {
				questions = 6;
				increaseQuestions();
				selectQuestion();
				res = responseWithFlavour(question, 11);
				recState = RecognitionState.WhichPlayer; break;
			}
			
			else {
				increaseQuestions();
				selectQuestion();
				res = responseWithFlavour3(question);
				recState = RecognitionState.WhichPlayer; break;
			}
			
		} case quit: {
			res = responseWithFlavour2(goodbyeMsg, 0); break;
		} case no: {
			res = responseWithFlavour2(goodbyeMsg, 0); break;
		} case menu: {
			res = responseWithFlavour(welcomeMsg, 5);
			recState = RecognitionState.OneTwo; break;
		} default: {
			res = askUserResponse(errorYesNoMsg);
		}
		}
		return res;
	}
	
	// Mehrspieler Level 2: Nach Beantwortung einer Frage weitermachen?
	private SpeechletResponse evaluateYesNoLevel(String userRequest) {
		SpeechletResponse res = null;
		recognizeUserIntent(userRequest);
		switch (ourUserIntent) {
		case resume: {
			if (sum777 >= 1) {
				questions = 6;
				increaseQuestions();
				selectQuestion();
				res = responseWithFlavour(question, 11);
				recState = RecognitionState.WhichPlayerThree; break;
			}
			
			else {
				increaseQuestions();
				selectQuestion();
				res = responseWithFlavour3(question);
				recState = RecognitionState.WhichPlayerThree; break;
			}
			
		} case quit: {
			res = responseWithFlavour2(goodbyeMsg, 0); break;
		} case no: {
			res = responseWithFlavour2(goodbyeMsg, 0); break;
		} case menu: {
			res = responseWithFlavour(welcomeMsg, 5);
			recState = RecognitionState.OneTwo; break;
		} default: {
			res = askUserResponse(errorYesNoMsg);
		}
		}
		return res;
	}
	
	// Mehrspieler Level 3: Nach Beantwortung einer Frage weitermachen?
	private SpeechletResponse evaluateYesNoLevelTwo(String userRequest) {
		SpeechletResponse res = null;
		recognizeUserIntent(userRequest);
		switch (ourUserIntent) {
		case resume: {
			if (sum777 >= 1) {
				questions = 6;
				increaseQuestions();
				selectQuestion();
				res = responseWithFlavour(question, 11);
				recState = RecognitionState.WhichPlayerFour; break;
			}
			
			else {
				increaseQuestions();
				selectQuestion();
				res = responseWithFlavour3(question);
				recState = RecognitionState.WhichPlayerFour; break;
			}
		
		} case quit: {
			res = responseWithFlavour2(goodbyeMsg, 0); break;
		} case no: {
			res = responseWithFlavour2(goodbyeMsg, 0); break;
		} case menu: {
			res = responseWithFlavour(welcomeMsg, 5);
			recState = RecognitionState.OneTwo; break;
		} default: {
			res = askUserResponse(errorYesNoMsg);
		}
		}
		return res;
	}
	
	// Mehrspieler Level 3: Nach Spielende nochmal spielen, aufhören oder ins Menü?
	private SpeechletResponse evaluateAgainOrMenu(String userRequest) {
		SpeechletResponse res = null;
		recognizeUserIntent(userRequest);
		switch (ourUserIntent) {
		case again: {
			sum = 0;
			sum2 = 0;
			sum3 = 0;
			sum4 = 0;
			sum5 = 0;
			sum6 = 5;
			sum7 = 5;
			sum8 = 0;
			sum9 = 0;
			sum777 = 0;
			increaseQuestions();
			selectQuestion();
			res = responseWithFlavour3(question);
			recState = RecognitionState.WhichPlayer; break;
		} case quit: {
			res = responseWithFlavour2(goodbyeMsg, 0); break;
		} case menu: {
			res = responseWithFlavour(welcomeMsg, 5);
			sum = 0;
			sum2 = 0;
			sum3 = 0;
			sum4 = 0;
			sum5 = 0;
			sum6 = 5;
			sum7 = 5;
			sum8 = 0;
			sum9 = 0;
			sum777 = 0;
			recState = RecognitionState.OneTwo; break;
		} default: {
			res = askUserResponse(errorAgainOrMenuMsg);
		}
		}
		return res;
	}
	
	// Ein oder zwei Spieler?
	private SpeechletResponse evaluateOneTwo(String userRequest) {
		SpeechletResponse res = null;
		recognizeUserIntent(userRequest);
		switch (ourUserIntent) {
		case onne: {
			sum = 0;
			sum2 = 0;
			sum3 = 0;
			sum4 = 0;
			sum5 = 0;
			sum6 = 5;
			sum7 = 5;
			sum8 = 0;
			sum9 = 0;
			sum777 = 0;
			res = askUserResponse(singleMsg);
			recState = RecognitionState.VokabelQuiz; break;
		} case twwo: {
			sum = 0;
			sum2 = 0;
			sum3 = 0;
			sum4 = 0;
			sum5 = 0;
			sum6 = 5;
			sum7 = 5;
			sum8 = 0;
			sum9 = 0;
			sum777 = 0;
			res = askUserResponse(multiMsg);
			recState = RecognitionState.MultiThemes; break;
		} default: {
			res = askUserResponse(errorOneTwoMsg);
		}
		}
		return res;
	}
	
	// Mehrspieler: Vokabeln allgemein oder bestimmtes Theme?
	private SpeechletResponse evaluateMultiThemes(String userRequest) {
		SpeechletResponse res = null;
		recognizeUserIntent(userRequest);
		switch (ourUserIntent) {
		case any: {
			questions = 1;
			increaseQuestions();
			selectQuestion();
			res = responseWithFlavour3(question);
			recState = RecognitionState.WhichPlayer; break;
		} case basics: {
			questions = 2;
			increaseQuestions();
			selectQuestion();
			res = responseWithFlavour3(question);
			recState = RecognitionState.WhichPlayer; break;
		} case expressions: {
			questions = 3;
			increaseQuestions();
			selectQuestion();
			res = responseWithFlavour3(question);
			recState = RecognitionState.WhichPlayer; break;
		} case food: {
			questions = 4;
			increaseQuestions();
			selectQuestion();
			res = responseWithFlavour3(question);
			recState = RecognitionState.WhichPlayer; break;
		} case animals: {
			questions = 5;
			increaseQuestions();
			selectQuestion();
			res = responseWithFlavour3(question);
			recState = RecognitionState.WhichPlayer; break;
		} case antonyms: {
			questions = 6;
			antonymsselected();
			increaseQuestions();
			selectQuestion();
			res = responseWithFlavour(question, 11);
			recState = RecognitionState.WhichPlayer; break;
		} case verbs: {
			questions = 7;
			increaseQuestions();
			selectQuestion();
			res = responseWithFlavour3(question);
			recState = RecognitionState.WhichPlayer; break;
		} case colours: {
			questions = 8;
			increaseQuestions();
			selectQuestion();
			res = responseWithFlavour3(question);
			recState = RecognitionState.WhichPlayer; break;
		} case time: {
			questions = 9;
			increaseQuestions();
			selectQuestion();
			res = responseWithFlavour3(question);
			recState = RecognitionState.WhichPlayer; break;
		} case adjectives: {
			questions = 10;
			increaseQuestions();
			selectQuestion();
			res = responseWithFlavour3(question);
			recState = RecognitionState.WhichPlayer; break;
		} case themes: {
			res = askUserResponse(ThemesMsg);
			recState = RecognitionState.MultiThemes; break;
		} case rules: {
			res = askUserResponse(multiRulesMsg);
			recState = RecognitionState.MultiThemes; break;
		} case menu: {
			res = responseWithFlavour(welcomeMsg, 5);
			recState = RecognitionState.OneTwo; break;
		} default: {
			res = askUserResponse(errorVokabelMsg);
		}
		}
		return res;
	}

	
	// Einzelspieler: Vokabeln oder Quiz?
	private SpeechletResponse evaluateVokabelQuiz(String userRequest) {
		SpeechletResponse res = null;
		recognizeUserIntent(userRequest);
		switch (ourUserIntent) {
		case vocabulary: {
			res = askUserResponse(ThemeMsg);
			recState = RecognitionState.Vokabel; break;
		} case quiz: {
			res = askUserResponse(ThemeMsg);
			recState = RecognitionState.SingleThemes; break;
		} case menu: {
			res = responseWithFlavour(welcomeMsg, 5);
			recState = RecognitionState.OneTwo; break;
		} default: {
			res = askUserResponse(errorVokabelQuizMsg);
		}
		}
		return res;
	}
	
	// Einzelspieler: Vokabeln allgemein oder bestimmtes Theme?
	private SpeechletResponse evaluateSingleThemes(String userRequest) {
		SpeechletResponse res = null;
		recognizeUserIntent(userRequest);
		switch (ourUserIntent) {
		case any: {
			questions = 1;
			increaseQuestions();
			selectQuestion();
			res = responseWithFlavour3(question);
			recState = RecognitionState.AnswerQuizLevelOne; break;
		} case basics: {
			questions = 2;
			increaseQuestions();
			selectQuestion();
			res = responseWithFlavour3(question);
			recState = RecognitionState.AnswerQuizLevelOne; break;
		} case expressions: {
			questions = 3;
			increaseQuestions();
			selectQuestion();
			res = responseWithFlavour3(question);
			recState = RecognitionState.AnswerQuizLevelOne; break;
		} case food: {
			questions = 4;
			increaseQuestions();
			selectQuestion();
			res = responseWithFlavour3(question);
			recState = RecognitionState.AnswerQuizLevelOne; break;
		} case animals: {
			questions = 5;
			increaseQuestions();
			selectQuestion();
			res = responseWithFlavour3(question);
			recState = RecognitionState.AnswerQuizLevelOne; break;
		} case antonyms: {
			questions = 6;
			antonymsselected();
			increaseQuestions();
			selectQuestion();
			res = responseWithFlavour(question, 11);
			recState = RecognitionState.AnswerQuizLevelOne; break;
		} case verbs: {
			questions = 7;
			increaseQuestions();
			selectQuestion();
			res = responseWithFlavour3(question);
			recState = RecognitionState.AnswerQuizLevelOne; break;
		} case colours: {
			questions = 8;
			increaseQuestions();
			selectQuestion();
			res = responseWithFlavour3(question);
			recState = RecognitionState.AnswerQuizLevelOne; break;
		} case time: {
			questions = 9;
			increaseQuestions();
			selectQuestion();
			res = responseWithFlavour3(question);
			recState = RecognitionState.AnswerQuizLevelOne; break;
		} case adjectives: {
			questions = 10;
			increaseQuestions();
			selectQuestion();
			res = responseWithFlavour3(question);
			recState = RecognitionState.AnswerQuizLevelOne; break;
		} case phrases: {
			questions = 11;
			increaseQuestions();
			selectQuestion();
			res = responseWithFlavour3(question);
			recState = RecognitionState.AnswerQuizLevelOne; break;
		} case themes: {
			res = askUserResponse(ThemesMsg);
			recState = RecognitionState.SingleThemes; break;
		} case menu: {
			res = responseWithFlavour(welcomeMsg, 5);
			recState = RecognitionState.OneTwo; break;
		} default: {
			res = askUserResponse(errorVokabelMsg);
		}
		}
		return res;
	}


	// Einzelspieler: Sind sie bereit?
	private SpeechletResponse evaluateSingleQuiz(String userRequest) {
		SpeechletResponse res = null;
		recognizeUserIntent(userRequest);
		switch (ourUserIntent) {
		case resume: {
			selectQuestion();
			res = responseWithFlavour3(question);
			recState = RecognitionState.AnswerQuiz; break;
		} 
		case no: {
			res = responseWithFlavour2(goodbyeMsg, 0); break;
		}
		default: {
			res = askUserResponse(errorVokabelMsg);
		}
		}
		return res;
	}
	
	// Mehrspieler Level 1: Welcher Spieler weiß die Antwort?
	private SpeechletResponse evaluateWhichPlayer(String userRequest) {
		SpeechletResponse res = null;
		recognizeUserIntent(userRequest);
		switch (ourUserIntent) {
		case playerone: {
			res = askUserResponse(SpielerEinsMsg);
			recState = RecognitionState.AnswerTwo; break;
		} case onne: {
			res = askUserResponse(SpielerEinsMsg);
			recState = RecognitionState.AnswerTwo; break;
		} case playertwo: {
			res = askUserResponse(SpielerZweiMsg);
			recState = RecognitionState.AnswerThree; break;
		} case twwo: {
			res = askUserResponse(SpielerZweiMsg);
			recState = RecognitionState.AnswerThree; break;
		} case menu: {
			res = responseWithFlavour(welcomeMsg, 5);
			recState = RecognitionState.OneTwo; break;
		} case answer: {
			res = askUserResponse(correctAnswerMsg+" "+correctAnswer+". "+continueMsg);
			recState = RecognitionState.YesNoTwo; break;	
			
		} default: {
			res = askUserResponse(errorSpielereinszweiMsg);
		}
		}
		return res;
	}
	
	// Mehrspieler Level 2: Welcher Spieler weiß die Antwort?
	private SpeechletResponse evaluateWhichPlayerThree(String userRequest) {
		SpeechletResponse res = null;
		recognizeUserIntent(userRequest);
		switch (ourUserIntent) {
		case playerone: {
			res = askUserResponse(SpielerEinsKurzMsg);
			recState = RecognitionState.AnswerFour; break;
		} case onne: {
			res = askUserResponse(SpielerEinsKurzMsg);
			recState = RecognitionState.AnswerFour; break;
		} case playertwo: {
			res = askUserResponse(SpielerZweiKurzMsg);
			recState = RecognitionState.AnswerFive; break;
		} case twwo: {
			res = askUserResponse(SpielerZweiKurzMsg);
			recState = RecognitionState.AnswerFive; break;
		} case menu: {
			res = responseWithFlavour(welcomeMsg, 5);
			recState = RecognitionState.OneTwo; break;
		} case answer: {
			res = askUserResponse(correctAnswerMsg+" "+correctAnswer+". "+continueMsg);
			recState = RecognitionState.YesNoLevel; break;	
		} default: {
			res = askUserResponse(errorSpielereinszweiMsg);
		}
		}
		return res;
	}
	
	// Mehrspieler Level 3: Welcher Spieler weiß die Antwort?
	private SpeechletResponse evaluateWhichPlayerFour(String userRequest) {
		SpeechletResponse res = null;
		recognizeUserIntent(userRequest);
		switch (ourUserIntent) {
		case playerone: {
			res = askUserResponse(SpielerEinsKurzMsg);
			recState = RecognitionState.AnswerSix; break;
		} case onne: {
			res = askUserResponse(SpielerEinsKurzMsg);
			recState = RecognitionState.AnswerSix; break;
		} case playertwo: {
			res = askUserResponse(SpielerZweiKurzMsg);
			recState = RecognitionState.AnswerSeven; break;
		} case twwo: {
			res = askUserResponse(SpielerZweiKurzMsg);
			recState = RecognitionState.AnswerSeven; break;
		} case menu: {
			res = responseWithFlavour(welcomeMsg, 5);
			recState = RecognitionState.OneTwo; break;
		} case answer: {
			res = askUserResponse(correctAnswerMsg+" "+correctAnswer+". "+continueMsg);
			recState = RecognitionState.YesNoLevelTwo; break;	
		} default: {
			res = askUserResponse(errorSpielereinszweiMsg);
		}
		}
		return res;
	}
	
	// Einzelspieler Vokabelmodus: Antwortevaluation
	private SpeechletResponse evaluateAnswerVokabelnEasy(String userRequest) {
		SpeechletResponse res = null;
		recognizeUserIntent(userRequest);{
				logger.info("User answer ="+ userRequest.toLowerCase()+ "/correct answer="+correctAnswer);
				if (userRequest.toLowerCase().equals(correctAnswer.toLowerCase())) {
					logger.info("User answer recognized as correct.");
						recState = RecognitionState.YesNoVokabelnEasy;
						res = responseWithFlavour4(continueMsg);
				}else if (userRequest.toLowerCase().equals(Answer)) {
						 logger.info("User doesn´t know the answer.");
							 recState = RecognitionState.YesNoVokabelnEasy;
							 res = askUserResponse(correctAnswerMsg+" "+correctAnswer+". "+continueMsg);
				} else {
					recState = RecognitionState.YesNoVokabelnEasy;
					res = responseWithFlavour(wrongVocMsg+" "+correctAnswer+". "+continueMsg,7);
				}
			}
		return res;
	}

	
	// Einzelspieler Quizmodus Level 1: Antwortevaluation
	private SpeechletResponse evaluateAnswerQuizLevelOne(String userRequest) {
		SpeechletResponse res = null;
		recognizeUserIntent(userRequest);{
			logger.info("User answer ="+ userRequest.toLowerCase()+ "/correct answer="+correctAnswer);
			if (userRequest.toLowerCase().equals(correctAnswer.toLowerCase())) {
				logger.info("User answer recognized as correct.");
					increaseSum();
					if (sum >= 50) {
						recState = RecognitionState.YesNoQuizLevelTwo;
						res = responseWithFlavour(correctMsg+" "+buildString(sumMsg, String.valueOf(sum), " ")+" "+continueEinzelQuizLevelTwoMsg, 9);
					} else {
						recState = RecognitionState.YesNoQuizLevelOne;
						res = responseWithFlavour4(buildString(sumMsg, String.valueOf(sum), " ")+" "+continueMsg);	
					}
			}else if (userRequest.toLowerCase().equals(Answer)) {
				 logger.info("User doesn´t know the answer.");
				 recState = RecognitionState.YesNoQuizLevelOne;
					 res = askUserResponse(correctAnswerMsg+" "+correctAnswer+". "+continueMsg);
		  } else {
			  		decreaseSum();
					recState = RecognitionState.YesNoQuizLevelOne;
					res = responseWithFlavour(wrongMsg+" "+correctAnswer+". "+buildString(sumMsg, String.valueOf(sum), " ")+" "+continueMsg, 7);
				}
			} 
		return res;
	}
	
	// Einzelspieler Quizmodus Level 2: Antwortevaluation
	private SpeechletResponse evaluateAnswerQuizLevelTwo(String userRequest) {
		SpeechletResponse res = null;
		recognizeUserIntent(userRequest);{
			logger.info("User answer ="+ userRequest.toLowerCase()+ "/correct answer="+correctAnswer);
			if (userRequest.toLowerCase().equals(correctAnswer.toLowerCase())) {
				logger.info("User answer recognized as correct.");
					increaseSum();
					if (sum >= 100) {
						recState = RecognitionState.YesNoQuizLevelThree;
						res = responseWithFlavour(correctMsg+" "+buildString(sumMsg, String.valueOf(sum), " ")+" "+continueEinzelQuizLevelThreeMsg, 9);
					} else {
						recState = RecognitionState.YesNoQuizLevelTwo;
						res = responseWithFlavour4(buildString(sumMsg, String.valueOf(sum), " ")+" "+continueMsg);
					}
			}else if (userRequest.toLowerCase().equals(Answer)) {
				 logger.info("User doesn´t know the answer.");
				 	 recState = RecognitionState.YesNoQuizLevelTwo;
					 res = askUserResponse(correctAnswerMsg+" "+correctAnswer+". "+continueMsg);
				} else {
					decreaseSum();
					recState = RecognitionState.YesNoQuizLevelTwo;
					res = responseWithFlavour(wrongMsg+" "+correctAnswer+". "+buildString(sumMsg, String.valueOf(sum), " ")+" "+continueMsg, 7);
				}
			} 
		return res;
	}
	
	// Einzelspieler Quizmodus Level 3: Antwortevaluation
	private SpeechletResponse evaluateAnswerQuizLevelThree(String userRequest) {
		SpeechletResponse res = null;
		recognizeUserIntent(userRequest);{
			logger.info("User answer ="+ userRequest.toLowerCase()+ "/correct answer="+correctAnswer);
			if (userRequest.toLowerCase().equals(correctAnswer.toLowerCase())) {
				logger.info("User answer recognized as correct.");
					increaseSum();
					if (sum >= 150) {
						recState = RecognitionState.YesNoQuizLevelEnd;
						res = responseWithFlavour(buildString(sumMsg, String.valueOf(sum), " ")+" "+continueEinzelQuizEndMsg, 9);
					} else {
						recState = RecognitionState.YesNoQuizLevelThree;
						res = responseWithFlavour4(buildString(sumMsg, String.valueOf(sum), " ")+" "+continueMsg);
					}
			}else if (userRequest.toLowerCase().equals(Answer)) {
				 logger.info("User doesn´t know the answer.");
				 recState = RecognitionState.YesNoQuizLevelThree;
				 res = askUserResponse(correctAnswerMsg+" "+correctAnswer+". "+continueMsg);
				} else {
					decreaseSum();
					recState = RecognitionState.YesNoQuizLevelThree;
					res = responseWithFlavour(wrongMsg+" "+correctAnswer+". "+buildString(sumMsg, String.valueOf(sum), " ")+" "+continueMsg, 7);
				}
			} 
		return res;
	}

	
	// Mehrspielermodus: Antwortevaluation von Spieler 1 in Level 1
	private SpeechletResponse evaluateAnswerTwo(String userRequest) {
		SpeechletResponse res = null;
		recognizeUserIntent(userRequest);{
				logger.info("User answer ="+ userRequest.toLowerCase()+ "/correct answer="+correctAnswer);
				if (userRequest.toLowerCase().equals(correctAnswer.toLowerCase())) {
					logger.info("User answer recognized as correct.");
					increaseSum2();
					if (sum2 == 40) {
						recState = RecognitionState.YesNoLevel;
						res = responseWithFlavour(correctMsg+" "+buildString2(sumTwoMsg, String.valueOf(sum2), " ")+" "+buildString3(sumThreeMsg, String.valueOf(sum3), " ")+" "+playerOneWins+" "+continueLevelMsg, 9);
					} else {
						recState = RecognitionState.YesNoTwo;
						res = responseWithFlavour4(buildString2(sumTwoMsg, String.valueOf(sum2), " ")+" "+buildString3(sumThreeMsg, String.valueOf(sum3), " ")+" "+continueMsg);
					}
				} else {
					recState = RecognitionState.YesNoTwo;
					res = responseWithFlavour(wrongMsg+" "+correctAnswer+". "+" "+buildString2(sumTwoMsg, String.valueOf(sum2), " ")+" "+buildString3(sumThreeMsg, String.valueOf(sum3), " ")+" "+continueMsg, 7);
				}
			} 
				return res;
	}
	
	// Mehrspielermodus: Antwortevaluation von Spieler 1 in Level 2
	private SpeechletResponse evaluateAnswerFour(String userRequest) {
		SpeechletResponse res = null;
		recognizeUserIntent(userRequest);{
				logger.info("User answer ="+ userRequest.toLowerCase()+ "/correct answer="+correctAnswer);
				if (userRequest.toLowerCase().equals(correctAnswer.toLowerCase())) {
					logger.info("User answer recognized as correct.");
					increaseSum4();
					decreaseSum5();
					if (sum4 >= 40) {
						recState = RecognitionState.YesNoLevelTwo;
						res = responseWithFlavour(correctMsg+" "+buildString2(sumTwoMsg, String.valueOf(sum2+sum4), " ")+" "+buildString3(sumThreeMsg, String.valueOf(sum3+sum5), " ")+" "+playerOneWins+" "+continueLevelTwoMsg, 8);
					} else {
						recState = RecognitionState.YesNoLevel;
						res = responseWithFlavour4(buildString2(sumTwoMsg, String.valueOf(sum2+sum4), " ")+" "+buildString3(sumThreeMsg, String.valueOf(sum3+sum5), " ")+" "+continueMsg);
					}
				} else {
					decreaseSum4();
					increaseSum5();
					if (sum5 >= 40) {
						recState = RecognitionState.YesNoLevelTwo;
						res = responseWithFlavour(wrongMsg+" "+correctAnswer+". "+buildString2(sumTwoMsg, String.valueOf(sum2+sum4), " ")+" "+buildString3(sumThreeMsg, String.valueOf(sum3+sum5), " ")+" "+playerTwoWins+" "+continueLevelTwoMsg, 7);
					} else {
					recState = RecognitionState.YesNoLevel;
					res = responseWithFlavour(wrongMsg+" "+correctAnswer+". "+buildString2(sumTwoMsg, String.valueOf(sum2+sum4), " ")+" "+buildString3(sumThreeMsg, String.valueOf(sum3+sum5), " ")+" "+continueMsg, 7);
					}
				}	
			} 
				return res;
	}
	
	//Mehrspielermodus: Antwortevaluation von Spieler 1 in Level 3
	private SpeechletResponse evaluateAnswerSix(String userRequest) {
		SpeechletResponse res = null;
		recognizeUserIntent(userRequest);{
				logger.info("User answer ="+ userRequest.toLowerCase()+ "/correct answer="+correctAnswer);
				if (userRequest.toLowerCase().equals(correctAnswer.toLowerCase())) {
					logger.info("User answer recognized as correct.");
					decreaseSum7();
					if (sum7 == 0) {
						increaseSum8();
						if (sum2+sum4+sum8>sum3+sum5+sum9) {
						recState = RecognitionState.AgainOrMenu;
						res = responseWithFlavour(correctMsg+" "+buildString2(sumTwoMsg, String.valueOf(sum2+sum4+sum8), " ")+" "+buildString3(sumThreeMsg, String.valueOf(sum3+sum5+sum9), " ")+" "+playerOneWinsGame+" "+againOrMenuMsg, 9);
						} else {
							recState = RecognitionState.AgainOrMenu;
							res = responseWithFlavour(correctMsg+" "+buildString2(sumTwoMsg, String.valueOf(sum2+sum4+sum8), " ")+" "+buildString3(sumThreeMsg, String.valueOf(sum3+sum5+sum9), " ")+" "+playerTwoWinsGame+" "+againOrMenuMsg, 9);
						}
					
					} else {
						recState = RecognitionState.YesNoLevelTwo;
						res = responseWithFlavour4(buildString2(sumLifesTwoMsg, String.valueOf(sum6), " ")+" "+buildString3(sumLifesThreeMsg, String.valueOf(sum7), " ")+" "+continueMsg);
					}
				} else {
					decreaseSum6();
					if (sum6 == 0) {
						increaseSum9();
						if (sum2+sum4+sum8>sum3+sum5+sum9) {
						recState = RecognitionState.AgainOrMenu;
						res = responseWithFlavour(wrongMsg+" "+buildString2(sumTwoMsg, String.valueOf(sum2+sum4+sum8), " ")+" "+buildString3(sumThreeMsg, String.valueOf(sum3+sum5+sum9), " ")+" "+playerOneWinsGame+" "+againOrMenuMsg, 9);
						} else {
							recState = RecognitionState.AgainOrMenu;
							res = responseWithFlavour(wrongMsg+" "+buildString2(sumTwoMsg, String.valueOf(sum2+sum4+sum8), " ")+" "+buildString3(sumThreeMsg, String.valueOf(sum3+sum5+sum9), " ")+" "+playerTwoWinsGame+" "+againOrMenuMsg, 9);
						}
					
				} else {
					recState = RecognitionState.YesNoLevelTwo;
					res = responseWithFlavour(wrongMsg+" "+correctAnswer+". "+buildString2(sumLifesTwoMsg, String.valueOf(sum6), " ")+" "+buildString3(sumLifesThreeMsg, String.valueOf(sum7), " ")+" "+continueMsg, 7);
				}
			} 
		}		return res;
	}
	
	// Mehrspielermodus: Antwortevaluation von Spieler 2 in Level 1
	private SpeechletResponse evaluateAnswerThree(String userRequest) {
		SpeechletResponse res = null;
		recognizeUserIntent(userRequest);{
				logger.info("User answer ="+ userRequest.toLowerCase()+ "/correct answer="+correctAnswer);
				if (userRequest.toLowerCase().equals(correctAnswer.toLowerCase())) {
					logger.info("User answer recognized as correct.");
					increaseSum3();
					if (sum3 >= 40) {
						recState = RecognitionState.YesNoLevel;
						res = responseWithFlavour(correctMsg+" "+buildString2(sumTwoMsg, String.valueOf(sum2), " ")+" "+buildString3(sumThreeMsg, String.valueOf(sum3), " ")+" "+playerTwoWins+" "+continueLevelMsg, 9);
					} else {
						recState = RecognitionState.YesNoTwo;
						res = responseWithFlavour4(buildString2(sumTwoMsg, String.valueOf(sum2), " ")+" "+buildString3(sumThreeMsg, String.valueOf(sum3), " ")+" "+continueMsg);
					}
				} else {
					recState = RecognitionState.YesNoTwo;
					res = responseWithFlavour(wrongMsg+" "+correctAnswer+". "+buildString2(sumTwoMsg, String.valueOf(sum2), " ")+" "+buildString3(sumThreeMsg, String.valueOf(sum3), " ")+" "+continueMsg, 7);
				}
			} 
				return res;
	}
	
	// Mehrspielermodus: Antwortevaluation von Spieler 2 in Level 2
	private SpeechletResponse evaluateAnswerFive(String userRequest) {
		SpeechletResponse res = null;
		recognizeUserIntent(userRequest);{
				logger.info("User answer ="+ userRequest.toLowerCase()+ "/correct answer="+correctAnswer);
				if (userRequest.toLowerCase().equals(correctAnswer.toLowerCase())) {
					logger.info("User answer recognized as correct.");
					increaseSum5();
					decreaseSum4();
					if (sum5 >= 40) {
						recState = RecognitionState.YesNoLevelTwo;
						res = responseWithFlavour(correctMsg+" "+buildString2(sumTwoMsg, String.valueOf(sum2+sum4), " ")+" "+buildString3(sumThreeMsg, String.valueOf(sum3+sum5), " ")+" "+playerTwoWins+" "+continueLevelTwoMsg, 9);
					} else {
						recState = RecognitionState.YesNoLevel;
						res = responseWithFlavour4(buildString2(sumTwoMsg, String.valueOf(sum2+sum4), " ")+" "+buildString3(sumThreeMsg, String.valueOf(sum3+sum5), " ")+" "+continueMsg);
					}
				} else {
					decreaseSum5();
					increaseSum4();
					if (sum4 >= 40) {
						recState = RecognitionState.YesNoLevelTwo;
						res = responseWithFlavour(wrongMsg+" "+correctAnswer+". "+buildString2(sumTwoMsg, String.valueOf(sum2+sum4), " ")+" "+buildString3(sumThreeMsg, String.valueOf(sum3+sum5), " ")+" "+playerOneWins+" "+continueLevelTwoMsg, 9);
					} else {
					recState = RecognitionState.YesNoLevel;
					res = responseWithFlavour(wrongMsg+" "+correctAnswer+". "+buildString2(sumTwoMsg, String.valueOf(sum2+sum4), " ")+" "+buildString3(sumThreeMsg, String.valueOf(sum3+sum5), " ")+" "+continueMsg, 7);
					}
				}
			} 
				return res;
	}
	
	// Mehrspielermodus: Antwortevaluation von Spieler 2 in Level 3
	private SpeechletResponse evaluateAnswerSeven(String userRequest) {
		SpeechletResponse res = null;
		recognizeUserIntent(userRequest);{
				logger.info("User answer ="+ userRequest.toLowerCase()+ "/correct answer="+correctAnswer);
				if (userRequest.toLowerCase().equals(correctAnswer.toLowerCase())) {
					logger.info("User answer recognized as correct.");
					decreaseSum6();
					if (sum6 == 0) {
						increaseSum9();
						if (sum2+sum4+sum8<sum3+sum5+sum9) {
						recState = RecognitionState.AgainOrMenu;
						res = responseWithFlavour(correctMsg+" "+buildString2(sumTwoMsg, String.valueOf(sum2+sum4+sum8), " ")+" "+buildString3(sumThreeMsg, String.valueOf(sum3+sum5+sum9), " ")+" "+playerTwoWinsGame+" "+againOrMenuMsg, 9);
						} else {
							recState = RecognitionState.AgainOrMenu;
							res = responseWithFlavour(correctMsg+" "+buildString2(sumTwoMsg, String.valueOf(sum2+sum4+sum8), " ")+" "+buildString3(sumThreeMsg, String.valueOf(sum3+sum5+sum9), " ")+" "+playerOneWinsGame+" "+againOrMenuMsg, 9);
						}
					} else {
						
				recState = RecognitionState.YesNoLevelTwo;
				res = responseWithFlavour4(buildString2(sumLifesTwoMsg, String.valueOf(sum6), " ")+" "+buildString3(sumLifesThreeMsg, String.valueOf(sum7), " ")+" "+continueMsg);
					}
				} else {	
					decreaseSum7();
					if (sum7 == 0) {
						increaseSum8();
						if (sum2+sum4+sum8<sum3+sum5+sum9) {
						recState = RecognitionState.AgainOrMenu;
						res = responseWithFlavour(wrongMsg+" "+buildString2(sumTwoMsg, String.valueOf(sum2+sum4+sum8), " ")+" "+buildString3(sumThreeMsg, String.valueOf(sum3+sum5+sum9), " ")+" "+playerTwoWinsGame+" "+againOrMenuMsg, 9);
						} else {
							recState = RecognitionState.AgainOrMenu;
							res = responseWithFlavour(wrongMsg+" "+buildString2(sumTwoMsg, String.valueOf(sum2+sum4+sum8), " ")+" "+buildString3(sumThreeMsg, String.valueOf(sum3+sum5+sum9), " ")+" "+playerOneWinsGame+" "+againOrMenuMsg, 9);
						}
					}
				 else {
					recState = RecognitionState.YesNoLevelTwo;
					res = responseWithFlavour(wrongMsg+" "+correctAnswer+". "+buildString2(sumLifesTwoMsg, String.valueOf(sum6), " ")+" "+buildString3(sumLifesThreeMsg, String.valueOf(sum7), " ")+" "+continueMsg, 7);
				}
			} 
		}	return res;
	}
	
	// Fragenänderung
	private void increaseQuestions() {
		switch(questions){
		case 1: questions = 1; break;
		case 2: questions = 2; break;
		case 3: questions = 3; break;
		case 4: questions = 4; break;
		case 5: questions = 5; break;
		case 6: questions = 6; break;
		case 7: questions = 7; break;
		case 8: questions = 8; break;
		case 9: questions = 9; break;
		case 10: questions = 10; break;
		}
	}

	// Summenänderung Einzelspieler Quiz (richtige Antwort)
	private void increaseSum() {
		switch(sum){
		case 0: sum = 10; break;
		case 10: sum = 20; break;
		case 20: sum = 30; break;
		case 30: sum = 40; break;
		case 40: sum = 50; break;
		case 50: sum = 60; break;
		case 60: sum = 70; break;
		case 70: sum = 80; break;
		case 80: sum = 90; break;
		case 90: sum = 100; break;
		case 100: sum = 110; break;
		case 110: sum = 120; break;
		case 120: sum = 130; break;
		case 130: sum = 140; break;
		case 140: sum = 150; break;
		}
	}
	
	// Summenänderung Einzelspieler Quiz (falsche Antwort)
	private void decreaseSum() {
		switch(sum){
		case 10: sum = 0; break;
		case 20: sum = 10; break;
		case 30: sum = 20; break;
		case 40: sum = 30; break;
		case 50: sum = 40; break;
		case 60: sum = 50; break;
		case 70: sum = 60; break;
		case 80: sum = 70; break;
		case 90: sum = 80; break;
		case 100: sum = 90; break;
		case 110: sum = 100; break;
		case 120: sum = 110; break;
		case 130: sum = 120; break;
		case 140: sum = 130; break;
		case 150: sum = 140; break;
		}
	}
	
	// Summenänderung Mehrspieler Spieler 1 Level 1
	private void increaseSum2() {
		switch(sum2){
		case 0: sum2 = 10; break;
		case 10: sum2 = 20; break;
		case 20: sum2 = 30; break;
		case 30: sum2 = 40; break;
		case 40: sum2 = 50; break;
		case 50: sum2 = 60; break;
		case 60: sum2 = 70; break;
		case 70: sum2 = 80; break;
		case 80: sum2 = 90; break;
		case 90: sum2 = 100; break;
		case 100: sum2 = 110; break;
		case 110: sum2 = 120; break;
		case 120: sum2 = 130; break;
		case 130: sum2 = 140; break;
		case 140: sum2 = 150; break;
		}
	}
	
	// Summenänderung Mehrspieler Spieler 2 Level 1
	private void increaseSum3() {
		switch(sum3){
		case 0: sum3 = 10; break;
		case 10: sum3 = 20; break;
		case 20: sum3 = 30; break;
		case 30: sum3 = 40; break;
		case 40: sum3 = 50; break;
		case 50: sum3 = 60; break;
		case 60: sum3 = 70; break;
		case 70: sum3 = 80; break;
		case 80: sum3 = 90; break;
		case 90: sum3 = 100; break;
		case 100: sum3 = 110; break;
		case 110: sum3 = 120; break;
		case 120: sum3 = 130; break;
		case 130: sum3 = 140; break;
		case 140: sum3 = 150; break;
		}
	}
	
	// Summenänderung Mehrspieler Spieler 1 Level 2 (richtige Antwort)
	private void increaseSum4() {
		switch(sum4){
		case 0: sum4 = 10; break;
		case 10: sum4 = 20; break;
		case 20: sum4 = 30; break;
		case 30: sum4 = 40; break;
		case 40: sum4 = 50; break;
		case 50: sum4 = 60; break;
		case 60: sum4 = 70; break;
		case 70: sum4 = 80; break;
		case 80: sum4 = 90; break;
		case 90: sum4 = 100; break;
		case 100: sum4 = 110; break;
		case 110: sum4 = 120; break;
		case 120: sum4 = 130; break;
		case 130: sum4 = 140; break;
		case 140: sum4 = 150; break;
		}
	}
	
	// Summenänderung Mehrspieler Spieler 1 Level 2 (falsche Antwort)
	private void decreaseSum4() {
		switch(sum4){
		case 10: sum4 = 0; break;
		case 20: sum4 = 10; break;
		case 30: sum4 = 20; break;
		case 40: sum4 = 30; break;
		case 50: sum4 = 40; break;
		case 60: sum4 = 50; break;
		case 70: sum4 = 60; break;
		case 80: sum4 = 70; break;
		case 90: sum4 = 80; break;
		case 100: sum4 = 90; break;
		case 110: sum4 = 100; break;
		case 120: sum4 = 110; break;
		case 130: sum4 = 120; break;
		case 140: sum4 = 130; break;
		case 150: sum4 = 140; break;
		}
	}
	
	// Summenänderung Mehrspieler Spieler 2 Level 2 (richtige Antwort)
	private void increaseSum5() {
		switch(sum5){
		case 0: sum5 = 10; break;
		case 10: sum5 = 20; break;
		case 20: sum5 = 30; break;
		case 30: sum5 = 40; break;
		case 40: sum5 = 50; break;
		case 50: sum5 = 60; break;
		case 60: sum5 = 70; break;
		case 70: sum5 = 80; break;
		case 80: sum5 = 90; break;
		case 90: sum5 = 100; break;
		case 100: sum5 = 110; break;
		case 110: sum5 = 120; break;
		case 120: sum5 = 130; break;
		case 130: sum5 = 140; break;
		case 140: sum5 = 150; break;
		}
	}
	
	// Summenänderung Mehrspieler Spieler 2 Level 2 (falsche Antwort)
	private void decreaseSum5() {
		switch(sum5){
		case 10: sum5 = 0; break;
		case 20: sum5 = 10; break;
		case 30: sum5 = 20; break;
		case 40: sum5 = 30; break;
		case 50: sum5 = 40; break;
		case 60: sum5 = 50; break;
		case 70: sum5 = 60; break;
		case 80: sum5 = 70; break;
		case 90: sum5 = 80; break;
		case 100: sum5 = 90; break;
		case 110: sum5 = 100; break;
		case 120: sum5 = 110; break;
		case 130: sum5 = 120; break;
		case 140: sum5 = 130; break;
		case 150: sum5 = 140; break;
		}
	}
	
	// Lebensverlust Mehrspieler Spieler 1 Level 3
	private void decreaseSum6() {
		switch(sum6){
		case 5: sum6 = 4; break;
		case 4: sum6 = 3; break;
		case 3: sum6 = 2; break;
		case 2: sum6 = 1; break;
		case 1: sum6 = 0; break;
		}
	}
	
	// Summenänderung Mehrspieler Spieler 1 Level 3 (Levelsieg Spieler 1)
	private void increaseSum8() {
		switch(sum8){
		case 0: sum8 = 30; break;
		}
	}
	
	// Lebensverlust Mehrspieler Spieler 2 Level 3
	private void decreaseSum7() {
		switch(sum7){
		case 5: sum7 = 4; break;
		case 4: sum7 = 3; break;
		case 3: sum7 = 2; break;
		case 2: sum7 = 1; break;
		case 1: sum7 = 0; break;
		}
	}
	
	// Summenänderung Mehrspieler Spieler 2 Level 3 (Levelsieg Spieler 2)
	private void increaseSum9() {
		switch(sum9){
		case 0: sum9 = 30; break;
		}
	}
	
	private void antonymsselected() {
		switch(sum777){
		case 0: sum777 = 1; break;
		}
	}
	
	
	void recognizeUserIntent(String userRequest) {
		userRequest = userRequest.toLowerCase();
		String pattern1 = "(.*)?(\\bresume\\b)(.*)?";
		String pattern2 = "(.*)?(\\b(one)|(1)\\b)(.*)?";
		String pattern3 = "(.*)?(\\b(two)|(2)\\b)(.*)?";
		String pattern4 = "(.*)?(\\b(vocabulary)|(vocab)\\b)(.*)?";
		String pattern5 = "(.*)?(\\bquiz\\b)(.*)?";
		String pattern7 = "(.*)?(\\bbasics\\b)(.*)?";
		String pattern8 = "(.*)?(\\bexpressions\\b)(.*)?";
		String pattern9 = "(.*)?(\\bone\\b)(.*)?";
		String pattern10 = "(.*)?(\\btwo\\b)(.*)?";
		String pattern11 = "(.*)?(\\bmenu\\b)(.*)?";
		String pattern12 = "(.*)?(next)?(\\blevel\\b)(.*)?";
		String pattern13 = "(.*)?(\\byes\\b)(.*)?";
		String pattern14 = "(.*)?(\\bno\\b)(.*)?";
		String pattern15 = "(.*)?(\\bquiz\\b)(.*)?";
		String pattern16 = "(.*)?(\\bquit\\b)(.*)?";
		String pattern17 = "(.*)?(\\bthemes\\b)(.*)?";
		String pattern18 = "(.*)?(\\bvocab\\b)(.*)?";
		String pattern19 = "(.*)?(\\blevel\\sone\\b)(.*)?";
		String pattern20 = "(.*)?(\\blevel\\stwo\\b)(.*)?";
		String pattern21 = "(.*)?(\\bi\\sdon't\\sknow\\b)(.*)?";
		String pattern22 = "(.*)?(\\bagain\\b)(.*)?";
		String pattern23 = "(.*)?(\\bfood\\b)(.*)?";
		String pattern24 = "(.*)?(\\banimals\\b)(.*)?";
		String pattern25 = "(.*)?(\\bantonyms\\b)(.*)?";
		String pattern26 = "(.*)?(\\bverbs\\b)(.*)?";
		String pattern27 = "(.*)?(\\bcolours\\b)(.*)?";
		String pattern28 = "(.*)?(\\btime\\b)(.*)?";
		String pattern29 = "(.*)?(\\badjectives\\b)(.*)?";
		
		String pattern30 = "(.*)?(\\bphrases\\b)(.*)?";

		String pattern32 = "(.*)?(\\bcontinue\\b)(.*)?";
		String pattern33 = "(.*)?(\\brules\\b)(.*)?";
		String pattern100 = "(.*)?";
		
		
		
		Pattern p1 = Pattern.compile(pattern1);
		Matcher m1 = p1.matcher(userRequest);
		Pattern p2 = Pattern.compile(pattern2);
		Matcher m2= p2.matcher(userRequest);
		Pattern p3 = Pattern.compile(pattern3);
		Matcher m3= p3.matcher(userRequest);
		Pattern p4 = Pattern.compile(pattern4);
		Matcher m4= p4.matcher(userRequest);
		Pattern p5 = Pattern.compile(pattern5);
		Matcher m5= p5.matcher(userRequest);
		Pattern p7 = Pattern.compile(pattern7);
		Matcher m7= p7.matcher(userRequest);
		Pattern p8 = Pattern.compile(pattern8);
		Matcher m8= p8.matcher(userRequest);
		Pattern p9 = Pattern.compile(pattern9);
		Matcher m9= p9.matcher(userRequest);
		Pattern p10 = Pattern.compile(pattern10);
		Matcher m10= p10.matcher(userRequest);
		Pattern p11 = Pattern.compile(pattern11);
		Matcher m11= p11.matcher(userRequest);
		Pattern p12 = Pattern.compile(pattern12);
		Matcher m12= p12.matcher(userRequest);
		Pattern p13 = Pattern.compile(pattern13);
		Matcher m13= p13.matcher(userRequest);
		Pattern p14 = Pattern.compile(pattern14);
		Matcher m14= p14.matcher(userRequest);
		Pattern p15 = Pattern.compile(pattern15);
		Matcher m15= p15.matcher(userRequest);
		Pattern p16 = Pattern.compile(pattern16);
		Matcher m16= p16.matcher(userRequest);
		Pattern p17 = Pattern.compile(pattern17);
		Matcher m17 = p17.matcher(userRequest);
		Pattern p18 = Pattern.compile(pattern18);
		Matcher m18 = p18.matcher(userRequest);
		Pattern p19 = Pattern.compile(pattern19);
		Matcher m19 = p19.matcher(userRequest);
		Pattern p20 = Pattern.compile(pattern20);
		Matcher m20 = p20.matcher(userRequest);
		Pattern p21 = Pattern.compile(pattern21);
		Matcher m21 = p21.matcher(userRequest);
		Pattern p22 = Pattern.compile(pattern22);
		Matcher m22 = p22.matcher(userRequest);
		Pattern p23 = Pattern.compile(pattern23);
		Matcher m23 = p23.matcher(userRequest);
		Pattern p24 = Pattern.compile(pattern24);
		Matcher m24 = p24.matcher(userRequest);
		Pattern p25 = Pattern.compile(pattern25);
		Matcher m25 = p25.matcher(userRequest);
		Pattern p26 = Pattern.compile(pattern26);
		Matcher m26 = p26.matcher(userRequest);
		Pattern p27 = Pattern.compile(pattern27);
		Matcher m27 = p27.matcher(userRequest);
		Pattern p28 = Pattern.compile(pattern28);
		Matcher m28 = p28.matcher(userRequest);
		Pattern p29 = Pattern.compile(pattern29);
		Matcher m29 = p29.matcher(userRequest);
		Pattern p30 = Pattern.compile(pattern30);
		Matcher m30 = p30.matcher(userRequest);
		
		
		Pattern p32 = Pattern.compile(pattern32);
		Matcher m32 = p32.matcher(userRequest);
		Pattern p33 = Pattern.compile(pattern33);
		Matcher m33 = p33.matcher(userRequest);
		Pattern p100 = Pattern.compile(pattern100);
		Matcher m100 = p100.matcher(userRequest);
		
		
		if (m1.find()) {
			ourUserIntent = UserIntent.resume;
		} else if (m2.find()) {
			ourUserIntent = UserIntent.onne;
		} else if (m3.find()) {
			ourUserIntent = UserIntent.twwo;
		} else if (m4.find()) {
			ourUserIntent = UserIntent.vocabulary;
		} else if (m5.find()) {
			ourUserIntent = UserIntent.quiz;
		} else if (m7.find()) {
			ourUserIntent = UserIntent.basics;
		} else if (m8.find()) {
			ourUserIntent = UserIntent.expressions;
		} else if (m9.find()) {
			ourUserIntent = UserIntent.playerone;
		} else if (m10.find()) {
			ourUserIntent = UserIntent.playertwo;
		} else if (m11.find()) {
			ourUserIntent = UserIntent.menu;
		} else if (m12.find()) {
			ourUserIntent = UserIntent.nextlevel;
		} else if (m13.find()) {
			ourUserIntent = UserIntent.resume;
		} else if (m14.find()) {
			ourUserIntent = UserIntent.no;
		} else if (m15.find()) {
			ourUserIntent = UserIntent.quiz;
		} else if (m16.find()) {
			ourUserIntent = UserIntent.quit;
		} else if (m17.find()) {
			ourUserIntent = UserIntent.themes;
		} else if (m18.find()) {
			ourUserIntent = UserIntent.vocab;
		} else if (m19.find()) {
			ourUserIntent = UserIntent.levelone;
		} else if (m20.find()) {
			ourUserIntent = UserIntent.leveltwo;
		} else if (m21.find()) {
			ourUserIntent = UserIntent.answer;
		} else if (m22.find()) {
			ourUserIntent = UserIntent.again;
		} else if (m23.find()) {
			ourUserIntent = UserIntent.food;
		} else if (m24.find()) {
			ourUserIntent = UserIntent.animals;
		} else if (m25.find()) {
			ourUserIntent = UserIntent.antonyms;
		} else if (m26.find()) {
			ourUserIntent = UserIntent.verbs;
		} else if (m27.find()) {
			ourUserIntent = UserIntent.colours;
		} else if (m28.find()) {
			ourUserIntent = UserIntent.time;
		} else if (m29.find()) {
			ourUserIntent = UserIntent.adjectives;
		} else if (m30.find()) {
			ourUserIntent = UserIntent.phrases;
		} else if (m32.find()) {
			ourUserIntent = UserIntent.resume;
		} else if (m33.find()) {
			ourUserIntent = UserIntent.rules;
		} else if (m100.find()) {
			ourUserIntent = UserIntent.any;
		} else {
			ourUserIntent = UserIntent.Error;
		}
		logger.info("set ourUserIntent to " +ourUserIntent);
	}

	 
	/**
	 * formats the text in weird ways
	 * @param text
	 * @param i
	 * @return
	 */
	private SpeechletResponse responseWithFlavour3(String text) {
		SsmlOutputSpeech speech = new SsmlOutputSpeech();
		Random r = new Random();
		int ques = r.nextInt(6);
		switch(ques) {
		case 1: speech.setSsml("<speak> What is the english term for <voice name=\"Marlene\"><lang xml:lang=\"de-DE\">" + question + "</lang> </voice></speak>");
		break;	
		case 2: speech.setSsml("<speak> What does the german term <voice name=\"Marlene\"><lang xml:lang=\"de-DE\">" + question + "</lang> </voice>mean in English?</speak>");
		break;
		case 3: speech.setSsml("<speak> Give me the english term for <voice name=\"Marlene\"><lang xml:lang=\"de-DE\">" + question + "</lang> </voice></speak>");
		break;
		case 4: speech.setSsml("<speak> This one´s interesting. <voice name=\"Marlene\"><lang xml:lang=\"de-DE\">" + question + "</lang> </voice>in english please.</speak>");
		break;
		case 5: speech.setSsml("<speak> How about the term <voice name=\"Marlene\"><lang xml:lang=\"de-DE\">" + question + "</lang> </voice></speak>");
		break;
		case 6: speech.setSsml("<speak> Okay, here´s another one. <voice name=\"Marlene\"><lang xml:lang=\"de-DE\">" + question + "</lang> </voice></speak>");
		break;
		default: 
			speech.setSsml("<speak> Here we go. <voice name=\"Marlene\"><lang xml:lang=\"de-DE\">" + question + "</lang> </voice>in english?</speak>");break;
		}
		Reprompt rep = new Reprompt();
		rep.setOutputSpeech(speech);

		return SpeechletResponse.newAskResponse(speech, rep);
	}
	
	
	private SpeechletResponse responseWithFlavour4(String text) {
		SsmlOutputSpeech speech = new SsmlOutputSpeech();
		Random r = new Random();
		int ques = r.nextInt(4);
		switch(ques) {
		case 1: speech.setSsml("<speak><audio src=\"soundbank://soundlibrary/ui/gameshow/amzn_ui_sfx_gameshow_positive_response_01\"/> <amazon:emotion name=\"excited\" intensity=\"high\"> Correct! </amazon:emotion> " + text + "</speak>");
		break;	
		case 2: speech.setSsml("<speak><audio src=\"soundbank://soundlibrary/ui/gameshow/amzn_ui_sfx_gameshow_positive_response_01\"/> <amazon:emotion name=\"excited\" intensity=\"high\"> Well done! </amazon:emotion>" + text + " </speak>");
		break;
		case 3: speech.setSsml("<speak><audio src=\"soundbank://soundlibrary/ui/gameshow/amzn_ui_sfx_gameshow_positive_response_01\"/> <amazon:emotion name=\"excited\" intensity=\"high\">  Excellent!  </amazon:emotion>" + text + "</speak>");
		break;
		case 4: speech.setSsml("<speak><audio src=\"soundbank://soundlibrary/ui/gameshow/amzn_ui_sfx_gameshow_positive_response_01\"/> <amazon:emotion name=\"excited\" intensity=\"high\"> Perfect! </amazon:emotion>" + text + " </speak>");
		break;
		
		default: 
			speech.setSsml("<speak><audio src=\"soundbank://soundlibrary/ui/gameshow/amzn_ui_sfx_gameshow_positive_response_01\"/> <amazon:emotion name=\"excited\" intensity=\"high\"> Correct! </amazon:emotion>" + text + " </speak>");break;
		}
		Reprompt rep = new Reprompt();
		rep.setOutputSpeech(speech);

		return SpeechletResponse.newAskResponse(speech, rep);
		
	
	}
	
	
	
	private SpeechletResponse responseWithFlavour(String text, int i) {

		SsmlOutputSpeech speech = new SsmlOutputSpeech();
		switch(i){ 
		// normal:
		case 0: 
			speech.setSsml("<speak>" + text + "</speak>");
			break; 
		case 1: 
			speech.setSsml("<speak><emphasis level=\"strong\">" + text + "</emphasis></speak>");
			break; 
		case 2: 
			String half1=text.split(" ")[0];
			String[] rest = Arrays.copyOfRange(text.split(" "), 1, text.split(" ").length);
			speech.setSsml("<speak>"+half1+"<break time=\"3s\"/>"+ StringUtils.join(rest," ") + "</speak>");
			break; 
		case 3: 
			String firstNoun="erstes Wort buchstabiert";
			String firstN=text.split(" ")[3];
			speech.setSsml("<speak>"+firstNoun+ "<say-as interpret-as=\"spell-out\">"+firstN+"</say-as>"+"</speak>");
			break; 
	
		case 4: 
			speech.setSsml("<speak><audio src='soundbank://soundlibrary/transportation/amzn_sfx_airplane_takeoff_whoosh_01'/></speak>");
			break;
			
		// in Kombination mit WelcomeMsg - Menu (GAMESHOW INTRO SOUND):
		case 5: 
			speech.setSsml("<speak> <audio src=\"soundbank://soundlibrary/ui/gameshow/amzn_ui_sfx_gameshow_intro_01\"/> <amazon:emotion name=\"excited\" intensity=\"high\">" + text + "</amazon:emotion> <amazon:emotion name=\"excited\" intensity=\"medium\"> One or two players? </amazon:emotion></speak>");
			break;
		
		case 6: 
			speech.setSsml("<speak> What is the english term for <voice name=\"Marlene\"><lang xml:lang=\"de-DE\">" + question + "</lang> </voice></speak>");
			break;	
			
		// wrong feedback sound	
		case 7: 
			speech.setSsml("<speak><audio src=\"soundbank://soundlibrary/ui/gameshow/amzn_ui_sfx_gameshow_negative_response_02\"/> <amazon:emotion name=\"excited\" intensity=\"low\"> " + text + "</amazon:emotion> </speak>");
			break;
		// correct feedback sound	
		case 8: 
			speech.setSsml("<speak><audio src=\"soundbank://soundlibrary/ui/gameshow/amzn_ui_sfx_gameshow_positive_response_01\"/> <amazon:emotion name=\"excited\" intensity=\"high\"> " + text + "</amazon:emotion> </speak>");
			break;
			
		// new level sound
		case 9: 
			speech.setSsml("<speak><audio src=\"soundbank://soundlibrary/ui/gameshow/amzn_ui_sfx_gameshow_bridge_02\"/> <amazon:emotion name=\"excited\" intensity=\"high\"> " + text + "</amazon:emotion> </speak>");
						break;	
						
		//  Two player mode. What does the german word ++DEUTSCHE AUSSPRACHE++  mean in english?
		case 10: 
			speech.setSsml("<speak> You're in two player mode. Please clarify who wants to be player one and who wants to be player two. If you think you know the correct answer, say you're player number. You will get points if your answer is correct. Let's begin! What does the german word <voice name=\"Marlene\"><lang xml:lang=\"de-DE\">" + question + "</lang> </voice>mean in english?</speak>");
			break;
			
		// antonym	
		case 11: 
			speech.setSsml("<speak> What´s the opposite of " + question + "?</speak>");
			break; 
						
			
		default: 
			speech.setSsml("<speak><amazon:effect name=\"whispered\">" + text + "</amazon:effect></speak>");
		} 
		
		Reprompt rep = new Reprompt();
		rep.setOutputSpeech(speech);

		return SpeechletResponse.newAskResponse(speech, rep);
	}

	//anwendung wird anschliessend beendet:in Kombination mit goodbyeMsg (GAMESHOW OUTRO SOUND):
	
	private SpeechletResponse responseWithFlavour2(String text, int i) {

		SsmlOutputSpeech speech = new SsmlOutputSpeech();
		switch(i){ 
		case 0: 
			speech.setSsml("<speak><amazon:emotion name=\"excited\" intensity=\"medium\">" + text + "</amazon:emotion><audio src=\"soundbank://soundlibrary/ui/gameshow/amzn_ui_sfx_gameshow_outro_01\" /></speak>");
			break; 
		
		default: 
			speech.setSsml("<speak><amazon:effect name=\"whispered\">Das ist ein Test lol!</amazon:effect></speak>");
		} 

		return SpeechletResponse.newTellResponse(speech);
		
		
	}



	@Override
	public void onSessionEnded(SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope)
	{
		logger.info("Alexa session ends now");
	}



	/**
	 * Tell the user something - the Alexa session ends after a 'tell'
	 */
	private SpeechletResponse response(String text)
	{
		// Create the plain text output.
		PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
		speech.setText(text);

		return SpeechletResponse.newTellResponse(speech);
	}

	/**
	 * A response to the original input - the session stays alive after an ask request was send.
	 *  have a look on https://developer.amazon.com/de/docs/custom-skills/speech-synthesis-markup-language-ssml-reference.html
	 * @param text
	 * @return
	 */
	private SpeechletResponse askUserResponse(String text)
	{
		SsmlOutputSpeech speech = new SsmlOutputSpeech();
		speech.setSsml("<speak>" + text + "</speak>");

		// reprompt after 8 seconds
		SsmlOutputSpeech repromptSpeech = new SsmlOutputSpeech();
		repromptSpeech.setSsml("<speak><emphasis level=\"strong\">Hey!</emphasis> Bist du noch da?</speak>");

		Reprompt rep = new Reprompt();
		rep.setOutputSpeech(repromptSpeech);

		return SpeechletResponse.newAskResponse(speech, rep);
	}
}
