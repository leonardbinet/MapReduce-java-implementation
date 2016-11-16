package main;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

	public static Integer getId(String rawName){
		Pattern r = Pattern.compile("(\\d+)");
	    Matcher m = r.matcher(rawName);
	    m.find();
	    String id =  m.group(0);
	    return Integer.parseInt(id);
	}
}
