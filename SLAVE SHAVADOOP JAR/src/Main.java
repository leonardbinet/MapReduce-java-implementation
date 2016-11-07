import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Main {


    public static void main(String[] args) {
    	
    	// on prend en argument le nom d'un fichier
		String dossier = "/cal/homes/lbinet/workspace/Sys_distribue/";
		//String dossier = "/Users/leonardbinet/Documents/Formation/Cours Telecom/T1_Systemes_repartis_IN727/Shavadoop/Sx/";
		Path sx_input = Paths.get(dossier+"Sx/"+args[0]);
		List<String> lignes;
		
		// path to write files
		Path umx_output = Paths.get(dossier+"Umx/Um0");
		
		// try to read file
		try {
			lignes = Files.readAllLines(sx_input, Charset.forName("UTF-8"));
			
			// create dictionary unsorted map: maybe useful for later
			HashMap<String, Integer> Umx = new HashMap<String, Integer>();
			// create list to write
			ArrayList<String> Umx_write = new ArrayList<String>();
			// line after line
			for (String ligne: lignes ){
				// each line has words separeted by spaces
				String[] words = ligne.split(" ");
				for (String word: words){
					// append to dictionary
					Umx.put(word, 1);
					// append to list
					Umx_write.add(word + " 1");
				}
			}
			Files.write(umx_output, Umx_write, Charset.forName("UTF-8"));	
			} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

		
    }


}
