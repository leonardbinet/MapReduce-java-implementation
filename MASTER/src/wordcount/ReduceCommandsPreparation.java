package wordcount;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import main.Utils;

public class ReduceCommandsPreparation {
	public static ArrayList<List<String>> prepare_job_dispatch(HashMap<String, HashSet<String>> key_umxs ,ArrayList<String> machine_list){
		Utils.printBeautiful("Reduce commands preparation");

		// à partir du dict machine_keys on va déterminer à qui on va envoyer quels jobs
		// le dictionnaire machine => commande est enregistré en output
		// version simple et débile, itération sur les clés
		// TODO algo plus intelligent à faire
		
		ArrayList<List<String>> machine_command = new ArrayList<List<String>>();
		Integer i = 0;
		Integer nbr_mach =  machine_list.size();
		for (Map.Entry<String, HashSet<String>> key_values: key_umxs.entrySet()){
			String machine = machine_list.get(i % nbr_mach);
			String key = key_values.getKey();
			HashSet<String> umxs = key_values.getValue();
			String umx_concat = "";
			for (String umx: umxs){
				umx_concat += " "+umx; // attention, commence par un " "
			} 
			String command = "modeUMXSMX "+key+" SM"+i+umx_concat;
			List<String> temp =Arrays.asList(machine,command) ;
			machine_command.add(temp);
			i += 1;
		}
		System.out.println("Created "+machine_command.size()+" commands to send.");
		return machine_command;
	}
}
