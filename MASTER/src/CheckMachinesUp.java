import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CheckMachinesUp {

	private Path file_machines_to_test;
	private Path file_machines_ok;
	private ArrayList<String> liste_machines_ok;
	private Config config;
	
	public CheckMachinesUp(String input_path, String output_path){
		this.file_machines_to_test = Paths.get(input_path);
		this.file_machines_ok = Paths.get(output_path);
		this.config = new Config();
	}
	
	public void test_Machines_Up(){
		List<String> machines;
		ArrayList<ConnectionSSH> listeTests = new ArrayList<ConnectionSSH>();

		Path filein = this.file_machines_to_test;
		try {
			// on charge le nom des machines
			machines = Files.readAllLines(filein, Charset.forName("UTF-8"));
			for (String machine : machines) {
				/*
				 * on teste la connection SSH pendant 'timeout' secondes maximum
				 */
				ConnectionSSH test = new ConnectionSSH(machine, this.config.test_timeout);
				test.start();
				listeTests.add(test);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ArrayList<String> liste_machines_ok = new ArrayList<String>();
		for (ConnectionSSH test : listeTests) {
			try {
				test.join();// on attend la fin du test
				if (test.isConnectionOK()) {
					liste_machines_ok.add(test.getMachine());
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// on ecrit dans un fichier texte la liste des noms des machines qui répondent
		Path file = this.file_machines_ok;
		try {
			Files.write(file, liste_machines_ok, Charset.forName("UTF-8"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.liste_machines_ok = liste_machines_ok;
	}
	public ArrayList<String> get_Machines_Up(){
		return this.liste_machines_ok;
	}
}
