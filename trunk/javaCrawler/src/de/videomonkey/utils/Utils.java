package de.videomonkey.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

	private static ArrayList<String> replacements = new ArrayList<String>();
	
	private static void create_replacements() {
		replacements.add("dvdrip.*");
		replacements.add("german.*");
		replacements.add("english.*");
		replacements.add("by.*");
		replacements.add("\\(.*\\)");
		replacements.add("gerac3.*");
		replacements.add("engmp3.*");
		replacements.add("xvid.*");
		replacements.add("ac3.*");
		replacements.add("hdtvrip.*");
	}
	
	
	public static String cleanMovieFileName(String name) {

		create_replacements();
		
		name = name.replaceAll("\\.", " ");
		name = name.replaceAll("_", " ");

		// try to save the year
		String pattern = "([0-9][0-9][0-9][0-9])";
		String year = "";

		String backup = name;

		for (MatchResult r : findMatches(pattern, name)) {
			if (r.groupCount() > 0) {
				year = r.group(1);
				name = name.replaceAll(pattern, "").trim();
			}
		}

		if (name.trim().length() < 3) {
			name = backup;
		}

		for (String replace : Utils.replacements) {
			name = name.replaceAll(replace, "").trim();
		}

		if (year.length() > 0) {
			name += " (" + year + ")";
		}

		name = name.replaceAll("  ", " ");
		return name;
	}
	
	
	public static Iterable<MatchResult> findMatches(String pattern,
			CharSequence s) {
		List<MatchResult> results = new ArrayList<MatchResult>();

		for (Matcher m = Pattern.compile(pattern).matcher(s); m.find();)
			results.add(m.toMatchResult());

		return results;
	}

}
