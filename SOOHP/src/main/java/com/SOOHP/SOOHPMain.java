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
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.util.Random;


	public class SOOHPMain {

	static Vector<Question> allQuestions = new Vector<Question>();
	static Vector<Question> askedQuestions = new Vector<Question>();
	
		
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
	        
	        scanQuestions();
	        
	        //The callback is responsible for populating working memory and
	        // firing all rules
	        SOOHPUI ui = new SOOHPUI( allQuestions,new SOOHPCallback( kc ) );
	        ui.createAndShowGUI(exitOnClose);
	    }
	    
	    public static class SOOHPUI extends JPanel {

	        private JTextArea questionTextArea;
	        JFrame frame = new JFrame( "SOOHP" );
	        private JTextArea output;

	        private String SelectedAnswer ;
	        
	        private Question SelectedQuestion;

	        private SOOHPCallback callback;

	        private Vector<String> clueList = new Vector<String>();

	       
	        public SOOHPUI(Vector<Question> allQs,SOOHPCallback callback) {
	            super( new BorderLayout() );
	            this.callback = callback;
	            RadioListener myListener = null;
	            
	            //this bit shouldn't be necessary
	            SelectedQuestion = getBlankQuestion();

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
	            questionTextArea.setText("Press OK to start");
	            	
	            ///answer buttons
	            JPanel answerPane = new JPanel();
	            JRadioButton yesButton = new JRadioButton("Yes");
	            yesButton.setActionCommand(".Yes");
	            myListener = new RadioListener();
	            yesButton.addActionListener(myListener);
	            JRadioButton noButton = new JRadioButton("No");
	            noButton.setActionCommand(".No");
	            noButton.addActionListener(myListener);
	            ButtonGroup group = new ButtonGroup();


	            group.add(yesButton);
	            group.add(noButton);
	            answerPane.add(yesButton);
	            answerPane.add(noButton);
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
///	            JFrame frame = new JFrame( "SOOHP" );
	        	        	
	        	
	            frame.setDefaultCloseOperation(exitOnClose ? JFrame.EXIT_ON_CLOSE : JFrame.DISPOSE_ON_CLOSE);
	            
	            setOpaque( true );
	            frame.setContentPane( this );

	            //Display the window.
	            frame.pack();
	            frame.setLocationRelativeTo(null); // Center in screen
	            frame.setVisible( true );
	            

	            try
	            {
	              try {
					UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	              JFrame.setDefaultLookAndFeelDecorated(true);
	              SwingUtilities.updateComponentTreeUI(this.frame);
	            }
	            catch (UnsupportedLookAndFeelException e)
	            {
	              System.out.println(e);
	            }
	        	
	        }



	        private class okButtonHandler extends MouseAdapter {
	            public void mouseReleased(MouseEvent e) {
	            	
	            	output.setText( " button pressed is: " + SelectedAnswer );
	            	JButton button = (JButton) e.getComponent();
	            	

	            	///check if a test is requested if so display
	            	
	                callback.testClues( (JFrame) button.getTopLevelAncestor(),clueList,SelectedQuestion.getQuestionName()+SelectedAnswer );
	            	if (clueList.toString().contains("test5")){
	            		questionTextArea.setText("try test5");	            		
	            	}                       	
	            	else {
	    		    	if (!(askedQuestions.containsAll(allQuestions)))
	    		    	{
			            	SelectedQuestion = getRandomQuestion();
			            	questionTextArea.setText(SelectedQuestion.getQuestionText());	    		    		
	    		    	}
	    		    	else{
	    		    		System.out.println("No more questions");
	    		    	}

	            	}
	            }
	        }
	        
	    	/// Listens to the radio buttons
	    	class RadioListener implements ActionListener {

	    		public void actionPerformed(ActionEvent e) {
	    			SelectedAnswer = e.getActionCommand();
	    		}
	    	}
	    	
		    public Question getRandomQuestion()
		    {
		    	Random rnd = new Random();
		    	Question nextQuestion = (allQuestions.get(rnd.nextInt(allQuestions.size())));	


		    	while (askedQuestions.contains(nextQuestion))
		    	{
		    		System.out.println("we already asked the question : " + (nextQuestion.getQuestionName()));
		    		nextQuestion = (allQuestions.get(rnd.nextInt(allQuestions.size())));
		    	}
		    	Question randomQuestion = (nextQuestion);
		    	System.out.println("we have not yet asked the question : " + (nextQuestion.getQuestionName()));
		    	askedQuestions.add(nextQuestion);
		    	return randomQuestion;	
		    }
		    
		    public static Question getBlankQuestion()
		    {	    	
		    	Question blankQuestion = (allQuestions.get(0));
		    	return blankQuestion;
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
            	System.out.println("askedQuestions is is: " + askedQuestions.toString());
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
	          Scanner questionScanner = new Scanner(new File("C:\\allQuestions.V2.csv")).useDelimiter("\n");
	          while (questionScanner.hasNext()) {
	        	  //System.out.println(questionScanner.next());
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
	        System.out.println("Question Name: "+ (allQuestions.get(q).getQuestionName())+", Question Text: "+ (allQuestions.get(q).getQuestionText()));
	        }
	    }
	    
	    
	    public Question scanLine(String line)
	    {
	    	Scanner lineScanner = new Scanner(line);
	        lineScanner.useDelimiter(",");
	        String qName = lineScanner.next();
	        //System.out.println("Qn:"+qName);
	        String qText = lineScanner.next();
	        //System.out.println("Qt:"+qText);
	        return new Question(qName,qText);
	    }
	    
	    

	    

	    public class Question {

	    	private String QuestionName;
	    	private String QuestionText;
	    	private int successful;
	    	private boolean asked;
	    	
	    public Question()
	    {
	    	
	    }

	    public Question (String newQuestionName, String newQuestionText){
	    	this.QuestionName = newQuestionName;
	    	this.QuestionText = newQuestionText;
	    	successful = 0;
	    	asked = false;
	    }

	    public String getQuestionName() {
	    	return QuestionName;
	    }
	    
	    public String getQuestionText() {
	    	return QuestionText;
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
