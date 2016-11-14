import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {


    public static void main(String[] args) {
    	// racine: 
    	String dossier = "/cal/homes/lbinet/workspace/Sys_distribue/";
    	// lieu où sont stockés les Sx
    	String dossierSx = dossier+"Sx/";
    	// lieu où sont stockés les Umx
    	String dossierUmx = dossier+"Umx/";
    	// lieu où sont stockés les Smx
    	String dossierSmx = dossier+"Smx/";
    	// lieu où sont stockés les Rmx
    	String dossierRmx = dossier+"Rmx/";
    	
    	// Verification qu'on a bien un argument de passé
        if(args.length == 0)
        {
            System.out.println("Aucun argument n'a été passé.");
            System.exit(0);
        }
        else if (args[0].equals("modeSXUMX")){
        	
        	// chemin de lecture Sx
    		Path sx_input = Paths.get(dossierSx + args[1]);
    		// obtention de l'id à partir du nom de notre um
		    // Create a Pattern object
		    Pattern r = Pattern.compile("(\\d+)");
		    // Now create matcher object.
		    Matcher m = r.matcher(args[1]);
		    m.find();
		    String id =  m.group(0);

    		// chemin d'ecriture Umx
    		Path umx_output = Paths.get(dossierUmx+"Um"+id);

    		// lecture du fichier entrée
    		List<String> lignes;
    		try {
    			lignes = Files.readAllLines(sx_input, Charset.forName("UTF-8"));
    			ArrayList<String> Umx_write = new ArrayList<String>();
    			for (String ligne: lignes ){
    				// les mots sont séparés par des espaces
    				String[] words = ligne.split(" ");
    				for (String word: words){
    					if (word.length()>0){
        					Umx_write.add(word + " 1");
        					System.out.println(word);
    					}
    				}
    			}
    			Files.write(umx_output, Umx_write, Charset.forName("UTF-8"));
    			} catch (IOException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}
        }
        else if (args[0].equals("modeUMXSMX")){
        	
        
        	String key = args[1]; 									// clé demandée
        	String sm_name = args[2]; 								// nom du fichier de sortie
        	// obtention de l'id à partir du nom de notre um
		    // Create a Pattern object
		    Pattern r = Pattern.compile("(\\d+)");
		    // Now create matcher object.
		    Matcher m = r.matcher(sm_name);
		    m.find();
		    String id =  m.group(0);
    		
        	HashSet<String> um_names = new HashSet<String>(); 	// noms des fichiers entrée
        	for(int i=3; i <= args.length -1; i++) {
        		um_names.add(args[i]);
        	    }
        	//System.out.println("Création du fichier \""+sm_name+"\" pour la clé \""+key+"\" à partir des fichiers "+um_names.toString());
        	
    		// lecture des fichiers d'entrée Umx
    		List<String> allLignes = new ArrayList<String>(); 
    		try {
    			
    			for (String um_name:um_names){
    				Path Umx_input = Paths.get(dossierUmx+um_name);
    				List<String> lignes;
    				lignes = Files.readAllLines(Umx_input, Charset.forName("UTF-8"));
    				allLignes.addAll(lignes);
    			}
    			
    			ArrayList<String> smx_to_write = new ArrayList<String>();
    			for (String ligne: allLignes ){
    				// format: word 1
    				String word = ligne.split(" ")[0];
					// append to list if = key
    				if (word.toLowerCase().equals(key.toLowerCase())){
    					smx_to_write.add(word+" 1");
    					//System.out.println(word+" 1");
    				}    				
    			}
    			Path smx_output = Paths.get(dossierSmx+sm_name); 		// chemin d'ecriture Smx 
    			Files.write(smx_output, smx_to_write, Charset.forName("UTF-8"));
    			
    			// Ecriture de Rmx
    			Path rmx_output = Paths.get(dossierRmx+"Rm"+id); // chemin d'ecriture Rmx 
    			String rmx = key+" "+smx_to_write.size();
    			Files.write(rmx_output, Arrays.asList(rmx), Charset.forName("UTF-8"));
    			System.out.println(rmx);
			} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			}
    		
    	}
        else {
        	System.out.println("Le premier argument n'est pas correct: il faut 'modeSXUMX' ou 'modeUMXSMX'.");
            System.out.println("arg 0: "+args[0]);
        	System.exit(0);
        }	
    }
}
