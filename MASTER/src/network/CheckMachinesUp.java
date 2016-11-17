package network;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import main.Utils;

public class CheckMachinesUp {

	private List<String> machinesToTest;
	private ArrayList<String> respondingMachines;
	private NetworkConfig networkConfig;
	
	public CheckMachinesUp(NetworkConfig networkConfig){
		this.networkConfig = networkConfig;	
	}
	
	public void readMachinesToTest(Path path) throws IOException{
		this.machinesToTest = Files.readAllLines(path, Charset.forName("UTF-8"));
	}
	public void writeRespondingMachines(Path path) throws IOException{
		Files.write(path, this.respondingMachines, Charset.forName("UTF-8"));
	}
	
	public void test_Machines_Up() throws IOException, InterruptedException{
		Utils.printBeautiful("Machine test");
		ArrayList<SshCommand> sshTestList = new ArrayList<SshCommand>();
		
		for (String machineToTest : this.machinesToTest) {
			SshCommand sshCommand = new SshCommand(this.networkConfig, machineToTest,"test ssh", true);
			sshCommand.start();
			sshTestList.add(sshCommand);
		}
		
		ArrayList<String> respondingMachines = new ArrayList<String>();
		for (SshCommand sshTest : sshTestList) {
			sshTest.join();
			if (sshTest.isConnectionOK()) {
				respondingMachines.add(sshTest.getMachine());
			}
		}
		this.respondingMachines = respondingMachines;
		System.out.println(respondingMachines.size()+" machine(s) responding out of "+this.machinesToTest.size()+".");
	}
	
	public ArrayList<String> get_Machines_Up(){
		return this.respondingMachines;
	}
}
