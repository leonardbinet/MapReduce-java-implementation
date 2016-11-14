import java.util.Arrays;
import java.util.List;

public class Config {
	
	public List<String> filtered_words;
	public String dossier;
	public Integer max_thread_per_machine;
	
	public Config(){
		this.filtered_words = Arrays.asList("le","la","les","je","du","de","des","ou","nous","vous","leur","eux","et","ne","l","à","en","par","ses","ce","son","un","une");
		this.dossier = "/cal/homes/lbinet/workspace/Sys_distribue/";

	}
}
