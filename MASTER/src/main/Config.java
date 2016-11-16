package main;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Config {
	
	public List<String> filtered_words;
	public Path slaveJarLocation; 			//  SLAVE.jar location, relative (to ssh arrival point), or absolute
	public Path inputFilePath; 				//  input file location, relative (to MASTER.jar), or absolute
	public Integer max_thread_per_machine;
	public Integer timeout;
	public Integer test_timeout;			//  when testing machines
	public Integer lines_per_split;			
	public Path racine_jobs_master; 		//  generated files location, relative (to MASTER.jar), or absolute
	public Path folderSx;
	public Path folderUmx;
	public Path folderSmx;
	public Path folderRmx;
	public Path folderResult;
	public Path machinesToTestPath;
	public Path machinesRespondingPath;

	
	public Config(){
		this.filtered_words = Arrays.asList("le","la","les","je","du","de","des","ou","nous","vous","leur","eux","et","ne","l","à","en","par","ses","ce","son","un","une");
		this.slaveJarLocation = Paths.get("/cal/homes/lbinet/workspace/Sys_distribue/");
		this.max_thread_per_machine = 3;
		this.timeout = 3; 
		this.test_timeout = 3; 
		this.lines_per_split = 50;
		this.racine_jobs_master = Paths.get("/cal/homes/lbinet/workspace/Sys_distribue/Jobs/");
		this.folderSx = this.racine_jobs_master.resolve("Sx/");
    	this.folderUmx = this.racine_jobs_master.resolve("Umx/");
    	this.folderSmx = this.racine_jobs_master.resolve("Smx/");
    	this.folderRmx = this.racine_jobs_master.resolve("Rmx/");
    	this.folderResult = this.racine_jobs_master.resolve("Result/");
    	this.machinesToTestPath = Paths.get("liste_machines.txt");
    	this.machinesRespondingPath =Paths.get("liste_machines_OK.txt");
	}
	public void setInputLocation(Path inputPath){
		this.inputFilePath = inputPath;
	}
}
