package org.activityinfo.geoadmin;

import org.apache.commons.lang3.StringUtils;

public class MatchUtils {

	public static double similiarity(String a, String b) {
		String cleanA = cleanName(a);
		String cleanB = cleanName(b);
		double distance =  StringUtils.getLevenshteinDistance(cleanA, cleanB);
		double maxDistance = Math.max(cleanA.length(), cleanB.length());
		return (maxDistance - distance) / maxDistance;
	}

	public static int distance(String a, String b) {
		return StringUtils.getLevenshteinDistance(MatchUtils.cleanName(a), MatchUtils.cleanName(b));
	}

	public static String cleanName(String name) {
		StringBuilder cleaned = new StringBuilder();
		for(int i=0;i!=name.length();++i) {
			int cp = name.codePointAt(i);
			if(Character.isLetter(cp)) {
				cleaned.appendCodePoint(Character.toLowerCase(cp));
			}
		}
		return cleaned.toString();
	}

	public static String escapeName(String name) {
		return name.replace("'", "''");
	}
}
