package sphinxtest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.jsapi.JSGFGrammar;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;

public class SphinxTest {

	private Recognizer recognizer;
	private JSGFGrammar jsgfGrammarManager;
	private Microphone microphone;

	private String logPath;
	private String log;

	private int totalCorrect = 0;
	private int totalIncorrect = 0;

	private String actualSentence;
	private String actualTestType;

	private String speakerLevel;

	private ArrayList<String> sentences;
	private ArrayList<String> testTypes;

	private static final String COMPLETE_GRAMMAR_NAME = "osoa";

	public SphinxTest(String logPath) {
		URL url = SphinxTest.class.getResource("sphinxtest.config.xml");
		ConfigurationManager cm = new ConfigurationManager(url);

		recognizer = (Recognizer) cm.lookup("recognizer");
		jsgfGrammarManager = (JSGFGrammar) cm.lookup("jsgfGrammar");
		microphone = (Microphone) cm.lookup("microphone");

		this.logPath = logPath;

		loadSentences();
		loadTestTypes();
	}

	private void loadSentences() {
		sentences = new ArrayList<String>();
	}

	private void loadTestTypes() {
		testTypes = new ArrayList<String>();
	}

	public void execute() {
		System.out.println("SphixTest\n");

		System.out.println("Loading recognizer...");
		recognizer.allocate();
		System.out.println("Ready\n");

		if (!microphone.startRecording()) {
			System.out.println("Cannot start microphone.");
			recognizer.deallocate();
			System.exit(1);
		}

		System.out.println("Select speaker level:");
		System.out.println("1 - High");
		System.out.println("2 - Medium");
		System.out.println("3 - Low");

		int lev = 0;
		while (lev == 0) {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						System.in));
				lev = Integer.parseInt(br.readLine());
			} catch (IOException ioe) {
				System.out.println("Invalid level number");
			} catch (NumberFormatException e) {
				System.out.println("Invalid level number");
			}
		}

		switch (lev) {
		case 1:
			speakerLevel = "high";
			break;
		case 2:
			speakerLevel = "medium";
			break;
		case 3:
			speakerLevel = "low";
			break;
		default:
			speakerLevel = "unknown";
			break;
		}

		int resp1 = -1;
		int resp2 = -1;

		while (resp1 != 0) {
			System.out.println("Select sentence:");
			for (int i = 0; i < sentences.size(); i++)
				System.out.println(i + " - " + sentences.get(i));

			System.out.print("\nEnter number: ");
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						System.in));
				resp1 = Integer.parseInt(br.readLine());
			} catch (IOException ioe) {
			} catch (NumberFormatException e) {
			}

			actualSentence = sentences.get(resp1);

			System.out.println();
			if (resp1 != 0) {
				while (resp2 != 0) {
					System.out.println("Select test type:");
					for (int i = 0; i < testTypes.size(); i++)
						System.out.println(i + " - " + testTypes.get(i));

					System.out.print("\nEnter number: ");
					try {
						BufferedReader br = new BufferedReader(
								new InputStreamReader(System.in));
						resp2 = Integer.parseInt(br.readLine());

					} catch (IOException ioe) {
					} catch (NumberFormatException e) {
					}

					actualTestType = sentences.get(resp2);

					String grammarName;
					if (actualTestType.equals(COMPLETE_GRAMMAR_NAME))
						grammarName = COMPLETE_GRAMMAR_NAME;
					else
						grammarName = actualSentence.replace(' ', '_') + "." + actualTestType;

					try {
						loadAndRecognize(grammarName);
					} catch (IOException e) {
						System.out.println("Grammar " + grammarName
								+ " doesn't exist");
					}
					actualTestType = "";
				}
				writeLog();

				actualSentence = "";

				totalCorrect = 0;
				totalIncorrect = 0;
			}
		}
	}

	private void loadAndRecognize(String grammarName) throws IOException {
		jsgfGrammarManager.loadJSGF(grammarName);

		String results = "";
		int correct = 0;
		int incorrect = 0;

		while (true) {
			System.out.println("Start speaking.\n");

			Result recogResult = recognizer.recognize();
			if (recogResult != null) {
				String recogResultText = recogResult
						.getBestFinalResultNoFiller();
				System.out.println("You said: " + recogResultText + '\n');
				System.out
						.println("Correct result? (C - correct | I - incorrect | [S] - skip this one| R - return)");

				String resp = "";
				try {
					BufferedReader br = new BufferedReader(
							new InputStreamReader(System.in));
					resp = br.readLine();
				} catch (IOException ioe) {
					System.out.println("Result not saved");
				}

				if (resp.equalsIgnoreCase("C")) {
					results += "\n\n" + recogResultText + "\nCORRECT";
					correct++;
				} else if (resp.equalsIgnoreCase("I")) {
					results += "\n\n" + recogResultText + "\nINCORRECT";
					incorrect++;
				} else if (resp.equalsIgnoreCase("R")) {
					break;
				}
			} else {
				System.out.println("I can't hear what you said.\n");
			}
		}

		logResult(results, correct, incorrect, grammarName);
	}

	private void logResult(String results, int correct, int incorrect,
			String grammarName) {
		log += "=============================================================================";
		log += "  Sentence: " + actualSentence;
		log += "  Test type: " + actualTestType;
		log += "  Grammar name:" + grammarName;
		log += "=============================================================================";
		log += "\n\n";
		log += results;
		log += "\n\n";
		log += "-----------";
		log += "Correct: " + correct + "; Incorrect: " + incorrect;
		log += "\n\n\n";

		totalCorrect += correct;
		totalIncorrect += incorrect;
	}

	private void writeLog() {
		if (!log.isEmpty()) {
			log += "Speaker level: " + speakerLevel.toUpperCase()
					+ "Sentence: " + actualSentence
					+ "------------------------------" + "\n\n\n" + log
					+ "-------------------------------" + "Total correct: "
					+ totalCorrect + " ; Total incorrect: " + totalIncorrect;

			try {
				FileWriter fstream = new FileWriter(logPath + File.separator
						+ actualSentence + "_" + speakerLevel + "_results");
				BufferedWriter out = new BufferedWriter(fstream);
				out.write(log);
			} catch (Exception e) {
				System.out.println("Error writing results: " + e.getMessage());
			}

		}
	}

	/**
	 * @param args
	 *            The path to the folder where the files with the results will
	 *            be saved
	 */
	public static void main(String[] args) {
		if (args.length < 0) {
			System.out
					.println("Missing parameters: Specify the folder for saving results");
			System.exit(1);
		}
		SphinxTest sphinxTest = new SphinxTest(args[0]);
		sphinxTest.execute();
	}

}
