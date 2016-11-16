package main;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import network.CheckMachinesUp;
import network.SshCommand;

import java.lang.Integer;

public class WordCount {
	
	private ArrayList<String> input_content;
	private ArrayList<String> sx_list;
	private ArrayList<String> machine_list;
	private HashMap<String, String> umx_machine;
	private HashMap<String, ArrayList<String>> machine_keys;
	private HashMap<String, ArrayList<String>> umx_keys;
	private HashMap<String, HashSet<String>> key_umxs;
	private ArrayList<List<String>> machine_command;
	private HashMap<String, String> rmx_machine;
	private List<String> mot_filtres;
	private ArrayList<ReduceResult> rmx_final;
	private ArrayList<String> rmx_final_raw;
	private Config config;

	public WordCount(Config config){
		this.config = config;
	}
	
	public void computeWordCount() throws IOException, InterruptedException{
		
		// Initialize processing times
		long startTime = System.currentTimeMillis();
		long startStepTime;
		long totalTime;

		// Check responding machines
		startStepTime = System.currentTimeMillis();
		CheckMachinesUp checkMachines = new CheckMachinesUp(this.config.test_timeout);
		checkMachines.readMachinesToTest(this.config.machinesToTestPath);
		checkMachines.test_Machines_Up();
		checkMachines.writeRespondingMachines(this.config.machinesRespondingPath);
		ArrayList<String> liste_machines_ok = checkMachines.get_Machines_Up();
		System.out.println("---Time: "+(System.currentTimeMillis() - startStepTime)+" ---");
		
		// Import and clean file
		startStepTime = System.currentTimeMillis();
		this.cleanImportInput();
		System.out.println("---Time: "+(System.currentTimeMillis() - startStepTime)+" ---");
		
		// Split file (parts to be processed to slaves)
		startStepTime = System.currentTimeMillis();
		this.split();
		System.out.println("---Time: "+(System.currentTimeMillis() - startStepTime)+" ---");
		
		// Send map order to slaves
		startStepTime = System.currentTimeMillis();
		this.set_machines(liste_machines_ok);
		this.sendMapOrderToMachines();
		
		// And print Umx-Machine dictionnary 
		HashMap<String,String> umx_machine = this.getUmxMachineDict();
		System.out.println("Dictionnary Umx-Machine: \n"+umx_machine.toString());
		System.out.println("---Time: "+(System.currentTimeMillis() - startStepTime)+" ---");
		
		// Shuffling: reverse index and prepare job dispatch
		startStepTime = System.currentTimeMillis();
		this.reverse_index();
		HashMap<String, HashSet<String>> key_umxs = this.getKeyUmxs();
		System.out.println("Dictionnary key - [Umx] : \n"+ key_umxs.toString());
		this.prepare_job_dispatch();
		System.out.println("---Time: "+(System.currentTimeMillis() - startStepTime)+" ---");
		
		// Send reduce order to slaves
		startStepTime = System.currentTimeMillis();
		this.sendReduceOrder();
		System.out.println("---Time: "+(System.currentTimeMillis() - startStepTime)+" ---");
		
		
		startStepTime = System.currentTimeMillis();
		this.set_filtered_words(config.filtered_words);
		System.out.println("\nResult: \n"+this.get_rmx_ordered().toString());
		System.out.println("\nFiltered result: \n"+this.getFilteredResults().toString());
		this.write_rmx();
		System.out.println("---Time: "+(System.currentTimeMillis() - startStepTime)+" ---");
		totalTime = System.currentTimeMillis() - startTime ;
		System.out.println("---TOTAL TIME: "+totalTime+" ---");
	}
	

	public void cleanImportInput() throws IOException{
		System.out.println(
				"---------------------------------\n"
				+ "IMPORT AND CLEAN INPUT\n"
				+ "---------------------------------");
		List<String> lignes = new ArrayList<String>();
		ArrayList<String> lignes_clean = new ArrayList<String>();
		lignes = Files.readAllLines(this.config.inputFilePath, Charset.forName("UTF-8"));
		for (String ligne :lignes ){
			// get rid of empty lines
			if (ligne.length()!=0){
				// get rid of special characters
				ligne = ligne.replaceAll("[^\\p{L}\\p{Z}]","");
				ligne = ligne.trim();
				ligne = ligne.toLowerCase();
				if (ligne.length()!=0){
					lignes_clean.add(ligne);	
				}
			}
		}
		this.input_content = lignes_clean;
	}
	
