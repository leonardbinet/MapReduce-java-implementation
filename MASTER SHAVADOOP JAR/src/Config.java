import java.util.Arrays;
import java.util.List;

public class Config {
	
	public List<String> filtered_words;
	public String dossier;
	public Integer max_thread_per_machine;
	public Integer timeout;
	public Integer test_timeout;
	
	public Config(){
		this.filtered_words = Arrays.asList("le","la","les","je","du","de","des","ou","nous","vous","leur","eux","et","ne","l","à","en","par","ses","ce","son","un","une");
		this.dossier = "/cal/homes/lbinet/workspace/Sys_distribue/";
		this.max_thread_per_machine = 4;
		this.timeout = 10; 
		this.test_timeout = 3; // lorsque l'on teste le ssh
	}
}
