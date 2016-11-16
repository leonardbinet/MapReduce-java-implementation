package main;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Config {
	
	public Path racine_jobs_slave; // folder to place files generated during job
	public Path folderSx;
	public Path folderUmx;
	public Path folderSmx;
	public Path folderRmx;
	
	public Config(){
		this.racine_jobs_slave = Paths.get("/cal/homes/lbinet/workspace/Sys_distribue/Jobs/");
		this.folderSx = this.racine_jobs_slave.resolve("Sx/");
    	this.folderUmx = this.racine_jobs_slave.resolve("Umx/");
    	this.folderSmx = this.racine_jobs_slave.resolve("Smx/");
    	this.folderRmx = this.racine_jobs_slave.resolve("Rmx/");
	}
}
