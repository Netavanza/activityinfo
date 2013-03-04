package org.activityinfo.geoadmin;

import org.activityinfo.geoadmin.model.Bounds;

import com.vividsolutions.jts.geom.Envelope;

public class GeoUtils {

	public static Envelope toEnvelope(Bounds bounds) {
		return new Envelope(bounds.getX1(), bounds.getX2(), bounds.getY1(), bounds.getY2());
	}
}
