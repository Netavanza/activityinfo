package org.activityinfo.polygons;

import org.opengis.feature.simple.SimpleFeature;

public interface MatchingStrategy {

	public static final int NO_MATCH = -1;
	
	int match(SimpleFeature feature);

	void done();

}