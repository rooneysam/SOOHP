package com.SOOHP;

//imports
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.apache.commons.io.FileUtils;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import java.util.Random;


//import javax.swing.SwingUtilities;
//import javax.swing.UIManager;
//import javax.swing.UnsupportedLookAndFeelException;


public class SOOHPMain {
	//these Strings hold the local paths for the installed application
	public static String pathApplication = "C:\\Program Files\\SOOHP\\Application\\";
	public static String pathClueLists = "C:\\Program Files\\SOOHP\\ClueLists\\";
	public static String pathScripts = "C:\\Program Files\\SOOHP\\Scripts\\";
	public static String pathUpdates = "C:\\Program Files\\SOOHP\\Updates\\";

	//these Strings hold the remote paths for the TEST application
	public static String pathRemoteClueLists = "D:\\SOOHPServer\\ClueLists\\";
	public static String pathRemoteUpdates = "D:\\SOOHPServer\\Updates\\";
	public static String pathPing = "127.0.0.1";

	//these Strings hold the remote paths for the LIVE application
	//public static String pathScripts =
	// "\\\\SERV1234.company.com\\SOOHPServer\\Scripts\\";
	//public static String pathUpdates =
	// "\\\\SERV1234.company.com\\SOOHPServer\\Updates\\";
	//public static String pathPing = "SERV1234.company.com";

	static Vector<Question> allQuestions = new Vector<Question>();
	static Vector<Question> typeQuestions = new Vector<Question>();
	static Vector<Question> testQuestions = new Vector<Question>();
	static Vector<Question> askedQuestions = new Vector<Question>();
	public static Vector<String> questionTypes = new Vector<String>();
	public static String SelectedAnswer;
	public static String SelectedType;
	public static String Succeded;
	public static JFrame frame = new JFrame("SOOHP");
	public static JTextArea questionTextArea;
	public static Question SelectedQuestion;
	public static SOOHPCallback callback;
	public static Vector<String> clueList = new Vector<String>();
	public static JComboBox typeChoice = new JComboBox();
	//button handlers
	public static SOOHPMain.okButtonHandler myOKButtonHandler = new okButtonHandler();
	public static SOOHPMain.exitButtonHandler myExitButtonHandler = new exitButtonHandler();
	public static SOOHPMain.yesButtonHandler myYesButtonHandler = new yesButtonHandler();
	public static SOOHPMain.noButtonHandler myNoButtonHandler = new noButtonHandler();
	public static SOOHPMain.selectButtonHandler mySelectButtonHandler = new selectButtonHandler();
	public static SOOHPMain.testYesButtonHandler myTestYesButtonHandler = new testYesButtonHandler();
	public static SOOHPMain.testNoButtonHandler myTestNoButtonHandler = new testNoButtonHandler();
	public static SOOHPMain.finishedButtonHandler myFinishedButtonHandler = new finishedButtonHandler();
	//panels
	public static JPanel answerPane = new JPanel(new GridLayout(3, 1));
	public static JPanel topHalf = new JPanel();
	public static JPanel bottomHalf = new JPanel(new BorderLayout());
	public static JPanel buttonPanel = new JPanel();
	//buttons
	public static JRadioButton yesButton = new JRadioButton("Yes");
	public static JRadioButton noButton = new JRadioButton("No");
	public static JButton okButton = new JButton("OK");
	public static JButton selectButton = new JButton("Select");
	public static JButton exitButton = new JButton("Exit");
	public static JButton testYesButton = new JButton("testYes");
	public static JButton testNoButton = new JButton("testNo");
	public static JButton finishedButton = new JButton("Finished");

	public static void main(String[] args) throws IOException {
		new SOOHPMain().init(true);
	}

	public SOOHPMain() {
	}

