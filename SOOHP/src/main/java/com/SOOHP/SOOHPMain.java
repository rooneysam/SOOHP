package com.SOOHP;

//this comment is just here to test if GIT push/pull is working

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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



///import javax.swing.SwingUtilities;
///import javax.swing.UIManager;
///import javax.swing.UnsupportedLookAndFeelException;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.util.Random;

public class SOOHPMain {
	static Vector<Question> allQuestions = new Vector<Question>();
	static Vector<Question> askedQuestions = new Vector<Question>();
	public static String SelectedAnswer;
	public static JFrame frame = new JFrame("SOOHP");
	public static JTextArea questionTextArea;
	///public static JTextArea output;
	public static Question SelectedQuestion;
	public static SOOHPCallback callback;
	public static Vector<String> clueList = new Vector<String>();
	///button handlers
	public static SOOHPMain.okButtonHandler myOKButtonHandler = new okButtonHandler();
	public static SOOHPMain.exitButtonHandler myExitButtonHandler = new exitButtonHandler();
	public static SOOHPMain.yesButtonHandler myYesButtonHandler = new yesButtonHandler();
	public static SOOHPMain.noButtonHandler myNoButtonHandler = new noButtonHandler();
	public static SOOHPMain.skipButtonHandler mySkipButtonHandler = new skipButtonHandler();
	///panels
	public static JPanel answerPane = new JPanel(new GridLayout(3, 1));
	///buttons
	public static JRadioButton yesButton = new JRadioButton("Yes");
	
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
		// JFrame frame = new JFrame("SOOHP");
		// private JTextArea questionTextArea;
		// private JTextArea output;
		// private Question SelectedQuestion;
		// private SOOHPCallback callback;
		// private Vector<String> clueList = new Vector<String>();

