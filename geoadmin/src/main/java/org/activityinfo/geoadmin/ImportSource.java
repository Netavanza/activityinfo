package org.activityinfo.geoadmin;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.GeometryType;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import com.google.common.collect.Lists;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class ImportSource {


	private List<PropertyDescriptor> attributes;
	private List<Object[]> rows;
	private List<Envelope> envelopes;

	private FeatureSource featureSource;
	private MathTransform transform;

	public ImportSource(File shapefile) throws Exception {
		ShapefileDataStore ds = new ShapefileDataStore(shapefile.toURI().toURL());

		featureSource = ds.getFeatureSource();

		transform = createTransform();
		loadFeatures();   
	}

	/**
	 * Loads the feature's attributes and the envelope for the 
	 * geometry into memory.
	 */
	private void loadFeatures() throws IOException {

		attributes = getNonGeometryAttributes();
		FeatureCollection features = featureSource.getFeatures();

		rows = Lists.newArrayList();
		envelopes = Lists.newArrayList();



		FeatureIterator it = features.features();
		while(it.hasNext()) {
			SimpleFeature feature = (SimpleFeature) it.next();

			rows.add(toAttributeArray(feature));
			envelopes.add(calcWgs84Envelope(feature));
		}
	}

	private Envelope calcWgs84Envelope(SimpleFeature feature) {
		try {
			Geometry geometry = (Geometry)feature.getDefaultGeometryProperty().getValue();
			Geometry geometryInWgs84 = JTS.transform(geometry, transform);
			Envelope envelope = geometryInWgs84.getEnvelopeInternal();
			return envelope;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Object[] toAttributeArray(SimpleFeature feature) {
		Object[] attribs = new Object[attributes.size()];
		for(int i=0;i!=attribs.length;++i) {
			attribs[i] = feature.getAttribute(attributes.get(i).getName());
		}
		return attribs;
	}

	private List<PropertyDescriptor> getNonGeometryAttributes() {
		attributes = Lists.newArrayList();
		for(PropertyDescriptor descriptor : featureSource.getSchema().getDescriptors()) {
			if(!(descriptor.getType() instanceof GeometryType)) {
				attributes.add(descriptor);
			}
		}
		return attributes;
	}

	public Envelope getEnvelope(int featureIndex) {
		return envelopes.get(featureIndex);
	}

	public List<Object[]> getRows() {
		return rows;
	}

	public List<PropertyDescriptor> getAttributes() {
		return attributes;
	}

	public int getFeatureCount() {
		return rows.size();
	}

	private MathTransform createTransform() throws Exception {
		GeometryDescriptor geometryType = featureSource.getSchema().getGeometryDescriptor();
		CoordinateReferenceSystem sourceCrs = geometryType.getCoordinateReferenceSystem();

		CoordinateReferenceSystem geoCRS = DefaultGeographicCRS.WGS84;
		boolean lenient = true; // allow for some error due to different datums
		return CRS.findMathTransform(sourceCrs, geoCRS, lenient);
	}

	public int getAttributeCount() {
		return attributes.size();
	}

	public Object getAttributeValue(int featureIndex, int attribIndex) {
		return rows.get(featureIndex)[attribIndex];
	}

	public Object getAttributeValue(int featureIndex,
			PropertyDescriptor attribute) {

		return getAttributeValue(featureIndex, attributes.indexOf(attribute));

	}

	public String getAttributeStringValue(int featureIndex,
			PropertyDescriptor attribute) {

		Object value = getAttributeValue(featureIndex, attribute);
		if(value == null) {
			return null;
		} else {
			return value.toString();
		}
	}
	
	public String[] getAttributeNames() {
		String[] names = new String[attributes.size()];
		for(int i=0;i!=names.length;++i) {
			names[i] = attributes.get(i).getName().getLocalPart();
		}
		return names;
	}

	public FeatureSource getFeatureSource() {
		return featureSource;
	}

	public String featureToString(int bestFeature) {
		StringBuilder sb = new StringBuilder();
		for(int i=0;i!=getAttributeCount();++i) {
			if(i > 0) {
				sb.append(" ");
			}
			sb.append(getAttributeValue(bestFeature, i));
		}
		return sb.toString();
	}
	
	public double similarity(int featureIndex, String name) {
		double nameSimilarity = 0;
		for(int attributeIndex=0;attributeIndex!=getAttributeCount();++attributeIndex) {
			Object value = getAttributeValue(featureIndex, attributeIndex);
			if(value != null) {
				nameSimilarity = Math.max(nameSimilarity, MatchUtils.similiarity(name, value.toString()));
			}
		}
		return nameSimilarity;
	}

	public boolean hasCode(int featureIndex, String code) {
		if(code.matches("\\d+")) {
			return hasIntCode(featureIndex, Integer.parseInt(code));
		} else {
			// TODO
			return false;
		}
	} 


	private boolean hasIntCode(int featureIndex, int code) {
		for(int attributeIndex=0;attributeIndex!=getAttributeCount();++attributeIndex) {
			try {
				Object value = getAttributeValue(featureIndex, attributeIndex);
				if(value instanceof Number) {
					if(((Number) value).intValue() == code) {
						return true;
					}
				} else if(value instanceof String) {
					if(Integer.parseInt((String)value) == code) {
						return true;
					}
				}
			} catch(Exception e) {
			}
		}
		return false;
	}
}