	public void init(boolean exitOnClose) throws IOException {
		// call update checker
		updateChecker();

		// KieServices is the factory for all KIE services
		KieServices ks = KieServices.Factory.get();

		// //test
		// KieFileSystem kfs = ks.newKieFileSystem();
		// FileInputStream fis = new FileInputStream(
		// "C:\\Program Files\\SOOHP\\Application\\SOOHP.drl" );
		// kfs.write( "src/main/resources/simple.drl",
		// ks.getResources().newInputStreamResource( fis ) );
		// KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
		//
		// KieContainer kieContainer =
		// ks.newKieContainer( ks.getRepository().getDefaultReleaseId() );
		//
		// KieBase kieBase = kieContainer.getKieBase();
		// KieSession kieSession = kieContainer.newKieSession();
		// //endTest

		// From the kie services, a container is created from the classpath
		KieContainer kc = ks.getKieClasspathContainer();

		// read in all the questions from file
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
			//add button listners
			testYesButton.addMouseListener(myTestYesButtonHandler);
			testNoButton.addMouseListener(myTestNoButtonHandler);
			exitButton.addMouseListener(myExitButtonHandler);
			selectButton.addMouseListener(mySelectButtonHandler);
			okButton.addMouseListener(myOKButtonHandler);
			finishedButton.addMouseListener(myFinishedButtonHandler);

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
			
			//set up the questin text area
			questionTextArea = new JTextArea(1, 10);
			questionTextArea.setEditable(false);
			questionTextArea.setFont(new Font("Serif", Font.ITALIC, 20));
			questionTextArea.setLineWrap(true);
			questionTextArea.setWrapStyleWord(true);
			JScrollPane questionTextPane = new JScrollPane(questionTextArea,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			topHalf.add(questionTextPane, BorderLayout.CENTER);

			// answer buttons
			answerPane.setOpaque(true);
			bottomHalf.add(answerPane, BorderLayout.NORTH);
			bottomHalf.add(buttonPanel, BorderLayout.SOUTH);

			// call show type screen to becin the user interaction
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
			
//			 //this is the look and feel code put it in createAndShowGUI
//			 try
//			 {
//			 try {
//			 UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
//			 } catch (ClassNotFoundException e) {
//			 // TODO Auto-generated catch block
//			 e.printStackTrace();
//			 } catch (InstantiationException e) {
//			 // TODO Auto-generated catch block
//			 e.printStackTrace();
//			 } catch (IllegalAccessException e) {
//			 // TODO Auto-generated catch block
//			 e.printStackTrace();
//			 }
//			 JFrame.setDefaultLookAndFeelDecorated(true);
//			 SwingUtilities.updateComponentTreeUI(frame);
//			 }
//			 catch (UnsupportedLookAndFeelException e)
//			 {
//			 System.out.println(e);
//			 }

		}

		//these methods call the different client screens
		
		//this calls the show type screen which allows the user to choose a type of question
		public void showTypeScreen() {

			questionTextArea
					.setText("Please choose a problem type from the dropdown box and press Select to start");

			// the type choice combo box pulls the list of question types from
			// the questionTypes Vector
			for (int b = 0; b < questionTypes.size(); b++) {
				if (!questionTypes.get(b).equals("Test")) {
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

		//this shows the question screen where the questions are asked
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
			testYesButton.setVisible(false);
			buttonPanel.remove(testYesButton);
			testNoButton.setVisible(false);
			buttonPanel.remove(testNoButton);
			buttonPanel.removeAll();
			buttonPanel.add(exitButton, BorderLayout.WEST);
			buttonPanel.add(okButton, BorderLayout.EAST);
			buttonPanel.setVisible(true);

			// if the list of questions of the chosen type has anything left in it then we call a new question and display it 
			if (typeQuestions.size() > 0) {
				SelectedQuestion = getRandomQuestion();
				questionTextArea.setText(SelectedQuestion.getQuestionText());
			}
			frame.setVisible(true);
		}

		//this calls the screen which says there are no more questions of the selected type so the problem could not be diagnosed
		public static void showNoMoreQuestionsScreen() {
			Succeded = "unsolved";
			testYesButton.setVisible(false);
			buttonPanel.remove(testYesButton);
			testNoButton.setVisible(false);
			buttonPanel.remove(testNoButton);
			okButton.setVisible(false);
			buttonPanel.removeAll();
			answerPane.removeAll();
			questionTextArea
					.setText("Unfortunately SOOHP has run out of ideas on how to fix your problem, details of the issue will be uploaded to assist support staff.");
			buttonPanel.add(finishedButton, BorderLayout.EAST);
			buttonPanel.setVisible(true);
			frame.setVisible(true);
		}

		//this calls the test screen where we test a possible fix or diagnoses for the problem
		public static void showTestScreen(String testName) {
			//this for loop finds the question which has the passed in question name and puts its text in the question text area
			for (int g = 0; g < testQuestions.size(); g++) {
				if (testQuestions.get(g).getQuestionName().equals(testName)) {
					questionTextArea.setText(testQuestions.get(g)
							.getQuestionText());
				}
				testNoButton.setActionCommand("testNo");
				testYesButton.setActionCommand("testYes");
				answerPane.removeAll();
				buttonPanel.removeAll();
				testYesButton.setVisible(true);
				testNoButton.setVisible(true);
				buttonPanel.add(testYesButton, BorderLayout.EAST);
				buttonPanel.add(testNoButton, BorderLayout.EAST);

				frame.setVisible(true);
			}
		}

		//this calls the success screen 
		public static void showSuccessScreen() {
			Succeded = "fixed";
			testYesButton.setVisible(false);
			buttonPanel.remove(testYesButton);
			testNoButton.setVisible(false);
			buttonPanel.remove(testNoButton);
			buttonPanel.add(finishedButton);
			questionTextArea
					.setText("Success! SOOHP is glad it could fix your problem details of the fix will be uploaded to make future diagnoses more efficient.");
			frame.setVisible(true);
		}

	}

	/**
	 * Other Classes
	 */
	//this button is on the question screen
	public static class okButtonHandler extends MouseAdapter {
		public void mouseReleased(MouseEvent e) {
			JButton button = (JButton) e.getComponent();
			
			//add the answer of the current question to the clueList and send it to the knowledge-base for processing
			callback.testClues((JFrame) button.getTopLevelAncestor(), clueList,
					SelectedQuestion.getQuestionName() + SelectedAnswer);
			
			// if the clue list contains a test then this stops another question being displayed instead of the test text
			if (clueList.toString().contains("Test")) {
				System.out.println("test present");
			} else 
			
			//if there are any questions of the selected type and no test in the clue list left then get a question
				if (!(typeQuestions.isEmpty())) {
				SelectedQuestion = SOOHPMain.getRandomQuestion();
				questionTextArea.setText(SelectedQuestion.getQuestionText());
			} else {
				SOOHPUI.showNoMoreQuestionsScreen();
			}
		}
	}

	//this button is on the type choice screen
	public static class selectButtonHandler extends MouseAdapter {
		public void mouseReleased(MouseEvent e) {
			//first we add the tests to a vector for use later
			for (int c = 0; c < allQuestions.size(); c++) {
				if ((allQuestions.get(c).getQuestionType()).equals("Test")) {
					testQuestions.add(allQuestions.get(c));
				}
			}
			//now we add the questions of the selected type to a vector
			for (int d = 0; d < allQuestions.size(); d++) {
				if ((allQuestions.get(d).getQuestionType())
						.equals(SelectedType)) {
					typeQuestions.add(allQuestions.get(d));
				}
			}			
			SOOHPUI.showQuestionScreen();
		}
	}

	public static class exitButtonHandler extends MouseAdapter {
		public void mouseReleased(MouseEvent e) {
			// /code goes here
			System.exit(0);
		}
	}

	//this button is on both the no more questions and success screens
	public static class finishedButtonHandler extends MouseAdapter {
		public void mouseReleased(MouseEvent e) {
			//first we get the date and time for use in the filename
			Date dNow = new Date();
			SimpleDateFormat ft = new SimpleDateFormat("y.M.d.H.m.s");
			String currentDate = (ft.format(dNow));
			PrintWriter writer;		
			try {
				//the file name contains whether there was a successful fix,  and the date and time
				writer = new PrintWriter(pathClueLists + "clueList." + Succeded
						+ "." + currentDate + ".txt");
				//the file contains the clue list , whether there was a successful fix, the username, and the date and time
				writer.println(clueList);
				writer.println(Succeded);
				writer.println(currentDate);
				writer.println((System.getProperty("user.name")));
				writer.close();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.exit(0);
		}
	}

	public static class yesButtonHandler extends MouseAdapter {
		public void mouseReleased(MouseEvent e) {
			// /code goes here
		}
	}

	public static class noButtonHandler extends MouseAdapter {
		public void mouseReleased(MouseEvent e) {
			// /code goes here
		}
	}

	//this button indicates a test was successful and so the problem is fixed or diagnosed
	public static class testYesButtonHandler extends MouseAdapter {
		public void mouseReleased(MouseEvent e) {
			// /code goes here
			SOOHPUI.showSuccessScreen();
		}
	}

	//clicking this button indicates a test was unsuccessful and so the next question must be called
	public static class testNoButtonHandler extends MouseAdapter {
		public void mouseReleased(MouseEvent e) {
			//change the word test for the word tried this stops the rules from firing more than once or the test screen from being called repeatedly
			for (int v = 0; v < clueList.size(); v++) {
				clueList.set(v, clueList.get(v).replace("Test", "Tried"));
			}
			//if there are any more questions of the relevant type go to the question screen 
			if (typeQuestions.size()>0){
				SOOHPUI.showQuestionScreen();
			}
			else{
				SOOHPUI.showNoMoreQuestionsScreen();
			}
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

		//this is the method that passes the clue list to the knowledge-base
		public void testClues(JFrame frame, Vector<String> currentClueList,
				String selectedAnswer) {
			currentClueList.add(selectedAnswer);

			// session listed in META-INF/kmodule.xml file
			KieSession ksession = kcontainer.newKieSession("SOOHPKS");
			ksession.insert(currentClueList);
			ksession.fireAllRules();
		}
	}

	/**
	 * Methods
	 */
	
	//this method scans in all the questions from a file to the allQuestions array
	public void scanQuestions() {
		// /test read in questions from file
		try {
			@SuppressWarnings("resource")
			Scanner questionScanner = new Scanner(new File(pathApplication
					+ "allQuestions.csv")).useDelimiter("\n");
			while (questionScanner.hasNext()) {
				// /this bit passes each line to a separate scanner which turns
				// it into a question
				allQuestions.add(scanLine(questionScanner.next()));
			}
			for (int x = 0; x < allQuestions.size(); x++) {
				if (!(questionTypes.contains(allQuestions.get(x)
						.getQuestionType()))) {
					questionTypes.add(allQuestions.get(x).getQuestionType());
				}
			}
			questionScanner.close();
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}

	}
	//this is the line scanner used by the question scanner
	public Question scanLine(String line) {
		@SuppressWarnings("resource")
		Scanner lineScanner = new Scanner(line);
		lineScanner.useDelimiter(",");
		String qType = lineScanner.next();
		String qName = lineScanner.next();
		String qText = lineScanner.next();
		return new Question(qType, qName, qText);
	}

	//this is the question chooser method
	public static Question getRandomQuestion() {
		Random rnd = new Random();
		//if there is more than one question it picks a random one otherwise it takes the only available one
		int q = 0;		
		if (typeQuestions.size() > 1) {
			q = rnd.nextInt(typeQuestions.size());			
		}
		Question nextQuestion = (typeQuestions.get(q));
		typeQuestions.remove(typeQuestions.get(q));
		return nextQuestion;
	}

	//this method checks for updates to the program and uploads any saved cluelist files to the server
	public static void updateChecker() throws IOException {
		File updateFolder = new File(pathUpdates);
		File currentJarFile = new File(updateFolder + "\\SOOHP.jar");
		//if an update is ready in the local update folder apply it
		if (FileUtils.directoryContains(updateFolder, currentJarFile)) {
			String runString = ("cmd /c start copyFiles.bat");
			Runtime.getRuntime().exec(runString);
			System.exit(0);
		}

		// check connectivity
		boolean connected = false;
		try {
			InetAddress address = InetAddress.getByName(pathPing);
			connected = (address.isReachable(3000));
		} catch (UnknownHostException e) {
			System.err.println("Unable to lookup server");
		} catch (IOException e) {
			System.err.println("Unable to reach server");
		}

		if (connected) {
			
			File clueListFolder = new File(pathClueLists);
			File remoteUpdateFolder = new File(pathRemoteUpdates);
			File applicationFolder = new File(pathApplication);

			// upload cluelists
			if (clueListFolder.list().length > 0) {
				Date dNow = new Date();
				SimpleDateFormat ft = new SimpleDateFormat("y.M.d");
				String currentDate = (ft.format(dNow));
				File destinationDirectory = new File(pathRemoteClueLists
						+ currentDate);
				try {
					FileUtils.copyDirectory(clueListFolder,
							destinationDirectory);
					FileUtils.cleanDirectory(clueListFolder);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// check for new versions of the questions or the application and if they exist copy them to the local update folder
			File newVersionFile = new File(remoteUpdateFolder + "\\version.txt");
			File currentVersionFile = new File(applicationFolder
					+ "\\version.txt");

			File currentQuestionFile = new File(updateFolder
					+ "\\allQuestions.csv");
			File newJarFile = new File(remoteUpdateFolder + "\\SOOHP.jar");
			File newQuestionFile = new File(remoteUpdateFolder
					+ "\\allQuestions.csv");
			Scanner currentVersionScanner = new Scanner(new File(
					applicationFolder + "\\version.txt")).useDelimiter("\n");
			Scanner newVersionScanner = new Scanner(new File(remoteUpdateFolder
					+ "\\version.txt")).useDelimiter("\n");
			int currentVersionNumber = currentVersionScanner.nextInt();
			System.out.println("current version: " + currentVersionNumber);
			int newVersionNumber = newVersionScanner.nextInt();
			System.out.println("new version: " + newVersionNumber);
			if (newVersionNumber > currentVersionNumber) {
				FileUtils.copyFile(newJarFile, currentJarFile);
				FileUtils.copyFile(newQuestionFile, currentQuestionFile);
				FileUtils.copyFile(newVersionFile, currentVersionFile);
				currentVersionScanner.close();
				newVersionScanner.close();
				System.out.println("copied update files");
			} else {
				System.out.println("no update needed");
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