		public SOOHPUI(Vector<Question> allQs, SOOHPCallback newCallback) {
			super(new BorderLayout());
			callback = newCallback;
			RadioListener myListener = null;

			// this bit shouldn't be necessary
			SelectedQuestion = getBlankQuestion();

			// Create main vertical split panel
			JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
			add(splitPane, BorderLayout.CENTER);

			// create top half of split panel and add to parent
			JPanel topHalf = new JPanel();
			topHalf.setLayout(new BoxLayout(topHalf, BoxLayout.X_AXIS));
			topHalf.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
			topHalf.setMinimumSize(new Dimension(400, 50));
			topHalf.setPreferredSize(new Dimension(450, 250));
			splitPane.add(topHalf);

			// create bottom top half of split panel and add to parent
			JPanel bottomHalf = new JPanel(new BorderLayout());
			bottomHalf.setMinimumSize(new Dimension(400, 50));
			bottomHalf.setPreferredSize(new Dimension(450, 300));
			splitPane.add(bottomHalf);
			questionTextArea = new JTextArea(1, 10);
			questionTextArea.setEditable(false);
			JScrollPane questionTextPane = new JScrollPane(questionTextArea,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			topHalf.add(questionTextPane, BorderLayout.CENTER);
			questionTextArea.setText("Press OK to start");
			
			
			// /answer buttons
			///JPanel answerPane = new JPanel(new GridLayout(3, 1));
			answerPane.setOpaque(true);
			answerPane.setBackground(Color.BLUE);
			
			yesButton.setActionCommand(".Yes");
			myListener = new RadioListener();
			yesButton.addActionListener(myListener);
			JRadioButton noButton = new JRadioButton("No");
			noButton.setActionCommand(".No");
			noButton.addActionListener(myListener);
			ButtonGroup group = new ButtonGroup();
			group.add(yesButton);
			group.add(noButton);

			answerPane.add(noButton, BorderLayout.SOUTH);
			bottomHalf.add(answerPane, BorderLayout.NORTH);
			///ok button
			JPanel buttonPanel = new JPanel();
			
			JButton okButton = new JButton("OK");
			okButton.setVerticalTextPosition(AbstractButton.CENTER);
			okButton.setHorizontalTextPosition(AbstractButton.TRAILING);
			// attach handler to assert items into working memory
			okButton.addMouseListener(myOKButtonHandler);
			okButton.setActionCommand("OK");
			
			JButton exitButton = new JButton("EXIT");
			exitButton.addMouseListener(myExitButtonHandler);
			exitButton.setActionCommand("EXIT");
			buttonPanel.add(exitButton, BorderLayout.WEST);

			
			buttonPanel.add(okButton, BorderLayout.EAST);

			
			
			buttonPanel.setOpaque(true);
			buttonPanel.setBackground(Color.GREEN);
			bottomHalf.add(buttonPanel, BorderLayout.SOUTH);
//			output = new JTextArea(1, 5);
//			output.setEditable(false);
//			callback.setOutput(output);
			
			//to be put in show question pane
			
			
			//call showTypeScreen here
			showQuestionScreen();
			
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

		// /these 2 methods need to be filled in
		public void showTypeScreen() {
			System.out.println("Type Screen Called");
		}

		public static void showQuestionScreen() {

			answerPane.add(yesButton, BorderLayout.NORTH);
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
			if (clueList.toString().contains("test5")) {
				questionTextArea.setText("try test5");
			} else {
				if (!(askedQuestions.containsAll(allQuestions))) {
					SelectedQuestion = SOOHPMain.getRandomQuestion();
					questionTextArea
							.setText(SelectedQuestion.getQuestionText());
				} else {
					System.out.println("No more questions");
				}

			}
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

	// /Listens to the radio buttons
	public static class RadioListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			SelectedAnswer = e.getActionCommand();
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
			System.out.println("currentClueList is: "
					+ currentClueList.toString());
			System.out.println("askedQuestions is is: "
					+ askedQuestions.toString());
			// session listed in META-INF/kmodule.xml file
			KieSession ksession = kcontainer.newKieSession("SOOHPKS");
			ksession.insert(currentClueList);
			ksession.fireAllRules();
		}
	}

	/**
	 * Methods  
	 */
	// /these methods call the different client screens

	public void scanQuestions() {
		// /test read in questions from file
		try {
			@SuppressWarnings("resource")
			Scanner questionScanner = new Scanner(new File(
					"C:\\allQuestions.V2.csv")).useDelimiter("\n");
			while (questionScanner.hasNext()) {
				// System.out.println(questionScanner.next());
				// /this bit passes each line to a separate scanner which turns
				// it into a question
				allQuestions.add(scanLine(questionScanner.next()));
			}
			questionScanner.close();
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
		for (int q = 0; q < allQuestions.size(); q++) {
			System.out.println("Question Name: "
					+ (allQuestions.get(q).getQuestionName())
					+ ", Question Text: "
					+ (allQuestions.get(q).getQuestionText()));
		}
	}

	public Question scanLine(String line) {
		@SuppressWarnings("resource")
		Scanner lineScanner = new Scanner(line);
		lineScanner.useDelimiter(",");
		String qName = lineScanner.next();
		String qText = lineScanner.next();
		return new Question(qName, qText);
	}

	public static Question getBlankQuestion() {
		Question blankQuestion = (allQuestions.get(0));
		return blankQuestion;
	}

	public static Question getRandomQuestion() {
		Random rnd = new Random();
		Question nextQuestion = (allQuestions.get(rnd.nextInt(allQuestions
				.size())));
		while (askedQuestions.contains(nextQuestion)) {
			System.out.println("we already asked the question : "
					+ (nextQuestion.getQuestionName()));
			nextQuestion = (allQuestions.get(rnd.nextInt(allQuestions.size())));
		}
		Question randomQuestion = (nextQuestion);
		System.out.println("we have not yet asked the question : "
				+ (nextQuestion.getQuestionName()));
		askedQuestions.add(nextQuestion);
		return randomQuestion;
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
