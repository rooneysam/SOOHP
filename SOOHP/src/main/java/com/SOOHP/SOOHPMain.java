package com.SOOHP;

//this comment is just here to test if GIT push/pull is working

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;




	public class SOOHPMain {

	Vector<Question> allQuestions = new Vector<Question>();
		
	    public static void main(String[] args) {
	        new SOOHPMain().init(true);
	    }

	    public SOOHPMain() {
	    }

	    public void init(boolean exitOnClose) {
	        // KieServices is the factory for all KIE services 
	        KieServices ks = KieServices.Factory.get();
	        
	        // From the kie services, a container is created from the classpath
	        KieContainer kc = ks.getKieClasspathContainer();
	        
	        //create test questions, these will have to be done another way in finished
	        //program, read in from a csv or something, also each question will have to have a 
	        //text asking the user a question and a set of choices/clues/answers 
//	        ///Vector<Question> allQuestions = new Vector<Question>();
//	        allQuestions.add(new Question("Q1"));
//	        allQuestions.add(new Question("Q2"));
//	        allQuestions.add(new Question("Q3"));
//	        allQuestions.add(new Question("Q4"));
//	        allQuestions.add(new Question("Q5"));
	        
//	        ///test read in questions from file 
//	        try
//	        {
//	          Scanner s = new Scanner(new File("C:\\allQuestions.csv")).useDelimiter("\n");
//	          while (s.hasNext()) {
//	        	
//	        		  allQuestions.add(new Question(s.next()));
//	        	  
//	          }
//	          s.close();
//	        }
//	        catch (IOException ioe)
//	        {
//	          System.out.println(ioe.getMessage());
//	        }
//	        
//	        ///test print out all question names
//	        for (int aB = 1; aB < allQuestions.size();aB++) {
//	        	System.out.println("Question "+aB+" "+ (allQuestions.get(aB-1).getQuestionName()));
//	        }
//	        
	        scanQuestions();
	        
	        //The callback is responsible for populating working memory and
	        // firing all rules
	        SOOHPUI ui = new SOOHPUI( allQuestions,new SOOHPCallback( kc ) );
	        ui.createAndShowGUI(exitOnClose);
	    }
	    
	    


	    public static class SOOHPUI extends JPanel {

	        private JTextArea questionTextArea;
	        
	        private JTextArea output;

	        private String SelectedAnswer ;

	        private SOOHPCallback callback;

	        private Vector<String> clueList = new Vector<String>();

	       
	        public SOOHPUI(Vector<Question> allQs,SOOHPCallback callback) {
	            super( new BorderLayout() );
	            this.callback = callback;
	            RadioListener myListener = null;
	            Vector<Question> allQuestions = allQs;
	            ///Vector<String> currentClueList = clueList;
	            
	            //Create main vertical split panel
	            JSplitPane splitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
	            add( splitPane,BorderLayout.CENTER );

	            //create top half of split panel and add to parent
	            JPanel topHalf = new JPanel();
	            topHalf.setLayout( new BoxLayout( topHalf,BoxLayout.X_AXIS ) );
	            topHalf.setBorder( BorderFactory.createEmptyBorder( 5,5,0,5 ) );
	            topHalf.setMinimumSize( new Dimension( 400,50 ) );
	            topHalf.setPreferredSize( new Dimension( 450,250 ) );
	            splitPane.add( topHalf );

	            //create bottom top half of split panel and add to parent
	            JPanel bottomHalf = new JPanel( new BorderLayout() );
	            bottomHalf.setMinimumSize( new Dimension( 400,50 ) );
	            bottomHalf.setPreferredSize( new Dimension( 450,300 ) );
	            splitPane.add( bottomHalf );
	            
	            questionTextArea = new JTextArea( 1,10 );
	            questionTextArea.setEditable( false );
	            JScrollPane questionTextPane = new JScrollPane( questionTextArea,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );
	            topHalf.add( questionTextPane, BorderLayout.CENTER );	           
	            questionTextArea.setText(allQuestions.elementAt(1).getQuestionName());
	            	
	            ///answer buttons
	            JPanel answerPane = new JPanel();
	            JRadioButton firstButton = new JRadioButton("first");
	            firstButton.setActionCommand("first");
	            myListener = new RadioListener();
	            firstButton.addActionListener(myListener);
	            JRadioButton secondButton = new JRadioButton("second");
	            secondButton.setActionCommand("second");
	            secondButton.addActionListener(myListener);
	            ButtonGroup group = new ButtonGroup();


	            group.add(firstButton);
	            group.add(secondButton);
	            answerPane.add(firstButton);
	            answerPane.add(secondButton);
	            bottomHalf.add(answerPane,BorderLayout.NORTH);
	            
	            ///ok button
	            JPanel okPanel = new JPanel();
	            JButton okButton = new JButton( "OK" );
	            okButton.setVerticalTextPosition( AbstractButton.CENTER );
	            okButton.setHorizontalTextPosition( AbstractButton.TRAILING );
	            //attach handler to assert items into working memory
	            okButton.addMouseListener( new okButtonHandler() );
	            okButton.setActionCommand( "OK" );
	            okPanel.add(okButton );
	            bottomHalf.add(okPanel);
	            
	            output = new JTextArea( 1,5);
	            output.setEditable( false );
	            JScrollPane outputPane = new JScrollPane( output,  ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );
	            bottomHalf.add( outputPane, BorderLayout.SOUTH);

	            this.callback.setOutput( this.output );
	            

	        }

	        /**
	         * Create and show the GUI
	         */
	        public void createAndShowGUI(boolean exitOnClose) {
	            //Create and set up the window.
	            JFrame frame = new JFrame( "SOOHP" );
	            frame.setDefaultCloseOperation(exitOnClose ? JFrame.EXIT_ON_CLOSE : JFrame.DISPOSE_ON_CLOSE);

	            setOpaque( true );
	            frame.setContentPane( this );

	            //Display the window.
	            frame.pack();
	            frame.setLocationRelativeTo(null); // Center in screen
	            frame.setVisible( true );
	        }



	        private class okButtonHandler extends MouseAdapter {
	            public void mouseReleased(MouseEvent e) {
	     
	            	output.setText( " button pressed is: " + SelectedAnswer );
	            	JButton button = (JButton) e.getComponent();
	            	///clueList.add(SelectedAnswer);
	            	///check if a test is requested if so display

	                callback.testClues( (JFrame) button.getTopLevelAncestor(),clueList,SelectedAnswer );
	            	if (clueList.toString().contains("test5")){
	            		questionTextArea.setText("try test5");
	            		
	            	}
	            }
	        }
	        
	    	/// Listens to the radio buttons
	    	class RadioListener implements ActionListener {

	    		public void actionPerformed(ActionEvent e) {
	    			SelectedAnswer = e.getActionCommand();
	    		}
	    	}
	    	

	    }


	    public static class SOOHPCallback {
	        KieContainer kcontainer;
	        JTextArea     output;

	        public SOOHPCallback(KieContainer kcontainer) {
	            this.kcontainer = kcontainer;
	        }

	        public void setOutput(JTextArea output) {
	            this.output = output;
	        }

	       
	        public void testClues(JFrame frame, Vector<String> currentClueList, String selectedAnswer) {

	        	currentClueList.add(selectedAnswer);
            	System.out.println("currentClueList is: " + currentClueList.toString());
	          
	            // session listed in META-INF/kmodule.xml file 
	            KieSession ksession = kcontainer.newKieSession("SOOHPKS");

	            ksession.insert( currentClueList );
  
	            ksession.fireAllRules();
	            
	        }
	    }


	    
	    public void scanQuestions()
	    {
	        ///test read in questions from file 
	        try
	        {
	          Scanner questionScanner = new Scanner(new File("C:\\allQuestions.csv")).useDelimiter("\n");
	          while (questionScanner.hasNext()) {
	        	  ///this bit passes each line to a separate scanner which turns it into a question
	        	  allQuestions.add(scanLine(questionScanner.next()));
	          }
	          questionScanner.close();
	        }
	        catch (IOException ioe)
	        {
	          System.out.println(ioe.getMessage());
	        }
	        for (int q =0;q<allQuestions.size();q++)
	        {
	        System.out.println("Question Name: "+ (allQuestions.get(q).getQuestionName())+"Question Text: "+ (allQuestions.get(q).getQuestionText()));
	        }
	    }
	    
	    
	    public Question scanLine(String line)
	    {
	    	Scanner lineScanner = new Scanner(line);
	        lineScanner.useDelimiter(",");
	        String qName = lineScanner.next();
	        String qText = lineScanner.next();
	        String a1 = lineScanner.next();
	        String a2 = lineScanner.next();
	        String a3 = lineScanner.next();
	        return new Question(qName,qText,a1,a2,a3);
	    }
	    
	    
	    
	    

	    public class Question {

	    	private String QuestionName;
	    	private String QuestionText;
	    	private String AnswerOne;
	    	private String AnswerTwo;
	    	private String AnswerThree;
	    	private int successful;
	    	private boolean asked;

	    public Question (String newQuestionName, String newQuestionText,String newAnswerOne,String newAnswerTwo,String newAnswerThree){
	    	this.QuestionName = newQuestionName;
	    	this.QuestionText = newQuestionText;
	    	this.AnswerOne = newAnswerOne;
	    	this.AnswerTwo = newAnswerTwo;
	    	this.AnswerThree = newAnswerThree;
	    	successful = 0;
	    	asked = false;
	    }

	    public String getQuestionName() {
	    	return QuestionName;
	    }
	    
	    public String getQuestionText() {
	    	return QuestionText;
	    }
	    
	    public String getAnswerOne() {
	    	return AnswerOne;
	    }
	    
	    public String getAnswerTwo() {
	    	return AnswerTwo;
	    }
	    
	    public String getAnswerThree() {
	    	return AnswerThree;
	    }

	    public void incrementSuccessful(){
	    	successful ++;
	    }

	    public void decrementSuccessful(){
	    	successful --;
	    }

	    public void setAsked(){
	    	asked = true;
	    }

	    public boolean getAsked(){
	    	return asked;
	    }

	    }
	    


	}
