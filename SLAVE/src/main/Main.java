package main;

import operations.Mapping;
import operations.Reducing;

public class Main {

    public static void main(String[] args) {
    	Config config = new Config();
	
    	// First argument decides which mode we use: map or reduce
        if(args.length == 0)
        {
            System.out.println("No argument was passed.");
            System.exit(0);
        }
        else if (args[0].equals("modeSXUMX")){
        	
        	Mapping mapping = new Mapping(args[1], config);
        	mapping.launchMapping();
        }
        
        else if (args[0].equals("modeUMXSMX")){
        	Reducing reducing = new Reducing(args, config);
        	reducing.launchReduce();
    		
    	}
        else {
        	System.out.println("First argument is not correct: we need to pass either 'modeSXUMX' or 'modeUMXSMX'.");
            System.out.println("arg 0: "+args[0]);
        	System.exit(0);
        }	
    }
}
