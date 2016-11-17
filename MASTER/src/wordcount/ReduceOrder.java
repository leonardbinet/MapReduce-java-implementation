package wordcount;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.Utils;
import network.NetworkConfig;
import network.SshCommand;
import wordcount.Result.ReduceResult;

public class ReduceOrder {
	
	private ArrayList<List<String>> machine_command;
	private Path slaveJarLocation;
	private ArrayList<String> machineList;
	private HashMap<String, String> rmx_machine;
	private ArrayList<ReduceResult> rmx_final;
	private ArrayList<String> rmx_final_raw;
	private NetworkConfig networkConfig;

	public ReduceOrder(NetworkConfig networkConfig,ArrayList<List<String>> machine_command, ArrayList<String> machineList){
		this.machine_command = machine_command;
		this.machineList = machineList;
		this.networkConfig = networkConfig;
	}
	
	public void setSlaveLocation(Path slaveJarLocation){
		this.slaveJarLocation = slaveJarLocation;
	}
	
	public ArrayList<ReduceResult> send(Integer timeout, Integer maxThreadPerMachine){
		Utils.printBeautiful("Reduce");
		// on envoie les ordres contenus dans this.machine_command
		Integer max_threads = this.machineList.size()*maxThreadPerMachine;
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
            			this.networkConfig, 
            			entry.get(0),
                        "cd "+this.slaveJarLocation+";java -jar SLAVE.jar "+entry.get(1), 
            			false);
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
		return this.rmx_final;
	}
}
