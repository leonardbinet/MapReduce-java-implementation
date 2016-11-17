package wordcount;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import network.NetworkConfig;
import network.SshCommand;
import main.Utils;

public class MapOrder {
	
	private ArrayList<String> splitsToSend;
	private ArrayList<String> machineList;
	private Path slaveJarLocation;
	private NetworkConfig networkConfig;
	
	public MapOrder(ArrayList<String> splitsToSend, ArrayList<String> machineList,NetworkConfig networkConfig ){
		this.splitsToSend= splitsToSend;
		this.machineList = machineList;
		this.networkConfig = networkConfig;
	}
	
	public void setSlaveLocation(Path slaveJarLocation){
		this.slaveJarLocation = slaveJarLocation;
	}
	
	public HashMap<String, ArrayList<String>> send(){
		Utils.printBeautiful("Map");
		
		/* cette méthode créé le dictionnaire Umx-machines
		// idéalement on réalise un scp pour envoyer les fichiers (ici on triche)
		// Initialisation de notre dictionnaire (qui trace ce que l'on a envoyé)
		*/
		Integer maxThreads = this.machineList.size()*this.networkConfig.maxThreadsPerMachine;
		HashMap<String,String> umx_machine = new HashMap<String,String>();
		HashMap<String, ArrayList<String>> machine_keys = new HashMap<String, ArrayList<String>>();
		HashMap<String, ArrayList<String>> umx_keys = new HashMap<String, ArrayList<String>>();
				
		while (! this.splitsToSend.isEmpty()) {
            Integer limit = Math.min(maxThreads, this.splitsToSend.size());
            // slave-id dict pour savoir de qui on reçoit les réponses
            HashMap<SshCommand,Integer> slaves_dict = new HashMap<SshCommand,Integer>();

            for (int k = 0; k < limit; k++) {
            	String split = this.splitsToSend.get(k);
            	Integer id = Utils.getId(split);
				String machine = machineList.get(k % this.machineList.size());
				umx_machine.put("Um"+id, machine);
				
				System.out.println("Envoi de "+split+" à la machine "+machine+" devant nous renvoyer Um"+id);
				
            	String command = "cd "+this.slaveJarLocation+";java -jar SLAVE.jar modeSXUMX S"+id;
				SshCommand slave = new SshCommand(this.networkConfig, machine, command, false);
                slave.start(); 
                slaves_dict.put(slave,id);
			}
            
            for (Map.Entry<SshCommand,Integer> entry:slaves_dict.entrySet()) {
                try {
                	SshCommand slave = entry.getKey();
                	Integer id = entry.getValue();
                    slave.join();
                    // on attend que le thread soit terminé pour ajouter au dictionnaire
                    machine_keys.put(slave.getMachine(), slave.get_response());
                    umx_keys.put("Um"+id, slave.get_response());
                    // on supprime de la liste à traiter
                    this.splitsToSend.remove("S"+id);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }	
		}
		return umx_keys;
		
	}
}
