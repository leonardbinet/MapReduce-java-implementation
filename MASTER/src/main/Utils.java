package main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

	public static Integer getId(String rawName) {
		Pattern r = Pattern.compile("(\\d+)");
		Matcher m = r.matcher(rawName);
		m.find();
		String id = m.group(0);
		return Integer.parseInt(id);
	}

	public static void printBeautiful(String toPrint) {
		String line = "---------------------------------";
		System.out.println(line);
		System.out.println(toPrint.toUpperCase());
		System.out.println(line);

	}
	public static void printDiffTime(long stepTime) {
		System.out.println("---Time: "+(System.currentTimeMillis() - stepTime)+" ---");

	}
}
