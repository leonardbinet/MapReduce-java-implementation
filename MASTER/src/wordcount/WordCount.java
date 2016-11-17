package wordcount;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import main.Config;
import main.Utils;
import network.CheckMachinesUp;
import network.NetworkConfig;
import wordcount.ResultMerge.ReduceResult;

public class WordCount {
	
	private Config config;
	private NetworkConfig networkConfig;
	private ArrayList<String> input_content;
	private ArrayList<String> sx_list;
	private HashMap<String, ArrayList<String>> umx_keys;
	private HashMap<String, HashSet<String>> key_umxs;
	private ArrayList<List<String>> machine_command;
	private ArrayList<ReduceResult> rmx_final;


	public WordCount(Config config, NetworkConfig networkConfig) {
		this.config = config;
		this.networkConfig = networkConfig;
	}

	public void computeWordCount() throws IOException, InterruptedException {

		// Initialize processing times
		long startTime = System.currentTimeMillis();
		long startStepTime;
		long totalTime;

		// Check responding machines
		startStepTime = System.currentTimeMillis();
		CheckMachinesUp checkMachines = new CheckMachinesUp(this.networkConfig);
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
		this.sx_list = Split.split(this.config.folderSx, this.input_content, this.config.linesPerSplit);
		Utils.printDiffTime(startStepTime);

		// Send map order to slaves
		startStepTime = System.currentTimeMillis();
		MapOrder mapOrder = new MapOrder(this.sx_list, liste_machines_ok, this.networkConfig);
		mapOrder.setSlaveLocation(this.config.slaveJarLocation);
		this.umx_keys = mapOrder.send();

		Utils.printDiffTime(startStepTime);

		// Shuffling: reverse index and prepare job dispatch
		startStepTime = System.currentTimeMillis();
		this.key_umxs = ReverseIndex.reverseIndex(umx_keys);
		this.machine_command = ReduceCommandsPreparation.prepare_job_dispatch(this.key_umxs, liste_machines_ok);
		Utils.printDiffTime(startStepTime);

		// Send reduce order to slaves
		startStepTime = System.currentTimeMillis();
		ReduceOrder reduceOrder = new ReduceOrder(this.networkConfig,this.machine_command, liste_machines_ok);
		reduceOrder.setSlaveLocation(this.config.slaveJarLocation);
		this.rmx_final = reduceOrder.send(this.networkConfig.timeout, this.networkConfig.maxThreadsPerMachine);
		Utils.printDiffTime(startStepTime);

		startStepTime = System.currentTimeMillis();

		ResultMerge result = new ResultMerge(rmx_final);
		result.writeResult(this.config.folderResult);
		result.set_filtered_words(config.filteredWords);
		System.out.println("\nResult: \n" + result.get_rmx_ordered().toString());
		System.out.println("\nFiltered result: \n" + result.getFilteredResults().toString());
		Utils.printDiffTime(startStepTime);

		totalTime = System.currentTimeMillis() - startTime;
		System.out.println("---TOTAL TIME: " + totalTime + " ---");
	}


}
