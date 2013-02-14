package org.activityinfo.polygons;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Properties;
import java.util.logging.Logger;

import org.opengis.feature.simple.SimpleFeature;

import com.google.common.base.Strings;

public class IdMatchingStrategy implements MatchingStrategy {

	private Properties properties;
	private String idColumn;

	private static final Logger LOGGER = Logger.getLogger(IdMatchingStrategy.class.getName());
	
	public IdMatchingStrategy(Properties properties) {
		this.properties = properties;
		this.idColumn = properties.getProperty("id.column");
	}

	public int match(SimpleFeature feature) {
		String shapeFileId = toIdString(feature.getValue(idColumn));
		String entityId = properties.getProperty(shapeFileId);
		if(Strings.isNullOrEmpty(entityId)) {
			return NO_MATCH;
		}
		return Integer.parseInt(entityId);
	}

	private String toIdString(Object value) {
		if(value == null) {
			LOGGER.severe("null id");
			return "NULL";
		} else if(value instanceof Double) {
			return Integer.toString(((Double) value).intValue());
		} else {
			return value.toString();
		}
	}

	public void done() {
		
	}

}
