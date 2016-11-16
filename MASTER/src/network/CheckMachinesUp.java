package network;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CheckMachinesUp {

	private List<String> machinesToTest;
	private ArrayList<String> respondingMachines;
	private Integer timeout;
	
	public CheckMachinesUp(Integer timeout){
		this.timeout = timeout;
	}
	
	public void readMachinesToTest(Path path) throws IOException{
		this.machinesToTest = Files.readAllLines(path, Charset.forName("UTF-8"));
	}
	public void writeRespondingMachines(Path path) throws IOException{
		Files.write(path, this.respondingMachines, Charset.forName("UTF-8"));
	}
	
	public void test_Machines_Up() throws IOException, InterruptedException{
		System.out.println(
				"---------------------------------\n"
				+ "CHECK MACHINES UP\n"
				+ "---------------------------------");
		ArrayList<SshCommand> sshTestList = new ArrayList<SshCommand>();
		
		for (String machineToTest : this.machinesToTest) {
			SshCommand test = new SshCommand(true, machineToTest,"test ssh", this.timeout);
			test.start();
			sshTestList.add(test);
		}
		
		ArrayList<String> respondingMachines = new ArrayList<String>();
		for (SshCommand sshTest : sshTestList) {
			sshTest.join();
			if (sshTest.isConnectionOK()) {
				respondingMachines.add(sshTest.getMachine());
			}
		}
		this.respondingMachines = respondingMachines;
	}
	
	public ArrayList<String> get_Machines_Up(){
		return this.respondingMachines;
	}
}
