package wordcount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ReverseIndex {
	public static HashMap<String, HashSet<String>> reverseIndex(HashMap<String, ArrayList<String>> umx_keys){
		// INPUT 	Umx - [keys]
		// OUTPUT	key - [Umxs]		
		HashMap<String,HashSet<String>> inversed = new HashMap<String,HashSet<String>>(umx_keys.size());
		for(Map.Entry<String, ArrayList<String>> entry : umx_keys.entrySet()) {
	        for(String key : entry.getValue()) {    // entry.getValue() est ArrayList<String> (keys)         
	            if(!inversed.containsKey(key)) { 
	                inversed.put(key,new HashSet<String>());
	            }
	            inversed.get(key).add(entry.getKey());
	        } 
	    }
		return inversed;
	}
}
