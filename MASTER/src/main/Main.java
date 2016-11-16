package main;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import wordcount.WordCount;

public class Main {

	public static void main(String[] args) throws IOException, InterruptedException {
		
		// Check passed arguments
		if (args.length != 1){
			System.out.println("Please pass (only) one argument to specify input file location relative to jar file, or absolute path.");;
			System.exit(0);
		}
		
		// Set config
		Config config = new Config();
		Path inputPath = Paths.get(args[0]);
		config.setInputLocation(inputPath);
		
		
		// Launch wordcount
		System.out.println("Launch programm on " + inputPath);
		WordCount wordcount = new WordCount(config);
		wordcount.computeWordCount();
		
	}
}