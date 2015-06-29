package com.SOOHP;

//this comment is just here to test if GIT push/pull is working

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
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
import javax.swing.JComboBox;





///import javax.swing.SwingUtilities;
///import javax.swing.UIManager;
///import javax.swing.UnsupportedLookAndFeelException;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.util.Random;

public class SOOHPMain {
	static Vector<Question> allQuestions = new Vector<Question>();
	static Vector<Question> typeQuestions = new Vector<Question>();
	static Vector<Question> testQuestions = new Vector<Question>();
	static Vector<Question> askedQuestions = new Vector<Question>();
	public static Vector<String> questionTypes = new Vector<String>();
	public static String SelectedAnswer;
	public static String SelectedType;
	public static JFrame frame = new JFrame("SOOHP");
	public static JTextArea questionTextArea;
	public static Question SelectedQuestion;
	public static SOOHPCallback callback;
	public static Vector<String> clueList = new Vector<String>();
	public static JComboBox typeChoice = new JComboBox();
	///button handlers
	public static SOOHPMain.okButtonHandler myOKButtonHandler = new okButtonHandler();
	public static SOOHPMain.exitButtonHandler myExitButtonHandler = new exitButtonHandler();
	public static SOOHPMain.yesButtonHandler myYesButtonHandler = new yesButtonHandler();
	public static SOOHPMain.noButtonHandler myNoButtonHandler = new noButtonHandler();
	public static SOOHPMain.skipButtonHandler mySkipButtonHandler = new skipButtonHandler();
	public static SOOHPMain.selectButtonHandler mySelectButtonHandler = new selectButtonHandler();
	public static SOOHPMain.testYesButtonHandler myTestYesButtonHandler = new testYesButtonHandler();
	public static SOOHPMain.testNoButtonHandler myTestNoButtonHandler = new testNoButtonHandler();
	///panels
	public static JPanel answerPane = new JPanel(new GridLayout(3, 1));
	public static JPanel topHalf = new JPanel();
	public static JPanel bottomHalf = new JPanel(new BorderLayout());
	public static JPanel buttonPanel = new JPanel();
	///buttons
	public static JRadioButton yesButton = new JRadioButton("Yes");
	public static JRadioButton noButton = new JRadioButton("No");
	public static JButton okButton = new JButton("OK");
	public static JButton selectButton = new JButton("Select");
	public static JButton exitButton = new JButton("Exit");
	public static JButton testYesButton = new JButton("testYes");
	public static JButton testNoButton = new JButton("testNo");
	
	
	
	
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

