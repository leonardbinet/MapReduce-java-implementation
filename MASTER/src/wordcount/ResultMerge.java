package wordcount;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.Utils;

public class ResultMerge {
	
	private ArrayList<ReduceResult> rmxs;
	private List<String> filtered_words;
	
	public ResultMerge(ArrayList<ReduceResult> rmxs){
		this.rmxs = rmxs;
	}
	
	public void writeResult(Path folderResult) throws IOException{
		Utils.printBeautiful("Results");
		Path output = folderResult.resolve("Results.txt");
		ArrayList<String> converted = new ArrayList<String>();
		for (ReduceResult resultat :this.rmxs){
			converted.add(resultat.toString());
		}
		Files.write(output, converted, Charset.forName("UTF-8"));
	}

	public ArrayList<ReduceResult> get_rmx_ordered(){
		
		Collections.sort(this.rmxs, result_comp);
		return this.rmxs;
	}

	public void set_filtered_words(List<String> filtered_words){
		this.filtered_words = filtered_words;
	}
	
	public ArrayList<ReduceResult> getFilteredResults(){
		ArrayList<ReduceResult> filtered_result = new ArrayList<ReduceResult>();
		for (ReduceResult entry: this.rmxs){
			if (! this.filtered_words.contains(entry.key)){
				filtered_result.add(entry);
			}
		}
		return filtered_result;
	};
	public static class ReduceResult {
		  private String key;
		  private Integer value;
		  
		  public ReduceResult(String rawResult) {
			  Pattern r = Pattern.compile("(\\D+) (\\d+)");
			  Matcher m = r.matcher(rawResult);
			  m.find();
			  this.key = m.group(1);
			  this.value = Integer.parseInt(m.group(2));
		  }
		  public String toString(){
			  return this.key +" "+ this.value.toString();
		  };
		  public String getKey() { return this.key; }
		  public Integer getValue() { return this.value; }
	}
	
	Comparator<ReduceResult> result_comp = new Comparator<ReduceResult>() {
		@Override
		public int compare(ReduceResult a, ReduceResult b) {
		    return b.getValue().compareTo(a.getValue());
		}
	};
}
