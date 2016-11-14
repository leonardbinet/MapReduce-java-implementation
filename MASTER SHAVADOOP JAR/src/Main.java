import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

	public static void main(String[] args) throws IOException {
		
		
		// INITIALISATION : VERIFICATION DES MACHINES UP
		System.out.println("---------------------------------\nINITIALISATION\n---------------------------------");
		CheckMachinesUp checkMachines = new CheckMachinesUp("liste_machines.txt", "liste_machines_OK.txt");
		checkMachines.test_Machines_Up();
		ArrayList<String> liste_machines_ok = checkMachines.get_Machines_Up();
		// TODO vérifier fonctionnement path
		
		// LANCEMENT PROCEDURE
		System.out.println("---------------------------------\nLANCEMENT\n---------------------------------");
		System.out.println("Lancement de l'algorithme principal sur le fichier " + args[0]);
		String dossier = "/cal/homes/lbinet/workspace/Sys_distribue/";
		Path input_file = Paths.get(dossier+args[0]);
		AlgoMaster algo1 = new AlgoMaster(input_file,dossier);
		algo1.cleanImportInput();

		// 1ERE ETAPE : MASTER : split 
		System.out.println("---------------------------------\nSPLIT\n---------------------------------");
		
		algo1.split();
		// ArrayList<String> sx_list = algo1.getListSxNames();
		
		// MASTER : envoi aux slaves l'ordre de mapper
		System.out.println("---------------------------------\nMAP\n---------------------------------");
		algo1.set_machines(liste_machines_ok);
		algo1.sendMapOrderToMachines();
		// on récupère le dictionnaire
		HashMap<String,String> umx_machine = algo1.getUmxMachineDict();
		System.out.println("Notre dictionnaire Umx - machine: \n"+umx_machine.toString());
		// on génère l'index inversé
		algo1.reverse_index();
		// on récupère les key - [Umx]
		HashMap<String, ArrayList<String>> key_umxs = algo1.getKeyUmxs();
		System.out.println("Notre dictionnaire key - [Umx] : \n"+ key_umxs.toString());
		
		// MASTER : shuffling: envoi des ordres de shuffling aux slaves
		System.out.println("---------------------------------\nSHUFFLE\n---------------------------------");
		algo1.prepare_job_dispatch();
		System.out.println(algo1.get_machine_command().toString());
		// MASTER : reduce: envoi des ordres de reduce aux slaves
		System.out.println("---------------------------------\nREDUCE\n---------------------------------");
		algo1.sendReduceOrder();
		
		// MASTER : écriture des résultats
		System.out.println("---------------------------------\nRESULTAT\n---------------------------------");
		System.out.println("Réponses :\n"+algo1.get_rmx_final().toString());
		algo1.write_rmx();

	}


}