		// The callback is responsible for populating working memory and
		// firing all rules
		SOOHPUI ui = new SOOHPUI(allQuestions, new SOOHPCallback(kc));
		ui.createAndShowGUI(exitOnClose);
	}

	@SuppressWarnings("serial")
	public static class SOOHPUI extends JPanel {

		public SOOHPUI(Vector<Question> allQs, SOOHPCallback newCallback) {
			super(new BorderLayout());
			callback = newCallback;
			
			testNoButton.addMouseListener(myTestNoButtonHandler);
			exitButton.addMouseListener(myExitButtonHandler);
			selectButton.addMouseListener(mySelectButtonHandler);
			okButton.addMouseListener(myOKButtonHandler);
			// this bit shouldn't be necessary
			///SelectedQuestion = getRandomQuestion();

			// Create main vertical split panel
			JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
			add(splitPane, BorderLayout.CENTER);

			// create top half of split panel and add to parent

			topHalf.setLayout(new BoxLayout(topHalf, BoxLayout.X_AXIS));
			topHalf.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
			topHalf.setMinimumSize(new Dimension(400, 50));
			topHalf.setPreferredSize(new Dimension(450, 250));
			splitPane.add(topHalf);

			// create bottom top half of split panel and add to parent

			bottomHalf.setMinimumSize(new Dimension(400, 50));
			bottomHalf.setPreferredSize(new Dimension(450, 300));
			splitPane.add(bottomHalf);
			questionTextArea = new JTextArea(1, 10);
			questionTextArea.setEditable(false);
			questionTextArea.setFont(new Font("Serif", Font.ITALIC, 20));
			questionTextArea.setLineWrap(true);
			questionTextArea.setWrapStyleWord(true);
			JScrollPane questionTextPane = new JScrollPane(questionTextArea,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			topHalf.add(questionTextPane, BorderLayout.CENTER);
			
						
			// /answer buttons
			answerPane.setOpaque(true);
///			answerPane.setBackground(Color.BLUE);			
			
			bottomHalf.add(answerPane, BorderLayout.NORTH);
			///ok button
			

			bottomHalf.add(buttonPanel, BorderLayout.SOUTH);
			
			//call showQuestionScreen here
			///showQuestionScreen();
			showTypeScreen();
		}

		/**
		 * Create and show the GUI
		 */
		public void createAndShowGUI(boolean exitOnClose) {
			// Create and set up the window.
			frame.setDefaultCloseOperation(exitOnClose ? JFrame.EXIT_ON_CLOSE
					: JFrame.DISPOSE_ON_CLOSE);
			setOpaque(true);
			frame.setContentPane(this);
			// Display the window.
			frame.pack();
			frame.setLocationRelativeTo(null); // Center in screen
			frame.setVisible(true);
		}

		///these methods call the different client screens
		public void showTypeScreen() {
			questionTextArea.setText("Please choose a problem type from the dropdown box and press Select to start");

			///the type choice combo box pulls the list of question types from the questionTypes Vector
			///JComboBox typeChoice = new JComboBox();
			for(int b = 0; b < questionTypes.size(); b++){
				if (!questionTypes.get(b).equals("Test")){
				typeChoice.addItem(questionTypes.get(b));
				}
			}
			
			ComboListener myComboListener = null;
			myComboListener = new ComboListener();
			typeChoice.addActionListener(myComboListener);
			
			answerPane.add(typeChoice);

			exitButton.setActionCommand("EXIT");
			buttonPanel.add(exitButton, BorderLayout.WEST);

			selectButton.setActionCommand("Select");
			selectButton.setEnabled(false);
			selectButton.setVisible(false);
			buttonPanel.add(selectButton, BorderLayout.EAST);
		}

		public static void showQuestionScreen() {
			ButtonGroup myButtonGroup = new ButtonGroup();
			myButtonGroup.add(yesButton);
			myButtonGroup.add(noButton);
			RadioListener myRadioListener = null;

			yesButton.setActionCommand(".Yes");
			myRadioListener = new RadioListener();
			yesButton.addActionListener(myRadioListener);
			noButton.setActionCommand(".No");
			noButton.addActionListener(myRadioListener);
			

			okButton.setActionCommand("OK");
			
			answerPane.remove(typeChoice);
			answerPane.add(yesButton, BorderLayout.NORTH);
			answerPane.add(noButton, BorderLayout.SOUTH);

			buttonPanel.remove(testNoButton);
			buttonPanel.remove(testYesButton);
			buttonPanel.removeAll();
			buttonPanel.add(okButton, BorderLayout.EAST);
			buttonPanel.setVisible(true);
			System.out.println("a");
			SelectedQuestion = getRandomQuestion();
			System.out.println("b");
			questionTextArea.setText(SelectedQuestion.getQuestionText());
			System.out.println("c");
			frame.setVisible(true);

			System.out.println("Question Screen Called");
		}

		public static void showTestScreen(String testName) {
			for (int g = 0 ; g < testQuestions.size();g++){
				if (testQuestions.get(g).getQuestionName().equals(testName)){
					System.out.println(testQuestions.get(g).getQuestionText());
					questionTextArea.setText(testQuestions.get(g).getQuestionText());
				}
				testNoButton.setActionCommand("testNo");
				answerPane.removeAll();
				buttonPanel.removeAll();
				buttonPanel.add(testYesButton, BorderLayout.EAST);
				buttonPanel.add(testNoButton, BorderLayout.EAST);
				
				frame.setVisible(true);
			}
		}
		
		
	}

	/**
	 * Other Classes
	 */
	public static class okButtonHandler extends MouseAdapter {
		public void mouseReleased(MouseEvent e) {

			///output.setText(" button pressed is: " + SelectedAnswer);
			JButton button = (JButton) e.getComponent();
			// /check if a test is requested if so display
			callback.testClues((JFrame) button.getTopLevelAncestor(), clueList,
					SelectedQuestion.getQuestionName() + SelectedAnswer);
			System.out.println("currentClueList is: "
			+ clueList.toString());
//			checkForTest();
			if (clueList.toString().contains("Test")){
				System.out.println("test present");
			}
			else if (!(typeQuestions.isEmpty())) {
					SelectedQuestion = SOOHPMain.getRandomQuestion();
					questionTextArea
							.setText(SelectedQuestion.getQuestionText());
				} else {
					System.out.println("No more questions");
					questionTextArea.setText("No more questions");
			}
		}
	}
	
	public static class selectButtonHandler extends MouseAdapter {
		public void mouseReleased(MouseEvent e) {
			///code goes here
			///System.out.println ("AllQuestions size: "+ allQuestions.size());
			for(int c = 0; c < allQuestions.size(); c++){
				if ((allQuestions.get(c).getQuestionType()).equals("Test")){
				testQuestions.add(allQuestions.get(c));
				}
			}
			for(int d = 0; d < allQuestions.size(); d++){
				if ((allQuestions.get(d).getQuestionType()).equals(SelectedType)){
				typeQuestions.add(allQuestions.get(d));
				}
			}
			System.out.println("typeQuestion size : " + typeQuestions
					.size());
			SOOHPUI.showQuestionScreen();
		}
	}
	
	public static class exitButtonHandler extends MouseAdapter {
		public void mouseReleased(MouseEvent e) {
			///code goes here
			System.exit(0);
		}
	}
		
	public static class yesButtonHandler extends MouseAdapter {
		public void mouseReleased(MouseEvent e) {
			///code goes here
		}
	}
	
	public static class noButtonHandler extends MouseAdapter {
		public void mouseReleased(MouseEvent e) {
			///code goes here
		}
	}
	
	public static class skipButtonHandler extends MouseAdapter {
		public void mouseReleased(MouseEvent e) {
			///code goes here
		}
	}

	public static class testYesButtonHandler extends MouseAdapter {
		public void mouseReleased(MouseEvent e) {
			///code goes here
		}
	}
	
	public static class testNoButtonHandler extends MouseAdapter {
		public void mouseReleased(MouseEvent e) {
			System.out.println("Count of listeners: " + ((JButton) e.getSource()).getActionListeners().length);
			///change the word test for the word tried
			System.out.println("a "+clueList.toString());
			for (int v =0 ; v < clueList.size();v++){
				clueList.set(v, clueList.get(v).replace("Test","Tried")); 
			}
			
			System.out.println("b "+ clueList.toString());
			testNoButton.setEnabled(false);
			SOOHPUI.showQuestionScreen();
///			return;
		}
	}
	
	// /Listens to the radio buttons
	public static class RadioListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			SelectedAnswer = e.getActionCommand();
		}
	}

	// /Listens to the combo box
	public static class ComboListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			SelectedType = (String) typeChoice.getSelectedItem();
			selectButton.setEnabled(true);
			selectButton.setVisible(true);
		}
	}
	
	public static class SOOHPCallback {
		KieContainer kcontainer;
		JTextArea output;

		public SOOHPCallback(KieContainer kcontainer) {
			this.kcontainer = kcontainer;
		}

		public void setOutput(JTextArea output) {
			this.output = output;
		}

		public void testClues(JFrame frame, Vector<String> currentClueList,
				String selectedAnswer) {
			currentClueList.add(selectedAnswer);
			///System.out.println("currentClueList is: "
					///+ currentClueList.toString());
			///System.out.println("askedQuestions is is: "
					///+ askedQuestions.toString());
			// session listed in META-INF/kmodule.xml file
			KieSession ksession = kcontainer.newKieSession("SOOHPKS");
			ksession.insert(currentClueList);
			ksession.fireAllRules();
		}
	}

	/**
	 * Methods  
	 */
	public void scanQuestions() {
		// /test read in questions from file
		try {
			@SuppressWarnings("resource")
			Scanner questionScanner = new Scanner(new File(
					"C:\\allQuestions.V2.csv")).useDelimiter("\n");
			while (questionScanner.hasNext()) {
				// /this bit passes each line to a separate scanner which turns
				// it into a question
				allQuestions.add(scanLine(questionScanner.next()));
			}
			for (int x = 0; x < allQuestions.size(); x++){
				if (!(questionTypes.contains(allQuestions.get(x).getQuestionType()))){
					questionTypes.add(allQuestions.get(x).getQuestionType());
				}
			}
			questionScanner.close();
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}

	}

	public Question scanLine(String line) {
		@SuppressWarnings("resource")
		Scanner lineScanner = new Scanner(line);
		lineScanner.useDelimiter(",");
		String qType = lineScanner.next();
		String qName = lineScanner.next();
		String qText = lineScanner.next();
		return new Question(qType,qName, qText);
	}

	public static Question getBlankQuestion() {
		Question blankQuestion = (allQuestions.get(0));
		return blankQuestion;
	}

	public static Question getRandomQuestion() {
		Random rnd = new Random();
		int q=0;
		System.out.println("typeQuestionSize is " + typeQuestions.size());
		if (typeQuestions.size()>1){
		q = rnd.nextInt(typeQuestions.size());
		System.out.println("there is more than 1 question left");
		}
		System.out.println("typeQuestion size : " + typeQuestions.size());
		Question nextQuestion = (typeQuestions.get(q));
		///Question randomQuestion = (nextQuestion);
		typeQuestions.remove(typeQuestions.get(q));
		return nextQuestion;
	}

	public static void checkTest1(String testName){
		
		for (int g = 0 ; g < testQuestions.size();g++){
			if (testQuestions.get(g).getQuestionName().equals(testName)){
				System.out.println(testQuestions.get(g).getQuestionText());
			}
		}
	}
}

// /this is the look and feel code put it in createAndShowGUI
// try
// {
// try {
// UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
// } catch (ClassNotFoundException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// } catch (InstantiationException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// } catch (IllegalAccessException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }
// JFrame.setDefaultLookAndFeelDecorated(true);
// SwingUtilities.updateComponentTreeUI(this.frame);
// }
// catch (UnsupportedLookAndFeelException e)
// {
// System.out.println(e);
// }
