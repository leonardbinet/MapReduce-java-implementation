package operations;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import main.Config;
import main.Utils;

public class Mapping {
	private Config config;
	private String sxName;

	
	public Mapping(String sxName, Config config){
		this.config = config;
		this.sxName = sxName;
	}
	
	public void launchMapping(){
		Path sx_input = config.folderSx.resolve(this.sxName);
	    Integer id =  Utils.getId(sxName);
		Path umx_output = config.folderUmx.resolve("Um"+id);
		List<String> lines;
		try {
			lines = Files.readAllLines(sx_input, Charset.forName("UTF-8"));
			ArrayList<String> Umx_write = new ArrayList<String>();
			for (String ligne: lines ){
				// words are separated by spaces
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
			e1.printStackTrace();
			}
	}
	
}
