import java.util.Arrays;
import java.util.List;

public class Config {
	
	public List<String> filtered_words;
	public String racine_master; // emplacement du MASTER.jar
	public String racine_slave; // emplacement du SLAVE.jar
	public Integer max_thread_per_machine;
	public Integer timeout;
	public Integer test_timeout;
	public Integer lines_per_split;
	public String racine_jobs_master;
	public String dossierSx;
	public String dossierUmx;
	public String dossierSmx;
	public String dossierRmx;
	public String dossierResult;

	
	public Config(){
		this.filtered_words = Arrays.asList("le","la","les","je","du","de","des","ou","nous","vous","leur","eux","et","ne","l","à","en","par","ses","ce","son","un","une");
		this.racine_master = "/cal/homes/lbinet/workspace/Sys_distribue/";
		this.racine_slave = "/cal/homes/lbinet/workspace/Sys_distribue/";
		this.max_thread_per_machine = 3;
		this.timeout = 3; 
		this.test_timeout = 3; // lorsque l'on teste le ssh
		this.lines_per_split = 50;
		this.racine_jobs_master = "/cal/homes/lbinet/workspace/Sys_distribue/Jobs/";
		this.dossierSx = this.racine_jobs_master+"Sx/";
    	this.dossierUmx = this.racine_jobs_master+"Umx/";
    	this.dossierSmx = this.racine_jobs_master+"Smx/";
    	this.dossierRmx = this.racine_jobs_master+"Rmx/";
    	this.dossierResult = this.racine_jobs_master+"Result/";

	}
}
