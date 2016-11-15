
public class Config {
	
	public String racine_jobs_slave; // emplacement des fichiers générés lors du job
	public String dossierSx;
	public String dossierUmx;
	public String dossierSmx;
	public String dossierRmx;
	
	public Config(){
		this.racine_jobs_slave = "/cal/homes/lbinet/workspace/Sys_distribue/Jobs/";
		this.dossierSx = this.racine_jobs_slave+"Sx/";
    	this.dossierUmx = this.racine_jobs_slave+"Umx/";
    	this.dossierSmx = this.racine_jobs_slave+"Smx/";
    	this.dossierRmx = this.racine_jobs_slave+"Rmx/";
	}
}
