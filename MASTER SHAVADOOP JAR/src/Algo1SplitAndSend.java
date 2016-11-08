import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Algo1SplitAndSend {

	// prend un chemin de fichier input en argument
	// prend un chemin de fichier output en argument
	// écrit sur le disque le split sur fichier output
	// peut renvoyer la liste des Sx
	
	private Path input_file;
	private String output_folder;
	private ArrayList<String> sx_list;
	private ArrayList<String> machine_list;
	private HashMap<String, String> umx_machine;
	private HashMap<String, ArrayList<String>> machine_keys;
	private HashMap<String, ArrayList<String>> umx_keys;
	private HashMap<String, ArrayList<String>> key_umxs;

	public Algo1SplitAndSend(Path input_file, String output_folder){
		this.input_file = input_file;
		this.output_folder = output_folder;
		
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
		
		// Initialisation de notre dictionnaire (qui trace ce que l'on a envoyé)
		this.umx_machine = new HashMap<String,String>();
		// Initialisation de notre dictionnaire de réponse
		this.machine_keys = new HashMap<String, ArrayList<String>>();
		this.umx_keys = new HashMap<String, ArrayList<String>>();

		if (this.machine_list != null) {
            ArrayList<LaunchSlaveShavadoop> slaves = new ArrayList<LaunchSlaveShavadoop>();
            for (int k = 0; k < sx_list.size(); k++) {
            	// on prend la kième machine
				String machine = this.machine_list.get(k);
				this.umx_machine.put("Um"+k, machine);
				System.out.println("Envoi de S"+k+" à la machine "+machine+" devant nous renvoyer Um"+k);
            	LaunchSlaveShavadoop slave = new LaunchSlaveShavadoop(machine,
                        "cd workspace/Sys_distribue;java -jar SLAVESHAVADOOP.jar S"+k, 20);
                slave.start();
                slaves.add(slave);
			}
            for (int k = 0; k < sx_list.size(); k++) {
                try {
                	LaunchSlaveShavadoop slave = slaves.get(k);
                    slave.join();
                    // on attend que le thread soit terminé pour ajouter au dictionnaire
                    this.machine_keys.put(slave.getMachine(), slave.get_response());
                    this.umx_keys.put("Um"+k, slave.get_response());
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            
            System.out.println("Algo1 terminé");
        }
	}
	public HashMap<String, String> getUmxMachineDict(){
		return this.umx_machine;
	}
	
	public HashMap<String, ArrayList<String>> getResponses(){
		return this.machine_keys;
	};
	
	public void reverse_index(){
		// INPUT
		//          Umx - [keys]
		// TRANSFORM
		// =>       Umx - key // Umx - key // etc
		// =>       key - [Umxs]
		// OUTPUT
		//          key - [Umxs]
		
		HashMap<String,ArrayList<String>> inversed = new HashMap<String,ArrayList<String>>(this.umx_keys.size());
	    
		for(Map.Entry<String, ArrayList<String>> entry : this.umx_keys.entrySet()) {
	        for(String key : entry.getValue()) {    // entry.getValue() est ArrayList<String> (keys)         
	            if(!inversed.containsKey(key)) { 
	                inversed.put(key,new ArrayList<String>());
	            }
	            inversed.get(key).add(entry.getKey());
	        } 
	    }
		this.key_umxs = inversed;
	}
	
	public HashMap<String,ArrayList<String>> getKeyUmxs(){
		return this.key_umxs;
		
	}

}
