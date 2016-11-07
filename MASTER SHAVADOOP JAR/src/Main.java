import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

	public static void main(String[] args) {
		
		// on va lancer l'algo sur le fichier dont le nom est passé en argument
		System.out.println("Lancement de l'algo sur le fichier " + args[0]);
		
		String dossier = "/cal/homes/lbinet/workspace/Sys_distribue/";
		
		Path input_file = Paths.get(dossier+args[0]);
		List<String> lignes;
		Integer i = 0;
		try {
			// on lit le fichier ligne par ligne
			lignes = Files.readAllLines(input_file, Charset.forName("UTF-8"));
			for (String ligne :lignes ){
				// on affiche la ligne
				System.out.println(ligne);
				// on l'écrit dans un fichier nommé Sx_<num ligne>
				Path sx = Paths.get(dossier+"/Sx/Sx"+i+".txt");
				Files.write(sx, Arrays.asList(ligne), Charset.forName("UTF-8"));
				i += 1;
				
			}
			
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		List<String> machines;
		ArrayList<TestConnectionSSH> listeTests = new ArrayList<TestConnectionSSH>();

		Path filein = Paths.get("liste_machines.txt");
		try {
			// on charge le nom des machines
			machines = Files.readAllLines(filein, Charset.forName("UTF-8"));
			for (String machine : machines) {
				/*
				 * on teste la connection SSH pendant 7 secondes maximum
				 */
				TestConnectionSSH test = new TestConnectionSSH(machine, 7);
				test.start();
				listeTests.add(test);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		ArrayList<String> liste_machines_ok = new ArrayList<String>();

		for (TestConnectionSSH test : listeTests) {
			try {
				test.join();// on attend la fin du test
				if (test.isConnectionOK()) {
					liste_machines_ok.add(test.getMachine());
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		Path file = Paths.get("liste_machines_OK.txt");
		try {
			Files.write(file, liste_machines_ok, Charset.forName("UTF-8"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (liste_machines_ok != null) {
            ArrayList<LaunchSlaveShavadoop> slaves = new ArrayList<LaunchSlaveShavadoop>();
            for (String machine : liste_machines_ok) {
                LaunchSlaveShavadoop slave = new LaunchSlaveShavadoop(machine,
                        "cd workspace/Sys_distribue;java -jar SLAVESHAVADOOP.jar", 20);
                slave.start();
                slaves.add(slave);
            }
            for (LaunchSlaveShavadoop slave : slaves) {
                try {
                    slave.join();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            System.out.println("tout est fini");
        }
    }


}
