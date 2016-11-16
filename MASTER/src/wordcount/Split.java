package wordcount;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import main.Utils;

public class Split {
	
	public static ArrayList<String> split(Path sxFolder, ArrayList<String> inputContent, Integer linesPerSplit) throws IOException{
		Utils.printBeautiful("SPLIT");
		
		ArrayList<String> sxList = new ArrayList<String>();
		Integer i = 0;
		ArrayList<String> bloc = new ArrayList<String>();
		
		for (int k=0; k< inputContent.size(); k++){
			String ligne = inputContent.get(k);
			System.out.println(ligne);
			bloc.add(ligne);
			if (k+1 % linesPerSplit == 0 || k==inputContent.size()-1){
				// on écrit par blocs de 100 lignes
				// on l'écrit dans un fichier nommé S<num ligne>	
				Path sxi = sxFolder.resolve("S"+i);
				Files.write(sxi, bloc, Charset.forName("UTF-8"));
				sxList.add("S"+i);
				i += 1;
				bloc = new ArrayList<String>();
			}
		}
		return sxList;
	}
}
