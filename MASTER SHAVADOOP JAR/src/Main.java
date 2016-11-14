import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

	public static void main(String[] args) throws IOException {
		
		long startTime = System.currentTimeMillis();
		long startStepTime;
		long stepTime;
		long totalTime;
		
		System.out.println("Lancement de l'algorithme principal sur le fichier " + args[0]);
		String dossier = "/cal/homes/lbinet/workspace/Sys_distribue/";
		Path input_file = Paths.get(dossier+args[0]);
		
		
		System.out.println("---------------------------------\nINITIALISATION - TEST MACHINES \n---------------------------------");
		startStepTime = System.currentTimeMillis();
		
		AlgoMaster algo1 = new AlgoMaster(input_file,dossier);
		CheckMachinesUp checkMachines = new CheckMachinesUp("liste_machines.txt", "liste_machines_OK.txt");
		checkMachines.test_Machines_Up();
		ArrayList<String> liste_machines_ok = checkMachines.get_Machines_Up();
		
		stepTime = System.currentTimeMillis() - startStepTime;
		System.out.println("---Temps: "+stepTime+" ---");
		
		
		System.out.println("---------------------------------\nIMPORT ET NETTOYAGE INPUT\n---------------------------------");
		startStepTime = System.currentTimeMillis();

		algo1.cleanImportInput();
		
		stepTime   = System.currentTimeMillis() - startStepTime;
		System.out.println("---Temps: "+stepTime+" ---");

		
		System.out.println("---------------------------------\nSPLIT\n---------------------------------");
		startStepTime = System.currentTimeMillis();

		algo1.split();
		
		stepTime   = System.currentTimeMillis() - startStepTime;
		System.out.println("---Temps: "+stepTime+" ---");

		
		System.out.println("---------------------------------\nMAP\n---------------------------------");
		startStepTime = System.currentTimeMillis();

		algo1.set_machines(liste_machines_ok);
		algo1.sendMapOrderToMachines();

		HashMap<String,String> umx_machine = algo1.getUmxMachineDict();
		System.out.println("Notre dictionnaire Umx - machine: \n"+umx_machine.toString());

		algo1.reverse_index();

		HashMap<String, ArrayList<String>> key_umxs = algo1.getKeyUmxs();
		System.out.println("Notre dictionnaire key - [Umx] : \n"+ key_umxs.toString());
		
		stepTime   = System.currentTimeMillis() - startStepTime;
		System.out.println("---Temps: "+stepTime+" ---");
		
		
		System.out.println("---------------------------------\nSHUFFLE\n---------------------------------");
		startStepTime = System.currentTimeMillis();

		algo1.prepare_job_dispatch();
		System.out.println(algo1.get_machine_command().toString());
		
		stepTime   = System.currentTimeMillis() - startStepTime;
		System.out.println("---Temps: "+stepTime+" ---");
		

		System.out.println("---------------------------------\nREDUCE\n---------------------------------");
		startStepTime = System.currentTimeMillis();
		algo1.sendReduceOrder();
		
		stepTime   = System.currentTimeMillis() - startStepTime;
		System.out.println("---Temps: "+stepTime+" ---");
		
		
		System.out.println("---------------------------------\nRESULTAT\n---------------------------------");
		startStepTime = System.currentTimeMillis();

		System.out.println("Réponses :\n"+algo1.get_rmx_final().toString());
		algo1.write_rmx();
		
		stepTime   = System.currentTimeMillis() - startStepTime;
		System.out.println("---Temps: "+stepTime+" ---");
		
		totalTime = System.currentTimeMillis() - startTime ;
		System.out.println("---TEMPS TOTAL: "+totalTime+" ---");

	}


}
