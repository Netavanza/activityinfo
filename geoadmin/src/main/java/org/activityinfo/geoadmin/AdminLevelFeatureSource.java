package org.activityinfo.geoadmin;

import org.activityinfo.geoadmin.model.AdminLevel;
import org.activityinfo.geoadmin.model.AdminUnit;
import org.geotools.data.memory.MemoryDataStore;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Polygon;

public class AdminLevelFeatureSource  {


	public static MemoryDataStore load(GeoClient client, AdminLevel adminLevel) throws TransformException {
		
		SimpleFeatureType featureType = featureType(adminLevel);
		MemoryDataStore ds = new MemoryDataStore(featureType);
		
		for(AdminUnit adminUnit : client.getAdminEntities(adminLevel)) {
			SimpleFeatureBuilder builder = new SimpleFeatureBuilder(featureType);

			//add the values
			builder.add( adminUnit.getName() );
			builder.add( adminUnit.getCode() );
			builder.add( JTS.toGeographic( GeoUtils.toEnvelope( adminUnit.getBounds() ), DefaultGeographicCRS.WGS84 ) );
			
			ds.addFeature(builder.buildFeature( Integer.toString(adminUnit.getId()) ));
		}
		return ds;
	}

	private static SimpleFeatureType featureType(AdminLevel adminLevel) {
		SimpleFeatureTypeBuilder b = new SimpleFeatureTypeBuilder();

		//set the name
		b.setName( adminLevel.getName() );

		//add some properties
		b.add( "name", String.class );
		b.add( "code", String.class );

		//add a geometry property
		b.setCRS( DefaultGeographicCRS.WGS84 ); // set crs first
		b.add( "limits", Polygon.class ); // then add geometry

		//build the type
		return b.buildFeatureType();
	}
	

}
