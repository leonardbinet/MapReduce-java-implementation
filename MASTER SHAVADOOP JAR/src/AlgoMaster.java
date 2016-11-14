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
import java.lang.Integer;

public class AlgoMaster {

	// prend un path de fichier input en argument
	// prend un nom de chemin output en argument
	
	private Path input_path;
	private ArrayList<String> input_content;
	private String root_folder;
	private ArrayList<String> sx_list;
	private ArrayList<String> machine_list;
	private HashMap<String, String> umx_machine;
	private HashMap<String, ArrayList<String>> machine_keys;
	private HashMap<String, ArrayList<String>> umx_keys;
	private HashMap<String, ArrayList<String>> key_umxs;
	private ArrayList<List<String>> machine_command;
	private HashMap<String, String> rmx_machine;
	private ArrayList<String> rmx_final;
	

	public AlgoMaster(Path input_file, String root_folder){
		this.input_path = input_file;
		this.root_folder = root_folder;
	}
	
	public void cleanImportInput() throws IOException{
		// renvoi liste de string
		Path input_file = this.input_path;
		List<String> lignes = new ArrayList<String>();
		ArrayList<String> lignes_clean = new ArrayList<String>();
		lignes = Files.readAllLines(input_file, Charset.forName("UTF-8"));
		for (String ligne :lignes ){
			// on enlève les lignes vides
			if (ligne.length()!=0){
				// on enlève les caractères spéciaux
				ligne = ligne.replaceAll("[^\\p{L}\\p{Z}]","");
				ligne = ligne.trim();
				if (ligne.length()!=0){
					lignes_clean.add(ligne);	
				}
			}
		}
		this.input_content = lignes_clean;
	}
	
	public void split() throws IOException{
		// on lit le fichier Input.txt ligne par ligne
		String sx_Folder = this.root_folder+"Sx/";
		ArrayList<String> sx_list = new ArrayList<String>();
		List<String> lignes;
		Integer i = 0;
		lignes = this.input_content;
		for (String ligne :lignes ){
			// on affiche la ligne
			System.out.println(ligne);
			// on l'écrit dans un fichier nommé S<num ligne>
			Path sx = Paths.get(sx_Folder+"S"+i);
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
	
	public void sendMapOrderToMachines(){
		// cette méthode créé le dictionnaire Umx-machines
		// idéalement on réalise un scp pour envoyer les fichiers (ici on triche)
		// on lance la procédure s'il y a des machines
		// TODO répartir si machines < jobs
		
		// Initialisation de notre dictionnaire (qui trace ce que l'on a envoyé)
		this.umx_machine = new HashMap<String,String>();
		// Initialisation de notre dictionnaire de réponse
		this.machine_keys = new HashMap<String, ArrayList<String>>();
		this.umx_keys = new HashMap<String, ArrayList<String>>();

		// on divise le nombre de split par le nombre de machines
		if (this.machine_list != null) {
			Integer nbr_mach =  this.machine_list.size();
			Integer nbr_splits =  this.sx_list.size();
			ArrayList<LaunchSlaveShavadoop> slaves = new ArrayList<LaunchSlaveShavadoop>();
            for (int k = 0; k < nbr_splits; k++) {
            	// on prend la k module nbr_mach ième machine
				String machine = this.machine_list.get(k % nbr_mach);
				this.umx_machine.put("Um"+k, machine);
				System.out.println("Envoi de S"+k+" à la machine "+machine+" devant nous renvoyer Um"+k);
            	LaunchSlaveShavadoop slave = new LaunchSlaveShavadoop(machine,
                        "cd workspace/Sys_distribue;java -jar SLAVESHAVADOOP.jar modeSXUMX S"+k, 20);
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
            
            //System.out.println("Mapping terminé");
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
	
	public void prepare_job_dispatch(){
		// à partir du dict machine_keys on va déterminer à qui on va envoyer quels jobs
		// le dictionnaire machine => commande est enregistré en output
		// version simple et débile, itération sur les clés
		// TODO algo plus intelligent à faire
		
		ArrayList<List<String>> machine_command = new ArrayList<List<String>>();
		Integer i = 0;
		Integer nbr_mach =  this.machine_list.size();
		for (Map.Entry<String, ArrayList<String>> key_values: this.key_umxs.entrySet()){
			String machine = this.machine_list.get(i % nbr_mach);
			String key = key_values.getKey();
			ArrayList<String> umxs = key_values.getValue();
			String umx_concat = "";
			for (String umx: umxs){
				umx_concat += " "+umx; // attention, commence par un " "
			} 
			String command = "modeUMXSMX "+key+" SM"+i+umx_concat;
			List<String> temp =Arrays.asList(machine,command) ;
			machine_command.add(temp);
			i += 1;
		}
		this.machine_command = machine_command;
	}
	
	public void sendReduceOrder(){
		// on envoie les ordres 
		this.rmx_machine = new HashMap<String, String>();
		this.rmx_final = new ArrayList<String>();
		
		if (this.machine_command != null) {
            ArrayList<LaunchSlaveShavadoop> slaves = new ArrayList<LaunchSlaveShavadoop>();
            for (List<String>  entry: this.machine_command) {
				System.out.println("Envoi de la commande "+ entry.get(1)+" à la machine "+entry.get(0)+".");
            	LaunchSlaveShavadoop slave = new LaunchSlaveShavadoop(entry.get(0),
                        "cd workspace/Sys_distribue;java -jar SLAVESHAVADOOP.jar "+entry.get(1), 20);
                slave.start();
                slaves.add(slave);
			}
            for (int k = 0; k < slaves.size(); k++) {
                try {
                	LaunchSlaveShavadoop slave = slaves.get(k);
                    slave.join();
                    // on attend que le thread soit terminé pour ajouter au dictionnaire
                    this.rmx_machine.put(slave.get_response().get(0), slave.getMachine());
                    this.rmx_final.add(slave.get_response().get(0));
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            
        }
	}
	public ArrayList<List<String>> get_machine_command(){
		return this.machine_command;
	}
	
	public HashMap<String, String> get_rmx_machine(){
		return this.rmx_machine;
	}
	public ArrayList<String> get_rmx_final(){
		return this.rmx_final;
	}
	public void write_rmx() throws IOException{
		String rmx_Folder = this.root_folder+"Result/";
		Path output = Paths.get(rmx_Folder+"Rmx");
		Files.write(output, this.rmx_final, Charset.forName("UTF-8"));
	}
}
