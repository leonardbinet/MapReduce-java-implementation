package wordcount;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import main.Utils;

public class CleanImport {

	public static ArrayList<String> cleanImport(Path inputPath) throws IOException {
		Utils.printBeautiful("Clean import");
		List<String> lignes = new ArrayList<String>();
		ArrayList<String> lignes_clean = new ArrayList<String>();
		lignes = Files.readAllLines(inputPath, Charset.forName("UTF-8"));
		for (String ligne : lignes) {
			// get rid of empty lines
			if (ligne.length() != 0) {
				// get rid of special characters
				ligne = ligne.replaceAll("[^\\p{L}\\p{Z}]", "");
				ligne = ligne.trim();
				ligne = ligne.toLowerCase();
				if (ligne.length() != 0) {
					lignes_clean.add(ligne);
				}
			}
		}
		return lignes_clean;
	}
}
