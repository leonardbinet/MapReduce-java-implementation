import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Algo1SplitAndSend {

	// prend un chemin de fichier input en argument
	// prend un chemin de fichier output en argument
	// écrit sur le disque le split sur fichier output
	// peut renvoyer la liste des Sx
	
	private Path input_file;
	private String output_folder;
	private ArrayList<String> sx_list;
	private ArrayList<String> machine_list;
	private HashMap<String, String> umx_machines_dict;
	private HashMap<String, ArrayList<String>> response_dict;
	
	public Algo1SplitAndSend(Path input_file, String output_folder){
		this.input_file = input_file;
		this.output_folder = output_folder;
		this.response_dict = new HashMap<String, ArrayList<String>>();
	}
	
	public void split() throws IOException{
		// on lit le fichier Input.txt ligne par ligne
		Path input_file = this.input_file;
		String folder = this.output_folder;
		ArrayList<String> sx_list = new ArrayList<String>();
		List<String> lignes;
		Integer i = 0;
		lignes = Files.readAllLines(input_file, Charset.forName("UTF-8"));
		for (String ligne :lignes ){
			// on affiche la ligne
			System.out.println(ligne);
			// on l'écrit dans un fichier nommé S<num ligne>
			Path sx = Paths.get(folder+i);
			Files.write(sx, Arrays.asList(ligne), Charset.forName("UTF-8"));
			i += 1;
			sx_list.add("S"+i);
		}
		this.sx_list = sx_list;
	}
	
	public ArrayList<String> getListSxNames(){
		return this.sx_list;
	}
	
	public void set_machines(ArrayList<String> liste_machines){
		this.machine_list = liste_machines;
	}
	
	public void sendSplitOrderToMachines(){
		// cette méthode renvoie le dictionnaire Umx-machines
		// idéalement on réalise un scp pour envoyer les fichiers (ici on triche)
		// on lance la procédure s'il y a des machines
		// TODO répartir si machines < jobs
		HashMap<String,String> umx_machine = new HashMap<String,String>();
		ArrayList<String> liste_machines_ok = this.machine_list;
		ArrayList<String> sx_list = this.sx_list;
		
		if (liste_machines_ok != null) {
            ArrayList<LaunchSlaveShavadoop> slaves = new ArrayList<LaunchSlaveShavadoop>();
            for (int k = 0; k < sx_list.size(); k++) {
				String machine = liste_machines_ok.get(k);
				umx_machine.put("Um"+k, machine);
				System.out.println("Envoi de S"+k+" à la machine "+machine+" devant nous renvoyer Um"+k);
            	LaunchSlaveShavadoop slave = new LaunchSlaveShavadoop(machine,
                        "cd workspace/Sys_distribue;java -jar SLAVESHAVADOOP.jar S"+k, 20);
                slave.start();
                slaves.add(slave);
			}
            for (LaunchSlaveShavadoop slave : slaves) {
                try {
                    slave.join();
                    // on attend que le thread soit terminé pour ajouter au dictionnaire
                    this.response_dict.put(slave.getMachine(), slave.get_response());
                    
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            
            System.out.println("Algo1 terminé");
        }
		this.umx_machines_dict = umx_machine;
	}
	public HashMap<String, String> getUmxMachineHM(){
		return this.umx_machines_dict;
	}
	
	public HashMap<String, ArrayList<String>> getResponses(){
		return this.response_dict;
	};

}
