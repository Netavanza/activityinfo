package org.activityinfo.polygons;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import org.activityinfo.jsonrpc.ActivityInfoService;
import org.activityinfo.jsonrpc.AdminUnit;
import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureCollection;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.simplify.TopologyPreservingSimplifier;

public class PolygonBuilder {
	
	private static final Logger LOGGER = Logger.getLogger(PolygonBuilder.class.getName());
	
	private int adminLevelId;
	private File propertiesFile;
	private List<AdminUnit> entities;
	private MatchingStrategy matchingStrategy;
	private Properties properties;
	private List<OutputWriter> writers = Lists.newArrayList();
	private File geodbRoot; 
	private Filter filter = Filter.INCLUDE;

	public PolygonBuilder(File geodbRoot, File propertiesFile) throws Exception {
		this.geodbRoot = geodbRoot;
		this.propertiesFile = propertiesFile;
		
		properties = new Properties();
		properties.load(new FileInputStream(propertiesFile));

		if(!Strings.isNullOrEmpty(properties.getProperty("adminLevelId"))) {
			this.adminLevelId = Integer.parseInt(properties.getProperty("adminLevelId"));
		}
	}
	
	public void setAdminLevelId(int id) {
		this.adminLevelId = id;
	}
	
	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public void run() throws Exception, IOException {
		fetchEntities();
		initMatchingStrategy();
		initWriters();
		buildPolygons();
	}

	private void fetchEntities() throws Exception {
		if(adminLevelId == 0) {
			throw new IllegalStateException("AdminLevelId has not been set");
		}
		
		ActivityInfoService service = new ActivityInfoService();
		this.entities = service.getAdminEntities(adminLevelId);
	}
	
	private void initMatchingStrategy() {
		if(!Strings.isNullOrEmpty(properties.getProperty("id.column"))) {
			matchingStrategy = new IdMatchingStrategy(properties);
		} else {
			matchingStrategy = new NameMatchingStrategy(
					entities,
					properties);
		} 
	}
	
	private void initWriters() throws IOException {
		File outputDir = new File(geodbRoot, "geometry");
		outputDir.mkdirs();
		writers.add(new WkbOutput(outputDir, adminLevelId));
		writers.add(new GoogleMapsWriter(outputDir, adminLevelId));
		writers.add(new SqlBoundsOutput(outputDir, adminLevelId));
	}

	private void buildPolygons() throws Exception {
		ShapefileDataStore ds = new ShapefileDataStore(shapeFilePath());

		FeatureSource featureSource = ds.getFeatureSource();
        SimpleFeatureCollection features = featureSource.getFeatures(filter);
            
        MathTransform transform = createTransform(features);
        
        MatchChecker matchChecker = new MatchChecker(entities);
     
        for(OutputWriter writer : writers) {
        	writer.start(features);
        }
        
        Iterator it = features.iterator();
        if(!it.hasNext()) {
        	throw new RuntimeException("Feature set is empty!");
        }
        
        while(it.hasNext()) {
        	SimpleFeature feature = (SimpleFeature) it.next();
        	int entityId = matchingStrategy.match(feature);
        	if(entityId != -1) {
	        	
	        	matchChecker.onMatched(entityId);
	        	
	        	Geometry polygon = (Geometry) feature.getDefaultGeometry().getValue();
	        	polygon = JTS.transform(polygon, transform);
	        	//polygon = simplify(polygon);
	        	
	        	for(OutputWriter writer : writers) {
	        		writer.write(entityId, polygon);
	        	}
        	}
        }
        
        matchingStrategy.done();
        matchChecker.check();
        
        for(OutputWriter writer : writers) {
        	writer.close();
        }
	}

	private MathTransform createTransform(SimpleFeatureCollection features) throws Exception {
		CoordinateReferenceSystem sourceCrs = null;
		
		if(features.getCRS() != null) {
			sourceCrs = features.getCRS();
		}
        if(!Strings.isNullOrEmpty(properties.getProperty("source.crs"))) {
        	sourceCrs = CRS.decode(properties.getProperty("source.crs"));
        }
        if(sourceCrs == null) {
        	LOGGER.warning("No source CRS specified, assuming WGS84...");
        	sourceCrs = DefaultGeographicCRS.WGS84;
        }
        
        CoordinateReferenceSystem geoCRS = DefaultGeographicCRS.WGS84;
    	boolean lenient = true; // allow for some error due to different datums
        return CRS.findMathTransform(sourceCrs, geoCRS, lenient);
	}


	private Geometry simplify(Geometry polygon) {
		double dist = Simplification.getMinDistance(polygon);
		Geometry simplified = TopologyPreservingSimplifier.simplify(polygon, dist);
		if(!simplified.isValid()) {
			throw new IllegalStateException("simplification resulted in invalid geometry");
		}
		return simplified;
	}

	private URL shapeFilePath() throws MalformedURLException {
		String source = properties.getProperty("source");
		File sourceFile = new File(geodbRoot.getAbsolutePath() + File.separator + "sources" + File.separator + source);
		if(!sourceFile.exists()) {
			throw new RuntimeException("Source file '" + source + "' cannot be found at '" + sourceFile + "'");
		}
		return sourceFile.toURI().toURL();
	}

	public int getAdminLevelId() {
		return adminLevelId;
	}
}
