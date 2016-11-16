package operations;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import main.Config;
import main.Utils;

public class Reducing {
	private Config config;
	private String[] args;
	
	public Reducing(String[] args, Config config){
		this.config = config;
		this.args = args;
	}
	
	public void launchReduce(){
    	String key = this.args[1];
    	String sm_name = this.args[2]; 								
	    Integer id =  Utils.getId(sm_name);
    	HashSet<String> um_names = new HashSet<String>(); 	
    	for(int i=3; i <= args.length -1; i++) {
    		um_names.add(args[i]);
    	    }
    	
		List<String> allLignes = new ArrayList<String>(); 
		try {
			
			for (String um_name:um_names){
				Path Umx_input = config.folderUmx.resolve(um_name);
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
				}    				
			}
			Path smx_output = config.folderSmx.resolve(sm_name); 		
			Files.write(smx_output, smx_to_write, Charset.forName("UTF-8"));
			
			Path rmx_output = config.folderRmx.resolve("Rm"+id); 
			String rmx = key+" "+smx_to_write.size();
			Files.write(rmx_output, Arrays.asList(rmx), Charset.forName("UTF-8"));
			System.out.println(rmx);
		} catch (IOException e1) {
		e1.printStackTrace();
		}
	}
}
