package network;

import java.nio.file.Path;
import java.nio.file.Paths;

public class NetworkConfig {
	
	public Integer maxThreadsPerMachine;
	public Integer timeout;
	public Integer test_timeout;			//  when testing machines
	public Path machinesToTestPath;
	public Path machinesRespondingPath;
	public String sshUser;
	public String sshKeyPath;

	
	public NetworkConfig(){
		
		this.maxThreadsPerMachine = 3;
		this.timeout = 3; 
		this.test_timeout = 3; 
    	this.machinesToTestPath = Paths.get("liste_machines.txt");
    	this.machinesRespondingPath =Paths.get("liste_machines_OK.txt");
    	this.sshUser = "lbinet";
    	this.sshKeyPath = "/cal/homes/lbinet/.ssh/intra_telecom";
	
	}
}