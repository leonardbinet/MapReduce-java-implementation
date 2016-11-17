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
import wordcount.ResultMerge.ReduceResult;

public class ReduceOrder {
	
	private ArrayList<List<String>> machine_command;
	private Path slaveJarLocation;
	private ArrayList<String> machineList;
	private ArrayList<ReduceResult> rmx_final;
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
		Integer max_threads = this.machineList.size()*maxThreadPerMachine;
		ArrayList<List<String>> machine_command_to_compute = this.machine_command;
		ArrayList<List<String>> machine_command_failed = new ArrayList<List<String>>();

		this.rmx_final = new ArrayList<ReduceResult>();
		
		while (! machine_command_to_compute.isEmpty()) {
			
            HashMap<SshCommand,List<String>> slaves_dict = new HashMap<SshCommand,List<String>>();
            Integer limit = Math.min(max_threads, machine_command_to_compute.size());
            
            for (List<String>  entry: machine_command_to_compute) {
				// System.out.println("Command "+ entry.get(1)+" to machine "+entry.get(0)+".");
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
                    // wait for response
                    if (slave.get_response().size()!=0){
                        ReduceResult result = new ReduceResult(slave.get_response().get(0));
                        this.rmx_final.add(result);
                        machine_command_to_compute.remove(entry.getValue());
                    }
                    else {
                    	List<String> mach_comm = Arrays.asList(slave.getMachine(),slave.get_command());
                    	machine_command_failed.add(mach_comm);
                    	System.out.println(
                    			"-------\nERROR:"
                    			+ " machine "+slave.getMachine()
                    			+" commande: "+slave.get_command()
                    			+"\n=> New try"
                    			+"\n------");
                    }
                    
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }	
		}
		System.out.println("Received "+rmx_final.size()+" elements.");
		return this.rmx_final;
	}
}
