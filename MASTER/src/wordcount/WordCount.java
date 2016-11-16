package wordcount;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import main.Config;
import main.Utils;
import network.CheckMachinesUp;
import wordcount.Result.ReduceResult;

public class WordCount {

	private ArrayList<String> input_content;
	private ArrayList<String> sx_list;
	private HashMap<String, String> umx_machine;
	private HashMap<String, ArrayList<String>> machine_keys;
	private HashMap<String, ArrayList<String>> umx_keys;
	private HashMap<String, HashSet<String>> key_umxs;
	private ArrayList<List<String>> machine_command;
	private HashMap<String, String> rmx_machine;
	private ArrayList<ReduceResult> rmx_final;
	private Config config;

	public WordCount(Config config) {
		this.config = config;
	}

	public void computeWordCount() throws IOException, InterruptedException {

		// Initialize processing times
		long startTime = System.currentTimeMillis();
		long startStepTime;
		long totalTime;

		// Check responding machines
		startStepTime = System.currentTimeMillis();
		CheckMachinesUp checkMachines = new CheckMachinesUp(this.config.test_timeout);
		checkMachines.readMachinesToTest(this.config.machinesToTestPath);
		checkMachines.test_Machines_Up();
		checkMachines.writeRespondingMachines(this.config.machinesRespondingPath);
		ArrayList<String> liste_machines_ok = checkMachines.get_Machines_Up();
		Utils.printDiffTime(startStepTime);

		// Import and clean file
		startStepTime = System.currentTimeMillis();
		this.input_content = CleanImport.cleanImport(this.config.inputFilePath);
		System.out.println("---Time: " + (System.currentTimeMillis() - startStepTime) + " ---");

		// Split file (parts to be processed to slaves)
		startStepTime = System.currentTimeMillis();
		this.sx_list = Split.split(this.config.folderSx, this.input_content, this.config.lines_per_split);
		Utils.printDiffTime(startStepTime);

		// Send map order to slaves
		startStepTime = System.currentTimeMillis();
		MapOrder mapOrder = new MapOrder(this.sx_list, liste_machines_ok);
		mapOrder.setSlaveLocation(this.config.slaveJarLocation);
		this.umx_keys = mapOrder.send(this.config.timeout, this.config.max_thread_per_machine);

		Utils.printDiffTime(startStepTime);

		// Shuffling: reverse index and prepare job dispatch
		startStepTime = System.currentTimeMillis();
		this.key_umxs = ReverseIndex.reverseIndex(umx_keys);
		this.machine_command = ReduceCommandsPreparation.prepare_job_dispatch(key_umxs, liste_machines_ok);
		Utils.printDiffTime(startStepTime);

		// Send reduce order to slaves
		startStepTime = System.currentTimeMillis();
		ReduceOrder reduceOrder = new ReduceOrder(this.machine_command, liste_machines_ok);
		reduceOrder.setSlaveLocation(this.config.slaveJarLocation);
		this.rmx_final = reduceOrder.send(this.config.timeout, this.config.max_thread_per_machine);
		Utils.printDiffTime(startStepTime);

		startStepTime = System.currentTimeMillis();

		Result result = new Result(this.config.folderResult, rmx_final);
		result.write_rmx();
		result.set_filtered_words(config.filtered_words);
		System.out.println("\nResult: \n" + result.get_rmx_ordered().toString());
		System.out.println("\nFiltered result: \n" + result.getFilteredResults().toString());
		Utils.printDiffTime(startStepTime);

		totalTime = System.currentTimeMillis() - startTime;
		System.out.println("---TOTAL TIME: " + totalTime + " ---");
	}

	public ArrayList<String> getListSxNames() {
		return this.sx_list;
	}

	public HashMap<String, String> getUmxMachineDict() {
		return this.umx_machine;
	}

	public HashMap<String, ArrayList<String>> getResponses() {
		return this.machine_keys;
	};

	public HashMap<String, HashSet<String>> getKeyUmxs() {
		return this.key_umxs;
	}

	public ArrayList<List<String>> get_machine_command() {
		return this.machine_command;
	}

	public HashMap<String, String> get_rmx_machine() {
		return this.rmx_machine;
	}

}
