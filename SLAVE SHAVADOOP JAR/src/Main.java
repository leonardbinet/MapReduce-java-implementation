import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


public class Main {


    public static void main(String[] args) {
    	
    	// on prend en argument le nom d'un fichier
		String dossier = "/cal/homes/lbinet/workspace/Sys_distribue/Sx/";
		Path sx_input = Paths.get(dossier+args[0]);
		List<String> lignes;
		try {
			lignes = Files.readAllLines(sx_input, Charset.forName("UTF-8"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    }


}
