import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class Main {


    public static void main(String[] args) {
    	
    	// Verification qu'on a bien un argument de passé
        // Check how many arguments were passed in
        if(args.length == 0)
        {
            System.out.println("Aucun argument n'a été passé.");
            System.exit(0);
        }
    	
    	// on prend en argument le nom d'un fichier
		String dossier = "/cal/homes/lbinet/workspace/Sys_distribue/";
		//String dossier = "/Users/leonardbinet/Documents/Formation/Cours Telecom/T1_Systemes_repartis_IN727/Shavadoop/Sx/";
		Path sx_input = Paths.get(dossier+"Sx/"+args[0]);
		List<String> lignes;
		
		// get number in arg[0]
		String id = args[0].substring(args[0].length() - 1);
		// path to write files
		Path umx_output = Paths.get(dossier+"Umx/Um"+id);
		
		// try to read file
		try {
			lignes = Files.readAllLines(sx_input, Charset.forName("UTF-8"));
			ArrayList<String> Umx_write = new ArrayList<String>();
			for (String ligne: lignes ){
				// each line has words separeted by spaces
				String[] words = ligne.split(" ");
				for (String word: words){
					// append to list
					Umx_write.add(word + " 1");
					System.out.println(word);
				}
			}
			Files.write(umx_output, Umx_write, Charset.forName("UTF-8"));
			//System.out.println("Ecriture terminée de Um"+id);
			} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

		
    }


}
