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

	/*
	 * static String acModelFile =
	 * "/home/blizarazu/Babelia/acoustic_model_files"; static String file =
	 * "/home/blizarazu/Dropbox/Babelia/speech_aldatuta"; static String
	 * saveFolder = "/home/blizarazu/Babelia/Angel_Heart-The_Egg_Scene"; static
	 * String fileName = "babelia";
	 */

	static String dictionary = "";
	static String file = "";
	static String saveFolder = "";

	static String log = "";

	static Vector<String> voca;

	static Vector<String> esaldiak;

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
		voca = new Vector<String>();
		esaldiak = new Vector<String>();

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
					esaldiak.addElement(lerroBerri);
					Vector<String> v = new Vector<String>(Arrays
							.asList(lerroBerri.split(" ")));
					for (String s : v) {
						s = s.toLowerCase();
						if (!voca.contains(s))
							voca.addElement(s);
					}
				}
			}

			for (String esaldia : esaldiak) {
				sortuEsaldikaGrammar(esaldia, h, esaldiak.indexOf(esaldia));
				sortuEsaldikaOsoaGrammar(esaldia, h, esaldiak
						.indexOf(esaldia));
				sortuHitzKopurkaGrammar(esaldia, h, esaldiak.indexOf(esaldia));
			}

			sortuOsoaGrammar();

			//sortuDfaJconf();

			System.out.println(log);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void sortuEsaldikaGrammar(String esaldia, Hiztegia h,
			int lerroKont) {
		String name = "esaldika";

		String esaldiPath = saveFolder + File.separator + lerroKont + "-"
				+ esaldia.replace(' ', '_');

		File file = new File(esaldiPath);
		if (!file.exists())
			file.mkdirs();

		String grammarEdukia = "grammar " + name + ";\n\n";
		grammarEdukia += "public <commands> = " + esaldia.toLowerCase();

		try {
			// Create file
			FileWriter fstream = new FileWriter(esaldiPath + File.separator
					+ name + ".grammar");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(grammarEdukia);
			// Close the output stream
			out.close();
			log += "\n\nGRAMMAR CREATED: " + name + ".gram\n\n";
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	private static void sortuEsaldikaOsoaGrammar(String esaldia,
			Hiztegia h, int lerroKont) {
		String name = "esaldika_osoa";

		String esaldiPath = saveFolder + File.separator + lerroKont + "-"
				+ esaldia.replace(' ', '_');

		File file = new File(esaldiPath);
		if (!file.exists())
			file.mkdirs();

		String grammarEdukia = "grammar " + name + ";\n\n";
		grammarEdukia += "public <commands> =";

		int hitzKop = esaldia.toLowerCase().split(" ").length;
		for (int i = 0; i < hitzKop; i++)
			grammarEdukia += " <word>";
		grammarEdukia += ";\n\n";

		grammarEdukia += "public <word> = "
				+ esaldia.toLowerCase().replace(" ", " | ");

		try {
			// Create file
			FileWriter fstream = new FileWriter(esaldiPath + File.separator
					+ name + ".gram");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(grammarEdukia);
			// Close the output stream
			out.close();
			log += "\n\nGRAMMAR CREATED: " + name + ".gram\n\n";
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	private static void sortuHitzKopurkaGrammar(String esaldia, Hiztegia h,
			int lerroKont) {
		String name = "hitz_kopuruka";

		String esaldiPath = saveFolder + File.separator + lerroKont + "-"
				+ esaldia.replace(' ', '_');

		File file = new File(esaldiPath);
		if (!file.exists())
			file.mkdirs();

		String grammarEdukia = "grammar " + name + ";\n\n";
		grammarEdukia += "public <commands> =";

		int hitzKop = esaldia.toLowerCase().split(" ").length;
		for (int i = 0; i < hitzKop; i++)
			grammarEdukia += " <word>";
		grammarEdukia += ";\n\n";

		grammarEdukia += "public <word> = ";

		boolean first = true;
		for (String word : voca) {
			if (!first) {
				grammarEdukia += " | ";
				first = false;
			}
			grammarEdukia += word;
		}
		grammarEdukia += ";";

		try {
			// Create file
			FileWriter fstream = new FileWriter(esaldiPath + File.separator
					+ name + ".grammar");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(grammarEdukia);
			// Close the output stream
			out.close();
			log += "\n\nGRAMMAR CREATED: " + name + ".gram\n\n";
		} catch (Exception e) {// Catch exception if any
			log += "makeHitzKopurkaGrammar: " + e.getMessage() + "\n";
			System.err.println("Error: " + e.getMessage());
		}
	}

	private static void sortuOsoaGrammar() {
		String name = "osoa";

		String grammarEdukia = "grammar " + name + ";\n\n";
		grammarEdukia += "public <commands> = <words>*;\n\n";

		grammarEdukia += "public <word> = ";

		boolean first = true;
		for (String word : voca) {
			if (!first) {
				grammarEdukia += " | ";
				first = false;
			}
			grammarEdukia += word;
		}
		grammarEdukia += ";";

		try {
			File dir = new File(saveFolder);
			String[] azpiDir = dir.list();
			for (int i = 0; i < azpiDir.length; i++) {
				File f = new File(saveFolder + File.separator + azpiDir[i]);
				if (f.isDirectory()) {
					FileWriter fstream = new FileWriter(f.getPath()
							+ File.separator + "osoa" + File.separator + name
							+ ".grammar");
					BufferedWriter out = new BufferedWriter(fstream);
					out.write(grammarEdukia);
					out.close();
					log += "\n\nGRAMMAR CREATED: " + name + ".gram\n\n";
				} else
					System.out.println(f.getPath() + " ez da dir");
			}
		} catch (IOException e) {
			log += "sortuOsoaGrammar: " + e.getMessage() + "\n";
			e.printStackTrace();
		}
	}

	/*
	 * private static void sortuOsoaVoca() { String vocaEdukia =
	 * "% NS_B\n<s>\tsil\n\n% NS_E\n</s>\tsil\n\n% WORD\n";
	 * 
	 * Set<String> keys = voca.keySet(); for (String key : keys) { vocaEdukia +=
	 * key + "\t\t"; Vector<String> v = voca.get(key); for (String f : v) if
	 * (!f.equals("sp")) vocaEdukia += " " + f; vocaEdukia += "\n"; }
	 * 
	 * try { File dir = new File(saveFolder); String[] azpiDir = dir.list(); for
	 * (int i = 0; i < azpiDir.length; i++) { File f = new File(saveFolder +
	 * File.separator + azpiDir[i]); if (f.isDirectory()) { File file = new
	 * File(f.getPath() + File.separator + "osoa"); if (!file.exists())
	 * file.mkdirs(); File file2 = new File(f.getPath() + File.separator +
	 * "hitz_kopuruka"); if (!file2.exists()) file2.mkdirs(); FileWriter fstream
	 * = new FileWriter(f.getPath() + File.separator + "osoa" + File.separator +
	 * fileName + ".voca"); BufferedWriter out = new BufferedWriter(fstream);
	 * out.write(vocaEdukia); out.close(); FileWriter fstream2 = new
	 * FileWriter(f.getPath() + File.separator + "hitz_kopuruka" +
	 * File.separator + fileName + ".voca"); BufferedWriter out2 = new
	 * BufferedWriter(fstream2); out2.write(vocaEdukia); out2.close(); } else
	 * System.out.println(f.getPath() + " ez da dir"); } } catch (IOException e)
	 * { log += "sortuOsoaGrammar: " + e.getMessage() + "\n";
	 * e.printStackTrace(); } }
	 */

	/*private static void sortuDfaJconf() {

		String jconfEdukia = "-dfa " + fileName + ".dfa\n";
		jconfEdukia += "-v " + fileName + ".dict\n";
		jconfEdukia += "-h " + dictionary + File.separator + "hmmdefs\n";
		jconfEdukia += "-hlist " + dictionary + File.separator + "tiedlist\n";
		jconfEdukia += "-smpFreq 48000\n";

		File dir = new File(saveFolder);
		String[] azpiDir = dir.list();
		for (int i = 0; i < azpiDir.length; i++) {
			File f = new File(saveFolder + File.separator + azpiDir[i]);
			if (f.isDirectory()) {
				String[] azpiDir2 = f.list();
				for (int j = 0; j < azpiDir2.length; j++) {
					File f2 = new File(f.getAbsolutePath() + File.separator
							+ azpiDir2[j]);
					if (f2.isDirectory()) {
						try {
							String command = "mkdfa " + fileName;
							Runtime.getRuntime().exec(command, null, f2);

							try {
								// Create file
								FileWriter fstream = new FileWriter(f2
										.getAbsolutePath()
										+ File.separator + fileName + ".jconf");
								BufferedWriter out = new BufferedWriter(fstream);
								out.write(jconfEdukia);
								// Close the output stream
								out.close();
							} catch (Exception e) {// Catch exception if any
								log += "sortuDfaJconf: jconf; "
										+ e.getMessage() + "\n";
								System.err.println("Error: " + e.getMessage());
							}

						} catch (IOException e) {
							log += "sortuDFA: mkdfa; " + e.getMessage() + "\n";
							e.printStackTrace();
						}
					}
				}
			}
		}
	}*/

}
