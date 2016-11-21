package main;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Config {
	
	public List<String> filteredWords;
	public Path slaveJarLocation; 			//  SLAVE.jar location, relative (to ssh arrival point), or absolute
	public Path inputFilePath; 				//  input file location, relative (to MASTER.jar), or absolute
	public Integer linesPerSplit;			
	public Path masterJobsPath; 		//  generated files location, relative (to MASTER.jar), or absolute
	public Path folderSx;
	public Path folderUmx;
	public Path folderSmx;
	public Path folderRmx;
	public Path folderResult;
	public Path machinesToTestPath;
	public Path machinesRespondingPath;


	
	public Config(){
		this.filteredWords = Arrays.asList("le","la","les","je","du","de","des","ou","nous","vous","leur","eux","et","ne","l","à","en","par","ses","ce","son","un","une");
		this.slaveJarLocation = Paths.get("/cal/homes/lbinet/workspace/Sys_distribue/"); 
		this.linesPerSplit = 1;
		this.masterJobsPath = Paths.get("/cal/homes/lbinet/workspace/Sys_distribue/Jobs/");
		this.folderSx = this.masterJobsPath.resolve("Sx/");
    	this.folderUmx = this.masterJobsPath.resolve("Umx/");
    	this.folderSmx = this.masterJobsPath.resolve("Smx/");
    	this.folderRmx = this.masterJobsPath.resolve("Rmx/");
    	this.folderResult = this.masterJobsPath.resolve("Result/");
    	this.machinesToTestPath = Paths.get("liste_machines.txt");
    	this.machinesRespondingPath =Paths.get("liste_machines_OK.txt");

	}
	public void setInputLocation(Path inputPath){
		this.inputFilePath = inputPath;
	}
}
