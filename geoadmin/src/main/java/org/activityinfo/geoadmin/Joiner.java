package org.activityinfo.geoadmin;

import java.util.List;
import java.util.Set;

import org.activityinfo.geoadmin.model.AdminUnit;

import com.google.common.collect.Lists;
import com.vividsolutions.jts.geom.Envelope;

public class Joiner {

	private ImportSource importSource;
	private List<AdminUnit> units;

	private List<Join> joins = Lists.newArrayList();
	private Set<AdminUnit> unmatchedUnits;
	private Set<Integer> unmatchedFeatures;
	
	
	public void setImportSource(ImportSource importSource) {
		this.importSource = importSource;
	}
	public void setUnits(List<AdminUnit> units) {
		this.units = units;
	}
	

	public List<Join> join() {
		
		List<Join> joins = Lists.newArrayList();
	
		for(AdminUnit unit : units) {
			int bestIndex = findBestMatch(unit);
			joins.add(new Join(unit, bestIndex));
		}
		
		return joins;
	}
	
	private int findBestMatch(AdminUnit unit) {
		double bestScore = 0;
		int bestFeature = -1;
		for(int featureIndex=0;featureIndex!=importSource.getFeatureCount();++featureIndex) {
			double score = scoreJoin(unit, featureIndex);
			if(score > bestScore) {
				bestScore = score;
				bestFeature = featureIndex;
			}
		}
		return bestFeature;
	}
	
	private double scoreJoin(AdminUnit unit, int featureIndex) {
		// calculate the proportion of overlap with this admin unit
		Envelope unitEnvelope = GeoUtils.toEnvelope(unit.getBounds());
		Envelope featureEnvelope = importSource.getEnvelope(featureIndex);
		double geoOverlap = unitEnvelope.intersection(featureEnvelope).getArea() /
				unitEnvelope.getArea();
		
		// calculate the name overlap
		double nameSimilarity = importSource.similarity(featureIndex, unit.getName());
		
		System.out.println(String.format("%s <> %s %.2f %.2f", unit.getName(), importSource.featureToString(featureIndex), 
				geoOverlap, nameSimilarity));
	
		return nameSimilarity + geoOverlap;
	}
	
	
}
