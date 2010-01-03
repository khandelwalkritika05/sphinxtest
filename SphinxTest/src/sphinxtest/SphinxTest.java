package sphinxtest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

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

	private List<String> sentences;
	private List<String> testTypes;

	private static final String COMPLETE_GRAMMAR_NAME = "osoa";

	public SphinxTest(String logPath) {
		URL url = SphinxTest.class.getResource("sphinxtest.config.xml");
		ConfigurationManager cm = new ConfigurationManager(url);

		recognizer = (Recognizer) cm.lookup("recognizer");
		jsgfGrammarManager = (JSGFGrammar) cm.lookup("jsgfGrammar");
		microphone = (Microphone) cm.lookup("microphone");

		this.logPath = logPath;
		this.log = "";

		loadSentences();
		loadTestTypes();
	}

	private void loadSentences() {
		sentences = Arrays
				.asList(
						"did you see him",
						"no",
						"why not",
						"it would've been difficult",
						"why",
						"why because he's not there",
						"johnny favorite walked out of the clinic years ago",
						"in his best suit with a new face wrapped in bandages and a headache",
						"he left with a guy called kelley and a girl",
						"do you know this kelley",
						"it seems this kelley paid off some bent doctor called fowler",
						"to pinch hit for your guy",
						"he's covered up for him all these years",
						"looks like our johnny has a perfect disappearing act",
						"it seems so",
						"but you know what they say about slugs",
						"no what do they say about slugs",
						"they always leave slime in their tracks",
						"you'll find him",
						"no i won't find him",
						"because i left out one little detail",
						"this dr fowler ended up dead with his fucking brains blown out",
						"did you kill him",
						"but the cops might think i did",
						"i took on a dollar dollars a-day missing persons job for you",
						"now i'm a murder suspect",
						"that's it i'm out",
						"such are the hazards of your profession",
						"if the fee bothers you we'll adjust it",
						"you bother me",
						"the closest i ever come to death is watching a hearse go by on nd avenue",
						"that's the way i like it",
						"are you afraid",
						"yeah i'm afraid",
						"i'll instruct my lawyer immediately to send you a check for dollar dollars",
						"if you don't want the job i'll engage someone else",
						"you want this johnny pretty bad eh",
						"i don't like messy accounts",
						"some religions think the egg is the symbol of the soul did you know that",
						"would you like an egg", "no thank you",
						"i got a thing about chickens");
	}

	private void loadTestTypes() {
		testTypes = Arrays.asList("esaldika", "esaldika_osoa", "hitz_kopuruka",
				"osoa");
	}

	public void execute() {
		System.out.println("SphinxTest\n");

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

		while (true) {
			actualSentence = "";

			System.out.println("Select sentence:");
			for (int i = 0; i < sentences.size(); i++)
				System.out.println(i + " - " + sentences.get(i));

			System.out.print("\nEnter number: ");
			int resp1;
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						System.in));
				resp1 = Integer.parseInt(br.readLine());
			} catch (IOException ioe) {
				System.out.println("Invalid level number");
				resp1 = -2;
			} catch (NumberFormatException e) {
				System.out.println("Invalid level number");
				resp1 = -2;
			}

			if (resp1 > -1) {
				actualSentence = sentences.get(resp1);

				System.out.println();
				while (true) {
					actualTestType = "";
					System.out.println("Select test type:");
					for (int i = 0; i < testTypes.size(); i++)
						System.out.println(i + " - " + testTypes.get(i));
					System.out.println("-1 - Return");

					System.out.print("\nEnter number: ");
					int resp2;
					try {
						BufferedReader br = new BufferedReader(
								new InputStreamReader(System.in));
						resp2 = Integer.parseInt(br.readLine());

					} catch (IOException ioe) {
						System.out.println("Invalid level number");
						resp2 = -2;
					} catch (NumberFormatException e) {
						System.out.println("Invalid level number");
						resp2 = -2;
					}

					if (resp2 > -1) {
						actualTestType = testTypes.get(resp2);

						String grammarName;
						if (actualTestType.equals(COMPLETE_GRAMMAR_NAME))
							grammarName = COMPLETE_GRAMMAR_NAME;
						else
							grammarName = actualSentence.replace(' ', '_')
									+ "_" + actualTestType;

						try {
							loadAndRecognize(grammarName);
						} catch (IOException e) {
							System.out.println("Grammar " + grammarName
									+ " doesn't exist");
						}
					} else if (resp2 == -1) {
						writeLog();
						actualSentence = "";

						totalCorrect = 0;
						totalIncorrect = 0;
						break;
					}
				}
			} else if (resp1 == -1)
				break;
		}
	}

	private void loadAndRecognize(String grammarName) throws IOException {
		jsgfGrammarManager.loadJSGF(grammarName);

		String results = "";
		int correct = 0;
		int incorrect = 0;

		while (true) {
			System.out.println("Start speaking: " + actualSentence + "\n");

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
					results += "\n\nResult: " + recogResultText + "\nCORRECT";
					correct++;
				} else if (resp.equalsIgnoreCase("I")) {
					results += "\n\nResult: " + recogResultText + "\nINCORRECT";
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
		log += "=============================================================================\n";
		log += "  Sentence: " + actualSentence + "\n";
		log += "  Test type: " + actualTestType + "\n";
		log += "  Grammar name:" + grammarName + "\n";
		log += "=============================================================================";
		log += "\n\n";
		log += results;
		log += "\n\n";
		log += "-----------\n";
		log += "Correct: " + correct + "; Incorrect: " + incorrect;
		log += "\n\n\n";

		totalCorrect += correct;
		totalIncorrect += incorrect;
	}

	private void writeLog() {
		if (!log.isEmpty()) {
			String header = "Speaker level: " + speakerLevel.toUpperCase()
					+ "\n" + "Sentence: " + actualSentence + "\n"
					+ "------------------------------" + "\n\n\n"
					+ "Total correct: " + totalCorrect + " ; Total incorrect: "
					+ totalIncorrect;
			log = header + "\n\n\n=======RESULTS======\n" + log;

			File file = new File(logPath + File.separator
					+ speakerLevel);
			if (!file.exists())
				file.mkdir();

			try {
				FileWriter fstream = new FileWriter(logPath + File.separator
						+ speakerLevel + File.separator
						+ actualSentence.replace(" ", "_") + "_" + speakerLevel
						+ "_results");
				BufferedWriter out = new BufferedWriter(fstream);
				out.write(log);
				out.close();
				log = "";
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
		if (args.length < 1) {
			System.out
					.println("Missing parameters: Specify the folder for saving results");
			System.exit(1);
		}
		SphinxTest sphinxTest = new SphinxTest(args[0]);
		sphinxTest.execute();
	}

}