	public void split() throws IOException{
		System.out.println(
				"---------------------------------\n"
				+ "SPLIT\n"
				+ "---------------------------------");
		Path sxFolder = this.config.folderSx;
		ArrayList<String> sx_list = new ArrayList<String>();
		List<String> lignes;
		Integer i = 0;
		lignes = this.input_content;
		ArrayList<String> bloc = new ArrayList<String>();
		
		for (int k=0; k< lignes.size(); k++){
			String ligne = lignes.get(k);
			System.out.println(ligne);
			bloc.add(ligne);
			if (k+1 % this.config.lines_per_split == 0 || k==lignes.size()-1){
				// on écrit par blocs de 100 lignes
				// on l'écrit dans un fichier nommé S<num ligne>	
				Path sxi = sxFolder.resolve("S"+i);
				Files.write(sxi, bloc, Charset.forName("UTF-8"));
				sx_list.add("S"+i);
				i += 1;
				bloc = new ArrayList<String>();
			}
		}
		this.sx_list = sx_list;
	}
	
	public ArrayList<String> getListSxNames(){
		return this.sx_list;
	}
	
	public void set_machines(ArrayList<String> liste_machines){
		this.machine_list = liste_machines;
	}
	
	public Integer getId(String rawName){
		Pattern r = Pattern.compile("(\\d+)");
	    Matcher m = r.matcher(rawName);
	    m.find();
	    String id =  m.group(0);
	    return Integer.parseInt(id);
	}
	
