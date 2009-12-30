package nagusia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

public class Nagusia {

	// static String dicFile =
	// "/home/blizarazu/Babelia/acoustic_model_files/dict";

	// static String acModelFile =
	// "/home/blizarazu/Babelia/acoustic_model_files";
	// static String file = "/home/blizarazu/Dropbox/Babelia/speech_aldatuta";
	// static String saveFolder =
	// "/home/blizarazu/Babelia/Angel_Heart-The_Egg_Scene";
	// static String fileName = "babelia";

	static String dictionary = "";
	static String file = "";
	static String saveFolder = "";

	static String log = "";

	static Vector<String> allWords;

	static Vector<String> esaldiak;
	static Vector<String> motak;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length < 3) {
			System.out
					.println("Missing parameters: specify the path to the dictionary, the file with text and the folder for saving the grammar files");
			System.exit(1);
		}

		dictionary = args[0];
		file = args[1];
		saveFolder = args[2];

		Hiztegia h = new Hiztegia(dictionary);
		allWords = new Vector<String>();
		esaldiak = new Vector<String>();
		motak = new Vector<String>();

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));

			String line;
			// int lerroKont = 0;
			while ((line = br.readLine()) != null) {
				String lerroBerri = line.replaceAll(
						"[!\\\"#%&()\\*+,\\./:;<=>?@[\\\\]_`{|}~]|^-", "");
				lerroBerri = lerroBerri.replaceAll(" +- ?", " ");
				lerroBerri = lerroBerri.replaceAll("\\$", " DOLLAR DOLLARS ");
				lerroBerri = lerroBerri.replaceAll("[ \\t\\r\\n\\v\\f]+", " ");
				lerroBerri = lerroBerri.trim();

				if (lerroBerri != null && lerroBerri.length() != 0) {
					// lerroKont++;
					if (!esaldiak.contains(lerroBerri.toLowerCase()))
						esaldiak.addElement(lerroBerri.toLowerCase());
					Vector<String> v = new Vector<String>(Arrays
							.asList(lerroBerri.split(" ")));
					for (String s : v) {
						s = s.toLowerCase();
						if (!allWords.contains(s))
							allWords.addElement(s);
					}
				}
			}

			for (String esaldia : esaldiak) {
				sortuEsaldikaGrammar(esaldia, h, esaldiak.indexOf(esaldia));
				sortuEsaldikaOsoaGrammar(esaldia, h, esaldiak.indexOf(esaldia));
				sortuHitzKopurkaGrammar(esaldia, h, esaldiak.indexOf(esaldia));
			}

			sortuOsoaGrammar();

			// sortuDfaJconf();

			System.out.println(log);

			System.out.println("\n\n==============ESALDIAK==============\n");
			for (String s1 : esaldiak)
				System.out.println("\"" + s1 + "\"");
			System.out.println("\n\n==============MOTAK==============\n");
			for (String s2 : motak)
				System.out.println("\"" + s2 + "\"");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void sortuEsaldikaGrammar(String esaldia, Hiztegia h,
			int lerroKont) {
		String name = "esaldika";
		if (!motak.contains(name))
			motak.addElement(name);

		// String esaldiPath = saveFolder + File.separator + lerroKont + "-"
		// + esaldia.replace(' ', '_');

		File file = new File(saveFolder);
		if (!file.exists())
			file.mkdirs();

		String grammarEdukia = "grammar " + esaldia.replace(' ', '_') + "_"
				+ name + ";\n\n";
		grammarEdukia += "public <commands> = " + esaldia.toLowerCase();

		try {
			// Create file
			FileWriter fstream = new FileWriter(saveFolder + File.separator
					+ esaldia.replace(' ', '_') + "_" + name + ".gram");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(grammarEdukia);
			// Close the output stream
			out.close();
			log += "GRAMMAR CREATED: " + esaldia.replace(' ', '_') + "_" + name
					+ ".gram\n";
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	private static void sortuEsaldikaOsoaGrammar(String esaldia, Hiztegia h,
			int lerroKont) {
		String name = "esaldika_osoa";
		if (!motak.contains(name))
			motak.addElement(name);

		// String esaldiPath = saveFolder + File.separator + lerroKont + "-"
		// + esaldia.replace(' ', '_');

		File file = new File(saveFolder);
		if (!file.exists())
			file.mkdirs();

		String grammarEdukia = "grammar " + esaldia.replace(' ', '_') + "_"
				+ name + ";\n\n";
		grammarEdukia += "public <commands> =";

		int hitzKop = esaldia.toLowerCase().split(" ").length;
		for (int i = 0; i < hitzKop; i++)
			grammarEdukia += " <word>";
		grammarEdukia += ";\n\n";

		grammarEdukia += "public <word> = "
				+ esaldia.toLowerCase().replace(" ", " | ");

		try {
			// Create file
			FileWriter fstream = new FileWriter(saveFolder + File.separator
					+ esaldia.replace(' ', '_') + "_" + name + ".gram");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(grammarEdukia);
			// Close the output stream
			out.close();
			log += "GRAMMAR CREATED: " + esaldia.replace(' ', '_') + "_" + name
					+ ".gram\n";
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	private static void sortuHitzKopurkaGrammar(String esaldia, Hiztegia h,
			int lerroKont) {
		String name = "hitz_kopuruka";
		if (!motak.contains(name))
			motak.addElement(name);

		// String esaldiPath = saveFolder + File.separator + lerroKont + "-"
		// + esaldia.replace(' ', '_');

		File file = new File(saveFolder);
		if (!file.exists())
			file.mkdirs();

		String grammarEdukia = "grammar " + esaldia.replace(' ', '_') + "_"
				+ name + ";\n\n";
		grammarEdukia += "public <commands> =";

		int hitzKop = esaldia.toLowerCase().split(" ").length;
		for (int i = 0; i < hitzKop; i++)
			grammarEdukia += " <word>";
		grammarEdukia += ";\n\n";

		grammarEdukia += "public <word> = ";

		for (String word : allWords) {
			if (allWords.indexOf(word) > 0)
				grammarEdukia += " | ";
			grammarEdukia += word;
		}
		grammarEdukia += ";";

		try {
			// Create file
			FileWriter fstream = new FileWriter(saveFolder + File.separator
					+ esaldia.replace(' ', '_') + "_" + name + ".gram");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(grammarEdukia);
			// Close the output stream
			out.close();
			log += "GRAMMAR CREATED: " + esaldia.replace(' ', '_') + "_" + name
					+ ".gram\n";
		} catch (Exception e) {// Catch exception if any
			log += "makeHitzKopurkaGrammar: " + e.getMessage() + "\n";
			System.err.println("Error: " + e.getMessage());
		}
	}

	private static void sortuOsoaGrammar() {
		String name = "osoa";
		if (!motak.contains(name))
			motak.addElement(name);

		String grammarEdukia = "grammar " + name + ";\n\n";
		grammarEdukia += "public <commands> = <word>*;\n\n";

		grammarEdukia += "public <word> = ";

		for (String word : allWords) {
			if (allWords.indexOf(word) > 0)
				grammarEdukia += " | ";
			grammarEdukia += word;
		}
		grammarEdukia += ";";

		try {
			// Create file
			FileWriter fstream = new FileWriter(saveFolder + File.separator
					+ name + ".gram");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(grammarEdukia);
			// Close the output stream
			out.close();
			log += "GRAMMAR CREATED: " + name + ".gram\n";
		} catch (Exception e) {// Catch exception if any
			log += "makeHitzKopurkaGrammar: " + e.getMessage() + "\n";
			System.err.println("Error: " + e.getMessage());
		}
	}
}
