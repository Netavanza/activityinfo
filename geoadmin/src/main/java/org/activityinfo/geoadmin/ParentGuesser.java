package org.activityinfo.geoadmin;

import java.io.IOException;
import java.util.List;

import org.activityinfo.geoadmin.model.AdminUnit;
import org.opengis.feature.type.PropertyDescriptor;

import com.google.common.collect.Lists;
import com.vividsolutions.jts.geom.Envelope;

public class ParentGuesser {

	private static final double MIN_SCORE = 0.75;

	private PropertyDescriptor parentNameAttribute;
	private ImportSource importSource;
	private List<AdminUnit> parents;

	public void setParentName(PropertyDescriptor parentName) {
		this.parentNameAttribute = parentName;
	}

	public void setImportSource(ImportSource importSource) {
		this.importSource = importSource;
	}

	public void setParents(List<AdminUnit> parents) {
		this.parents = parents;
	}

	public AdminUnit[] run() throws IOException {
		AdminUnit[] matches = new AdminUnit[importSource.getFeatureCount()];
		for(int i=0;i!=matches.length;++i) {
			matches[i] = findBestMatch(i);
		}
		return matches;

	}

	private AdminUnit findBestMatch(int featureIndex) {
		double bestScore = MIN_SCORE;
		AdminUnit bestParent = null;
		for(AdminUnit parent : parents) {
			double score = scoreParent(featureIndex, parent);
			if(score > bestScore) {
				bestParent = parent;
			}
		}
		return bestParent;
	}

	private double scoreParent(int featureIndex, AdminUnit parent) {
		
		// parent should completely contain the child
		// find the proportion contained
		Envelope parentEnvelope = GeoUtils.toEnvelope(parent.getBounds());
		Envelope childEnvelope = importSource.getEnvelope(featureIndex);
		double propContained = parentEnvelope.intersection(childEnvelope).getArea() /
					parentEnvelope.getArea();
		
		// check the name similarity
		double nameSimilarity = importSource.similarity(featureIndex, parent.getName());
		
		// check for the presence of the code
		double codePresent = 0;
		if(parent.getCode() == null) {
			if(importSource.hasCode(featureIndex, parent.getCode())) {
				codePresent = 1;
			}
		}
		
		return propContained + nameSimilarity + codePresent;
	}
}