	public void sendMapOrderToMachines(){
		System.out.println(
				"---------------------------------\n"
				+ "MAP\n"
				+ "---------------------------------");
		// cette méthode créé le dictionnaire Umx-machines
		// idéalement on réalise un scp pour envoyer les fichiers (ici on triche)
		
		// Initialisation de notre dictionnaire (qui trace ce que l'on a envoyé)
		this.umx_machine = new HashMap<String,String>();
		// Initialisation de notre dictionnaire de réponse
		this.machine_keys = new HashMap<String, ArrayList<String>>();
		this.umx_keys = new HashMap<String, ArrayList<String>>();
		
		ArrayList<String> splits_to_send = this.sx_list;
		Integer nbr_mach =  this.machine_list.size();
		Integer max_threads = nbr_mach*this.config.max_thread_per_machine;
		
		while (! splits_to_send.isEmpty()) {
            Integer limit = Math.min(max_threads, splits_to_send.size());
            // slave-id dict pour savoir de qui on reçoit les réponses
            HashMap<SshCommand,Integer> slaves_dict = new HashMap<SshCommand,Integer>();

            for (int k = 0; k < limit; k++) {
            	String split = splits_to_send.get(k);
            	Integer id = getId(split);
				String machine = this.machine_list.get(k % nbr_mach);
				// on enregistre le dictionnaire Um-machine
				this.umx_machine.put("Um"+id, machine);
				
				System.out.println("Envoi de "+split+" à la machine "+machine+" devant nous renvoyer Um"+id);
				
            	String command = "cd "+this.config.slaveJarLocation+";java -jar SLAVE.jar modeSXUMX S"+id;
				SshCommand slave = new SshCommand(false, machine, command, this.config.timeout);
                slave.start(); 
                slaves_dict.put(slave,id);
			}
            
            for (Map.Entry<SshCommand,Integer> entry:slaves_dict.entrySet()) {
                try {
                	SshCommand slave = entry.getKey();
                	Integer id = entry.getValue();
                    slave.join();
                    // on attend que le thread soit terminé pour ajouter au dictionnaire
                    this.machine_keys.put(slave.getMachine(), slave.get_response());
                    this.umx_keys.put("Um"+id, slave.get_response());
                    // on supprime de la liste à traiter
                    splits_to_send.remove("S"+id);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }	
		}
		
	}
	public HashMap<String, String> getUmxMachineDict(){
		return this.umx_machine;
	}
	
	public HashMap<String, ArrayList<String>> getResponses(){
		return this.machine_keys;
	};
	
	public void reverse_index(){
		// INPUT 	Umx - [keys]
		// OUTPUT	key - [Umxs]		
		HashMap<String,HashSet<String>> inversed = new HashMap<String,HashSet<String>>(this.umx_keys.size());
		for(Map.Entry<String, ArrayList<String>> entry : this.umx_keys.entrySet()) {
	        for(String key : entry.getValue()) {    // entry.getValue() est ArrayList<String> (keys)         
	            if(!inversed.containsKey(key)) { 
	                inversed.put(key,new HashSet<String>());
	            }
	            inversed.get(key).add(entry.getKey());
	        } 
	    }
		this.key_umxs = inversed;
	}
	
	public HashMap<String,HashSet<String>> getKeyUmxs(){
		return this.key_umxs;
	}
	
	public void prepare_job_dispatch(){
		System.out.println(
				"---------------------------------\n"
				+ "SHUFFLE\n"
				+ "---------------------------------");

		// à partir du dict machine_keys on va déterminer à qui on va envoyer quels jobs
		// le dictionnaire machine => commande est enregistré en output
		// version simple et débile, itération sur les clés
		// TODO algo plus intelligent à faire
		
		ArrayList<List<String>> machine_command = new ArrayList<List<String>>();
		Integer i = 0;
		Integer nbr_mach =  this.machine_list.size();
		for (Map.Entry<String, HashSet<String>> key_values: this.key_umxs.entrySet()){
			String machine = this.machine_list.get(i % nbr_mach);
			String key = key_values.getKey();
			HashSet<String> umxs = key_values.getValue();
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
		System.out.println(
				"---------------------------------\n"
				+ "REDUCE\n"
				+ "---------------------------------");
		// on envoie les ordres contenus dans this.machine_command
		Integer max_threads = this.machine_list.size()*this.config.max_thread_per_machine;
		ArrayList<List<String>> machine_command_to_compute = this.machine_command;
		ArrayList<List<String>> machine_command_failed = new ArrayList<List<String>>();

		// initiation des résultats
		this.rmx_machine = new HashMap<String, String>();
		this.rmx_final = new ArrayList<ReduceResult>();
		this.rmx_final_raw = new ArrayList<String>();
		
		while (! machine_command_to_compute.isEmpty()) {
			
            HashMap<SshCommand,List<String>> slaves_dict = new HashMap<SshCommand,List<String>>();
            Integer limit = Math.min(max_threads, machine_command_to_compute.size());
            
            for (List<String>  entry: machine_command_to_compute) {
				System.out.println("Envoi de la commande "+ entry.get(1)+" à la machine "+entry.get(0)+".");
            	SshCommand slave = new SshCommand(
            			false, 
            			entry.get(0),
                        "cd "+this.config.slaveJarLocation+";java -jar SLAVE.jar "+entry.get(1), this.config.timeout);
                slave.start();
                slaves_dict.put(slave,entry);
                limit -= 1;
                if (limit.equals(0)){
                	break;
                }
			}
            for (Map.Entry<SshCommand,List<String>> entry:slaves_dict.entrySet()) {
                try {
                	SshCommand slave = entry.getKey();
                    slave.join();
                    // on attend que le thread soit terminé pour ajouter au dictionnaire
                    if (slave.get_response().size()!=0){
                    	this.rmx_machine.put(slave.get_response().get(0), slave.getMachine());
                        ReduceResult result = new ReduceResult(slave.get_response().get(0));
                        this.rmx_final.add(result);
                        this.rmx_final_raw.add(slave.get_response().get(0));
                        machine_command_to_compute.remove(entry.getValue());
                    }
                    else {
                    	List<String> mach_comm = Arrays.asList(slave.getMachine(),slave.get_command());
                    	machine_command_failed.add(mach_comm);
                    	System.out.println("-------\nERROR: machine "+slave.getMachine()+" commande: "+slave.get_command()+"\n------");
                    }
                    
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
	public class ReduceResult {
		  private String key;
		  private Integer value;
		  
		  public ReduceResult(String rawResult) {
			  Pattern r = Pattern.compile("(\\D+) (\\d+)");
			  Matcher m = r.matcher(rawResult);
			  m.find();
			  this.key = m.group(1);
			  this.value = Integer.parseInt(m.group(2));
		  }
		  public String toString(){
			  return this.key +" "+ this.value.toString();
		  };
		  public String getKey() { return this.key; }
		  public Integer getValue() { return this.value; }
	}
	
	Comparator<ReduceResult> result_comp = new Comparator<ReduceResult>() {
		@Override
		public int compare(ReduceResult a, ReduceResult b) {
		    return b.getValue().compareTo(a.getValue());
		}
	};
	
	public ArrayList<ReduceResult> get_rmx_ordered(){
		
		Collections.sort(this.rmx_final, result_comp);
		return this.rmx_final;
	}
	public void write_rmx() throws IOException{
		System.out.println(
				"---------------------------------\n"
				+ "RESULT\n"
				+ "---------------------------------");
		Path rmxFolder = this.config.folderResult;
		Path output = rmxFolder.resolve("Results.txt");
		ArrayList<String> converted = new ArrayList<String>();
		for (ReduceResult resultat :this.rmx_final){
			converted.add(resultat.toString());
		}
		Files.write(output, converted, Charset.forName("UTF-8"));
	}
	public void set_filtered_words(List<String> filtered_words){
		this.mot_filtres = filtered_words;
	}
	
	public ArrayList<ReduceResult> getFilteredResults(){
		ArrayList<ReduceResult> filtered_result = new ArrayList<ReduceResult>();
		for (ReduceResult entry: this.rmx_final){
			if (! this.mot_filtres.contains(entry.key)){
				filtered_result.add(entry);
			}
		}
		return filtered_result;
	};
	
}
